package com.icomp.Iswtmv10.v01c01.c01s009;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c01.c01s008.c01s008_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Request;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2017/11/18.
 */

public class C01S009_013Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;

    private Retrofit mRetrofit;

    C01S008Request params = new C01S008Request();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s009_013);
        ButterKnife.bind(this);
        //调用接口
        mRetrofit = RetrofitSingle.newInstance();
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传递的数值
        params = (C01S008Request) getIntent().getSerializableExtra(PARAM);
        et01.setText(params.getSynthesisParametersCodes());
        et01.setSelection(params.getSynthesisParametersCodes().length());
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        Intent intent = new Intent(C01S009_013Activity.this,C01S009_012Activity.class);
        startActivity(intent);
        finish();
    }

    //确定按钮处理--提交数据
    public void btnConfirm(View view) {
        //判断是否组合成新的合成刀具
        if (et01.getText().toString().trim().equals(params.getSynthesisParametersCodes())) {
            //刀具组装-提交组装信息
            requestDataForSave(params);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(C01S009_013Activity.this);
            builder.setTitle(R.string.prompt);
            builder.setMessage("是否将" + params.getSynthesisParametersCodes() + "组装成" + et01.getText().toString().trim());
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    requestNewDataForSave(params.getRfidString(), et01.getText().toString().trim());
                }
            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int a) {
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    //组装成新刀
    private void requestNewDataForSave(String rfidString, String toolSynthesisParametersCodes) {
        loading.show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveSynthesisToolInstallNewTool = iRequest.saveSynthesisToolInstall(rfidString, toolSynthesisParametersCodes, customerID);
        saveSynthesisToolInstallNewTool.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(C01S009_013Activity.this,c01s008_002Activity.class);
                        intent.putExtra("TAG","2");//刀具拆分
                        intent.putExtra("json",et01.getText().toString().trim());
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S009_013Activity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S009_013Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);

            }
        });
    }

    //刀具组装-提交组装信息
    private void requestDataForSave(final C01S008Request request) {
        loading.show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveSynthesisToolInstall = iRequest.saveSynthesisToolInstall(request.getSynthesisParametersCodes(),
                request.getRfidContainerIDs(),
                request.getToolConsumetypes(),
                customerID,
                "");
        saveSynthesisToolInstall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(C01S009_013Activity.this,c01s008_002Activity.class);
                        intent.putExtra("TAG","2");//刀具拆分
                        intent.putExtra("json",request.getSynthesisParametersCodes());
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S009_013Activity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S009_013Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);

            }
        });
    }

}
