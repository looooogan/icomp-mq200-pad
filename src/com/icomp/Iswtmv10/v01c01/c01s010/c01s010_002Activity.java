package com.icomp.Iswtmv10.v01c01.c01s010;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c01.c01s010.modul.HuanZhuangModul;
import com.icomp.Iswtmv10.v01c01.c01s010.modul.HuanZhuangResponse;
import com.icomp.common.activity.CommonActivity;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;

import com.ta.utdid2.android.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class c01s010_002Activity extends CommonActivity {

//    @BindView(R.id.llContainer)
//    LinearLayout mLlContainer;
    @BindView(R.id.tlContainer)
    LinearLayout mTlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnNext)
    Button mBtnNext;

    private String jsonData;
    private List<HuanZhuangModul> jsonList = new ArrayList<>();
    private List<String> ridfList = new ArrayList<>();
    private String spCodes = "";
    private Retrofit mRetrofit;
    private HuanZhuangResponse bean ;
    private String toolsCodes = "";
    private String changeNumbers = "";
    private String lostNumbers = "";
    private String synthesisParametersCodes = "";  //合成刀具编码s
    private String rfidContainerIDs = ""; //RFID载体ID
    private String authorizationFlgs = "";

    private Map<String,Integer> zzTools = new HashMap<>();
    private Map<String,Integer> ddTools = new HashMap<>();
    private Map<String,Integer> configTools = new HashMap<>();
    private Map<String,String> repalceStr = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s010_002);
        ButterKnife.bind(this);
        initRetrofit();
        initView();
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    /**
     * 初始化数据视图
     */
    private void initView() {
        jsonData = getIntent().getStringExtra("json");

        synthesisParametersCodes = getIntent().getStringExtra("synthesisParametersCodes");
        rfidContainerIDs = getIntent().getStringExtra("rfidContainerIDs");
        authorizationFlgs = getIntent().getStringExtra("authorizationFlgs");
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++){
                String h = jsonArray.getString(i);
                ridfList.add(h);
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        for (int i = 0;i<ridfList.size();i++){
            if(i != ridfList.size()-1){
                spCodes = spCodes+ridfList.get(i)+",";
            }else{
                spCodes = spCodes+ridfList.get(i);
            }
        }
        requestData(spCodes);
    }

    private void requestData(String card){
        loading.show();
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getSynthesisTool = iRequest.getSynthesisTool(card);
        getSynthesisTool.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                String re = response.body();
                try {
                    JSONObject jsonObject = new JSONObject(re);
                    if (jsonObject.getBoolean("success")){
                        Gson gson = new Gson();
                        bean = gson.fromJson(re,HuanZhuangResponse.class);
                        for (int i = 0;i<bean.getData().size();i++){
                            addLayout(i);
                        }
                    }else{
                        createAlertDialog(c01s010_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s010_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }


    @OnClick({R.id.btnReturn, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnNext:
//                bianliAndToJson();
                initData();
                if(authorizationFlgs.contains("1")){
                    showAuthorizationWindow();
                }else{
                    a11111();
                }
//                for (int i = 0; i < jsonList.size(); i++) {
//                    if (jsonList.get(i).getDiudaoNum() + jsonList.get(i).getHuanzhuangNum() > jsonList.get(i).getNum()) {
//                        createAlertDialog(c01s010_002Activity.this,"请确认换刀与丢刀的数量",Toast.LENGTH_SHORT);
//                        break;
//                    } else if (i == jsonList.size()-1) {
//                        if(authorizationFlgs.contains("1")){
//                            showAuthorizationWindow();
//                        }else{
////                            requestData1();
//                            a11111();
//                        }
//
//                    }
//
//                }
//                String json = bianliAndToJson();
//                if(json == null){
//                    return;
//                }else{
//                    Intent intent = new Intent(this,c01s010_003Activity.class);
//                    intent.putExtra("json",json);
//                    intent.putExtra("synthesisParametersCodes",synthesisParametersCodes);
//                    intent.putExtra("rfidContainerIDs",rfidContainerIDs);
//                    intent.putExtra("authorizationFlgs",authorizationFlgs);
//                    startActivity(intent);
//                }
                break;
            default:
        }
    }


    //授权弹框
    PopupWindow popupWindowAuthorization;
    //输入授权按钮、扫描授权按钮、返回按钮
    Button btnInputAuthorization, btnScanAuthorization, btnReturnAuthorization;
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
    private String activityName = "C01S010_001Activity";
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
        popupWindowAuthorization.setOutsideTouchable(true);

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
                if (popupWindowAuthorization != null && popupWindowAuthorization.isShowing()) {
                    //授权弹框消失
                    popupWindowAuthorization.dismiss();
                }
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
            try{
                //授权接口
                userRespons = c00S000Wsdl.userGruant(userRequest);
                Message message = new Message();
                if (null != userRespons) {
                    message.obj = userRespons;
                    //输入授权和扫描授权的handler
                    inputAuthorizationThreadHandler.sendMessage(message);
                } else {
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
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
                //R&D
//                requestData1();
                a11111();
            } else {
                createAlertDialog(null, userRespons.getStateMsg(), Toast.LENGTH_LONG);
                if (null != popupWindowAuthorization && popupWindowAuthorization.isShowing()) {
                    popupWindowAuthorization.dismiss();
                }
                if (null != popupWindowInput && popupWindowInput.isShowing()) {
                    popupWindowInput.dismiss();
                }
                if (null != popupWindow && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                btnScanAuthorization.setClickable(true);
            }
        }
    };



    private void requestData1() {
        toolsCodes = "";
        changeNumbers = "";
        lostNumbers = "";
        for (int k = 0; k<mTlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mTlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    if(k != mTlContainer.getChildCount()-1){
                                        toolsCodes += ((TextView) child2).getText().toString()+",";
                                    }else{
                                        toolsCodes += ((TextView) child2).getText().toString();
                                    }
                                    break;
                                default:
                            }

                        }
                        if(child2 instanceof EditText){
                            switch (child2.getId()){
                                case R.id.etHuanzhuangNum:
                                    if(k != mTlContainer.getChildCount()-1){
                                        if ("".equals(((TextView) child2).getText().toString())) {
                                            changeNumbers = ((TextView) child2).getText().toString()+","+ "0";
                                        } else {
                                            changeNumbers += ((TextView) child2).getText().toString()+",";
                                        }
                                    }else{
                                        if ("".equals(((TextView) child2).getText().toString())) {
                                            changeNumbers = ((TextView) child2).getText().toString()+ "0";
                                        } else {
                                            changeNumbers += ((TextView) child2).getText().toString();
                                        }
                                    }
                                    break;
                                case R.id.etDiuDaoNum:
                                    if(k != mTlContainer.getChildCount()-1){
                                        if ("".equals(((TextView) child2).getText().toString())) {
                                            lostNumbers = ((TextView) child2).getText().toString()+","+ "0";
                                        } else {
                                            lostNumbers += ((TextView) child2).getText().toString()+",";
                                        }
                                    }else{
                                        if ("".equals(((TextView) child2).getText().toString())) {
                                            lostNumbers = ((TextView) child2).getText().toString()+ "0";
                                        } else {
                                            lostNumbers += ((TextView) child2).getText().toString();
                                        }
                                    }

                                    break;
                                default:
                            }

                        }
                    }
                }

            }
        }

        if ("".equals(toolsCodes) || "".equals(changeNumbers) || "".equals(lostNumbers)){
            createAlertDialog(c01s010_002Activity.this,"数据出错", Toast.LENGTH_SHORT);
            return;
        } else {
            //调用接口，提交数据
            a11111();
        }

    }

    public void  dataHandler(){
        for (String s : zzTools.keySet()) {
            if (zzTools.get(s)>0){
                if (changeNumbers.length()>0){
                    toolsCodes+=",";
                    changeNumbers+=",";
                }
                toolsCodes+=s;
                changeNumbers+=",";
            }
        }

    }

    //调用接口，提交数据
    private void a11111() {
        loading.show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveHToolInfo = iRequest.saveHToolInfo(toolsCodes,changeNumbers,lostNumbers,customerID,
                synthesisParametersCodes,rfidContainerIDs,authorizationFlgs, gruantUserID);

        saveHToolInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                loading.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s010_002Activity.this,c01s010_004Activity.class);

                        startActivity(intent);
                    }else{

                        createAlertDialog(c01s010_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s010_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

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
                    btnScanAuthorization.setClickable(true);
                }
            }
        }
    }

    /**
     * 添加布局2
     */
    @android.support.annotation.IdRes
    int tvCailiao = 1000;
    int tvDaoJuType = 1001;
    int tvDaoJuNum = 1002;
    int tvZuzhuangNum = 1004;
    int tvDiudaoNum = 1003;


    private void addLayout(int i) {

        HuanZhuangResponse.DataBean dataBean = bean.getData().get(i);
        String tvDaoJuTypeText = "";
        String etHuanzhuangNumText = "";
        String etDiuDaoNumText  = "";
        boolean exchangeable = true;
        configTools.put(dataBean.getToolCode(),dataBean.getToolCount());
        if (!StringUtils.isEmpty(dataBean.getTempToolCode())){
            repalceStr.put(dataBean.getToolCode(),dataBean.getTempToolCode());
            String [] strs = dataBean.getTempToolCode().split(",");
            for (String str : strs) {
                zzTools.put(str,0);
                ddTools.put(str,0);
            }
        }

        if ("0".equals(bean.getData().get(i).getToolConsumetype()) || bean.getData().get(i).getToolConsumetype().equals(R.string.c03s001_002_001)) {
            tvDaoJuTypeText = "可刃磨钻头";
            //换装数量
//            etHuanzhuangNumText = String.valueOf(bean.getData().get(i).getToolCount());
            etHuanzhuangNumText = "0";
            //etDiuDaoNum
            etDiuDaoNumText = "0";
        } else if (bean.getData().get(i).getToolConsumetype().equals("1") || bean.getData().get(i).getToolConsumetype().equals(R.string.c03s001_002_002)) {
            tvDaoJuTypeText = "可刃磨刀片";
            //换装数量
            etHuanzhuangNumText = String.valueOf(bean.getData().get(i).getToolCount());
            //etDiuDaoNum
            etDiuDaoNumText = "0";
        } else if (bean.getData().get(i).getToolConsumetype().equals("2") || bean.getData().get(i).getToolConsumetype().equals(R.string.c03s001_002_003)) {
            tvDaoJuTypeText = "一次性刀片";
            //换装数量
            etHuanzhuangNumText = "0";
            //etDiuDaoNum
            etDiuDaoNumText = "0";
        } else if (bean.getData().get(i).getToolConsumetype().equals("9") || bean.getData().get(i).getToolConsumetype().equals("辅具") ||
                bean.getData().get(i).getToolConsumetype().equals("其他") || bean.getData().get(i).getToolConsumetype().equals("其它")) {
            //不让辅具换装
            exchangeable = false;
            tvDaoJuTypeText = "辅具";
            //换装数量
            etHuanzhuangNumText = "0";
            //etDiuDaoNum
            etDiuDaoNumText = "0";
        } else {
            tvDaoJuTypeText = bean.getData().get(i).getToolConsumetype();
        }

        ViewGroup.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);

        TableRow.LayoutParams param3 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);


        // 行
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(param);
        tableRow.setBackgroundResource(R.drawable.table_border_c);


        // 内部table1
        TableLayout tableLayout1 = new TableLayout(this);
        tableLayout1.setLayoutParams(param2);
        tableLayout1.addView(getRow(tvCailiao, dataBean.getToolCode()));

        String [] repalceTools = null;
        if (!StringUtils.isEmpty(dataBean.getTempToolCode())){
            repalceTools = dataBean.getTempToolCode().split(",");
            for (String repalceTool : repalceTools) {
                tableLayout1.addView(getRow(tvCailiao, repalceTool));
            }

        }
        // 添加到行中
        tableRow.addView(tableLayout1);
        tableRow.addView(getImage());


        // 内部table2
        TableLayout tableLayout2 = new TableLayout(this);
        tableLayout2.setLayoutParams(param2);
        tableLayout2.addView(getRow(tvDaoJuType, tvDaoJuTypeText));
        if (null!= repalceTools){
            tableLayout2.addView(getRow(tvDaoJuType, tvDaoJuTypeText));
        }
        // 添加到行中
        tableRow.addView(tableLayout2);
        tableRow.addView(getImage());


        TextView tv1 = new TextView(this);
        tv1.setId(tvDaoJuNum);
        tv1.setLayoutParams(param3);
        tv1.setGravity(Gravity.CENTER);
        tv1.setText(dataBean.getToolCount()+"");//总数量


        // 添加到行中
        tableRow.addView(tv1);
        tableRow.addView(getImage());


        // 内部table3
        TableLayout tableLayout3 = new TableLayout(this);
        tableLayout3.setLayoutParams(param2);
        tableLayout3.addView(getRowEdit(tvDaoJuNum, etHuanzhuangNumText, exchangeable, dataBean.getToolCode()));
        if (null!=repalceTools){
            for (String repalceTool : repalceTools) {
                tableLayout3.addView(getRowEdit(tvDaoJuNum, etHuanzhuangNumText, exchangeable,repalceTool));
            }
        }
        // 添加到行中
        tableRow.addView(tableLayout3);
        tableRow.addView(getImage());

        // 内部table4
        TableLayout tableLayout4 = new TableLayout(this);
        tableLayout4.setLayoutParams(param2);
        tableLayout4.addView(getRowEdit(tvDiudaoNum, "0", exchangeable, dataBean.getToolCode()));
        if (null!=repalceTools){
            for (String repalceTool : repalceTools) {
                tableLayout4.addView(getRowEdit(tvZuzhuangNum, etHuanzhuangNumText, exchangeable,repalceTool));
            }
        }
        // 添加到行中
        tableRow.addView(tableLayout4);
        mTlContainer.addView(tableRow);
    }


    private TableRow getRow(int id, String text) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())), 1f);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(param);

        TextView tv1 = new TextView(this);
        tv1.setLayoutParams(param2);
        tv1.setGravity(Gravity.CENTER);
        tv1.setId(id);
        tv1.setText(text);

        tableRow.addView(tv1);

        return tableRow;
    }

    private TableRow getRowEdit(int id, String text, boolean isZuanTou, final String cailiao) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics())), 1f);

        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(param);
        tableRow.setFocusable(true);
        tableRow.setFocusableInTouchMode(true);


        final EditText et1 = new EditText(this);
        et1.setLayoutParams(param2);
        et1.setGravity(Gravity.CENTER);
        et1.setId(id);
        et1.setText(text);
        et1.setInputType(InputType.TYPE_CLASS_NUMBER);
