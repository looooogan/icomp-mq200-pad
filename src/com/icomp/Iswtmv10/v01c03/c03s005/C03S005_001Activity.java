package com.icomp.Iswtmv10.v01c03.c03s005;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c03.c03s000.C03S000_001Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c03.c03s003.C03S003Wsdl;
import com.icomp.wsdl.v01c03.c03s003.endpoint.C03S003Request;
import com.icomp.wsdl.v01c03.c03s003.endpoint.C03S003Respons;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 员工卡初始化页面1
 * Created by FanLL on 2017/6/20.
 */

public class C03S005_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    //查询线程
    private SearchThread searchThread;

    //员工初始化参数类
    private C03S005Params params = new C03S005Params();
    private C03S003Request request = new C03S003Request();
    private C03S003Respons respons = new C03S003Respons();
    private C03S003Wsdl wsdl = new C03S003Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s005_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //将输入的材料号自动转化为大写
        et01.setTransformationMethod(new AllCapTransformationMethod());
        //接受下一个页面点击返回按钮时返回的员工号
        params.employeeCard = getIntent().getStringExtra(PARAM1);
        //显示员工卡号
        if (null != params.employeeCard) {
            et01.setText(params.employeeCard);
        }
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        finish();
    }

    //返回按钮处理--返回初始化菜单页面
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C03S000_001Activity.class);
        startActivity(intent);
        finish();
    }

    //查询按钮处理
    @OnClick(R.id.btnSearch)
    public void onViewClicked() {
        btnSearch.setClickable(false);
        params.employeeCard = et01.getText().toString().trim();
        if ("".equals(params.employeeCard)) {
            createAlertDialog(C03S005_001Activity.this, getString(R.string.c03s005_001_002), Toast.LENGTH_LONG);
            btnSearch.setClickable(true);
        } else {
            loading.show();
            //查询线程
            searchThread = new SearchThread();
            searchThread.start();
        }
    }

    //查询线程
    private class SearchThread extends Thread {
        @Override
        public void run() {
            super.run();
            //设定用户访问信息
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
            //用户ID
            String customerID = sharedPreferences.getString("customerID", "");
            request.setCustomerID(customerID);
            //手持机ID
            String handSetId = sharedPreferences.getString("handsetid", "");
            request.setHandSetId(handSetId);
            //员工号
            request.setEmployeeCard(exChangeBig(params.employeeCard));
            Message message = new Message();
            try {
                //按员工卡号查询员工信息
                respons = wsdl.findMemberMsgByCard(request);
                if (null != respons) {
                    message.obj = respons;
                    submitHandler.sendMessage(message);
                } else {
                    btnSearch.setClickable(true);
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnSearch.setClickable(true);
            }
        }
    }

    //正常提交数据的Handler
    @SuppressLint("HandlerLeak")
    Handler submitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loading.dismiss();
            respons =(C03S003Respons) msg.obj;
            if (ZERO.equals(respons.getStateCode())) {
                //员工号
                params.employeeCard = respons.getEmployeeCard();
                //真实姓名
                params.userName = respons.getUserName();
                //部门
                params.departmentName = respons.getDepartmentName();
                //绑定人用户ID
                params.blindCustomerID = respons.getBlindCustomerID();
                //通过员工号查询成功，跳转下一页面
                Intent intent = new Intent(C03S005_001Activity.this, C03S005_002Activity.class);
                intent.putExtra(PARAM, params);
                startActivity(intent);
                finish();
            } else {
                createAlertDialog(C03S005_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                btnSearch.setClickable(true);
            }
        }
    };

}
