//package com.icomp.Iswtmv10.v01c01.c01s007;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.icomp.Iswtmv10.R;
//import com.icomp.Iswtmv10.internet.IRequest;
//import com.icomp.Iswtmv10.internet.MyCallBack;
//import com.icomp.Iswtmv10.internet.RetrofitSingle;
//import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
//import com.icomp.common.activity.CommonActivity;
//import com.icomp.common.utils.SysApplication;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import retrofit2.Call;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
///**
// * 标签置换页面1
// * Created by Fanll on 2018/1/16.
// */
//
//public class C01S007_001Activity extends CommonActivity {
//
//    @BindView(R.id.et_01)
//    EditText et01;
//
//    //调用接口
//    private Retrofit retrofit;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_c01s007_001);
//        ButterKnife.bind(this);
//        //创建Activity时，添加到List进行管理
//        SysApplication.getInstance().addActivity(this);
//        //调用接口
//        retrofit = RetrofitSingle.newInstance();
//        //将输入的材料号自动转化为大写
//        et01.setTransformationMethod(new CommonActivity.AllCapTransformationMethod());
//        //接受下一个页面返回时传来的材料号
//        params.toolCode = getIntent().getStringExtra(PARAM1);
//        //如果材料号不为空，显示在页面上
//        if (null != params.toolCode) {
//            et01.setText(exChangeBig(params.toolCode));
//        }
//        //将光标设置在最后
//        et01.setSelection(et01.getText().length());
//    }
//
//    //返回按钮处理--返回上一页面
//    public void btnReturn(View view) {
//        //防止点击扫描后点击此按钮
//        finish();
//    }
//
//    //取消按钮处理--跳转到系统菜单页面
//    public void btnCancel(View view) {
//        Intent intent = new Intent(this, C00S000_002Activity.class);
//        startActivity(intent);
//        SysApplication.getInstance().exit();
//    }
//
//    //置换按钮处理
//    public void substitution(View view) {
//        loading.show();
//        IRequest iRequest = retrofit.create(IRequest.class);
//        Call<String> getOutFactoryOutDoorNomList = iRequest.getOutFactoryOutDoorNomList();
//        getOutFactoryOutDoorNomList.enqueue(new MyCallBack<String>() {
//            @Override
//            public void _onResponse(Response<String> response) {
//                try {
//                    String json = response.body();
//                    JSONObject jsonObject = new JSONObject(json);
//                    if (jsonObject.getBoolean("success")) {
//                        Intent intent = new Intent(C01S007_001Activity.this, C01S007_002Activity.class);
//                        intent.putExtra(PARAM, et01.getText().toString().trim());
//                        startActivity(intent);
//                    } else {
//                        createAlertDialog(C01S007_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } finally {
//                    loading.dismiss();
//                }dismiss
//            }
//
//            @Override
//            public void _onFailure(Throwable t) {
//                loading.dismiss();
//                createAlertDialog(C01S007_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
//            }
//        });
//    }
//
//}
