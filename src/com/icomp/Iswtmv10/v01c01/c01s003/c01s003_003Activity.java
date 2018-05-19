package com.icomp.Iswtmv10.v01c01.c01s003;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s001.C01S001_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s001.c01s001_003Activity;
import com.icomp.common.activity.CommonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s003_003Activity extends CommonActivity {

    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s003_003);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //继续换领
            case R.id.btnGoOn:
                Intent intent2 = new Intent(c01s003_003Activity.this,C01S003_001Activity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClear", true);
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            //跳转到菜单画面
            case R.id.btnComplete:
                Intent intent = new Intent(this,C00S000_002Activity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
