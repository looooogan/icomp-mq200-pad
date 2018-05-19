package com.icomp.Iswtmv10.v01c01.c01s023;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

/**
 * 单品绑定页面3
 * Created by FanLL on 2017/9/15.
 */

public class C01S023_003Activity extends CommonActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s023_003);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
    }

    //继续按钮处理--跳转到单品绑定页面1
    public void btnKeepOn(View view) {
        Intent intent = new Intent(this, C01S023_001Activity.class);
        startActivity(intent);
        SysApplication.getInstance().exit();
    }

    //完成按钮处理--跳转到刀具管理菜单页面
    public void btnComplete(View view) {
        Intent intent = new Intent(this, C00S000_002Activity.class);
        startActivity(intent);
        finish();
    }

}
