package com.icomp.Iswtmv10.v01c01.c01s013;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.adapter.C01S003_004Adapter;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s013.C01S013Wsdl;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Request;
import com.icomp.wsdl.v01c01.c01s013.endpoint.C01S013Respons;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备卸下2
 *
 * @author WHY
 * @ClassName: C01S013_002Activity
 * @date 2016-3-2 下午6:47:51
 */

public class C01S013_002Activity extends CommonActivity {
    private C01S013_Params params;
    private C01S013Respons respons;
    private TextView tv_01, tv_02, tv_011, tv_0111;
    private LinearLayout ll_011, ll_0111;//卸下原因点击区域,零部件种类点击区域
    private EditText et_01;
    private int tv_011_posttion, tv_0111_posttion;//当前选择的卸下原因，零部件种类在集合中的位置
    private List<String> removeReasonList = new ArrayList<String>();//保存所有卸下原因
    private List<String> partsNameList = new ArrayList<String>();//保存所有零部件种类
    private PopupWindow mPopWindow;//卸下原因下拉列表框PopupWindow
    private PopupWindow mPopWindow1;//零部件种类下拉列表框PopupWindow
    private String customerID;//用户ID
    private String handsetid;//用户ID


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s013_002activity);
        params = (C01S013_Params) getIntent().getSerializableExtra(PARAM);
        et_01 = (EditText) findViewById(R.id.et_01);//加工量
        tv_01 = (TextView) findViewById(R.id.tv_01);//对应工序
        tv_02 = (TextView) findViewById(R.id.tv_02);//对应设备
        ll_011 = (LinearLayout) findViewById(R.id.ll_011); //卸下原因点击区域
        ll_0111 = (LinearLayout) findViewById(R.id.ll_0111);//零部件种类点击区域
        tv_011 = (TextView) findViewById(R.id.tv_011);//卸下原因
        tv_0111 = (TextView) findViewById(R.id.tv_0111);//零部件种类
        tv_01.setText(params.processCode);
        tv_02.setText(params.equipmentName);
        //存储所有卸下原因
        for (int i = 0; i < getResources().getStringArray(R.array.RemoveReason).length; i++) {
            removeReasonList.add(getResources().getStringArray(R.array.RemoveReason)[i]);
        }
        //存储所有零部件名称
        for (int i = 0; i < params.partsEntity.size(); i++) {
            partsNameList.add(params.partsEntity.get(i).getPartsName());
        }
        tv_011.setText(removeReasonList.get(0));//卸下原因初始值
        tv_0111.setText(params.partsEntity.get(0).getPartsName());//零部件种类初始值
        tv_011_posttion = 0;//卸下原因初始值位置
        tv_0111_posttion = 0;//零部件种类初始值位置
        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        customerID = sharedPreferences.getString("customerID", "");
        handsetid = sharedPreferences.getString("handsetid", "");
        //额定加工量
        et_01.setText(params.partsEntity.get(0).getProcessCount());

    }

    /**
     * 卸下原因下拉框
     */
    public void dropdown(View view) {
        if (removeReasonList.size() > 0) {
            int tvWidht = ll_011.getWidth();
            View contentView = LayoutInflater.from(C01S013_002Activity.this).inflate(R.layout.item_c01s020_001, null);
            mPopWindow = new PopupWindow(contentView, tvWidht, 200, true);
            mPopWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopWindow.showAsDropDown(ll_011);//显示对话框
            ListView lv_01 = (ListView) contentView.findViewById(R.id.lv_01);
            C01S003_004Adapter adapter = new C01S003_004Adapter(C01S013_002Activity.this, removeReasonList);
            lv_01.setAdapter(adapter);
            lv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        et_01.setText(params.partsEntity.get(tv_0111_posttion).getProcessCount());
                    } else {
                        et_01.setText("");
                    }
                    tv_011.setText(removeReasonList.get(i));
                    mPopWindow.dismiss();
                    tv_011_posttion = i;
                }
            });
        }
    }

    /**
     * 零部件种类下拉框
     */
    public void dropdown1(View view) {
        if (partsNameList.size() > 0) {
            int tvWidht = ll_0111.getWidth();
            View contentView = LayoutInflater.from(C01S013_002Activity.this).inflate(R.layout.item_c01s020_001, null);
            mPopWindow1 = new PopupWindow(contentView, tvWidht, 200, true);
            mPopWindow1.setBackgroundDrawable(new BitmapDrawable());
            mPopWindow1.showAsDropDown(ll_0111);//显示对话框
            ListView lv_01 = (ListView) contentView.findViewById(R.id.lv_01);
            C01S003_004Adapter adapter = new C01S003_004Adapter(C01S013_002Activity.this, partsNameList);
            lv_01.setAdapter(adapter);
            lv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_0111.setText(partsNameList.get(i));
                    et_01.setText(params.partsEntity.get(i).getProcessCount());
                    mPopWindow1.dismiss();
                    tv_0111_posttion = i;
                }
            });
        }
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

        if ("".equals(et_01.getText().toString().trim())) {
            createAlertDialog(C01S013_002Activity.this, "请输入加工量", Toast.LENGTH_LONG);
        } else if (0 == Integer.valueOf(et_01.getText().toString().trim())) {
            createAlertDialog(C01S013_002Activity.this, "加工量不能为0", Toast.LENGTH_LONG);
        } else {
//            loading.show();
//            ThreadSubmitSynthetic = new SubmitSyntheticThread();
//            ThreadSubmitSynthetic.start();

            //跳到确认页面
            Intent intent = new Intent(C01S013_002Activity.this, C01S013_0021Activity.class);
            intent.putExtra("tv01",tv_01.getText().toString().trim());
            intent.putExtra("tv02",tv_02.getText().toString().trim());
            intent.putExtra("tv011",tv_011.getText().toString().trim());
            intent.putExtra("tv0111",tv_0111.getText().toString().trim());//
            intent.putExtra("et01",et_01.getText().toString().trim());
            intent.putExtra(PARAM, params);
            intent.putExtra("tv_011_posttion",String.valueOf(tv_011_posttion));//零部件ID
            intent.putExtra("tv_0111_posttion",String.valueOf(tv_0111_posttion));//零部件名称
            startActivity(intent);

        }

    }

