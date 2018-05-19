package com.icomp.Iswtmv10.v01c01.c01s013;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s013.C01S013Wsdl;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Request;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Respons;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;

/**
 * 请扫描要卸下的砂轮
 */

public class C01S013_003Activity extends CommonActivity {
    private Button btn_scan;
    private boolean type = true;//线程停止条件
    String rfidString;
    private boolean isCanScan = true; //是否可以按键扫描

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s013_003activity);
        btn_scan = (Button) findViewById(R.id.btn_scan);
    }

    /**
     * 扫描
     */
    public void scan(View view) {
        isCanScan = false;
        showWindow(view, R.layout.c00s000_010activity);
    }
    /**
     * 监听键盘扫描按键
     *
     * @return
     */
//    protected boolean keycodeScanf() {
//        if (popupWindow == null || !popupWindow.isShowing()) {
//            showWindow(null, R.layout.c00s000_010activity);
//        }
//        return true;
//    }

    @Override
    protected void btnScan() {
        super.btnScan();
        if (isCanScan) {
            isCanScan = false;
        }else{
            return;
        }
        if (popupWindow == null || !popupWindow.isShowing()) {
            showWindow(null, R.layout.c00s000_010activity);
        }
    }

    /**
     * 返回
     */
    public void cancel(View view) {
        finish();
        type = false;
    }

    /**
     * 打开扫描Rfid页面
     *
     * @param parent
     * @param pageID void
     * @title showWindow
     */
    public void showWindow(View parent, int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        popupWindow = new PopupWindow(vPopupWindow, 300,240);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        btn_scan.setClickable(false);
        thread = new VisitJniThread();
        thread.start();

    }

    private VisitJniThread thread;

    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            initRFID();// 打开Rfid读头模块
            rfidString = null;
            Date date = new Date();
            do {
                rfidString = readRfidString(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    rfidString = "close";
                }
            } while (rfidString == null && !"close".equals(rfidString) && type);
            close();
            if (null != rfidString) {
                Message message = new Message();
                message.obj = rfidString;
                scanfmhandler.sendMessage(message);
            }
        }

    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            btn_scan.setClickable(true);
            String returnString = msg.obj.toString();
            Log.d("message", "RFID:" + returnString);
            if ("close".equals(returnString)) {
                createAlertDialog(C01S013_003Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
                isCanScan = true;
            } else {
                loading.show();
                outEquThread = new SynthesisToolInfoOutEqu();
                outEquThread.start();
            }
            popupWindow.dismiss();
        }
    };

    /**
     * 线程
     * 取得设备下（砂轮）合成刀具信息
     */
    private SynthesisToolInfoOutEqu outEquThread;

    public class SynthesisToolInfoOutEqu extends Thread {
        @Override
        public void run() {
            C01S013Respons response = SynthesisToolInfoOutEqu(rfidString);
            if (null != response) {
                Message message = new Message();
                message.obj = response;
                SynthesisToolhandler.sendMessage(message);
            } else {
                Message message = new Message();
                internetErrorHandler.sendMessage(message);
            }

        }
    }

    Handler SynthesisToolhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            C01S013Respons respons = (C01S013Respons) msg.obj;

            if ("0".equals(respons.getStateCode())) {
                //**********传送给下一页面数据*********************
                C01S013_Params params = new C01S013_Params();
                params.setProcessID(respons.getProcessID());//对应工序ID
                params.setProcessCode(respons.getProcessCode());//对应工序编码
                params.setAssemblyLineName(respons.getAssemblyLineName());//流水线名称
                params.setAssemblyLineID(respons.getAssemblyLineID());//流水线ID
                params.setEquipmentID(respons.getEquipmentID());//设备ID
                params.setRfidContainerId(respons.getRfidContainerId());//载体id
                params.setSynthesisParametersCodeList(respons.getSynthesisParametersCodeList());//合成刀具编码列表

//                params.setSynthesisParametersCodeList(respons.getSynthesisParametersCodeList());
                //**********************结束***********************
                /*1116 修改*/
                params.setEquipmentName(respons.getEquipmentName());//设备名称

                Intent intent = new Intent(C01S013_003Activity.this, C01S013_004Activity.class);//卸下砂轮页
                intent.putExtra(PARAM, params);
                startActivity(intent);
            } else {
                createAlertDialog(C01S013_003Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
            }
            isCanScan = true;
            loading.dismiss();
        }
    };

    /**
     * 方法
     * 取得设备下（砂轮）合成刀具信息
     */
    public C01S013Respons SynthesisToolInfoOutEqu(String rfid) {
        C01S013Wsdl wsdl = new C01S013Wsdl();
        C01S013Request request = new C01S013Request();
        request.setRfidCode(rfid);//RFID
        request.setQueryType("4");//标签类型（0库位标签，1单品刀，2合成刀具，3员工卡，4设备，5容器，6备刀库）
        C01S013Respons respons = new C01S013Respons();
        try {
            respons = wsdl.getSynthesisToolInfoOutWheel(request);//取得设备下（砂轮）合成刀具信息
            if (null == respons) {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respons;
    }

    /**
     * 处理网络异常的handler
     */
    @SuppressLint("HandlerLeak")
    Handler internetErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            isCanScan = true;
            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
            loading.dismiss();
        }
    };
    /**
     * 友盟错误统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
