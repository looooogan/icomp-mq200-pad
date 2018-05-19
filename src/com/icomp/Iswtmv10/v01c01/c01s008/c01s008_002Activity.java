package com.icomp.Iswtmv10.v01c01.c01s008;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s009.C01S009_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s009.C01S009_003Activity;
import com.icomp.common.activity.CommonActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s008_002Activity extends CommonActivity {

    @BindView(R.id.tvTitle)
    TextView mTvTitle;
    @BindView(R.id.tvContent)
    TextView mTvContent;
    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;
    private String jsonData;
    private List<String> mList = new ArrayList<>();
    private String Tag;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s008_002);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        jsonData = intent.getStringExtra("json");
        Tag = intent.getStringExtra("TAG");
        if(Tag.equals("1")){
            mTvTitle.setText("刀具拆分");
            setData();
        }else{
            mTvTitle.setText("刀具组装");
            setDataZu();
        }


    }

    /**
     * 刀具拆分与道具组装公用画面，通过判断tag来确认跳转到哪些画面
     * @param view
     */
    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoOn:
                Intent intent2;
                if(Tag.equals("1")){
                    intent2 = new Intent(c01s008_002Activity.this,C01S008_001Activity.class);
                }else if (Tag.equals("2")){
                    intent2 = new Intent(c01s008_002Activity.this,C01S009_002Activity.class);
                }else{
                    intent2 = new Intent(c01s008_002Activity.this,C01S009_003Activity.class);
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClear", true);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.btnComplete:
                Intent intent = new Intent(this,C00S000_002Activity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    /**
     * 刀具拆分支线
     */
    private void setData(){
        mTvContent.setText("拆分"+jsonData+"完成");
    }


    /**
     * 刀具组装支线
     */
    private void setDataZu(){
        mTvContent.setText("组装"+jsonData+"完成");
    }
}