//        if (!isZuanTou) {
        if (1==2) {
            et1.setFocusable(false);
            et1.setFocusableInTouchMode(false);
        } else {
            et1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //输入文本之前的状态
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //输入文字中的状态，count是输入字符数
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (et1.getText() != null && !"".equals(et1.getText().toString())) {
                        //输入文字后的状态
                        Log.i("ceshi", et1.getText().toString());
                        if (et1.getId() == tvDaoJuNum){
                            zzTools.put(cailiao,Integer.parseInt(et1.getText().toString()));
                        }
                        if (et1.getId() == tvDiudaoNum){
                            ddTools.put(cailiao,Integer.parseInt(et1.getText().toString()));
                        }
                    }
                }
            });
        }

        tableRow.addView(et1);

        return tableRow;
    }

    private ImageView getImage() {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
//                getResources().getDimensionPixelOffset(R.dimen.image_height),// 设置1dp宽度
                ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics())),
                ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(param);
        imageView.setBackgroundResource(R.color.baseColor);

        return imageView;
    }

    /**
     * 遍历所有数据并转化为json
     */
    private boolean checkIsExit(String code,String num){
        if(mTlContainer.getChildCount() == 0){
            return false;
        }
        for (int k = 0; k<mTlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mTlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    if(((TextView) child2).getText().toString().equals(code)){
                                        return true;
                                    }
                                    break;
                                default:
                            }

                        }
                    }
                }

            }
        }
        return false;
    }

    /**
     * 捏合数据
     * @param code
     * @param num
     * @return
     */
    private void addData(String code,int num){
        boolean isAdd = false;
        for (int k = 0; k<mTlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mTlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
//                            c.setCaiLiao(((EditText) child2).getText().toString());
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    if(((TextView) child2).getText().toString().equals(code)){
                                        isAdd = true;
                                    }
                                    break;
                                case R.id.tvDaoJuNum:
                                    if(isAdd){
                                        int numOld = Integer.parseInt(String.valueOf(((TextView) child2).getText().toString()));
                                        int zong = numOld+num;
                                        ((TextView) child2).setText(String.valueOf(zong));
                                        isAdd = false;
                                        return;
                                    }
                                    break;
                                default:
                            }

                        }
                    }
                }

            }
        }
    }

    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        jsonList.clear();
        if(mTlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mTlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mTlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    HuanZhuangModul c = new HuanZhuangModul();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);

                        if(child2 instanceof EditText){
                            switch (child2.getId()){
                                case R.id.etDiuDaoNum:
                                    if(((EditText)child2).getText().toString().equals("")){
                                        c.setDiudaoNum(0);
                                    }else{
                                        c.setDiudaoNum(Integer.parseInt(((EditText)child2).getText().toString()));
                                    }
                                    break;
                                case R.id.etHuanzhuangNum:
                                    if(((EditText)child2).getText().toString().equals("")){
                                        c.setDiudaoNum(0);
                                    }else{
                                        c.setHuanzhuangNum(Integer.parseInt(((EditText)child2).getText().toString()));
                                    }
                                    break;
                                default:
                            }
                        }else if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    c.setCaiLiaoHao(((TextView)child2).getText().toString()) ;
                                    break;
                                case R.id.tvDaoJuType:
                                    c.setType(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvDaoJuNum:
                                    c.setNum(Integer.parseInt(((TextView)child2).getText().toString()));
                                    break;
                                default:
                            }
                        }
