package com.icomp.Iswtmv10.v01c03.c03s006;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c03.c03s000.C03S000_002Activity;
import com.icomp.Iswtmv10.v01c03.c03s001.C03S001Params;
import com.icomp.Iswtmv10.v01c03.c03s001.C03S001_001;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c03.c03s001.endpoint.SynthesisEntity;

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
 * 筒刀初始化页面1
 * Created by FanLL on 2017/9/11.
 */

public class C03S006_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.et_02)
    EditText et02;
    @BindView(R.id.btnScan)
    Button btnScan;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    //扫描线程
    private scanThread scanThread;
    //传递合成刀具组成信息
    private List<SynthesisEntity> list = new ArrayList<>();

    //合成刀具初始化参数类
    private C03S001Params params = new C03S001Params();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s006_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上一个页面返回的参数
        params.synthesisParametersCode = getIntent().getStringExtra(PARAM1);//筒刀编码
        params.tubeKnifeCode = getIntent().getStringExtra(PARAM2);//筒刀单品编码
        //将输入的材料号自动转化为大写
        et01.setTransformationMethod(new AllCapTransformationMethod());
        et02.setTransformationMethod(new AllCapTransformationMethod());
        //如果筒刀编码和单品号不为空，显示在页面上
        if (null != params.synthesisParametersCode) {
            et01.setText(exChangeBig(params.synthesisParametersCode));
        }
//        if (null != params.tubeKnifeCode) {
//            et02.setText(exChangeBig(params.tubeKnifeCode));
//        }
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
        et02.setSelection(et02.getText().length());
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //返回按钮处理--返回上一页面（刀具初始化菜单页面）
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Intent intent = new Intent(this, C03S000_002Activity.class);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.btnScan, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //扫描按钮处理
            case R.id.btnScan:
                if (!"".equals(et02.getText().toString().trim())) {
                    //扫描方法
                    scan();
                } else {
                    createAlertDialog(C03S006_001Activity.this, getString(R.string.c03s006_001_003), Toast.LENGTH_LONG);
                }
                break;
            //查询按钮处理
            case R.id.btnSearch:
                //防止点击扫描后点击此按钮
                stopScan();
                if ("".equals(et01.getText().toString().trim()) || "".equals(et02.getText().toString().trim())) {
                    createAlertDialog(C03S006_001Activity.this, getString(R.string.c03s006_001_004), Toast.LENGTH_LONG);
                } else {
                    btnSearch.setClickable(false);
                    btnScan.setClickable(false);
                    params.synthesisParametersCode = exChangeBig(et01.getText().toString().trim());//筒刀编码
                    params.tubeKnifeCode = "-" + exChangeBig(et02.getText().toString().trim());//筒刀单品编码
                    //根据材料号查询合成刀具组成信息
                    search();
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
            //单扫方法
            rfidString = singleScan();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                Message message = new Message();
                overtimeHandler.sendMessage(message);
            } else if (null != rfidString && !"close".equals(rfidString)) {
                //调用接口，查询合成刀具组成信息
                IRequest iRequest = retrofit.create(IRequest.class);
                Call<String> seachFInitSynthesisByRfid = iRequest.seachFInitSynthesisByRfid("", rfidString, "-" + exChangeBig(et02.getText().toString().trim()), ONE);
                seachFInitSynthesisByRfid.enqueue(new MyCallBack<String>() {
                    @Override
                    public void _onResponse(Response response) {
                        try {
                            String json = response.body().toString();
                            JSONObject jsonObject = new JSONObject(json);
                            Gson gson = new Gson();
                            if (jsonObject.getBoolean("success")) {
                                C03S001_001 c03s001001 = gson.fromJson(json, C03S001_001.class);
                                et01.setText(exChangeBig(c03s001001.getData().getSynthesisParametersCode()));//合成刀具编码显示在输入框上
                                params.synthesisParametersCode = c03s001001.getData().getSynthesisParametersCode();//筒刀编码
                                params.tubeKnifeCode = "-" + exChangeBig(et02.getText().toString().trim());//筒刀单品编码
                                for (int i = 0; i < c03s001001.getData().getToolList().size(); i++) {
                                    SynthesisEntity synthesisEntity = new SynthesisEntity();
                                    synthesisEntity.setToolCode(c03s001001.getData().getToolList().get(i).getToolCode());//材料号
                                    synthesisEntity.setCutterType(c03s001001.getData().getToolList().get(i).getToolConsumetype());//刀具类型
                                    synthesisEntity.setCounts(c03s001001.getData().getToolList().get(i).getToolCount());//刀具数量
                                    list.add(synthesisEntity);
                                }
                                params.setList(list);//合成刀具组成列表
                                params.createType = c03s001001.getData().getCreateType();//组成类型
                                //跳转到库存盘点刀具信息详细页面
                                Intent intent = new Intent(C03S006_001Activity.this, C03S006_002Activity.class);
                                intent.putExtra(PARAM, params);
                                startActivity(intent);
                                finish();
                            } else {
                                createAlertDialog(C03S006_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                        popupWindow.dismiss();
                        createAlertDialog(C03S006_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        btnScan.setClickable(true);
                        isCanScan = true;
                    }
                });
            }
        }
    }

    //根据材料号查询合成刀具组成信息
    private void search() {
        loading.show();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> seachFInitSynthesisByRfid = iRequest.seachFInitSynthesisByRfid(exChangeBig(params.synthesisParametersCode), "", "+" + exChangeBig(et02.getText().toString().trim()), ONE);
        seachFInitSynthesisByRfid.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                try {
                    String json = response.body();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        C03S001_001 c03s001001 = gson.fromJson(json, C03S001_001.class);
                        for (int i = 0; i < c03s001001.getData().getToolList().size(); i++) {
                            SynthesisEntity synthesisEntity = new SynthesisEntity();
                            synthesisEntity.setToolCode(c03s001001.getData().getToolList().get(i).getToolCode());//材料号
                            synthesisEntity.setCutterType(c03s001001.getData().getToolList().get(i).getToolConsumetype());//刀具类型
                            synthesisEntity.setCounts(c03s001001.getData().getToolList().get(i).getToolCount());//刀具数量
                            list.add(synthesisEntity);
                        }
                        params.setList(list);//合成刀具组成列表
                        params.createType = c03s001001.getData().getCreateType();//组成类型
                        //跳转到库存盘点刀具信息详细页面
                        Intent intent = new Intent(C03S006_001Activity.this, C03S006_002Activity.class);
                        intent.putExtra(PARAM, params);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S006_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                    btnSearch.setClickable(true);
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                createAlertDialog(C03S006_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                loading.dismiss();
                btnSearch.setClickable(true);
                btnScan.setClickable(true);
                isCanScan = true;
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
