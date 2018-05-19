package com.icomp.Iswtmv10.v01c01.c01s023;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 单品绑定页面1
 * Created by FanLL on 2017/9/14.
 */

public class C01S023_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.et_02)
    EditText et02;
//    @BindView(R.id.et_03)
//    EditText et03;
//    @BindView(R.id.btnGenerate)
//    Button btnGenerate;

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s023_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        et01.setTransformationMethod(new AllCapTransformationMethod());
        et02.setTransformationMethod(new AllCapTransformationMethod());
//        et03.setTransformationMethod(new AllCapTransformationMethod());
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
        et02.setSelection(et02.getText().length());
//        et03.setSelection(et03.getText().length());
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        finish();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        if ("".equals(et01.getText().toString().trim()) || "".equals(et02.getText().toString().trim())) {
            createAlertDialog(C01S023_001Activity.this, getString(R.string.c01s023_001_004), Toast.LENGTH_LONG);
        } else {
            Intent intent = new Intent(this, C01S023_002Activity.class);
            intent.putExtra(PARAM, exChangeBig(et01.getText().toString().trim()));
            intent.putExtra(PARAM1, exChangeBig(et02.getText().toString().trim()));
            startActivity(intent);
        }
    }

//    @OnClick(R.id.btnGenerate)
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.btnGenerate:
//                //点击生成激光码按钮的方法
//                generate();
//                break;
//            default:
//        }
//    }

    //点击生成激光码按钮的方法
    private void generate() {
        if (et01.getText().toString().trim().equals("") || et02.getText().toString().trim().equals("")) {
            createAlertDialog(C01S023_001Activity.this, getString(R.string.c01s023_001_003), Toast.LENGTH_LONG);
        } else {
//            et03.setText("");
            //调用接口，根据材料号查询激光码信息，并生成激光码
            IRequest iRequest = retrofit.create(IRequest.class);
            Call<String> getOneKnifeBinding = iRequest.getOneKnifeBinding(exChangeBig(et01.getText().toString().trim()), exChangeBig(et02.getText().toString().trim()));
            getOneKnifeBinding.enqueue(new MyCallBack<String>() {
                @Override
                public void _onResponse(Response<String> response) {
                    try {
                        String json = response.body();
                        JSONObject jsonObject = new JSONObject(json);
                        Gson gson = new Gson();
                        if (jsonObject.getBoolean("success")) {
                            C01S023_001 c01s023001 = gson.fromJson(json, C01S023_001.class);
//                            et03.setText(c01s023001.getData().getCount().trim());
                        } else {
                            createAlertDialog(C01S023_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        loading.dismiss();
                    }
                }

                @Override
                public void _onFailure(Throwable t) {
                    loading.dismiss();
                    createAlertDialog(C01S023_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                }
            });
        }
    }

}
