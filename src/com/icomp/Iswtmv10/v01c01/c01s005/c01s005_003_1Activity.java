package com.icomp.Iswtmv10.v01c01.c01s005;
/**
 * 单品刀具报废确认
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.DanPinModul;
import com.icomp.common.activity.CommonActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s005_003_1Activity extends CommonActivity {
    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;
    private String jsonData;
    private List<DanPinModul> list = new ArrayList<>();
    private String toolCodes = "";
    private String scrapNumbers = "";
    private String scrapCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_003_1);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        jsonData = getIntent().getStringExtra("json");
        scrapCode = getIntent().getStringExtra("scrapCode");
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                DanPinModul c = gson.fromJson(jsonArray.getJSONObject(i).toString(), DanPinModul.class);
                list.add(c);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0;i<list.size();i++){
            if(i != list.size()-1){
                toolCodes = toolCodes+list.get(i).getCaiLiao()+",";
                scrapNumbers = scrapNumbers+list.get(i).getNum()+",";
            }else{
                toolCodes = toolCodes+list.get(i).getCaiLiao();
                scrapNumbers = scrapNumbers+list.get(i).getNum();
            }
        }
        for (int i = 0; i < list.size(); i++) {
            addLayout(i);
        }
    }

    private void addLayout(int i) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_danpin_daojubaofei_static, null);
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);

        TextView tvBaofeishuliang = (TextView) mLinearLayout.findViewById(R.id.tvBaofeishuliangzu);
        tvCailiao.setText(exChangeBig(list.get(i).getCaiLiao()));
        tvBaofeishuliang.setText(list.get(i).getNum());
        mLlContainer.addView(mLinearLayout);
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
                intent.putExtra("num",scrapNumbers);
                intent.putExtra("TAG",1);
                intent.putExtra("scrapCode",scrapCode);
                startActivity(intent);
                finish();
                break;
        }
    }
}
