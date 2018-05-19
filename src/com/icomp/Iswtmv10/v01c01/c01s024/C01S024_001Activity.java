package com.icomp.Iswtmv10.v01c01.c01s024;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s024.C01S024Wsdl;
import com.icomp.wsdl.v01c01.c01s024.endpoint.C01S024Request;
import com.icomp.wsdl.v01c01.c01s024.endpoint.C01S024Respons;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 快速查询页面1
 * Created by FanLL on 2017/6/15.
 */

public class C01S024_001Activity extends CommonActivity {

    @BindView(R.id.btnScan)
    Button btnScan;

    //扫描线程
    private scanThread scanThread;

    //快速查询参数类
    C01S024Params params = new C01S024Params();
    C01S024Request request = new C01S024Request();
    C01S024Respons respons = new C01S024Respons();
    C01S024Wsdl wsdl = new C01S024Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s024_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
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
        finish();
    }

    //扫描按钮处理
    @OnClick(R.id.btnScan)
    public void onViewClicked() {
        //点击扫描按钮的方法
        scan();
    }

    //点击扫描按钮的方法
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
    public class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //调用单扫方法
            rfidString = singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                overtimeHandler.sendMessage(message);
            } else if(null != rfidString && !"close".equals(rfidString)) {
                //rfidString
                request.setRfidCode(rfidString);
                //设定用户访问信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                //用户ID
                String customerID = sharedPreferences.getString("customerID", "");
                request.setCustomerID(customerID);
                //手持机ID
                String handSetId = sharedPreferences.getString("handsetid", "");
                request.setHandSetId(handSetId);
                try {
                    //取得要查询的信息
                    respons = wsdl.getInformation(request);
                    if (null != respons) {
                        message.obj = respons;
                        scanHandler.sendMessage(message);
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
    public Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            try{
                respons = (C01S024Respons) message.obj;
                //取值成功，将数据集合显示在下一页面的ListView中
                if (ZERO.equals(respons.getStateCode())) {
                    Intent intent = new Intent(C01S024_001Activity.this, C01S024_002Activity.class);
                    params.setList(respons.getCon());
                    intent.putExtra(PARAM, params);
                    startActivity(intent);
                    finish();
                } else {
                    //取值失败，提示返回的错误消息
                    createAlertDialog(C01S024_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    btnScan.setClickable(true);
                    isCanScan = true;

                }
            } catch (Exception e) {
                e.printStackTrace();
                createAlertDialog(C01S024_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnScan.setClickable(true);
                isCanScan = true;
            }
        }
    };

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