//                        if(j == child2Coutn-1){
//                            int diudao = 0;
//                            int huanzhuang = 0;
//                            int daoju = 0;
//                            diudao = c.getDiudaoNum();
//                            huanzhuang = c.getHuanzhuangNum();
//                            daoju = c.getNum();
//                            if (daoju < (huanzhuang + diudao)) {
//                                createAlertDialog(c01s010_002Activity.this,"请确认换刀与丢刀的数量",Toast.LENGTH_SHORT);
//                                return null;
//                            } else if(authorizationFlgs.contains("1")){
//                                    showAuthorizationWindow();
//                                }else{
//                                    requestData1();
//                                }
//                              }


                    }
                    jsonList.add(c);


                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(jsonList);
    }

    public void cancle(View view) {
        popupWindowAuthorization.dismiss();
    }

    public void initData(){
        jsonList.clear();
        for (String s : configTools.keySet()) {
            int config = configTools.get(s);
            int zz = zzTools.get(s) == null ?0:zzTools.get(s);
            int dd = ddTools.get(s) == null?0:ddTools.get(s);
            if (!StringUtils.isEmpty(repalceStr.get(s))){
                String [] strs = repalceStr.get(s).split(",");
                for (String str : strs) {
                    zz+= zzTools.get(str) == null ?0:zzTools.get(str);
                    dd+= ddTools.get(str) == null?0:ddTools.get(str);
                }
            }
            if (config!=zz+dd){
                createAlertDialog(c01s010_002Activity.this,"请确认换刀与丢刀的数量",Toast.LENGTH_SHORT);
                return;
            }
        }

        for (String s : zzTools.keySet()) {

            if (toolsCodes.length()>0){
                toolsCodes+=",";
            }
            toolsCodes+=s;
            if (changeNumbers.length()>0){
                changeNumbers+=",";
            }
            changeNumbers+=zzTools.get(s);
            if (lostNumbers.length()>0){
                lostNumbers+=",";
            }
            lostNumbers+=ddTools.get(s)==null?0:ddTools.get(s);
            if (configTools.get(s) == null){
                for (String s1 : repalceStr.keySet()) {
                    if (repalceStr.get(s1).indexOf(s)>0){
                        //c.setNum(configTools.get(s1));
                    }
                }
            }else{
//                c.setNum(configTools.get(s));
            }

//            jsonList.add(c);
        }
    }
}
