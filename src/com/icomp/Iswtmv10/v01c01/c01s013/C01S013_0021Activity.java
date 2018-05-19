package com.icomp.Iswtmv10.v01c01.c01s013;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s013.C01S013Wsdl;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Request;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Respons;

/**
 * 设备卸下3
 * 确认页面
 */

public class C01S013_0021Activity extends CommonActivity {
    private C01S013_Params params;
    private C01S013Respons respons;
    private TextView tv_01, tv_02, tv_03, tv_04, tv_05;
    private String tv1, tv2,tv3,tv4,tv5;
    private String handsetid, tv_011_posttion, tv_0111_posttion;//用户ID\当前选择的卸下原因，零部件种类在集合中的位置

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s013_0021activity);
        params = (C01S013_Params) getIntent().getSerializableExtra(PARAM);
        tv1 = (String) getIntent().getSerializableExtra("tv01");
        tv2 = (String) getIntent().getSerializableExtra("tv02");
        tv3 = (String) getIntent().getSerializableExtra("tv011");
        tv4 = (String) getIntent().getSerializableExtra("tv0111");
        tv5 = (String) getIntent().getSerializableExtra("et01");
        tv_011_posttion = (String) getIntent().getSerializableExtra("tv_011_posttion");
        tv_0111_posttion = (String) getIntent().getSerializableExtra("tv_0111_posttion");

        tv_01 = (TextView) findViewById(R.id.tv_01);
        tv_02 = (TextView) findViewById(R.id.tv_02);
        tv_03 = (TextView) findViewById(R.id.tv_03);
        tv_04 = (TextView) findViewById(R.id.tv_04);
        tv_05 = (TextView) findViewById(R.id.tv_05);

        tv_01.setText(tv1);
        tv_02.setText(tv2);
        tv_03.setText(tv3);
        tv_04.setText(tv4);
        tv_05.setText(tv5);

        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        handsetid = sharedPreferences.getString("handsetid", "");

    }

    /**
     * 返回
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * 确认
     */
    public void appConfirm(View view) {

            loading.show();
            ThreadSubmitSynthetic = new SubmitSyntheticThread();
            ThreadSubmitSynthetic.start();

    }

    /**
     * 提交数据线程
     */
    public class SubmitSyntheticThread extends Thread {
        @Override
        public void run() {
            C01S013Respons respons = submitSynthetic();
            if (null != respons) {
                Message message = new Message();
                message.obj = respons;
                submitSyntheticHandler.sendMessage(message);
            } else {
                internetErrorHandler.sendEmptyMessage(0);
            }
        }
    }

    public SubmitSyntheticThread ThreadSubmitSynthetic;
    Handler submitSyntheticHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            C01S013Respons respons = (C01S013Respons) msg.obj;
            if ("0".equals(respons.getStateCode())) {
                // 创建一个AlertDialog的构建者对象
                AlertDialog.Builder builder = new AlertDialog.Builder(C01S013_0021Activity.this);
                builder.setTitle(R.string.infoMsg);// 设置标题
                builder.setMessage(respons.getStateMsg());// 提示信息
                builder.setCancelable(false);// 设置对话框不能被取消
                // 设置正面的按钮
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳到刀具管理主界面
//                        Intent intent = new Intent(C01S013_002Activity.this, C00S000_003Activity.class);
//                        startActivity(intent);
//                        finish();
                        SysApplication.getInstance().exit();
                    }
                });
                builder.show();  // 显示对话框
            } else {
                createAlertDialog(null, respons.getStateMsg(), Toast.LENGTH_LONG);
            }
            loading.dismiss();
        }
    };
    /**
     * 处理网络异常的handler
     */
    @SuppressLint("HandlerLeak")
    Handler internetErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
            loading.dismiss();
        }
    };

    /**
     * 提交数据方法
     *
     * @return
     */
    public C01S013Respons submitSynthetic() {
        C01S013Wsdl service = new C01S013Wsdl();
        C01S013Request request = new C01S013Request();
        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        customerID = sharedPreferences.getString("customerID", "");
        handsetid = sharedPreferences.getString("handsetid", "");
        request.setCustomerID(customerID); //用户ID
        request.setProcessAmount(Integer.valueOf(tv_05.getText().toString().trim()));//加工量processCount
        request.setRemoveReason(tv_011_posttion);//卸下原因。传的位置
        request.setPartsID(params.partsEntity.get(Integer.parseInt(tv_0111_posttion)).getPartsID());//零部件ID
        request.setPartsName(params.partsEntity.get(Integer.parseInt(tv_0111_posttion)).getPartsName());//零部件名称
        request.setSynthesisParametersCode(params.synthesisParametersCode);//合成刀具编码
        request.setEquipmentID(params.equipmentID);//设备ID
        request.setAxisID(params.axleID);//轴号ID
        request.setRfidCode(params.rfidCode);//RFID标签
        request.setHandSetId(handsetid);
        request.setSynthesisParametersID(params.getSynthesisParametersID());//合成刀具ID
        try {
            respons = service.submitSyntheticUnloadEquipmentInfo(request);//提交合成刀下设备信息
            if (null != respons) {
                return respons;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
