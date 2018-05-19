package com.icomp.Iswtmv10.v01c00.c00s000;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.adapter.C00S000_002Adapter;
import com.icomp.entity.base.System;
import com.icomp.entity.base.Vgrantlist;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.MenuRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.MenuRespons;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 系统主菜单
 *
 * @author yzq
 */
public class C00S000_002Activity extends CommonActivity {

    String[] menu_url_array;
    String[] menu_cap_array;
    String[] menu_capName_array;
    @BindView(R.id.btn_return)
    Button btnReturn;
    @BindView(R.id.gv_menu)
    GridView gvMenu;
    @BindView(R.id.tv_01)
    TextView tv01;
    private String cardString;

    @Override
    public boolean keycodeBack() {
        appReturn();
        return false;
    }

    /**
     * 返回按钮处理
     */
    @Override
    public void appReturn() {
        // 跳转到菜单页面
        Intent intent = new Intent();
        intent.setClass(this, C00S000_001Activity.class);//从哪里跳到哪里
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //上一个界面传来的员工卡号
        cardString = getIntent().getStringExtra(PARAM);
        super.onCreate(savedInstanceState);
//        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c00s000_002activity);
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        String name = preferences.getString("name", "");
        tv01.setText(name);
        loading.show();
        thread = new VisitJniThread();
        thread.start();
    }

    public VisitJniThread thread;

    @OnClick(R.id.btn_return)
    public void onClick() {
        appReturn();
    }

    public class VisitJniThread extends Thread {
        @Override
        public void run() {
            MenuRespons menuRespons = null;
            try {
                menuRespons = initViews();
                Message message = new Message();
                message.obj = menuRespons;
                VisitJniHandler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendEmptyMessage(0);
            }

        }

        private MenuRespons initViews() throws Exception {
//            Date date = new Date();
//            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            // 取得用户登录信息
            SharedPreferences preferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
            String userName = preferences.getString("userName", "");
            String langCode = preferences.getString("langCode", "");
            String langValue = preferences.getString("langValue", "");
            // 取得当前页面的菜单项目
            C00S000Wsdl t = new C00S000Wsdl();
            MenuRequest request = new MenuRequest();
            request.setUserName(userName);
            request.setLanguageCode(langCode);
            request.setLanguageValue(langValue);
            request.setCapabilityLevel(BigDecimal.ONE);
//            createAlertDialog(C00S000_002Activity.this , dateformat.format(date) , Toast.LENGTH_LONG);
            MenuRespons respons = t.getMenu(request);
//            createAlertDialog(C00S000_002Activity.this , dateformat.format(date) , Toast.LENGTH_LONG);
            return respons;
        }

        @SuppressLint("HandlerLeak")
        Handler VisitJniHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MenuRespons respons = (MenuRespons) msg.obj;
                if (loading != null) {
                    loading.dismiss();
                }
                if (null != respons.getStateMsg()) {
                    createAlertDialog(C00S000_002Activity.this, respons.getStateMsg(), Toast.LENGTH_SHORT);
                } else {
                    /** 菜单图片 **/
                    int[] menu_image_array = new int[respons.getVgrantlist().size()];
                    menu_url_array = new String[respons.getVgrantlist().size()];
                    menu_cap_array = new String[respons.getVgrantlist().size()];
                    menu_capName_array = new String[respons.getVgrantlist().size()];
                    int i = 0;
                    for (Vgrantlist vgrantlist : respons.getVgrantlist()) {
                        menu_image_array[i] = Integer.parseInt(vgrantlist.getCapabilityImg().replaceAll("^0[x|X]", ""), 16);
                        menu_url_array[i] = vgrantlist.getCapabilityUrl();
                        menu_cap_array[i] = vgrantlist.getCapabilityID();
                        menu_capName_array[i] = vgrantlist.getCapabilityName();
                        i++;
                    }
                    //menuGrid.setAdapter(getMenuAdapter(menu_image_array));
                    gvMenu.setAdapter(new C00S000_002Adapter(C00S000_002Activity.this, menu_image_array, menu_capName_array));
                    gvMenu.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            if (arg2 <= menu_url_array.length - 1) {
                                String url = menu_url_array[arg2];// 取得要跳转的Activit名
                                String capID = menu_cap_array[arg2];
                                //取得用户登录信息
                                SharedPreferences preferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                                Editor editor = preferences.edit();// 获取编辑器
                                editor.putString("capID", capID);
                                editor.commit();// 提交修改
                                try {
                                    Intent intent = new Intent();
                                    intent.setClass(C00S000_002Activity.this, Class.forName("com.icomp.Iswtmv10." + url));// 从哪里跳到哪里
                                    startActivity(intent);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        };
    }
    // private SimpleAdapter getMenuAdapter(int[] imageResourceArray) {
    // ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,
    // Object>>();
    // for (int i = 0; i < imageResourceArray.length; i++) {
    // HashMap<String, Object> map = new HashMap<String, Object>();
    // map.put("itemImage", imageResourceArray[i]);
    // data.add(map);
    // }
    // SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
    // R.layout.item_c00s000_003, new String[] { "itemImage" },
    // new int[] { R.id.item_image });
    // return simperAdapter;
    // }
    // 设置GridView不滑动
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

}
