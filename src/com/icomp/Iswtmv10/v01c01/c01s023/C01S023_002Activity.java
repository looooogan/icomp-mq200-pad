package com.icomp.Iswtmv10.v01c01.c01s023;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 单品绑定页面2
 * Created by FanLL on 2017/9/14.
 */

public class C01S023_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.tv_02)
    TextView tv02;
    @BindView(R.id.et_01)
    EditText et01;
    @BindView(R.id.btnScan)
    Button btnScan;

    //平均加工量的列表
    private List<String> listString = new ArrayList<>();
    //扫描线程
    private scanThread scanThread;

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s023_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //显示一体刀的单品编码
        tv01.setText(exChangeBig(getIntent().getSerializableExtra(PARAM).toString()) + "-" + exChangeBig(getIntent().getSerializableExtra(PARAM1).toString()));
        et01.setText(ZERO);
        et01.setTransformationMethod(new AllCapTransformationMethod());
        //将光标设置在最后
        et01.setSelection(et01.getText().length());
        //获取平均加工量
        loading.show();
        requestAverageProcessingCapacity(exChangeBig(getIntent().getSerializableExtra(PARAM).toString().trim()));
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Intent intent = new Intent(this, C00S000_002Activity.class);
        startActivity(intent);
        SysApplication.getInstance().exit();
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //获取平均加工量
    private void requestAverageProcessingCapacity(String materialNum) {
        //调用接口，提交单品绑定信息
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getToolDurableList = iRequest.getToolDurableList(materialNum);
        getToolDurableList.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        C01S023_002 c01s023002 = gson.fromJson(json, C01S023_002.class);
                        for (int i = 0; i < c01s023002.getData().size(); i++) {
                            listString.add(c01s023002.getData().get(i).getToolDurable()+"");
                        }
                    } else {
                        createAlertDialog(C01S023_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S023_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    @OnClick({R.id.btnScan, R.id.ll_01})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_01:
                //显示平均加工量的列表
                showPopupWindow();
                break;
            case R.id.btnScan:
                //点击扫描按钮的方法
                scan();
                break;
            default:
        }
    }

    //显示平均加工量的列表
    private void showPopupWindow() {
        View view = LayoutInflater.from(C01S023_002Activity.this).inflate(R.layout.spinner_c03s004_001, null);
        ListView listView = (ListView) view.findViewById(R.id.ll_spinner);
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        final PopupWindow popupWindow = new PopupWindow(view, ll01.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv02.setText(listString.get(i));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listString.size();
        }

        @Override
        public Object getItem(int i) {
            return listString.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(C01S023_002Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(listString.get(i));
            return view1;
        }

    }

    //点击扫描按钮的方法
    private void scan() {
        //判断修磨次数和加工量的关系
        if ("".equals(et01.getText().toString().trim())) {
            createAlertDialog(C01S023_002Activity.this, getString(R.string.c03s006_002_001), Toast.LENGTH_LONG);
        } else if (Integer.parseInt(et01.getText().toString()) != 0 && "".equals(tv02.getText().toString().trim())) {
            createAlertDialog(C01S023_002Activity.this, getString(R.string.c01s023_002_002), Toast.LENGTH_LONG);
            isCanScan = true;
        } else {
            if (null == popupWindow || !popupWindow.isShowing()) {
                //点击扫描按钮以后，设置扫描按钮不可用
                btnScan.setClickable(false);
                //显示扫描弹框的方法
                scanPopupWindow();
                //开启扫描线程
                scanThread = new scanThread();
                scanThread.start();
            }
        }
    }

    //扫描线程
    public class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //调用单扫方法
            rfidString = singleScan();
            final Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                overtimeHandler.sendMessage(message);
            } else if (null != rfidString && !"close".equals(rfidString)) {
                //设定用户访问信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                //用户ID
                String customerID = sharedPreferences.getString("customerID", "");
                //调用接口，提交单品绑定信息
                IRequest iRequest = retrofit.create(IRequest.class);
                Call<String> saveOneKnifeBinding = iRequest.saveOneKnifeBinding(exChangeBig(getIntent().getSerializableExtra(PARAM).toString().trim()), tv01.getText().toString(), rfidString, customerID, et01.getText().toString().trim(), tv02.getText().toString().trim());
                saveOneKnifeBinding.enqueue(new MyCallBack<String>() {
                    @Override
                    public void _onResponse(Response response) {
                        try {
                            String json = response.body().toString();
                            JSONObject jsonObject = new JSONObject(json);
                            if (jsonObject.getBoolean("success")) {
                                Intent intent = new Intent(C01S023_002Activity.this, C01S023_003Activity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                createAlertDialog(C01S023_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            if (null != popupWindow && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                            btnScan.setClickable(true);
                            isCanScan = true;
                        }
                    }

                    @Override
                    public void _onFailure(Throwable t) {
                        popupWindow.dismiss();
                        btnScan.setEnabled(true);
                        isCanScan = true;
                        createAlertDialog(C01S023_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                    }
                });
            }
        }
    }

    //重写键盘上扫描按键的方法
    @Override
    protected void btnScan() {
        super.btnScan();
        if (isCanScan) {
            isCanScan = false;
        } else {
            return;
        }
        //扫描方法
        scan();
    }

}
