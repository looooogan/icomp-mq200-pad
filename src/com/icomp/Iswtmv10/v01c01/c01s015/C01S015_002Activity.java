package com.icomp.Iswtmv10.v01c01.c01s015;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c04.c04s001.C04S001Wsdl;
import com.icomp.wsdl.v01c04.c04s001.endpoint.C04S001Request;
import com.icomp.wsdl.v01c04.c04s001.endpoint.C04S001Respons;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 库房盘点页面2
 * Created by FanLL on 2017/6/16.
 */

public class C01S015_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.tv_02)
    TextView tv02;
    @BindView(R.id.et_01)
    EditText et01;

    //提交线程
    private SubmitThread submitThread;

    //库房盘点参数类
    private C01S015Params params = new C01S015Params();
    private C04S001Request request = new C04S001Request();
    private C04S001Respons respons = new C04S001Respons();
    private C04S001Wsdl wsdl = new C04S001Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s015_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传来的参数
        params = (C01S015Params) getIntent().getSerializableExtra(PARAM);
        //材料号
        tv01.setText(exChangeBig(params.toolCode));
        //在库数量
        tv02.setText(exChangeBig(params.libraryCount));
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C01S015_001Activity.class);
        //点击返回时将材料号传递到上一个页面
        intent.putExtra(PARAM1,params.toolCode);
        startActivity(intent);
        finish();
    }

    //确定按钮处理--跳转到下一页面
    public void btnConfirm(View view) {
        params.realLibraryCount = et01.getText().toString().trim();
        if ("".equals(params.realLibraryCount)) {
            createAlertDialog(C01S015_002Activity.this, getString(R.string.c01s015_002_003), Toast.LENGTH_LONG);
        } else {
            loading.show();
            //提交线程
            submitThread = new SubmitThread();
            submitThread.start();
        }
    }

    //提交线程
    private class SubmitThread extends Thread {
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
            //材料号
            request.setToolCode(params.toolCode);
            //刀具ID
            request.setToolID(params.toolID);
            //刀具类型（0--非单品刀具，1--单品刀具）
            request.setToolConsumetype(ONE);
            //在库数量
            request.setLibraryCount(params.libraryCount);
            //实际在库数量
            request.setRealLibraryCount(params.realLibraryCount);
            Message message = new Message();
            try {
                //提交在库盘点刀具
                respons = wsdl.submitCheckToolDate(request);
                if (null != respons) {
                    message.obj = respons;
                    submitHandler.sendMessage(message);
                } else {
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
            }
        }
    }

    //提交的Handler
    @SuppressLint("HandlerLeak")
    private Handler submitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            respons = (C04S001Respons) msg.obj;
            loading.dismiss();
            //判断接口是否调用成功（0--成功，1--失败）
            if (ZERO.equals(respons.getStateCode())) {
                Intent intent = new Intent(C01S015_002Activity.this, C01S015_003Activity.class);
                startActivity(intent);
                finish();
            } else {
                createAlertDialog(C01S015_002Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
            }
        }
    };

}
