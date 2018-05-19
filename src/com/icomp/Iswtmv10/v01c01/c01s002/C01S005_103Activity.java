package com.icomp.Iswtmv10.v01c01.c01s002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

/**
 * 清空RFID标签
 *
 * @author Taoyy
 * @ClassName: C01S005_103Activity
 * @date 2016年9月4日22:14:53
 */
public class C01S005_103Activity extends CommonActivity {
    private String str;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c01s005_103activity);
        SysApplication.getInstance().addActivity(this);
    }

    //继续按钮
    public void appContinue(View view) {
        Intent intent = new Intent(this, C01S002_001Activity.class);
        startActivity(intent);
        SysApplication.getInstance().exit();
    }

    //完成按钮
    public void appComplete(View view) {
        SysApplication .getInstance().exit();
    }

    //返回按钮处理，跳转到系统菜单界面
    public void appReturn(View view) {
        SysApplication.getInstance().exit();
    }
}