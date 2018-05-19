package com.icomp.Iswtmv10.v01c01.c01s011;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s011.C01S011Wsdl;
import com.icomp.wsdl.v01c01.c01s011.endpoint.C01S011Request;
import com.icomp.wsdl.v01c01.c01s011.endpoint.C01S011Respons;
import com.umeng.analytics.MobclickAgent;

/**
 * 设备安上3
 *
 * @author WHY
 * @ClassName: C01S011_003Activity
 * @date 2016-3-2 下午5:19:50
 */

public class C01S011_003Activity extends CommonActivity {
    private C01S011Request request = new C01S011Request();
    private C01S011Respons respons;
    private C01S011Wsdl service = new C01S011Wsdl();
    private TextView tv_01, tv_02;
    private PopupWindow popupWindow1;
    private Button btn_confirm;
    //合成刀具ID,合成刀编码,装入设备的设备ID,装入设备的轴号ID,合成刀具RFID
    private String synthesisParametersID, synthesisParametersCode, equipmentID, equipmentAxisNumberID, synthesisParametersRfid;
    //设备名。轴编号
    private String equipmentName, axleCode, gruantUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s011_003activity);
//        synthesisParametersID = getIntent().getStringExtra("synthesisParametersID");
        synthesisParametersCode = getIntent().getStringExtra("synthesisParametersCode");
        equipmentID = getIntent().getStringExtra("equipmentID");
        equipmentAxisNumberID = getIntent().getStringExtra("equipmentAxisNumberID");
        synthesisParametersRfid = getIntent().getStringExtra("synthesisParametersRfid");
        equipmentName = getIntent().getStringExtra("equipmentName");
        axleCode = getIntent().getStringExtra("axleCode");
        gruantUserID = getIntent().getStringExtra("gruantUserID");//授权人ID

        //合成刀编码
        tv_01 = (TextView) findViewById(R.id.tv_01);
        tv_01.setText(synthesisParametersCode);

        //装入设备
        tv_02 = (TextView) findViewById(R.id.tv_02);
        tv_02.setText(equipmentName + "——" + axleCode);

        btn_confirm = (Button) findViewById(R.id.btn_confirm);

    }




    //返回按钮处理
    public void appReturn(View view) {
//        Intent intent = new Intent(this, C00S000_002Activity.class);
//        startActivity(intent);
        finish();
    }

    //确定按钮处理
    public void appConfirm(View view) {
        loading.show();
        threadLoad = new LoadingThread();
        threadLoad.start();
    }

    private LoadingThread threadLoad;

    public class LoadingThread extends Thread {
        @Override
        public void run() {
            C01S011Respons respons = submitSyntheticInstall();
            Message message = new Message();
            message.obj = respons;
            loadingmhandler.sendMessage(message);

        }
    }

    @SuppressLint("HandlerLeak")
    Handler loadingmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            respons = (C01S011Respons) msg.obj;
            loading.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(C01S011_003Activity.this);
            if ("0".equals(respons.getStateCode())) {
                // 创建一个AlertDialog的构建者对象
                builder.setTitle(R.string.infoMsg);// 设置标题
                builder.setMessage(respons.getStateMsg());// 提示信息
                builder.setCancelable(false);// 设置对话框不能被取消
                // 设置正面的按钮
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(C01S011_003Activity.this, C00S000_003Activity.class);
//                        //跳转页面
//                        startActivity(intent);
//                        finish();
                        SysApplication.getInstance().exit();
                    }
                });
                builder.show();
            } else {
                createAlertDialog(C01S011_003Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
            }
            btn_confirm.setEnabled(true);
        }
    };


    /**
     * 点击下一步提交数据方法
     */
    public C01S011Respons submitSyntheticInstall() {
        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        String customerID = sharedPreferences.getString("customerID", ""); // 用户id
        String handsetid = sharedPreferences.getString("handsetid", "");
        request.setHandSetId(handsetid);
        request.setCustomerID(customerID);
//            request.setSynthesisParametersID(synthesisParametersID);//合成刀具id
        request.setSynthesisParametersCode(synthesisParametersCode);//合成刀编码
        request.setEquipmentID(equipmentID);// 设备Id
        request.setEquipmentAxisNumberID(equipmentAxisNumberID);//轴id
        request.setSynthesisParametersRfid(synthesisParametersRfid);//合成刀具rfid
        request.setGruantUserID(gruantUserID);//授权人ID
        // 合成刀具安上设备
        try {
            respons = service.submitSyntheticInstallEquipmentInfo(request);
            return respons;
        } catch (Exception e) {
            respons = new C01S011Respons();
            respons.setStateCode("1");
            respons.setStateMsg(getResources().getString(R.string.netConnection));
            e.printStackTrace();
            return respons;
        }
    }

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
