//package com.icomp.Iswtmv10.v01c01.c01s007;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.icomp.Iswtmv10.R;
//import com.icomp.Iswtmv10.internet.IRequest;
//import com.icomp.Iswtmv10.internet.MyCallBack;
//import com.icomp.Iswtmv10.internet.RetrofitSingle;
//import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
//import com.icomp.Iswtmv10.v01c01.c01s015.C01S015_001Activity;
//import com.icomp.Iswtmv10.v01c01.c01s015.C01S015_002Activity;
//import com.icomp.common.activity.CommonActivity;
//import com.icomp.common.utils.SysApplication;
//import com.icomp.wsdl.v01c04.c04s001.endpoint.C04S001Respons;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import retrofit2.Call;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
///**
// * 标签置换页面2
// * Created by Fanll on 2018/1/16.
// */
//
//public class C01S007_002Activity extends CommonActivity {
//
//    @BindView(R.id.btnScan)
//    Button btnScan;
//
//    //调用接口
//    private Retrofit retrofit;
//    //扫描线程
//    private scanThread scanThread;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_c01s007_002);
//        ButterKnife.bind(this);
//        //创建Activity时，添加到List进行管理
//        SysApplication.getInstance().addActivity(this);
//        //调用接口
//        retrofit = RetrofitSingle.newInstance();
//    }
//
//    //取消按钮处理--跳转到系统菜单页面
//    public void btnCancel(View view) {
//        //防止点击扫描后点击此按钮
//        stopScan();
//        Intent intent = new Intent(this, C00S000_002Activity.class);
//        startActivity(intent);
//        SysApplication.getInstance().exit();
//    }
//
//    //返回按钮处理--返回上一页面
//    public void btnReturn(View view) {
//        //防止点击扫描后点击此按钮
//        stopScan();
//        finish();
//    }
//
//    @OnClick(R.id.btnScan)
//    public void onViewClicked() {
//        //扫描方法
//        scan();
//    }
//
//    //扫描方法
//    private void scan() {
//        btnScan.setClickable(false);
//        //显示扫描弹框的方法
//        scanPopupWindow();
//        //扫描线程
//        scanThread = new scanThread();
//        scanThread.start();
//    }
//
//    //扫描线程
//    private class scanThread extends Thread {
//        @Override
//        public void run() {
//            super.run();
//            //调用单扫方法
//            rfidString = singleScan();
//            if ("close".equals(rfidString)) {
//                Message message = new Message();
//                overtimeHandler.sendMessage(message);
//                btnScan.setClickable(true);
//                isCanScan = true;
//            } else if (null != rfidString && !"close".equals(rfidString)) {
//                //调用接口，扫码查询一体刀信息，判断当前一体刀是否已经修磨
//                IRequest iRequest = retrofit.create(IRequest.class);
//                Call<String> getOneKnifeInfo = iRequest.getOneKnifeInfo(rfidString);
//                getOneKnifeInfo.enqueue(new MyCallBack<String>() {
//                    @Override
//                    public void _onResponse(Response response) {
//                        try {
//                            popupWindow.dismiss();
//                            String json = response.body().toString();
//                            JSONObject jsonObject = new JSONObject(json);
//                            Gson gson = new Gson();
//                            if (jsonObject.getBoolean("success")) {
//                                Intent intent = new Intent(C01S007_002Activity.this, C01S007_003Activity.class);
//                                startActivity(intent);
//
//                            } else {
//                                createAlertDialog(C01S007_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                                btnScan.setClickable(true);
//                                isCanScan = true;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } finally {
//                            if (popupWindow.isShowing()) {
//                                popupWindow.dismiss();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void _onFailure(Throwable t) {
//                        createAlertDialog(C01S007_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
//                        popupWindow.dismiss();
//                        btnScan.setClickable(true);
//                        isCanScan = true;
//                    }
//                });
//            }
//        }
//    }
//
//    //重写键盘上扫描按键的方法
//    @Override
//    protected void btnScan() {
//        super.btnScan();
//        if (isCanScan) {
//            isCanScan = false;
//        } else {
//            return;
//        }
//        //扫描方法
//        scan();
//    }
//}