//    /**
//     * 提交数据线程
//     */
//    public class SubmitSyntheticThread extends Thread {
//        @Override
//        public void run() {
//            C01S013Respons respons = submitSynthetic();
//            if (null != respons) {
//                Message message = new Message();
//                message.obj = respons;
//                submitSyntheticHandler.sendMessage(message);
//            } else {
//                internetErrorHandler.sendEmptyMessage(0);
//            }
//        }
//    }
//
//    public SubmitSyntheticThread ThreadSubmitSynthetic;
//    Handler submitSyntheticHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            C01S013Respons respons = (C01S013Respons) msg.obj;
//            if ("0".equals(respons.getStateCode())) {
//                // 创建一个AlertDialog的构建者对象
//                AlertDialog.Builder builder = new AlertDialog.Builder(C01S013_002Activity.this);
//                builder.setTitle(R.string.infoMsg);// 设置标题
//                builder.setMessage(respons.getStateMsg());// 提示信息
//                builder.setCancelable(false);// 设置对话框不能被取消
//                // 设置正面的按钮
//                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //跳到刀具管理主界面
////                        Intent intent = new Intent(C01S013_002Activity.this, C00S000_003Activity.class);
////                        startActivity(intent);
////                        finish();
//                        SysApplication.getInstance().exit();
//                    }
//                });
//                builder.show();  // 显示对话框
//            } else {
//                createAlertDialog(null, respons.getStateMsg(), Toast.LENGTH_LONG);
//            }
//            loading.dismiss();
//        }
//    };
//    /**
//     * 处理网络异常的handler
//     */
//    @SuppressLint("HandlerLeak")
//    Handler internetErrorHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
//            loading.dismiss();
//        }
//    };
//
//    /**
//     * 提交数据方法
//     *
//     * @return
//     */
//    public C01S013Respons submitSynthetic() {
//        C01S013Wsdl service = new C01S013Wsdl();
//        C01S013Request request = new C01S013Request();
//        request.setCustomerID(customerID); //用户ID
//        request.setProcessAmount(Integer.valueOf(et_01.getText().toString().trim()));//加工量processCount
//        request.setRemoveReason(String.valueOf(tv_011_posttion));//卸下原因。传的位置
//        request.setPartsID(params.partsEntity.get(tv_0111_posttion).getPartsID());//零部件ID
//        request.setPartsName(params.partsEntity.get(tv_0111_posttion).getPartsName());//零部件名称
//        request.setSynthesisParametersCode(params.synthesisParametersCode);//合成刀具编码
//        request.setEquipmentID(params.equipmentID);//设备ID
//        request.setAxisID(params.axleID);//轴号ID
//        request.setRfidCode(params.rfidCode);//RFID标签
//        request.setHandSetId(handsetid);
//        request.setSynthesisParametersID(params.getSynthesisParametersID());//合成刀具ID
//        try {
//            respons = service.submitSyntheticUnloadEquipmentInfo(request);//提交合成刀下设备信息
//            if (null != respons) {
//                return respons;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 点击空白收起键盘
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
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


