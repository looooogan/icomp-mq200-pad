package com.icomp.Iswtmv10.v01c01.c01s017;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 回厂确认页面2
 * Created by FanLL on 2017/9/15.
 */

public class C01S017_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.lv_01)
    ListView lv01;

    //回厂单号
    private String outDoorNom;
    //取得回厂刀具信息的参数类
    C01S017_001 c01s017001 = new C01S017_001();

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s017_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //接受上一个页面返回的参数，订单号
        outDoorNom = getIntent().getStringExtra(PARAM);
        tv01.setText(outDoorNom.trim());
        //调用接口，根据单号查厂外修复信息
        getOrderMessage();
    }

    //返回按钮处理--跳转到上一页面
    public void btnReturn(View view) {
        returnby();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        //若所有材料回厂数量都为零，给予相应提示，不允许跳转到下一页面
        if (!c01s017001.isSuccess()){
            return;
        }
        if (isEmpty()) {
            createAlertDialog(this, getString(R.string.c01s017_001_003), Toast.LENGTH_LONG);
        } else if (isBigger()) {
            createAlertDialog(this, getString(R.string.c01s017_001_005), Toast.LENGTH_LONG);
        } else {
            Intent intent = new Intent(this, C01S017_003Activity.class);
            intent.putExtra(PARAM, c01s017001);
            intent.putExtra(PARAM1, outDoorNom);
            startActivity(intent);
        }
    }

    //封装点击返回按钮的方法
    private void returnby() {
        Intent intent = new Intent(this, C01S017_001Activity.class);
        startActivity(intent);
        finish();
    }

    //调用接口，根据单号查厂外修复信息
    private void getOrderMessage() {
        loading.show();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getOutFactoryDescByOutDoorNom = iRequest.getOutFactoryDescByOutDoorNom(outDoorNom);
        getOutFactoryDescByOutDoorNom.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                try {
                    String json = response.body();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        c01s017001 = gson.fromJson(json, C01S017_001.class);
                        //将数据显示在列表上
                        lv01.setAdapter(new MyAdapter());
                    } else {
                        createAlertDialog(C01S017_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                createAlertDialog(C01S017_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    //判断是否所有材料回厂数量都为零或为空
    private boolean isEmpty() {

        for (int i = 0; i < c01s017001.getData().size(); i++) {
            if (null != c01s017001.getData().get(i).getTrueNum() && !("").equals(c01s017001.getData().get(i).getTrueNum()) && !ZERO.equals(c01s017001.getData().get(i).getTrueNum())) {
                return false;
            }
        }
        return true;
    }

    //判断实际回厂数量是否大于可回厂数量
    private boolean isBigger() {
        for (int i = 0; i < c01s017001.getData().size(); i++) {
            if (null != c01s017001.getData().get(i).getTrueNum() && !("").equals(c01s017001.getData().get(i).getTrueNum())) {
                if(Integer.parseInt(c01s017001.getData().get(i).getTrueNum().trim()) > Integer.parseInt(c01s017001.getData().get(i).getSurplusNumber().trim())){
                    return true;
                }
            }
        }
        return false;
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
            View view1 = LayoutInflater.from(C01S017_002Activity.this).inflate(R.layout.item_c01s017_002, null);
            TextView tvCaiLiao = (TextView) view1.findViewById(R.id.tvCaiLiao);
            TextView tvDanPin = (TextView) view1.findViewById(R.id.tvDanPin);
            EditText tvHuiChangNumTrue = (EditText) view1.findViewById(R.id.tvHuiChangNumTrue);
            TextView tvHuiChangNum = (TextView) view1.findViewById(R.id.tvHuiChangNum);
            tvCaiLiao.setText(c01s017001.getData().get(position).getMaterialNum());
            tvDanPin.setText(c01s017001.getData().get(position).getLaserCode());
            tvHuiChangNum.setText(c01s017001.getData().get(position).getSurplusNumber());
            if (null != c01s017001.getData().get(position).getTrueNum()){
                tvHuiChangNumTrue.setText(c01s017001.getData().get(position).getTrueNum());
            }

            tvHuiChangNumTrue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    c01s017001.getData().get(position).setTrueNum(s.toString());
                }
            });

            return view1;
        }

    }

    @Override
    protected void btnReturn() {
        returnby();
    }

}
