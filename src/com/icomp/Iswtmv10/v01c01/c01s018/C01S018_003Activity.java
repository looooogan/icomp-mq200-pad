package com.icomp.Iswtmv10.v01c01.c01s018;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 厂内修磨页面3
 * Created by FanLL on 2017/7/10.
 */

public class C01S018_003Activity extends CommonActivity {

    @BindView(R.id.ll_01)
    LinearLayout ll01;
    
    //接受参数的JsonString
    private String jsonString;
    //接受上一个页面传递的List结果集
    private List<C01S018Params> list = new ArrayList<>();
    //传递给后台的材料号拼接的字符串，传递给后台的修磨数量拼接的字符串
    private String toolCodes, numbers;

    //厂外修磨参数类
    private C01S018Params params = new C01S018Params();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s018_003);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        try {
            jsonString = getIntent().getStringExtra(PARAM);
            JSONArray jsonArray = new JSONArray(jsonString);
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
                params = gson.fromJson(jsonArray.getJSONObject(i).toString().trim(), C01S018Params.class);
                //将相同的材料号的条目捏合在一起
                if (0 == list.size()) {
                    list.add(params);
                } else {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getMaterialNumber().equals(params.getMaterialNumber())) {
                            Integer grindingQuantity = Integer.parseInt(params.getGrindingQuantity()) + Integer.parseInt(list.get(j).getGrindingQuantity());
                            list.get(j).setGrindingQuantity(grindingQuantity.toString());
                        } else {
                            if (j == list.size() - 1) {
                                list.add(params);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将接受的数据显示在列表上
        for (int i = 0; i < list.size(); i++) {
            final View linearLayout = LayoutInflater.from(this).inflate(R.layout.item_c01s019_002, null);
            TextView tvmaterialNumber = (TextView) linearLayout.findViewById(R.id.tvmaterialNumber);
            tvmaterialNumber.setText(list.get(i).getMaterialNumber());
            TextView tvgrindingQuantity = (TextView) linearLayout.findViewById(R.id.tvgrindingQuantity);
            tvgrindingQuantity.setText(list.get(i).getGrindingQuantity());
            ll01.addView(linearLayout);
            //拼接材料号和修磨数量为String类型以","为间隔
            if (i == 0) {
                toolCodes = tvmaterialNumber.getText().toString().trim();
                numbers = tvgrindingQuantity.getText().toString().trim();
            } else if (i == list.size()) {
                toolCodes = toolCodes + tvmaterialNumber.getText().toString().trim();
                numbers = numbers + tvgrindingQuantity.getText().toString().trim();
            } else {
                toolCodes = toolCodes + "," + tvmaterialNumber.getText().toString().trim();
                numbers = numbers + "," + tvgrindingQuantity.getText().toString().trim();
            }
        }
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        finish();
    }

    //确定按钮处理--跳转到下一页面
    public void btnConfirm(View view) {
        loading.show();
        //设定用户访问信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        //用户ID
        String customerID = sharedPreferences.getString("customerID", "");
        //调用接口，查询输入的材料号是否可以厂内修磨，数量是否在厂内修磨数量范围内
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> saveGrindingToolInfo = iRequest.saveGrindingToolInfo(toolCodes, numbers, customerID);
        saveGrindingToolInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C01S018_003Activity.this, C01S018_004Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C01S018_003Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                createAlertDialog(C01S018_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

}
