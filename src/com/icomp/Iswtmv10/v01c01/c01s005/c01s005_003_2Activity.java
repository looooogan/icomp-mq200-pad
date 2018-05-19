package com.icomp.Iswtmv10.v01c01.c01s005;
/**
 * 一体刀具报废确认
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s005_003_2Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;
    private String json;
    private String rifd;
    private List<String> jsonData = new ArrayList<>();
    private List<String> rifdData = new ArrayList<>();

    private String toolCodes = "";
    private String rfidContainerIDs = "";
    private int position = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_003_2);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        json = getIntent().getStringExtra("json");
        rifd = getIntent().getStringExtra("rifd");
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String temp = jsonArray.getString(i);
                jsonData.add(temp);
            }

            JSONArray jsonArray2 = new JSONArray(rifd);
            for (int i = 0; i < jsonArray2.length(); i++) {
                String temp = jsonArray2.getString(i);
                rifdData.add(temp);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0;i<jsonData.size();i++){
            if(i != jsonData.size()-1){
                toolCodes = toolCodes+jsonData.get(i)+",";
                rfidContainerIDs = rfidContainerIDs+rifdData.get(i)+",";
            }else{
                toolCodes = toolCodes+jsonData.get(i);
                rfidContainerIDs = rfidContainerIDs+rifdData.get(i);
            }
        }
        for (int i = 0; i < jsonData.size(); i++) {
            addLayout(i);
        }
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
                intent.putExtra("TAG",2);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void addLayout(int i) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_yiti_daojubaofei_static, null);
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvSericle = (TextView) mLinearLayout.findViewById(R.id.tvSerizle);
        tvSericle.setText(""+position);
        position++;
        tvCailiao.setText(jsonData.get(i));
        mLlContainer.addView(mLinearLayout);
    }
}
