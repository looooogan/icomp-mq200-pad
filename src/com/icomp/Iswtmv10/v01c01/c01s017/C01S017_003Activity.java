package com.icomp.Iswtmv10.v01c01.c01s017;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 回厂确认页面3
 * Created by FanLL on 2017/9/16.
 */

public class C01S017_003Activity extends CommonActivity {

    @BindView(R.id.lv_01)
    ListView lv01;

    //回厂单号
    private String outDoorNom;
    //接受上个页面传递过来的参数
    private C01S017_001 c01s017001;
    //提交回厂刀具信息的参数类列表
    private List<C01S017_002> c01s017002list = new ArrayList<>();

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s017_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上个页面传递过来的参数
        c01s017001 = (C01S017_001) getIntent().getSerializableExtra(PARAM);
        outDoorNom = getIntent().getStringExtra(PARAM1);
        //将回厂数量为0的材料号移除
        clearListView();
        //将数据放在列表上
        createData();
        lv01.setAdapter(new MyAdapter());
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        finish();
    }

    //确定按钮处理--跳转到下一页面
    public void btnConfirm(View view) {
        loading.show();
        //调用接口，提交回厂刀具信息
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> saveFBackFactoryToolInfo = iRequest.saveFBackFactoryToolInfo(new Gson().toJson(c01s017002list));
        saveFBackFactoryToolInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C01S017_003Activity.this, C01S017_004Activity.class);
                        startActivity(intent);
                        SysApplication.getInstance().exit();
                    } else {
                        createAlertDialog(C01S017_003Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                createAlertDialog(C01S017_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

//    //将回厂数量为0的材料号移除
//    private void clearListView() {
//        if (!c01s017001.isSuccess()) {
//            return;
//        } else {
//            for (int i = 0; i < c01s017001.getData().size(); i++) {
//                if("".equals(c01s017001.getData().get(i).getTrueNum()) || null == c01s017001.getData().get(i).getTrueNum() || ZERO.equals(c01s017001.getData().get(i).getTrueNum().trim())) {
//                    c01s017001.getData().remove(i);
//                }
//            }
//        }
//    }

    //将回厂数量为0的材料号移除
    private void clearListView() {
        if (!c01s017001.isSuccess()) {
            return;
        } else {
            for (int i = 0; i < c01s017001.getData().size(); i++) {
                if("".equals(c01s017001.getData().get(i).getTrueNum()) || null == c01s017001.getData().get(i).getTrueNum() || ZERO.equals(c01s017001.getData().get(i).getTrueNum().trim())) {
                    c01s017001.getData().remove(i);
                    i -= 1;
                }
            }
        }
    }

    //将数据放在列表上
    private void createData() {
        loading.show();
        if (c01s017001 != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
            //新建编辑器
            String customerID = sharedPreferences.getString("customerID", "");
            for (int i = 0; i < c01s017001.getData().size(); i++) {
                C01S017_002 c01S017002 = new C01S017_002();
                if (!"".equals(c01s017001.getData().get(i).getTrueNum()) && null != c01s017001.getData().get(i).getTrueNum()) {
                    c01S017002.setToolID(c01s017001.getData().get(i).getToolID());//刀具ID
                    c01S017002.setMaterialNum(c01s017001.getData().get(i).getMaterialNum());//材料号
                    c01S017002.setRfidContainerID(c01s017001.getData().get(i).getRfidContainerID());//RFID载体ID
                    c01S017002.setLaserCode(c01s017001.getData().get(i).getLaserCode());//激光码（单品编码）
                    c01S017002.setToolType(c01s017001.getData().get(i).getToolType());//刀具类型
                    c01S017002.setBackCount(c01s017001.getData().get(i).getTrueNum());//实际回厂数量
                    c01S017002.setOutDoorNom(outDoorNom);//出门单号
                    c01S017002.setCustomerID(customerID);//用户ID
                    c01s017002list.add(c01S017002);
                }
            }
        }
        loading.dismiss();
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return c01s017001.getData().size();
        }

        @Override
        public Object getItem(int position) {
            return c01s017001.getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view1 = LayoutInflater.from(C01S017_003Activity.this).inflate(R.layout.item_c01s017_002, null);
            view1.setFocusable(false);
            view1.setClickable(false);
            TextView tvCaiLiao = (TextView) view1.findViewById(R.id.tvCaiLiao);
            TextView tvDanPin = (TextView) view1.findViewById(R.id.tvDanPin);
            EditText tvHuiChangNumTrue = (EditText) view1.findViewById(R.id.tvHuiChangNumTrue);
            TextView tvHuiChangNum = (TextView) view1.findViewById(R.id.tvHuiChangNum);

            if(!"".equals(c01s017001.getData().get(position).getTrueNum())
                    && null != c01s017001.getData().get(position).getTrueNum()){
                tvCaiLiao.setText(c01s017001.getData().get(position).getMaterialNum());
                tvDanPin.setText(c01s017001.getData().get(position).getLaserCode());
                tvHuiChangNum.setText(c01s017001.getData().get(position).getSurplusNumber());
                tvHuiChangNumTrue.setText(c01s017001.getData().get(position).getTrueNum());
            }

            return view1;
        }

    }

}
