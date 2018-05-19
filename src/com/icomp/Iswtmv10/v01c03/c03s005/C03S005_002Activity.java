package com.icomp.Iswtmv10.v01c03.c03s005;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c03.c03s003.C03S003Wsdl;
import com.icomp.wsdl.v01c03.c03s003.endpoint.C03S003Request;
import com.icomp.wsdl.v01c03.c03s003.endpoint.C03S003Respons;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

 /**
 * 员工卡初始化页面2
 * Created by FanLL on 2017/6/14.
 */

public class C03S005_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.tv_02)
    TextView tv02;
    @BindView(R.id.tv_03)
    TextView tv03;
    @BindView(R.id.btnScan)
    Button btnScan;

    //扫描线程
    private scanThread scanThread;
    //已经初始化的员工卡，重新初始化的线程
    private submitThread submitThread;

    //员工初始化参数类
    public C03S005Params params = new C03S005Params();
    public C03S003Request request = new C03S003Request();
    public C03S003Respons respons = new C03S003Respons();
    public C03S003Wsdl wsdl = new C03S003Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s005_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传递的数值
        params = (C03S005Params) getIntent().getSerializableExtra(PARAM);
        //员工号
        tv01.setText(params.employeeCard);
        //真实姓名
        tv02.setText(params.userName);
        //部门
        tv03.setText(params.departmentName);
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Intent intent = new Intent(this, C03S005_001Activity.class);
        //点击返回时将员工卡号传递到上一个页面
        intent.putExtra(PARAM1,params.employeeCard);
        startActivity(intent);
        finish();
    }

    //扫描按钮处理
    @OnClick(R.id.btnScan)
    public void onViewClicked() {
        //扫描方法
        scan();
    }

     //扫描方法
     private void scan() {
         //点击扫描按钮以后，设置扫描按钮不可用
         btnScan.setClickable(false);
         //显示扫描弹框的方法
         scanPopupWindow();
         //开启扫描线程
         scanThread = new scanThread();
         scanThread.start();
     }

    //扫描线程
    private class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //单扫方法
            singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                overtimeHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
            } else if(null != rfidString && !"close".equals(rfidString)) {
                //设定用户访问信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                //用户ID
                String customerID = sharedPreferences.getString("customerID", "");
                request.setCustomerID(customerID);
                //手持机ID
                String handSetId = sharedPreferences.getString("handsetid", "");
                request.setHandSetId(handSetId);
                //rfidString
                request.setRfidCode(rfidString);
                //员工号
                request.setEmployeeCard(params.employeeCard);
                //设定标签类型（3--员工卡）
                request.setQueryType(THREE);
                //绑定员工用户ID
                request.setBlindCustomerID(params.blindCustomerID);
                try {
                    //提交员工卡绑定数据
                    respons = wsdl.submitEmployeeCardMsg(request);
                    if (null != respons) {
                        message.obj = respons;
                        submithandler.sendMessage(message);
                    } else {
                        btnScan.setClickable(true);
                        isCanScan = true;
                        internetErrorHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    internetErrorHandler.sendMessage(message);
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            }
        }
    }

    //正常提交数据的Handler
    @SuppressLint("HandlerLeak")
    public Handler submithandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            if (null != loading && loading.isShowing()) {
                loading.dismiss();
            }
            try{
                respons = (C03S003Respons) message.obj;
                //取值成功，跳转页面
                if (ZERO.equals(respons.getStateCode())) {
                    Intent intent = new Intent(C03S005_002Activity.this, C03S005_003Activity.class);
                    startActivity(intent);
                    finish();
                } else if (FIVE.equals(respons.getStateCode())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(C03S005_002Activity.this);
                    builder.setTitle(R.string.prompt);
                    builder.setMessage(R.string.c03s005_002_001);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loading.show();
                            submitThread = new submitThread();
                            submitThread.start();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int a) {
                            dialogInterface.dismiss();
                            btnScan.setClickable(true);
                            isCanScan = true;
                        }
                    }).show();
                } else {
                    //取值失败，提示返回的错误消息
                    createAlertDialog(C03S005_002Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
            }
        }
    };

    //已经初始化的员工卡重新初始化的线程
    private class submitThread extends Thread {
        @Override
        public void run() {
            super.run();
            //授权ID
            request.setGruantUserID("已初始化过的员工卡重新初始化");
            Message message = new Message();
            try {
                //提交员工卡绑定数据
                respons = wsdl.submitEmployeeCardMsg(request);
                if (null != respons) {
                    message.obj = respons;
                    submithandler.sendMessage(message);
                } else {
                    btnScan.setClickable(true);
                    isCanScan = true;
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
            }
        }
    }

     //重写键盘上扫描按键的方法
     @Override
     protected void btnScan() {
         super.btnScan();
         if(isCanScan) {
             isCanScan = false;
         } else {
             return;
         }
         //扫描方法
         scan();
     }

}
