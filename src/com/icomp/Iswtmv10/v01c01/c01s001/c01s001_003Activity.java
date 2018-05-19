package com.icomp.Iswtmv10.v01c01.c01s001;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s001_003Activity extends CommonActivity {

    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;
//    @BindView(R.id.tvNumber)
//    TextView mTvNumber;
    @BindView(R.id.tvMuch)
    TextView mTvMuch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s001_003);
        ButterKnife.bind(this);
        SysApplication.getInstance().addActivity(this);

    }

    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoOn:
                //继续新刀入库
//                Intent intent2 = new Intent(c01s001_003Activity.this,C01S001_001Activity.class);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isClear", true);
//                intent2.putExtras(bundle);
//                startActivity(intent2);
                Intent intent2 = new Intent(c01s001_003Activity.this,C01S001_001Activity.class);
                SysApplication.getInstance().exit();
                startActivity(intent2);
                break;
            case R.id.btnComplete:
                //返回到菜单画面
                Intent intent = new Intent(this,C00S000_002Activity.class);
                startActivity(intent);
                finish();
                break;
            default:
        }
    }
}
