package com.icomp.Iswtmv10.v01c01.c01s019;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;

import org.json.JSONArray;
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
 * 厂外修磨页面2
 * Created by FanLL on 2017/7/4.
 */

public class C01S019_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.tv_02)
    TextView tv02;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.ll_02)
    LinearLayout ll02;

    //接受参数的JsonString
    private String jsonString;
    //接受上一个页面传递的List结果集
    private List<C01S019_001.DataBean> list = new ArrayList<>();
    //向后台提交数据的List结果集
    private List<C01S019_001.DataBean> list1 = new ArrayList<>();
    //修磨厂家列表
    private List<C01S019_002.DataBean> merchantsNameList;
    //传递给后台的参数：材料号、刀具ID、刀具类型、RFID载体ID、修磨数量、用户ID、商家ID以逗号拼接、是否需要授权标识的字符串
    private String toolCodes, toolIDs, toolTypes, rfidContainerIDs, numbers, authorizationFlgs;

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s019_002);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        try {
            jsonString = getIntent().getStringExtra(PARAM);
            Gson gson = new Gson();
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                //厂外修磨参数类
                C01S019_001.DataBean c01s019001 = gson.fromJson(jsonArray.getJSONObject(i).toString().trim(), C01S019_001.DataBean.class);
                if (0 == list.size()) {
                    list.add(c01s019001);
                } else {
                    //遍历列表，将相同材料号合在一起
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getToolCode().equals(c01s019001.getToolCode())) {
                            Integer grindingQuantity = Integer.parseInt(c01s019001.getGrindingQuantity()) + Integer.parseInt(list.get(j).getGrindingQuantity());
                            list.get(j).setGrindingQuantity(grindingQuantity.toString());
                            break;
                        } else {
                            if (j == list.size() - 1) {
                                list.add(c01s019001);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //将接受的数据显示在列表上
        for (int i = 0; i < list.size(); i++) {
            final View linearLayout = LayoutInflater.from(this).inflate(R.layout.item_c01s019_002, null);
            TextView tvmaterialNumber = (TextView) linearLayout.findViewById(R.id.tvmaterialNumber);//材料号
            tvmaterialNumber.setText(list.get(i).getToolCode());
            TextView tvgrindingQuantity = (TextView) linearLayout.findViewById(R.id.tvgrindingQuantity);//修磨数量
            tvgrindingQuantity.setText(list.get(i).getGrindingQuantity());
            TextView tvtoolIDs = (TextView) linearLayout.findViewById(R.id.tvtoolIDs);//刀具ID
            tvtoolIDs.setText(list.get(i).getToolID());
            TextView tvtoolTypes = (TextView) linearLayout.findViewById(R.id.tvtoolTypes);//刀具类型
            tvtoolTypes.setText(list.get(i).getToolType());
            TextView tvrfidContainerIDs = (TextView) linearLayout.findViewById(R.id.tvrfidContainerIDs);//RFID载体ID
            tvrfidContainerIDs.setText(list.get(i).getRfidContainerID());
            TextView tvauthorizationFlgs = (TextView) linearLayout.findViewById(R.id.tvauthorizationFlgs);//是否需要授权标识
            tvauthorizationFlgs.setText(list.get(i).getCode());
            ll01.addView(linearLayout);
        }
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        finish();
    }

    //确定按钮处理--跳转到下一页面
    public void btnConfirm(View view) {
        boolean repeat = false;
        for (int i = 0; i < ll01.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                View view1 = linearLayout.getChildAt(j);
                if (view1 instanceof TextView) {
                    switch (view1.getId()) {
                        case R.id.tvauthorizationFlgs://是否需要授权标识
                            if (((TextView) view1).getText().toString().trim().equals(ONE)) {
                                    repeat = true;
                                break;
                            }
                            break;
                    }
                }
            }
        }
        if (repeat) {
            //授权弹框
            showAuthorizationWindow();
        } else {
            //调用接口，提交厂外修磨信息
            upData();
        }
    }

    //调用接口，提交厂外修磨信息
    private void upData() {
        if ("".equals(tv02.getText().toString().trim())) {
            createAlertDialog(C01S019_002Activity.this, getString(R.string.c01s019_002_001), Toast.LENGTH_LONG);
        } else {
            loading.show();
            toolCodes = "";//材料号
            numbers = "";//修磨数量
            toolIDs = "";//刀具ID
            toolTypes = "";//刀具类型
            rfidContainerIDs = "";//RFID载体ID
            authorizationFlgs = "";//是否需要授权标识的字符串
            try {
                jsonString = getIntent().getStringExtra(PARAM);
                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    //厂外修磨参数类
                    C01S019_001.DataBean c01s019001 = gson.fromJson(jsonArray.getJSONObject(i).toString().trim(), C01S019_001.DataBean.class);
                    list1.add(c01s019001);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < list1.size(); i++) {
                if (i == list1.size() - 1) {
                    //拼接参数为String类型以","为间隔
                    toolCodes += list1.get(i).getToolCode().trim();//材料号
                    numbers += list1.get(i).getGrindingQuantity().trim();//修磨数量
                    toolIDs += list1.get(i).getToolID().trim();//刀具ID
                    toolTypes += list1.get(i).getToolType().trim();//刀具类型
                    rfidContainerIDs += list1.get(i).getRfidContainerID().trim();//RFID载体ID
                    authorizationFlgs += list1.get(i).getCode().trim();//是否需要授权标识
                } else {
                    //拼接参数为String类型以","为间隔
                    toolCodes += list1.get(i).getToolCode().trim() + ",";//材料号
                    numbers += list1.get(i).getGrindingQuantity().trim() + ",";//修磨数量
                    toolIDs += list1.get(i).getToolID().trim() + ",";//刀具ID
                    toolTypes += list1.get(i).getToolType().trim() + ",";//刀具类型
                    rfidContainerIDs += list1.get(i).getRfidContainerID().trim() + ",";//RFID载体ID
                    authorizationFlgs += list1.get(i).getCode().trim()+ ",";//是否需要授权标识
                }
            }
            //设定用户访问信息
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
            //用户ID
            String customerID = sharedPreferences.getString("customerID", "");
            //调用接口，提交厂外修磨信息
            IRequest iRequest = retrofit.create(IRequest.class);
            Call<String> saveOutFactoryToolInfo = iRequest.saveOutFactoryToolInfo(toolCodes, toolIDs, toolTypes, rfidContainerIDs, numbers, gruantUserID, tv02.getText().toString().trim(), authorizationFlgs);
            saveOutFactoryToolInfo.enqueue(new MyCallBack<String>() {
                @Override
                public void _onResponse(Response response) {
                    try {
                        String json = response.body().toString();
                        JSONObject jsonObject = new JSONObject(json);
                        if (jsonObject.getBoolean("success")) {
                            Intent intent = new Intent(C01S019_002Activity.this, C01S019_003Activity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            createAlertDialog(C01S019_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                    createAlertDialog(C01S019_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                }
            });
        }
    }


    @OnClick(R.id.ll_02)
    public void onViewClicked() {
        loading.show();
        //调用接口，查询厂外修复商家list
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getMerchantsList = iRequest.getMerchantsList();
        getMerchantsList.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                try {
                    String json = response.body();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        C01S019_002 c01s019002 = gson.fromJson(json, C01S019_002.class);
                        //显示修磨备列表
                        showPopupWindow(c01s019002.getData());
                    } else {
                        createAlertDialog(C01S019_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
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
                createAlertDialog(C01S019_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    //显示修磨备列表
    private void showPopupWindow(List<C01S019_002.DataBean> c01s019012DataBean) {
        merchantsNameList = c01s019012DataBean;
        View view = LayoutInflater.from(C01S019_002Activity.this).inflate(R.layout.spinner_c03s004_001, null);
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
                tv01.setText(merchantsNameList.get(i).getMerchantsName());
                tv02.setText(merchantsNameList.get(i).getMerchantsID());
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll02);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return merchantsNameList.size();
        }

        @Override
        public Object getItem(int i) {
            return merchantsNameList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(C01S019_002Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(merchantsNameList.get(i).getMerchantsName());
            return view1;
        }

    }

    //授权弹框
    private PopupWindow popupWindowAuthorization;
    //输入授权按钮、扫描授权按钮、返回按钮
    private Button btnInputAuthorization, btnScanAuthorization, btnReturnAuthorization;
    //输入授权弹框
    private PopupWindow popupWindowInput;
    //用户名、密码
    private String userName, passWord;
    //授权扫描的RfidString
    public String authorizationRfidString;
    //授权人ID
    private String gruantUserID;
    //授权参数类
    private UserRequest userRequest = null;
    private UserRespons userRespons = null;
    private C00S000Wsdl c00S000Wsdl = new C00S000Wsdl();
    //授权Activity
    private String activityName = "C01S018_002Activity";
    //输入授权线程
    private inputAuthorizationThread inputAuthorizationThread;
    //扫描授权线程
    private scanAuthorizationThread scanAuthorizationThread;

    //授权弹窗
    private void showAuthorizationWindow() {

        //设置显示弹框的参数
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.authorization, null);
        popupWindowAuthorization = new PopupWindow(view, (int) (0.8*screenWidth), (int) (0.6*screenHeight), true);
        popupWindowAuthorization.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);

        //输入授权按钮处理
        btnInputAuthorization = (Button) view.findViewById(R.id.btnInputAuthorization);
        btnInputAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowAuthorization.dismiss();
                //输入授权弹框
                showInputAuthorizationWindow();
            }
        });

        //扫描授权按钮处理
        btnScanAuthorization = (Button) view.findViewById(R.id.btnScanAuthorization);
        btnScanAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //扫描弹框
                scanAuthorization();
            }
        });

        //返回按钮处理
        btnReturnAuthorization = (Button) view.findViewById(R.id.btnReturnAuthorization);
        btnReturnAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //授权弹框消失
                popupWindowAuthorization.dismiss();
            }
        });

    }

    //输入授权弹框
    protected void showInputAuthorizationWindow() {

        //设置显示弹框的参数
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.c00s000_001_1activity, null);
        popupWindowInput = new PopupWindow(view, (int) (0.8*screenWidth), (int) (0.6*screenHeight), true);
        popupWindowInput.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);

        TextView btnTitle = (TextView) view.findViewById(R.id.btn_title);
        btnTitle.setText(R.string.inputAuthorization);
        //用户名
        final EditText etUserName = (EditText) view.findViewById(R.id.et_username);
        //将光标设置在最后
        etUserName.setSelection(etUserName.getText().length());
        userName = etUserName.getText().toString().trim();
        //密码
        final EditText etPassWord = (EditText) view.findViewById(R.id.et_password);
        //将光标设置在最后
        etPassWord.setSelection(etPassWord.getText().length());
        passWord = etPassWord.getText().toString().trim();
        LinearLayout llUser = (LinearLayout) view.findViewById(R.id.ll_user);
        llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/7/13
            }
        });
        LinearLayout llPassWord = (LinearLayout) view.findViewById(R.id.ll_lock);
        llPassWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/7/13
            }
        });
        //授权按钮
        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
        btnLogin.setText(R.string.authorization);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("".equals(etUserName.getText().toString().trim())) {
                    createAlertDialog(null, getString(R.string.pleaseInputUserName), Toast.LENGTH_LONG);
                } else if ("".equals(etPassWord.getText().toString().trim())) {
                    createAlertDialog(null, getString(R.string.pleaseInputPassWord), Toast.LENGTH_LONG);
                } else {
                    loading.show();
                    userName = etUserName.getText().toString().trim();
                    passWord = etPassWord.getText().toString().trim();
                    //开启输入授权线程
                    inputAuthorizationThread = new inputAuthorizationThread();
                    inputAuthorizationThread.start();
                }
            }
        });
        //重置按钮
        Button btnReset = (Button) view.findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUserName.setText("");
                userName = "";
                etPassWord.setText("");
                passWord = "";
            }
        });
        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //输入授权弹框消失，显示授权弹框
                popupWindowInput.dismiss();
                showAuthorizationWindow();
            }
        });
    }

    //输入授权线程
    protected class inputAuthorizationThread extends Thread {
        @Override
        public void run() {
            super.run();
            //设定用户访问信息
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
            //手持机ID
            String handSetId = sharedPreferences.getString("handsetid", "");
            userRequest = new UserRequest();
            userRequest.setHandSetId(handSetId);
            userRequest.setUserName(userName);
            userRequest.setUserPass(passWord);
            userRequest.setLoginType(ZERO);
            userRequest.setActivityName(activityName);
            Message message = new Message();
            try{
                //授权接口
                userRespons = c00S000Wsdl.userGruant(userRequest);
                if (null != userRespons) {
                    message.obj = userRespons;
                    //输入授权和扫描授权的handler
                    inputAuthorizationThreadHandler.sendMessage(message);
                } else {
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
            }
        }
    }

    //输入授权和扫描授权的handler
    @SuppressLint("HandlerLeak")
    Handler inputAuthorizationThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            if (null != loading && loading.isShowing()) {
                loading.dismiss();
            }
            userRespons =(UserRespons) msg.obj;
            if (ZERO.equals(userRespons.getStateCode())) {
                gruantUserID = userRespons.getCustomer().getCustomerID();
                loading.show();
                //调用接口--提交一体刀修磨信息的方法
                upData();
            } else {
                createAlertDialog(null, userRespons.getStateMsg(), Toast.LENGTH_LONG);
                btnScanAuthorization.setClickable(true);
            }
        }
    };

    //扫描授权弹框
    protected void scanAuthorization() {
        //点击扫描按钮以后，设置扫描按钮不可用
        btnScanAuthorization.setClickable(false);
        //显示扫描弹框的方法
        scanPopupWindow();
        //开启扫描授权线程
        scanAuthorizationThread = new scanAuthorizationThread();
        scanAuthorizationThread.start();
    }

    //扫描授权线程
    protected class scanAuthorizationThread extends Thread {
        @Override
        public void run() {
            //单扫方法
            authorizationRfidString = singleScan();
            Message message = new Message();
            if ("close".equals(authorizationRfidString)) {
                btnScanAuthorization.setClickable(true);
                overtimeHandler.sendMessage(message);
            } else if (null != authorizationRfidString && !"close".equals(authorizationRfidString)) {
                //设定用户访问信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                //手持机ID
                String handSetId = sharedPreferences.getString("handsetid", "");
                userRequest = new UserRequest();
                userRequest.setHandSetId(handSetId);
                userRequest.setEmployeeCard(authorizationRfidString);
                userRequest.setLoginType(ONE);
                userRequest.setActivityName(activityName);
                try{
                    //授权接口
                    userRespons = c00S000Wsdl.userGruant(userRequest);
                    if (null != userRespons) {
                        message.obj = userRespons;
                        //输入授权和扫描授权的handler
                        inputAuthorizationThreadHandler.sendMessage(message);
                    } else {
                        internetErrorHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    internetErrorHandler.sendMessage(message);
                    btnScanAuthorization.setClickable(true);
                }
            }
        }
    }

}
