package com.icomp.Iswtmv10.v01c01.c01s015;

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
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c04.c04s001.C04S001Wsdl;
import com.icomp.wsdl.v01c04.c04s001.endpoint.C04S001Request;
import com.icomp.wsdl.v01c04.c04s001.endpoint.C04S001Respons;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 库房盘点页面1
 * Created by FanLL on 2017/6/16.
 */

public class C01S015_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    //扫描线程
    private scanThread scanThread;
    //查询线程
    private searchThread searchThread;

    //库房盘点参数类
    private C01S015Params params = new C01S015Params();
    private C04S001Request request = new C04S001Request();
    private C04S001Respons respons = new C04S001Respons();
    private C04S001Wsdl wsdl = new C04S001Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s015_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //将输入的材料号自动转化为大写
        et01.setTransformationMethod(new AllCapTransformationMethod());
        //接受下一个页面返回时传来的材料号
        params.toolCode = getIntent().getStringExtra(PARAM1);
        //如果材料号不为空，显示在页面上
        if (null != params.toolCode) {
            et01.setText(exChangeBig(params.toolCode));
        }
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Intent intent = new Intent(this, C00S000_002Activity.class);
        startActivity(intent);
        finish();
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    @OnClick({R.id.btnScan, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //扫描按钮
            case R.id.btnScan:
                //扫描方法
                scan();
                break;
            case R.id.btnSearch:
                //防止点击扫描后点击此按钮
                stopScan();
                btnSearch.setClickable(false);
                btnScan.setClickable(false);
                params.toolCode = et01.getText().toString().trim();
                if ("".equals(params.toolCode)) {
                    createAlertDialog(C01S015_001Activity.this, getString(R.string.c01s015_001_002), Toast.LENGTH_LONG);
                    btnSearch.setClickable(true);
                    btnScan.setClickable(true);
                    isCanScan = true;
                } else {
                    loading.show();
                    //开启查询线程
                    searchThread = new searchThread();
                    searchThread.start();
                }
                break;
            default:
        }
    }

    //扫描方法
    private void scan() {
        btnScan.setClickable(false);
        //显示扫描弹框的方法
        scanPopupWindow();
        //扫描线程
        scanThread = new scanThread();
        scanThread.start();
    }

    //扫描线程
    private class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //调用单扫方法
            rfidString = singleScan();
            if ("close".equals(rfidString)) {
                Message message = new Message();
                overtimeHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
            } else if (null != rfidString && !"close".equals(rfidString)) {
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
                //输入类型（0--扫描，1--查询）
                request.setInfoType(ZERO);
                Message message = new Message();
                try {
                    //根据材料号查询刀具在库数量
                    respons = wsdl.seachInitNewVentory(request);
                    if (null != respons) {
                        message.obj = respons;
                        searchHandler.sendMessage(message);
                    } else {
                        internetErrorHandler.sendMessage(message);
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
        }
    }

    //查询线程
    private class searchThread extends Thread {
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
            //输入类型（0--扫描，1--查询）
            request.setInfoType(ONE);
            Message message = new Message();
            try {
                //根据材料号查询刀具在库数量
                respons = wsdl.seachInitNewVentory(request);
                if (null != respons) {
                    message.obj = respons;
                    searchHandler.sendMessage(message);
                } else {
                    btnScan.setClickable(true);
                    isCanScan = true;
                    btnSearch.setClickable(true);
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
                btnSearch.setClickable(true);
            }
        }
    }

    //扫描和查询的Handler
    @SuppressLint("HandlerLeak")
    private Handler searchHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            if (null != loading && loading.isShowing()) {
                loading.dismiss();
            }
            try {
                respons = (C04S001Respons) msg.obj;
                //判断接口是否调用成功（0--成功，1--失败）
                if (ZERO.equals(respons.getStateCode())) {
                    //材料号
                    params.toolCode = respons.getToolCode();
                    //刀具ID
                    params.toolID = respons.getToolID();
                    //库存数量
                    params.libraryCount = respons.getLibraryCount();
                    //刀具类型（0--非单品刀具，1--单品刀具）
                    params.toolConsumetype = respons.getToolConsumetype();
                    //跳转到库存盘点刀具信息详细页面
                    Intent intent = new Intent(C01S015_001Activity.this, C01S015_002Activity.class);
                    //向下个页面传递数据
                    intent.putExtra(PARAM, params);
                    startActivity(intent);
                    finish();
                } else {
                    createAlertDialog(C01S015_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    btnSearch.setClickable(true);
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                btnSearch.setClickable(true);
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
