package com.icomp.Iswtmv10.v01c03.c03s002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

/**
 * 库位标签初始化页面4
 * Created by FanLL on 2017/6/28.
 */

public class C03S002_004Activity extends CommonActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s002_004);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
    }

    //继续按钮处理--跳转到库位标签初始化页面1
    public void btnKeepOn(View view) {
        Intent intent = new Intent(this, C03S002_001Activity.class);
        startActivity(intent);
        finish();
    }

    //完成按钮处理--跳转到跳转到系统菜单页面
    public void btnComplete(View view) {
        finish();
    }

}
