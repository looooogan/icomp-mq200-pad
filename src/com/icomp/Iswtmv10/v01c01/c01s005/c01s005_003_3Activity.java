package com.icomp.Iswtmv10.v01c01.c01s005;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.TongDaoModul;
import com.icomp.common.activity.CommonActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s005_003_3Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;
    private String jsonData;
    private List<TongDaoModul> list = new ArrayList<>();

    private String toolCodes = "";
    private String rfidContainerIDs = "";
    private String synthesisParametersCode = "";
    private String toolCounts = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_003_3);
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.btnReturn, R.id.btnConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnConfirm:
                Intent intent = new Intent(this,c01s005_004_1Activity.class);
                intent.putExtra("name",toolCodes);
                intent.putExtra("rifd",rfidContainerIDs);
                intent.putExtra("syn",synthesisParametersCode);
                intent.putExtra("num",toolCounts);
                intent.putExtra("TAG",3);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void initView() {
        jsonData = getIntent().getStringExtra("json");
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                TongDaoModul c = gson.fromJson(jsonArray.getJSONObject(i).toString(), TongDaoModul.class);
                list.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0;i<list.size();i++){
            if(i != list.size()-1){
                toolCodes = toolCodes+list.get(i).getCaiLiao()+",";
                rfidContainerIDs = rfidContainerIDs+list.get(i).getrFID()+",";
                synthesisParametersCode = synthesisParametersCode+list.get(i).getSynthesisParametersCode()+",";
                toolCounts = toolCounts+list.get(i).getGroupNum()+",";
            }else{
                toolCodes = toolCodes+list.get(i).getCaiLiao();
                rfidContainerIDs = rfidContainerIDs+list.get(i).getrFID();
                synthesisParametersCode = synthesisParametersCode+list.get(i).getSynthesisParametersCode();
                toolCounts = toolCounts+list.get(i).getGroupNum();
            }
        }


        for (int i = 0; i < list.size(); i++) {
            addLayout(i);
        }
    }

    private void addLayout(int i) {
        if(checkIsExit(list.get(i).getCaiLiao())){
            return;
        }
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_danpin_daojubaofei_static, null);
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvBaofeishuliang = (TextView) mLinearLayout.findViewById(R.id.tvBaofeishuliang);
        TextView tvBaofeishuliangZu = (TextView) mLinearLayout.findViewById(R.id.tvBaofeishuliangzu);
        tvCailiao.setText(list.get(i).getCaiLiao());
        tvBaofeishuliang.setText(list.get(i).getGroupNum());
        String num = String.valueOf(checkNum(list.get(i)));
        tvBaofeishuliangZu.setText(num);
        mLlContainer.addView(mLinearLayout);
    }


    private int checkNum(TongDaoModul t){
        int num = 0;
        for (int i = 0; i<list.size();i++){
            if(list.get(i).getCaiLiao().equals(t.getCaiLiao())){
                num++;
            }
        }
        return num;
    }

    /**
     * 遍历所有数据并转化为json
     */
    private boolean checkIsExit(String code){
        if(mLlContainer.getChildCount() == 0){
            return false;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    if(((TextView) child2).getText().toString().equals(code)){
                                        return true;
                                    }break;
                            }
                        }
                    }
                }
            
            }
        }
        return false;
    }
}
