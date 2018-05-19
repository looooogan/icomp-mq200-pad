package com.icomp.Iswtmv10.v01c01.c01s009;

/**
 * 刀具组装
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class C01S009_001Activity extends CommonActivity {

    @BindView(R.id.tvHechengDaoJu)
    TextView mTvHechengDaoJu;
    @BindView(R.id.tvTongDao)
    TextView mTvTongDao;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01_s009_001);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tvHechengDaoJu, R.id.tvTongDao, R.id.btnCancel, R.id.btnReturn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //合成刀具组装
            case R.id.tvHechengDaoJu:
                Intent intent = new Intent(this,C01S009_012Activity.class);
                startActivity(intent);
                break;
            //筒刀组装
            case R.id.tvTongDao:
                Intent intent2 = new Intent(this,C01S009_003Activity.class);
                startActivity(intent2);
                break;
            case R.id.btnCancel:
                Intent intent3 = new Intent(this, C00S000_002Activity.class);
                startActivity(intent3);
                finish();
                break;
            case R.id.btnReturn:
                finish();
                break;
            default:
        }
    }
}
