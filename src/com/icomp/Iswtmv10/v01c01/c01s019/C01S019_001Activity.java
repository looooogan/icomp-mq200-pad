package com.icomp.Iswtmv10.v01c01.c01s019;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 厂外修磨页面1
 * Created by FanLL on 2017/7/4.
 */

public class C01S019_001Activity extends CommonActivity {

    @BindView(R.id.iv_01)
    ImageView iv01;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.btnScan)
    Button btnScan;

    //查询弹框
    private PopupWindow popupWindow2;
    //存放rfidString的Map的位置
    private int position = 0;
    //存放rfidString的Map
    private HashMap<Integer, String> rfidStringMap = new HashMap<>();
    //扫描线程
    private scanThread scanThread;
    //向下一个页面传递参数的List
    private List<C01S019_001.DataBean> list = new ArrayList<>();

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s019_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
    }

    //返回按钮处理--跳转到系统菜单页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        next();
    }

    @OnClick({R.id.iv_01, R.id.btnScan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_01:
                //点击加号增加一行的方法
                inputAddARow();
                break;
            case R.id.btnScan:
                //点击扫描按钮的方法
                scan();
                break;
        }
    }

    //点击加号增加一行的方法
    private void inputAddARow() {
        //控制只可以添加一个空行
        if (!fullOrNot) {
            createAlertDialog(this, getString(R.string.c01s019_001_002), Toast.LENGTH_LONG);
            return;
        } else {
            //加一行
            final View addRow = LayoutInflater.from(this).inflate(R.layout.item_c01s019_001, null);
            ImageView delete = (ImageView) addRow.findViewById(R.id.iv_01);//减号
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout child = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
                    boolean lastOrNot = false;
                    //如果addRow的位置和ll01最后一行的位置相同，设置lastOrNot = true
                    if (addRow.getTag() == child.getTag()) {
                        lastOrNot = true;
                    }
                    //如果删除的是最后一行并且为空，设置fullOrNot = true
                    if (!fullOrNot && lastOrNot) {
                        fullOrNot = true;
                    }
                    ll01.removeView(addRow);
                }
            });
            TextView materialNumber = (TextView) addRow.findViewById(R.id.tvmaterialNumber);//材料号
            materialNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //显示材料号和修磨数量的弹框
                    showDialog();
                }
            });
            //显示在列表上
            ll01.addView(addRow);
            fullOrNot = false;
        }
    }

    //显示材料号和修磨数量的弹框
    private void showDialog() {
        if (null == popupWindow2 || !popupWindow2.isShowing()) {
            //显示弹框时，设置扫描按钮不可用
            btnScan.setClickable(false);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.dialog_c01s019_001, null);
            popupWindow2 = new PopupWindow(view, (int) (0.8*screenWidth), (int) (0.6*screenHeight), true);
            popupWindow2.setFocusable(true);
            popupWindow2.setOutsideTouchable(false);
            popupWindow2.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
            final EditText etmaterialNumber = (EditText) view.findViewById(R.id.etmaterialNumber);//材料号
            etmaterialNumber.setTransformationMethod(new AllCapTransformationMethod());
            final EditText etgrindingQuantity = (EditText) view.findViewById(R.id.etgrindingQuantity);//修磨数量
            Button btnCancel = (Button) view.findViewById(R.id.btnCancel);//取消按钮
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow2.dismiss();
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            });
            Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);//确定按钮
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("".equals(etmaterialNumber.getText().toString().trim())) {
                        createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s019_001_003), Toast.LENGTH_LONG);
                    } else if ("".equals(etgrindingQuantity.getText().toString().trim())) {
                        createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s019_001_004), Toast.LENGTH_LONG);
                    }else if (Integer.parseInt(etgrindingQuantity.getText().toString().trim()) == 0) {
                        createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s019_001_006), Toast.LENGTH_LONG);
                    } else {
                        loading.show();
                        //调用接口，查询刀具信息判断是否可以出厂修磨
                        IRequest iRequest = retrofit.create(IRequest.class);
                        Call<String> getOutFactoryToolInfo = iRequest.getOutFactoryToolInfo("", etmaterialNumber.getText().toString().trim());
                        getOutFactoryToolInfo.enqueue(new MyCallBack<String>() {
                            @Override
                            public void _onResponse(Response response) {
                                try {
                                    String json = response.body().toString();
                                    JSONObject jsonObject = new JSONObject(json);
                                    Gson gson = new Gson();
                                    if (jsonObject.getBoolean("success")) {
                                        C01S019_001 c01s019001 = gson.fromJson(json, C01S019_001.class);
                                        C01S019_001.DataBean c01s019001DataBean = c01s019001.getData();
                                        c01s019001DataBean.setGrindingQuantity(etgrindingQuantity.getText().toString().trim());
                                        popupWindow2.dismiss();
                                        //将数据放到表格上
                                        addData(c01s019001DataBean);
                                    } else {
                                        createAlertDialog(C01S019_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {
                                    loading.dismiss();
                                    btnScan.setClickable(true);
                                    isCanScan = true;
                                }
                            }

                            @Override
                            public void _onFailure(Throwable t) {
                                loading.dismiss();
                                createAlertDialog(C01S019_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                                btnScan.setClickable(true);
                                isCanScan = true;
                            }
                        });
                    }
                }
            });
        }
    }

    //将数据放到表格上
    private void addData(C01S019_001.DataBean c01s019001DataBean) {
        //取列表的最后一行
        LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
        //遍历最后一行里的所有控件
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof TextView) {
                switch (view.getId()) {
                    case R.id.tvmaterialNumber://材料号
                        ((TextView) view).setText(exChangeBig(c01s019001DataBean.getToolCode()));
                        view.setClickable(false);
                        break;
                    case R.id.tvgrindingQuantity://修磨数量
                        ((TextView) view).setText(c01s019001DataBean.getGrindingQuantity());
                        view.setClickable(false);
                        break;
                    case R.id.tvtoolIDs://刀具ID
                        ((TextView) view).setText(c01s019001DataBean.getToolID());
                        break;
                    case R.id.tvtoolTypes://刀具类型
                        ((TextView) view).setText(c01s019001DataBean.getToolType());
                        break;
                    case R.id.tvrfidContainerIDs://RFID载体ID
                        ((TextView) view).setText(c01s019001DataBean.getRfidContainerID());
                        break;
                }
            }
        }
        fullOrNot = true;
    }

    //点击扫描按钮的方法
    private void scan() {
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

    //扫描线程
    public class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //单扫方法
            rfidString = singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                //超过扫描时间的Handler
                overtimeHandler.sendMessage(message);
            } else if(null != rfidString && !"close".equals(rfidString)) {
                message.obj = rfidString;
                //正常提交数据的Handler
                scanHandler.sendMessage(message);
            } else {
                btnScan.setClickable(true);
                isCanScan = true;
                //处理网络异常的Handler
                internetErrorHandler.sendMessage(message);
            }
        }
    }

    //正常提交数据的Handler
    @SuppressLint("HandlerLeak")
    public Handler scanHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            rfidString = (String) message.obj;
            //调用接口，查询刀具信息判断是否可以出厂修磨
            IRequest iRequest = retrofit.create(IRequest.class);
            Call<String> getOutFactoryToolInfo = iRequest.getOutFactoryToolInfo(rfidString, "");
            getOutFactoryToolInfo.enqueue(new MyCallBack<String>() {
                @Override
                public void _onResponse(Response response) {
                    loading.show();
                    try {
                        String json = response.body().toString();
                        JSONObject jsonObject = new JSONObject(json);
                        Gson gson = new Gson();
                        if (jsonObject.getBoolean("success")) {
                            final C01S019_001 c01s019001 = gson.fromJson(json, C01S019_001.class);
                            //如果重复扫描一个标签，提示请勿重复扫描
                            if (rfidStringMap.containsValue(c01s019001.getData().getRfidContainerID())) {
                                createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s017_001_007), Toast.LENGTH_LONG);
                            } else {
                                if (ZERO.equals(c01s019001.getData().getCode())) {
                                    //需要授权
                                    AlertDialog.Builder builder = new AlertDialog.Builder(C01S019_001Activity.this);
                                    builder.setTitle(R.string.prompt);
                                    builder.setMessage(R.string.c01s018_012_004);
                                    builder.setCancelable(false);
                                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //将扫描取得的数据显示在列表上的方法
                                            scanAuthorizationAddARow(c01s019001.getData());
                                        }
                                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int a) {
                                            dialogInterface.dismiss();
                                            btnScan.setClickable(true);
                                            isCanScan = true;
                                        }
                                    }).show();
                                } else {
                                    //将扫描取得的数据显示在列表上的方法
                                    scanAddARow(c01s019001.getData());
                                }
                            }
                        } else {
                            createAlertDialog(C01S019_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        loading.dismiss();
                        btnScan.setClickable(true);
                        isCanScan = true;
                    }
                }

                @Override
                public void _onFailure(Throwable t) {
                    loading.dismiss();
                    createAlertDialog(C01S019_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            });
        }
    };

    //将扫描取得的需要授权的数据显示在列表上的方法
    private void scanAuthorizationAddARow(C01S019_001.DataBean c01s019001DataBean) {
        if (!fullOrNot) {
            //取列表的最后一行
            LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
            //遍历最后一行里的所有控件
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View view = linearLayout.getChildAt(i);
                if (view instanceof TextView) {
                    switch (view.getId()) {
                        case R.id.tvmaterialNumber://材料号
                            ((TextView) view).setText(exChangeBig(c01s019001DataBean.getToolCode()));
                            break;
                        case R.id.tvgrindingQuantity://修磨数量
                            ((TextView) view).setText(ONE);
                            break;
                        case R.id.tvtoolIDs://刀具ID
                            ((TextView) view).setText(c01s019001DataBean.getToolID());
                            break;
                        case R.id.tvtoolTypes://刀具类型
                            ((TextView) view).setText(c01s019001DataBean.getToolType());
                            break;
                        case R.id.tvrfidContainerIDs://RFID载体ID
                            ((TextView) view).setText(c01s019001DataBean.getRfidContainerID());
                            break;
                        case R.id.tvauthorizationFlgs://是否需要授权标识
                            ((TextView) view).setText(ONE);
                            break;
                    }
                }
            }
            TextView tvmaterialNumber = (TextView) findViewById(R.id.tvmaterialNumber);//材料号
            tvmaterialNumber.setEnabled(false);
            TextView tvrfidContainerIDs = (TextView) findViewById(R.id.tvrfidContainerIDs);//RFID载体ID
            tvrfidContainerIDs.setTag(position);
            position++;
            rfidStringMap.put((Integer) tvrfidContainerIDs.getTag(), tvrfidContainerIDs.getText().toString());
            fullOrNot = true;
        } else {
            //加一行
            final View addRow = LayoutInflater.from(this).inflate(R.layout.item_c01s019_001, null);
            ImageView delete = (ImageView) addRow.findViewById(R.id.iv_01);//减号
            TextView tvmaterialNumber = (TextView) addRow.findViewById(R.id.tvmaterialNumber);//材料号
            tvmaterialNumber.setText(exChangeBig(c01s019001DataBean.getToolCode()));
            TextView tvgrindingQuantity = (TextView) addRow.findViewById(R.id.tvgrindingQuantity);//修磨数量
            tvgrindingQuantity.setText(ONE);
            TextView tvtoolID = (TextView) addRow.findViewById(R.id.tvtoolIDs);//刀具ID
            tvtoolID.setText(c01s019001DataBean.getToolID());
            TextView tvtoolTypes = (TextView) addRow.findViewById(R.id.tvtoolTypes);//刀具类型
            tvtoolTypes.setText(c01s019001DataBean.getToolType());
            final TextView tvRfidContainerID = (TextView) addRow.findViewById(R.id.tvrfidContainerIDs);//RFID载体ID
            tvRfidContainerID.setText(c01s019001DataBean.getRfidContainerID());
            TextView tvauthorizationFlgs = (TextView) addRow.findViewById(R.id.tvauthorizationFlgs);//是否需要授权标识
            tvauthorizationFlgs.setText(ONE);
            tvRfidContainerID.setTag(position);
            position++;
            rfidStringMap.put((Integer) tvRfidContainerID.getTag(), tvRfidContainerID.getText().toString().trim());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout child = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
                    boolean lastOrNot = false;
                    //如果addRow的位置和ll01最后一行的位置相同，设置lastOrNot = true
                    if (addRow.getTag() == child.getTag()) {
                        lastOrNot = true;
                    }
                    //如果删除的是最后一行并且为空，设置fullOrNot = true
                    if (!fullOrNot && lastOrNot) {
                        fullOrNot = true;
                    }
                    ll01.removeView(addRow);
                    rfidStringMap.remove(tvRfidContainerID.getTag());
                }
            });
            //将加入条目显示在页面上
            ll01.addView(addRow);
            fullOrNot = true;
        }
        btnScan.setClickable(true);
        isCanScan = true;
    }

    //将扫描取得的数据显示在列表上的方法
    private void scanAddARow(C01S019_001.DataBean c01s019001DataBean) {
        if (!fullOrNot) {
            //取列表的最后一行
            LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
            //遍历最后一行里的所有控件
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View view = linearLayout.getChildAt(i);
                if (view instanceof TextView) {
                    switch (view.getId()) {
                        case R.id.tvmaterialNumber://材料号
                            ((TextView) view).setText(exChangeBig(c01s019001DataBean.getToolCode()));
                            break;
                        case R.id.tvgrindingQuantity://修磨数量
                            ((TextView) view).setText(ONE);
                            break;
                        case R.id.tvtoolIDs://刀具ID
                            ((TextView) view).setText(c01s019001DataBean.getToolID());
                            break;
                        case R.id.tvtoolTypes://刀具类型
                            ((TextView) view).setText(c01s019001DataBean.getToolType());
                            break;
                        case R.id.tvrfidContainerIDs://RFID载体ID
                            ((TextView) view).setText(c01s019001DataBean.getRfidContainerID());
                            break;
                        case R.id.tvauthorizationFlgs://是否需要授权标识
                            ((TextView) view).setText(ZERO);
                            break;
                    }
                }
            }
            TextView tvmaterialNumber = (TextView) findViewById(R.id.tvmaterialNumber);//材料号
            tvmaterialNumber.setEnabled(false);
            TextView tvrfidContainerIDs = (TextView) findViewById(R.id.tvrfidContainerIDs);//RFID载体ID
            tvrfidContainerIDs.setTag(position);
            position++;
            rfidStringMap.put((Integer) tvrfidContainerIDs.getTag(), tvrfidContainerIDs.getText().toString());
            fullOrNot = true;
        } else {
            //加一行
            final View addRow = LayoutInflater.from(this).inflate(R.layout.item_c01s019_001, null);
            ImageView delete = (ImageView) addRow.findViewById(R.id.iv_01);//减号
            TextView tvmaterialNumber = (TextView) addRow.findViewById(R.id.tvmaterialNumber);//材料号
            tvmaterialNumber.setText(exChangeBig(c01s019001DataBean.getToolCode()));
            TextView tvgrindingQuantity = (TextView) addRow.findViewById(R.id.tvgrindingQuantity);//修磨数量
            tvgrindingQuantity.setText(ONE);
            TextView tvtoolID = (TextView) addRow.findViewById(R.id.tvtoolIDs);//刀具ID
            tvtoolID.setText(c01s019001DataBean.getToolID());
            TextView tvtoolTypes = (TextView) addRow.findViewById(R.id.tvtoolTypes);//刀具类型
            tvtoolTypes.setText(c01s019001DataBean.getToolType());
            final TextView tvRfidContainerID = (TextView) addRow.findViewById(R.id.tvrfidContainerIDs);//RFID载体ID
            tvRfidContainerID.setText(c01s019001DataBean.getRfidContainerID());
            TextView tvauthorizationFlgs = (TextView) addRow.findViewById(R.id.tvauthorizationFlgs);//是否需要授权标识
            tvauthorizationFlgs.setText(ZERO);
            tvRfidContainerID.setTag(position);
            position++;
            rfidStringMap.put((Integer) tvRfidContainerID.getTag(), tvRfidContainerID.getText().toString().trim());
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout child = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
                    boolean lastOrNot = false;
                    //如果addRow的位置和ll01最后一行的位置相同，设置lastOrNot = true
                    if (addRow.getTag() == child.getTag()) {
                        lastOrNot = true;
                    }
                    //如果删除的是最后一行并且为空，设置fullOrNot = true
                    if (!fullOrNot && lastOrNot) {
                        fullOrNot = true;
                    }
                    ll01.removeView(addRow);
                    rfidStringMap.remove(tvRfidContainerID.getTag());
                }
            });
            //将加入条目显示在页面上
            ll01.addView(addRow);
            fullOrNot = true;
        }
        btnScan.setClickable(true);
        isCanScan = true;
    }

    //下一步按钮处理
    private void next() {
        if (ll01.getChildCount() == 0) {
            createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s019_001_003), Toast.LENGTH_LONG);
        } else if (ll01.getChildCount() == 1) {
            //遍历页面列表数据传递到下一个页面
            list.clear();
            //如果只有一行判断是否为空
            C01S019_001.DataBean params1 = new C01S019_001.DataBean();
            LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(0);
            //遍历每一行中的每一个控件
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                View view1 = linearLayout.getChildAt(j);
                if (view1 instanceof TextView) {
                    switch (view1.getId()) {
                        case R.id.tvmaterialNumber://材料号
                            params1.setToolCode(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvgrindingQuantity://修磨数量
                            params1.setGrindingQuantity(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvtoolIDs://刀具ID
                            params1.setToolID(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvtoolTypes://刀具类型
                            params1.setToolType(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvrfidContainerIDs://RFID载体ID
                            params1.setRfidContainerID(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvauthorizationFlgs://是否需要授权标识
                            params1.setCode(((TextView) view1).getText().toString().trim());
                            break;
                    }
                }
            }
            if (!"".equals(params1.getToolCode()) && null != params1.getGrindingQuantity()) {
                list.add(params1);
                Gson gosn = new Gson();
                Intent intent = new Intent(this, C01S019_002Activity.class);
                intent.putExtra(PARAM, gosn.toJson(list));
                startActivity(intent);
            } else {
                createAlertDialog(C01S019_001Activity.this, getString(R.string.c01s019_001_003), Toast.LENGTH_LONG);
            }
        } else {
            //遍历页面列表数据传递到下一个页面
            list.clear();
            //遍历每一行
            for (int i = 0; i < ll01.getChildCount(); i++) {
                C01S019_001.DataBean params1 = new C01S019_001.DataBean();
                LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(i);
                //遍历每一行中的每一个控件
                for (int j = 0; j < linearLayout.getChildCount(); j++) {
                    View view1 = linearLayout.getChildAt(j);
                    if (view1 instanceof TextView) {
                        switch (view1.getId()) {
                            case R.id.tvmaterialNumber://材料号
                                params1.setToolCode(((TextView) view1).getText().toString().trim());
                                break;
                            case R.id.tvgrindingQuantity://修磨数量
                                params1.setGrindingQuantity(((TextView) view1).getText().toString().trim());
                                break;
                            case R.id.tvtoolIDs://刀具ID
                                params1.setToolID(((TextView) view1).getText().toString().trim());
                                break;
                            case R.id.tvtoolTypes://刀具类型
                                params1.setToolType(((TextView) view1).getText().toString().trim());
                                break;
                            case R.id.tvrfidContainerIDs://RFID载体ID
                                params1.setRfidContainerID(((TextView) view1).getText().toString().trim());
                                break;
                            case R.id.tvauthorizationFlgs://是否需要授权标识
                                params1.setCode(((TextView) view1).getText().toString().trim());
                                break;
                        }
                    }
                }
                if (null != params1.getToolCode() && !"".equals(params1.getToolCode())) {
                    list.add(params1);
                }
            }
            Gson gosn = new Gson();
            Intent intent = new Intent(this, C01S019_002Activity.class);
            intent.putExtra(PARAM, gosn.toJson(list));
            startActivity(intent);
        }
    }

    //重写键盘上扫描按键的方法
    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan) {
            isCanScan = false;
        } else {
            return;
        }
        //扫描方法
        scan();
    }

}
