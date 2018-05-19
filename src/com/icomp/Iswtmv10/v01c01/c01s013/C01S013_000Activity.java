package com.icomp.Iswtmv10.v01c01.c01s013;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import butterknife.ButterKnife;

/**
 * 卸下设备菜单页面
 * Created by FanLL on 2018/1/15.
 */

public class C01S013_000Activity extends CommonActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c01s013_000activity);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
    }

    /**
     * 合成刀点击
     */
    public void synthesis(View view) {
        Intent intent = new Intent(C01S013_000Activity.this, C01S013_001Activity.class);
        startActivity(intent);
    }

    /**
     * 砂轮点击
     */
    public void grinding(View view) {
        Intent intent = new Intent(C01S013_000Activity.this, C01S013_003Activity.class);
        startActivity(intent);
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        finish();
    }

    //返回按钮处理--返回上一页面（刀具管理菜单页面）
    public void btnReturn(View view) {
        finish();
    }

}
