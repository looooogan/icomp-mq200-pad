package com.icomp.Iswtmv10.v01c01.c01s005;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s004.C01S004_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s004.c01s004_004Activity;
import com.icomp.common.activity.CommonActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s005_005Activity extends CommonActivity {

    @BindView(R.id.btnGoOn)
    Button mBtnGoOn;
    @BindView(R.id.btnComplete)
    Button mBtnComplete;
    private int tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_005);
        ButterKnife.bind(this);
        tag = getIntent().getIntExtra("TAG",0);
    }

    @OnClick({R.id.btnGoOn, R.id.btnComplete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnGoOn:
                goOn();
                break;
            case R.id.btnComplete:
                complete();
                break;
        }
    }


    private void goOn(){
        switch (tag){
            case 1:
                Intent intent2 = new Intent(c01s005_005Activity.this,C01S005_001Activity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isClear", true);
                intent2.putExtras(bundle);
                startActivity(intent2);
                finish();
                break;
            case 2:
                Intent intent3 = new Intent(c01s005_005Activity.this,C01S005_001Activity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean("isClear", true);
                intent3.putExtras(bundle1);
                startActivity(intent3);
                finish();
                break;
            case 3:
                Intent intent4 = new Intent(c01s005_005Activity.this,C01S005_001Activity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putBoolean("isClear", true);
                intent4.putExtras(bundle2);
                startActivity(intent4);
                finish();
                break;
        }
    }

    private void complete(){
        Intent intent = new Intent(this,C00S000_002Activity.class);
        startActivity(intent);
        finish();
    }
}
