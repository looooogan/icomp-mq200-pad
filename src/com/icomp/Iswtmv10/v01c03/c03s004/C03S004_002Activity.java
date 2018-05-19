package com.icomp.Iswtmv10.v01c03.c03s004;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

/**
 * 修磨设备初始化2
 * Created by FanLL on 2017/6/29.
 */

public class C03S004_002Activity extends CommonActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s004_002);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
    }

    //继续按钮处理--跳转到修磨设备初始化页面1
    public void btnKeepOn(View view) {
        Intent intent = new Intent(this, C03S004_001Activity.class);
        startActivity(intent);
        finish();
    }

    //完成按钮处理--跳转到系统菜单页面
    public void btnComplete(View view) {
        finish();
    }

}
