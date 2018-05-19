package com.icomp.Iswtmv10.v01c01.c01s005;
/**
 * 单品刀中详细报废刀具信息
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class C01S005_002_add extends CommonActivity {

    @BindView(R.id.tvBeiYong)
    TextView mTvBeiYong;
    @BindView(R.id.tvDaiRenMo)
    TextView mTvDaiRenMo;
    @BindView(R.id.tvDaiChuChang)
    TextView mTvDaiChuChang;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.tvChangwai)
    TextView mTvChangwai;
    @BindView(R.id.tvShengchanxian)
    TextView mTvShengchanxian;
    @BindView(R.id.activity_c01_s005_002_add)
    LinearLayout mActivityC01S005002Add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01_s005_002_add);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvBeiYong, R.id.tvDaiRenMo, R.id.tvDaiChuChang, R.id.btnCancel, R.id.btnReturn,R.id.tvChangwai, R.id.tvShengchanxian})
    public void onViewClicked(View view) {
        Intent intent = new Intent(this, c01s005_002_1Activity.class);
        switch (view.getId()) {
            case R.id.tvBeiYong:
                LaunchActivity(intent,"scrapCode","1");
                break;
            case R.id.tvDaiRenMo:
                LaunchActivity(intent,"scrapCode","2");
                break;
            case R.id.tvDaiChuChang:
                LaunchActivity(intent,"scrapCode","3");
                break;
            case R.id.tvChangwai:
                LaunchActivity(intent,"scrapCode","4");
                break;
            case R.id.tvShengchanxian:
                LaunchActivity(intent,"scrapCode","5");
                break;
            case R.id.btnCancel:
                LaunchActivity();
                break;
            case R.id.btnReturn:
                finish();
                break;
        }
    }

    private void LaunchActivity(Intent intent,String name,String code){
        intent.putExtra(name,code);
        startActivity(intent);
    }
    private void LaunchActivity(){
        Intent intent1 = new Intent(this, C00S000_002Activity.class);
        startActivity(intent1);
    }
}
