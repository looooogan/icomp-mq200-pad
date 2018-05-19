package com.icomp.Iswtmv10.v01c01.c01s013;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
 * 设备卸下1
 *
 * @author WHY
 * @ClassName: C01S013_001Activity
 * @date 2016-3-2 下午6:33:48
 */

public class C01S013_001Activity extends CommonActivity {
    private C01S013_Params params;
    private C01S013Request request = new C01S013Request();
    private C01S013Respons respons;
    private C01S013Wsdl service = new C01S013Wsdl();
    private PopupWindow popupWindow;
    private Button btn_scan, btn_return;
    private boolean type = true;//线程停止条件
    private boolean isCanScan = true; //是否可以按键扫描

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s013_001activity);
        btn_scan = (Button) findViewById(R.id.btn_scan);//扫描按钮
    }

    /**
     * 扫描事件
     */
    public void scan(View view) {
        isCanScan = false;
        showWindow(view, R.layout.c00s000_010activity);
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan){
            isCanScan = false;
        }else{return;}
        if (popupWindow == null || !popupWindow.isShowing()) {
            showWindow(null, R.layout.c00s000_010activity);
        }
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

    //返回按钮处理
    public void appReturn(View view) {
        if (null != popupWindow) {
            popupWindow.dismiss();
            type = false;

        }
        finish();
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
        popupWindow = new PopupWindow(vPopupWindow, 300, 240);
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
            String rfidString = null;
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
            isCanScan = true;
            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                createAlertDialog(C01S013_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
            } else {
                try {

                    request.setRfidCode(returnString);//RfidCode
                    //设定用户信息
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                    String customerID = sharedPreferences.getString("customerID", "");
                    String handsetid = sharedPreferences.getString("handsetid", "");
                    request.setHandSetId(handsetid);
                    //customerID
                    request.setCustomerID(customerID);
                    //标签类型（0库位标签，1单品刀，2合成刀具，3员工卡，4设备，5容器，6备刀库）
                    request.setQueryType("2");
                    //取得合成刀信息和设备列表
                    respons = service.getSynthesisToolInfoOutEqu(request);
                    if ("0".equals(respons.getStateCode())) {
                        params = new C01S013_Params();
                        //对应工序ID
                        params.processID = respons.getProcessID();
                        //对应工序编码
                        params.processCode = respons.getProcessCode();
                        //设备名称
                        params.equipmentName = respons.getEquipmentName();
                        //设备ID
                        params.equipmentID = respons.getEquipmentID();
                        //轴号名称
                        params.axleName = respons.getAxleName();
                        //轴号ID
                        params.axleID = respons.getAxleID();
                        //RFID标签
                        params.rfidCode = returnString;
                        //合成刀编码
                        params.synthesisParametersCode = respons.getSynthesisParametersCode();//合成刀具编码
                        params.partsEntity = respons.getPartsList();//零部件种类信息{零部件ID，零部件名称，零部件类型}
                        params.setSynthesisParametersID(respons.getSynthesisParametersID());//合成刀具ID
                        Intent intent = new Intent(C01S013_001Activity.this, C01S013_002Activity.class);
                        intent.putExtra(PARAM, params);
                        startActivity(intent);
                    } else {
                        createAlertDialog(C01S013_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
                }
            }
            popupWindow.dismiss();
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
