package com.icomp.Iswtmv10.v01c03.c03s002;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.icomp.Iswtmv10.v01c03.c03s001.C03S001_002;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 库位标签初始化页面2
 * Created by FanLL on 2017/6/28.
 */

public class C03S002_002Activity extends CommonActivity {

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

    //按材料号查询库存信息参数类
    private C03S002_001.DataBean c03s002001DataBean = new C03S002_001.DataBean();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s002_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上一个页面传递的数值
        c03s002001DataBean = (C03S002_001.DataBean) getIntent().getSerializableExtra(PARAM);
        //材料号
        tv01.setText(c03s002001DataBean.getToolCode());
        //刀具类型（0--可刃磨钻头，1--可刃磨刀片，2--一次性刀具，9--其他）
        tv02.setText(c03s002001DataBean.getToolConsumeType());
        //库位码
        tv03.setText(c03s002001DataBean.getLibraryCodeID());
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
        //点击返回时将员工卡号传递到上一个页面
        Intent intent = new Intent(this, C03S002_001Activity.class);
        intent.putExtra(PARAM1, c03s002001DataBean.getToolCode());
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
            rfidString = singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                overtimeHandler.sendMessage(message);
            } else if(null != rfidString && !"close".equals(rfidString)) {
                c03s002001DataBean.setRfidString(rfidString);
                //调用接口，查询当前标签是否已初始化(库位标签)
                IRequest iRequest = retrofit.create(IRequest.class);
                Call<String> findLibraryInitializeMsg = iRequest.findLibraryInitializeMsg(c03s002001DataBean.getToolCode(), rfidString);
                findLibraryInitializeMsg.enqueue(new MyCallBack<String>() {
                    @Override
                    public void _onResponse(Response response) {
                        try {
                            String json = response.body().toString();
                            JSONObject jsonObject = new JSONObject(json);
                            Gson gson = new Gson();
                            if (jsonObject.getBoolean("success")) {
                                C03S001_002 c03s001002 = gson.fromJson(json, C03S001_002.class);
                                if ("".equals(c03s001002.getMessage())) {
                                    popupWindow.dismiss();
                                    Intent intent = new Intent(C03S002_002Activity.this, C03S002_003Activity.class);
                                    intent.putExtra(PARAM, c03s002001DataBean);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    //当前材料号已经初始化，是否重新初始化
                                    new AlertDialog.Builder(C03S002_002Activity.this).
                                            setTitle(R.string.prompt).
                                            setMessage(c03s001002.getMessage()).
                                            setCancelable(false).
                                            setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                    @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    Intent intent = new Intent(C03S002_002Activity.this, C03S002_003Activity.class);
                                                    intent.putExtra(PARAM, c03s002001DataBean);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }).
                                            setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                            } else {
                                createAlertDialog(C03S002_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            popupWindow.dismiss();
                            btnScan.setClickable(true);
                            isCanScan = true;
                        }
                    }

                    @Override
                    public void _onFailure(Throwable t) {
                        createAlertDialog(C03S002_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        popupWindow.dismiss();
                        btnScan.setClickable(true);
                        isCanScan = true;
                    }
                });
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
