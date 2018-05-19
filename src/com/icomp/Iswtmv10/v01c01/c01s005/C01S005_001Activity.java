package com.icomp.Iswtmv10.v01c01.c01s005;
/**
 * 刀具报废
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

/**
 * 刀具报废页面1
 */

public class C01S005_001Activity extends CommonActivity {

    @BindView(R.id.tvDanPin)
    TextView mTvDanPin;
    @BindView(R.id.tvYiTi)
    TextView mTvYiTi;
    @BindView(R.id.tvTongDao)
    TextView mTvTongDao;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_001);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.tvDanPin, R.id.tvYiTi, R.id.tvTongDao, R.id.btnCancel, R.id.btnReturn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDanPin:
                Intent intentDanPin = new Intent(this,C01S005_002_add.class);
                startActivity(intentDanPin);
                break;
            case R.id.tvYiTi:
                Intent intentYiTi = new Intent(this,c01s005_002_2Activity.class);
                startActivity(intentYiTi);
                break;
            case R.id.tvTongDao:
                Intent intentTong = new Intent(this,c01s005_002_3Activity.class);
                startActivity(intentTong);
                break;
            case R.id.btnCancel:
                Intent intent = new Intent(this, C00S000_002Activity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnReturn:
                finish();
                break;
            default:
        }
    }
}
