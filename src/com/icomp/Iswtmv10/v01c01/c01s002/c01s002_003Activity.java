package com.icomp.Iswtmv10.v01c01.c01s002;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s002_003Activity extends CommonActivity {
    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s002_003);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoOn:
                break;
            case R.id.btnComplete:
                break;
        }
    }
}
