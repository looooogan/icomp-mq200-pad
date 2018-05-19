package com.icomp.Iswtmv10.v01c03.c03s001;

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

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 合成刀具初始化页面3
 * Created by FanLL on 2017/6/15.
 */

public class C03S001_003Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.btnStop)
    Button btnStop;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    //扫描数量
    public String scanNumber;
    //群扫存放rfidString的List
    public List<String> rfidList;
    //群扫存放是否是空标签的的List
    public List<String> flgList;
    //拼接List为String类型以","为间隔 rfidString和是否为空标签标识
    public String rfidCode, flg = null;
    //扫描线程
    private scanThread scanThread;

    //合成刀具初始化参数类
    private C03S001Params params = new C03S001Params();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s001_003);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上一页面传递的参数
        params = (C03S001Params) getIntent().getSerializableExtra(PARAM);
        //显示扫描数量
        scanNumber = ZERO;
        tv01.setText(getResources().getString(R.string.c03s001_003_002) + scanNumber);
        //扫描按钮没有点击之前，设置停止和下一步按钮不可用
        btnStop.setClickable(false);
        btnSubmit.setClickable(false);
    }

    //返回按钮处理--返回到合成刀具初始化页面1
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Message message = new Message();
        returnHandler.sendMessage(message);
    }

    //提交按钮处理--调用接口，提交初始化合成刀具RFIDCodeList
    public void btnSubmit(View view) {
        //防止点击扫描后点击此按钮
        scanOrStop = false;
        close();
        btnScan.setClickable(false);
        btnScan.setBackgroundResource(R.color.hintcolor);
        btnStop.setClickable(false);
        btnStop.setBackgroundResource(R.color.hintcolor);
        btnSubmit.setClickable(false);
        if (0 == Integer.parseInt(scanNumber)) {
            createAlertDialog(this, getString(R.string.c03s001_003_006), Toast.LENGTH_LONG);
            btnScan.setClickable(true);
            isCanScan = true;
            btnScan.setBackgroundResource(R.drawable.border);
            btnStop.setClickable(false);
            btnStop.setBackgroundResource(R.drawable.border);
        } else {
            //点击提交按钮处理方法
            next();
        }
    }

    //返回Handler
    @SuppressLint("HandlerLeak")
    Handler returnHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            new AlertDialog.Builder(C03S001_003Activity.this).
                    setTitle(R.string.prompt).
                    setMessage(R.string.c03s001_003_003).
                    setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(C03S001_003Activity.this, C03S001_001Activity.class);
                            intent.putExtra(PARAM1, params.synthesisParametersCode);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    btnScan.setClickable(true);
                    isCanScan = true;
                    btnScan.setBackgroundResource(R.drawable.border);
                    btnStop.setClickable(false);
                    btnStop.setBackgroundResource(R.color.hintcolor);
                    btnSubmit.setClickable(true);
                }
            }).show();
        }
    };

    @OnClick({R.id.btnScan, R.id.btnStop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //扫描按钮处理
            case R.id.btnScan:
                //扫描方法
                scan();
                break;
            //停止按钮处理
            case R.id.btnStop:
                //防止点击扫描后点击此按钮
                scanOrStop = false;
                close();
                btnScan.setClickable(true);
                isCanScan = true;
                btnScan.setBackgroundResource(R.drawable.border);
                btnStop.setClickable(false);
                btnStop.setBackgroundResource(R.color.hintcolor);
                btnSubmit.setClickable(true);
                break;
            default:
        }
    }

    //扫描方法
    private void scan() {
        btnScan.setClickable(false);
        btnScan.setBackgroundResource(R.color.hintcolor);
        btnStop.setClickable(true);
        btnStop.setBackgroundResource(R.drawable.border);
        btnSubmit.setClickable(false);
        //设置扫描或停止条件为true
        scanOrStop = true;
        //打开读头
        initRFID();
        //启动扫描线程
        scanThread = new scanThread();
        scanThread.start();
    }

    //扫描线程
    private class scanThread extends Thread{
        @Override
        public void run() {
            super.run();
            //需每次置rfidString为null
            rfidString = null;
            while (null == rfidString && scanOrStop) {
                rfidString = readRfidString(scanOrStop);
            }
            if (null != rfidString) {




                Message message = new Message();
                message.obj = rfidString;
                scanHandler.sendMessage(message);
            }
        }
    }

    //扫描Handler
    @SuppressLint("HandlerLeak")
    Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            rfidString = msg.obj.toString();
            if (null == rfidList) {
                rfidList = new ArrayList<>();
            }
            if (null == flgList) {
                flgList = new ArrayList<>();
            }
            if (!rfidList.contains(rfidString)) {
                //调用接口，验证要初始化的合成刀具标签
                IRequest iRequest = retrofit.create(IRequest.class);
                Call<String> checkInitSynthesis = iRequest.checkInitSynthesis(rfidString, exChangeBig(params.synthesisParametersCode));
                checkInitSynthesis.enqueue(new MyCallBack<String>() {
                    @Override
                    public void _onResponse(Response response) {
                        try {
                            String json = response.body().toString();
                            JSONObject jsonObject = new JSONObject(json);
                            Gson gson = new Gson();
                            if (jsonObject.getBoolean("success")) {
                                C03S001_002 c03s001002 = gson.fromJson(json, C03S001_002.class);
                                if ("".equals(c03s001002.getMessage())) {
                                    //将rfidString放入params.rfidList列表中
                                    rfidList.add(rfidString);
                                    flgList.add(ZERO);
                                    //扫描数量params.scanNumber+1
                                    scanNumber = String.valueOf(Integer.parseInt(scanNumber) + 1);
                                    //显示当前初始化数量
                                    tv01.setText(getResources().getString(R.string.c03s001_003_002) + scanNumber);
                                    //重新启动扫描线程
                                    scanThread = new scanThread();
                                    scanThread.start();
                                } else {
                                    new AlertDialog.Builder(C03S001_003Activity.this).
                                    setTitle(R.string.prompt).
                                    setMessage(c03s001002.getMessage()).
                                    setCancelable(false).
                                    setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //将rfidString放入params.rfidList列表中
                                            rfidList.add(rfidString);
                                            flgList.add(ONE);
                                            //扫描数量params.scanNumber+1
                                            scanNumber = String.valueOf(Integer.parseInt(scanNumber) + 1);
                                            //显示当前初始化数量
                                            tv01.setText(getResources().getString(R.string.c03s001_003_002) + scanNumber);
                                            //重新启动扫描线程
                                            scanThread = new scanThread();
                                            scanThread.start();
                                        }
                                    }).
                                    setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            //重新启动扫描线程
                                            scanThread = new scanThread();
                                            scanThread.start();
                                        }
                                    }).show();
                                }
                            } else {
                                new AlertDialog.Builder(C03S001_003Activity.this).
                                        setTitle(R.string.prompt).
                                        setMessage(jsonObject.getString("message")).
                                        setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                //重新启动扫描线程
                                                scanThread = new scanThread();
                                                scanThread.start();
                                            }
                                        }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnScan.setClickable(true);
                            isCanScan = true;
                            btnScan.setBackgroundResource(R.drawable.border);
                            btnStop.setClickable(false);
                            btnStop.setBackgroundResource(R.color.hintcolor);
                            btnSubmit.setClickable(false);
                        }
                    }

                    @Override
                    public void _onFailure(Throwable t) {
                        createAlertDialog(C03S001_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        btnScan.setClickable(true);
                        isCanScan = true;
                        btnScan.setBackgroundResource(R.drawable.border);
                        btnStop.setClickable(false);
                        btnStop.setBackgroundResource(R.color.hintcolor);
                        btnSubmit.setClickable(false);
                    }
                });
            } else {
                //重新启动扫描线程
                scanThread = new scanThread();
                scanThread.start();
            }
        }
    };

    //点击提交按钮处理方法
    private void next() {
        loading.show();
        //设定用户访问信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        //用户ID
        String customerID = sharedPreferences.getString("customerID", "");
        //手持机ID
        String handSetId = sharedPreferences.getString("handsetid", "");
        //拼接List为String类型以","为间隔
        for (int i = 0; i < rfidList.size(); i++) {
            if (i == 0) {
                rfidCode = rfidList.get(i);
                flg = flgList.get(i);
            } else if (i == rfidList.size()) {
                rfidCode = rfidCode + rfidList.get(i);
                flg = flg + rfidList.get(i);
            } else {
                rfidCode = rfidCode + "," + rfidList.get(i);
                flg = flg + "," + rfidList.get(i);
            }
        }
        //调用接口，提交初始化合成刀具RFIDCodeList
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> submitFInitSynthesis = iRequest.submitFInitSynthesis(rfidCode, exChangeBig(params.synthesisParametersCode), params.createType, customerID, handSetId, flg, ZERO, "");
        submitFInitSynthesis.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C03S001_003Activity.this, C03S001_004Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S001_003Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    btnScan.setClickable(true);
                    isCanScan = true;
                    btnScan.setBackgroundResource(R.drawable.border);
                    btnStop.setClickable(false);
                    btnStop.setBackgroundResource(R.color.hintcolor);
                    btnSubmit.setClickable(true);
                } finally {
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C03S001_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnScan.setClickable(true);
                isCanScan = true;
                btnScan.setBackgroundResource(R.drawable.border);
                btnStop.setClickable(false);
                btnStop.setBackgroundResource(R.color.hintcolor);
                btnSubmit.setClickable(false);
            }
        });
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
