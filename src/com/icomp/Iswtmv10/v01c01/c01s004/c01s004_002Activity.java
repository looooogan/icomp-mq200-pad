package com.icomp.Iswtmv10.v01c01.c01s004;
/**
 * 补领出库详细画面
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c01.c01s003.ChuKuModul;
import com.icomp.Iswtmv10.v01c01.c01s004.modul.BuLingRequestModul;
import com.icomp.Iswtmv10.v01c01.c01s004.modul.BuLingResponseModul;
import com.icomp.common.activity.CommonActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class c01s004_002Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    @BindView(R.id.tvShenqingRen)
    TextView mTvShenqingRen;
    private String name;
    private List<BuLingRequestModul> beanList = new ArrayList<>(); //最后遍历传值的集合
    private List<BuLingTableModul> table = new ArrayList<>(); //最后遍历传值的集合
    private Retrofit retrofit;   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s004_002);
        ButterKnife.bind(this);
        initRetrofit();
        initView();

    }

    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }

    private void requestData(BuLingModul b) {
        loading.show();
        //网络请求获取数据
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getFReplacementApply = iRequest.getFReplacementApply(b.getId(),b.getTime(),b.getReplacementFlag());
        getFReplacementApply.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        BuLingResponseModul b = new Gson().fromJson(json,BuLingResponseModul.class);
                        initLayout(b);
                    }else{
                        createAlertDialog(c01s004_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s004_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    private void initLayout(BuLingResponseModul b) {
        for (int i = 0;i<b.getData().size();i++){
            addLayout(b.getData().get(i));
        }
    }


    private void initView() {
        BuLingModul b;
        b = (BuLingModul) getIntent().getSerializableExtra("name");
        if(b == null){
            return;
        }
        mTvShenqingRen.setText("申请人："+b.getName());
        name = b.getName();
        requestData(b);
    }
    //页面按钮的点击事件
    @OnClick({R.id.btnCancel, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                String jsonData = bianliAndToJson();
                //当jsonData为null或“【】”时说明数据有问题，终止方法
                if(jsonData == null || "[]".equals(jsonData)){
                    return;
                }
                Intent intent = new Intent(this,c01s004_003Activity.class);
                intent.putExtra("json",jsonData);
                intent.putExtra("name",name);
                intent.putExtra("table", (Serializable) table);
                startActivity(intent);
                break;
            default:
        }
    }

    /**
     * 添加布局
     */
    private void addLayout(BuLingResponseModul.DataBean b) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_bulingchuku, null);
        ChuKuModul c = new ChuKuModul();
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvHuoWei = (TextView) mLinearLayout.findViewById(R.id.tvHuoWei);
        TextView tvKucun = (TextView) mLinearLayout.findViewById(R.id.tvKuCun);
        TextView tvKeLing = (TextView) mLinearLayout.findViewById(R.id.tvKeLingb);
        TextView tvToolid = (TextView) mLinearLayout.findViewById(R.id.tvToolId);
        TextView tvToolType = (TextView) mLinearLayout.findViewById(R.id.tvtoolType);
        TextView tvreplacementID = (TextView) mLinearLayout.findViewById(R.id.tvreplacementID);
        TextView tvrfidCode = (TextView) mLinearLayout.findViewById(R.id.tvrfidCode);
        EditText tvChukushuliang = (EditText) mLinearLayout.findViewById(R.id.etChukushuliang);
        tvCailiao.setText(exChangeBig(b.getToolCode()));
        tvHuoWei.setText(b.getLibraryCodeID());
        tvKucun.setText(b.getKnifelnventoryNumber());
        tvKeLing.setText(String.valueOf(b.getAppliedNumber()));
        tvToolid.setText(b.getToolID());
        tvToolType.setText(b.getToolType());
        tvreplacementID.setText(b.getReplacementID());
        tvrfidCode.setText(b.getRfidCode());
        mLlContainer.addView(mLinearLayout);
    }

    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        table.clear();
        beanList.clear();
        if(mLlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    BuLingRequestModul c1 = new BuLingRequestModul();
                    BuLingTableModul c = new BuLingTableModul();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof EditText){
                            int chuku;
                            if("".equals(((EditText) child2).getText().toString())){
//                                createAlertDialog(c01s004_002Activity.this,"请填写数量",Toast.LENGTH_SHORT);
//                                return null;
//                                ((EditText) child2).setText("0");
                                chuku = 0;
                                c.setChuKu(String.valueOf(chuku));
                                c1.setReceiveCount(String.valueOf(chuku));
//                            c.setChuKu(String.valueOf(chuku));
//                            c1.setReceiveCount(String.valueOf(chuku));
                            } else {
                                chuku = Integer.parseInt(((EditText) child2).getText().toString());
                                c.setChuKu(String.valueOf(chuku));
                                c1.setReceiveCount(String.valueOf(chuku));
                            }

//                            chuku = Integer.parseInt(((EditText) child2).getText().toString());
//                            c.setChuKu(String.valueOf(chuku));
//                            c1.setReceiveCount(String.valueOf(chuku));
                        }else if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvHuoWei:
                                    c.setHuoWei(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvCailiao:
                                    c1.setToolCode(((TextView)child2).getText().toString());break;
                                case R.id.tvKuCun:
                                    c.setKuCun(((TextView)child2).getText().toString());break;
                                case R.id.tvKeLingb:
                                    c.setKeLing(((TextView)child2).getText().toString());
                                    c1.setAppliedNumber(((TextView)child2).getText().toString());break;
                                case R.id.tvToolId:
                                    c1.setToolID(((TextView)child2).getText().toString());break;
                                case R.id.tvrfidCode:
                                    c1.setRfidCode(((TextView)child2).getText().toString());break;
                                case R.id.tvreplacementID:
                                    c1.setReplacementID(((TextView)child2).getText().toString());break;
                                default:
                            }
                        }
                        if(j == child2Coutn-1){
                            int chuku = 0;
                            int keling = 0;
                            int kucun = 0;
                            int min = 0;


                            if("".equals(c.getChuKu()) || null == c.getChuKu()){
                                createAlertDialog(c01s004_002Activity.this,"请填写数据",Toast.LENGTH_SHORT);
                                return null;
                            }
                            chuku = Integer.parseInt(c.getChuKu());
                            keling = Integer.parseInt(c1.getAppliedNumber());
                            kucun = Integer.parseInt(c.getKuCun());
                            min = Math.min(kucun,keling);
                            if(chuku>min){
                                createAlertDialog(c01s004_002Activity.this,"出库数量大于库存数量或可领数量，请确认",Toast.LENGTH_SHORT);
                                return null;
                            }
//                            if(chuku<=0){
//                                createAlertDialog(c01s004_002Activity.this,"出库数量要大于0，请检查",Toast.LENGTH_SHORT);
//                                return null;
//                            }
                        }
                    }
                    if(Integer.valueOf(c1.getReceiveCount())>=0){
                        beanList.add(c1);
                        table.add(c);
                    }
                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(beanList);
    }
}
