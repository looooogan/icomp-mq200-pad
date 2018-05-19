package com.icomp.Iswtmv10.v01c03.c03s002;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
 * 库位标签初始化页面3
 * Created by FanLL on 2017/6/28.
 */

public class C03S002_003Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.btnSubmit)
    Button btnSubmit;

    //按材料号查询库存信息参数类
    private C03S002_001.DataBean c03s002001DataBean = new C03S002_001.DataBean();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s002_003);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上一个页面传递的参数
        c03s002001DataBean = (C03S002_001.DataBean) getIntent().getSerializableExtra(PARAM);
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        finish();
    }

    //返回按钮处理--返回库位标签初始化第一个页面
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C03S002_001Activity.class);
        //点击返回时将员工卡号传递回库位标签初始化第一个页面
        intent.putExtra(PARAM1, c03s002001DataBean.getToolCode());
        startActivity(intent);
        finish();
    }

    //提交按钮处理
    @OnClick(R.id.btnSubmit)
    public void onViewClicked() {
        btnSubmit.setClickable(false);
        if ("".equals(et01.getText().toString().trim())) {//初始化数量
            createAlertDialog(C03S002_003Activity.this, getString(R.string.c03s002_003_003), Toast.LENGTH_LONG);
            btnSubmit.setClickable(true);
        } else {
            //提交处理方法
            submit(et01.getText().toString().trim());
        }
    }

    //提交处理方法
    private void submit(String toolCount) {
        loading.show();
        //设定用户访问信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        //用户ID
        String customerID = sharedPreferences.getString("customerID", "");
        //调用接口，提交库位标签初始化数据
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> submitLibraryCodeMsg = iRequest.submitLibraryCodeMsg(c03s002001DataBean.getToolCode(), c03s002001DataBean.getRfidString(), customerID, toolCount);
        submitLibraryCodeMsg.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C03S002_003Activity.this, C03S002_004Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S002_003Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                    btnSubmit.setClickable(true);
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C03S002_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnSubmit.setClickable(true);
            }
        });
    }

}
