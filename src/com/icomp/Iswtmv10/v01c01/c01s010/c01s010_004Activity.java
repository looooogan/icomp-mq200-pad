package com.icomp.Iswtmv10.v01c01.c01s010;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s010_004Activity extends CommonActivity {

    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s010_004);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
    }

    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoOn:
                Intent intent2 = new Intent(c01s010_004Activity.this,C01S010_001Activity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClear", true);
                intent2.putExtras(bundle);
                startActivity(intent2);
                SysApplication.getInstance().exit();
                break;
            case R.id.btnComplete:
                Intent intent = new Intent(this,C00S000_002Activity.class);
                startActivity(intent);
                SysApplication.getInstance().exit();
                break;
            default:
        }
    }
}
