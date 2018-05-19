package com.icomp.Iswtmv10.v01c03.c03s002;

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
import com.icomp.Iswtmv10.v01c03.c03s000.C03S000_002Activity;
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
 * 库位标签初始化页面1
 * Created by FanLL on 2017/6/28.
 */

public class C03S002_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    //调用接口
    private Retrofit retrofit;
    //按材料号查询库存信息参数类
    private C03S002_001.DataBean c03s002001DataBean = new C03S002_001.DataBean();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s002_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //将输入的材料号自动转化为大写
        et01.setTransformationMethod(new AllCapTransformationMethod());
        //接受上一个页面返回的参数
        c03s002001DataBean.setToolCode(getIntent().getStringExtra(PARAM1));
        //如果材料号不为空，显示在页面上
        if (null != c03s002001DataBean.getToolCode()) {
            et01.setText(exChangeBig(c03s002001DataBean.getToolCode().trim()));
        }
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        finish();
    }

    //返回按钮处理--返回上一页面（刀具初始化菜单页面）
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C03S000_002Activity.class);
        startActivity(intent);
        finish();
    }

    //查询按钮处理
    @OnClick(R.id.btnSearch)
    public void onViewClicked() {
        btnSearch.setClickable(false);
        c03s002001DataBean.setToolCode(et01.getText().toString().trim());
        if ("".equals(c03s002001DataBean.getToolCode())) {
            createAlertDialog(C03S002_001Activity.this, getString(R.string.c01s015_001_002), Toast.LENGTH_LONG);
            btnSearch.setClickable(true);
        } else {
            //查询方法
            search();
        }
    }

    //查询方法
    private void search() {
        loading.show();
        //调用接口，按材料号查询库存信息
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> findLibraryCodeMsg = iRequest.findLibraryCodeMsg(et01.getText().toString().trim());
        findLibraryCodeMsg.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        C03S002_001 c03s002001 = gson.fromJson(json, C03S002_001.class);
                        c03s002001DataBean.setToolCode(c03s002001.getData().getToolCode());//材料号
                        c03s002001DataBean.setToolConsumeType(c03s002001.getData().getToolConsumeType());//刀具类型（0--可刃磨钻头，1--可刃磨刀片，2--一次性刀具，9--其他）
                        c03s002001DataBean.setLibraryCodeID(c03s002001.getData().getLibraryCodeID());//库位码
                        Intent intent = new Intent(C03S002_001Activity.this, C03S002_002Activity.class);
                        intent.putExtra(PARAM, c03s002001DataBean);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S002_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                    btnSearch.setClickable(true);
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C03S002_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnSearch.setClickable(true);
            }
        });
    }

}
