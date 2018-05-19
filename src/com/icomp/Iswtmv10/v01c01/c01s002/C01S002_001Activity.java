package com.icomp.Iswtmv10.v01c01.c01s002;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.adapter.C01S003_004Adapter;
import com.icomp.common.utils.SysApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 清空RFID标签
 *
 * @ClassName: C01S002_001Activity
 * @date 2016年9月4日20:54:53
 */

public class C01S002_001Activity extends CommonActivity {
    private C01S005_Params params;
    private TextView tv_011;
    private LinearLayout ll_011;//卸下原因点击区域,零部件种类点击区域
    private List<String> removeReasonList = new ArrayList<>();//保存所有卸下原因
    private PopupWindow mPopWindow;//卸下原因下拉列表框PopupWindow

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s005_101activity);
        params = new C01S005_Params();
        ll_011 = (LinearLayout) findViewById(R.id.ll_011); //卸下原因点击区域
        tv_011 = (TextView) findViewById(R.id.tv_011);//卸下原因
        //存储所有卸下原因
        for (int i = 0; i < getResources().getStringArray(R.array.QueryType).length; i++) {
            removeReasonList.add(getResources().getStringArray(R.array.QueryType)[i]);
        }
        tv_011.setText(removeReasonList.get(0));//卸下原因初始值
    }



    /**
     * 卸下原因下拉框
     */
    public void dropdown(View view) {
        if (removeReasonList.size() > 0) {
            int tvWidht = ll_011.getWidth();
            View contentView = LayoutInflater.from(C01S002_001Activity.this).inflate(R.layout.item_c01s020_001, null);
            mPopWindow = new PopupWindow(contentView, tvWidht, 200, true);
            mPopWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopWindow.showAsDropDown(ll_011);//显示对话框
            ListView lv_01 = (ListView) contentView.findViewById(R.id.lv_01);
            C01S003_004Adapter adapter = new C01S003_004Adapter(C01S002_001Activity.this, removeReasonList);
            lv_01.setAdapter(adapter);
            lv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_011.setText(removeReasonList.get(i));
                    mPopWindow.dismiss();
                    //选择类型
                    params.containerCarrierType = i;
                }
            });
        }
    }


    /**
     * 返回
     */
    public void appReturn(View view) {
        finish();
    }

    /**
     * 确认
     */
    public void gotoNextPage(View view) {
        Intent intent = new Intent(this, C01S005_102Activity.class);
        intent.putExtra(PARAM, params);
        startActivity(intent);
        finish();
    }


    /**
     * 点击空白收起键盘
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

}


