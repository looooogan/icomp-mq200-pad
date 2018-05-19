package com.icomp.Iswtmv10.v01c01.c01s010;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.icomp.Iswtmv10.v01c01.c01s010.modul.HuanZhuangModul;
import com.icomp.common.activity.CommonActivity;
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
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class c01s010_003Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnConfirm)
    Button mBtnConfirm;
    private List<HuanZhuangModul> mJsonList = new ArrayList<>();
    private String jsonData;
    private String toolsCodes = "";
    private String changeNumbers = "";
    private String lostNumbers = "";
    private Retrofit mRetrofit;

    private String synthesisParametersCodes = "";  //合成刀具编码s
    private String rfidContainerIDs = ""; //RFID载体ID
    private String authorizationFlgs = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s010_003);
        ButterKnife.bind(this);
        initRetrofit();
        initView();
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    private void initView() {
        jsonData = getIntent().getStringExtra("json");
        synthesisParametersCodes = getIntent().getStringExtra("synthesisParametersCodes");
        rfidContainerIDs = getIntent().getStringExtra("rfidContainerIDs");
        authorizationFlgs = getIntent().getStringExtra("authorizationFlgs");
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++){
                HuanZhuangModul h = gson.fromJson(jsonArray.getJSONObject(i).toString(),HuanZhuangModul.class);
                mJsonList.add(h);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0;i<mJsonList.size();i++){
            if(i != mJsonList.size()-1){
                    toolsCodes = toolsCodes+mJsonList.get(i).getCaiLiaoHao()+",";
                    changeNumbers = changeNumbers+mJsonList.get(i).getHuanzhuangNum()+",";
                    lostNumbers = lostNumbers+mJsonList.get(i).getDiudaoNum()+",";
                }else{
                    toolsCodes = toolsCodes+mJsonList.get(i).getCaiLiaoHao();
                    changeNumbers = changeNumbers+mJsonList.get(i).getHuanzhuangNum();
                    lostNumbers = lostNumbers+mJsonList.get(i).getDiudaoNum();
                }
        }


        for (int i = 0; i<mJsonList.size();i++){
            addLayout(i);
        }
    }

    @OnClick({R.id.btnReturn, R.id.btnConfirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReturn:
                finish();
                break;
            case R.id.btnConfirm:
//                requestData();
                if(authorizationFlgs.contains("1")){
                    showAuthorizationWindow();
                }else{
                    requestData();
                }
                break;
        }
    }

    private void requestData() {
        if (toolsCodes.equals("") || changeNumbers.equals("") || lostNumbers.equals("")){
            createAlertDialog(c01s010_003Activity.this,"数据出错", Toast.LENGTH_SHORT);
            return;
        }
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
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s010_003Activity.this,c01s010_004Activity.class);

                        startActivity(intent);
                    }else{

                        createAlertDialog(c01s010_003Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s010_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });


    }

    /**
     * 添加布局
     */
    private void addLayout(int i) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_huanzhuang_static, null);
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvDaoJuType = (TextView) mLinearLayout.findViewById(R.id.tvDaoJuType);
        TextView tvDiuDaoNum = (TextView) mLinearLayout.findViewById(R.id.tvDiuDaoNum);
        TextView tvHuanzhuangNum = (TextView) mLinearLayout.findViewById(R.id.tvHuanzhuangNum);
        TextView tvDaoJuNum = (TextView) mLinearLayout.findViewById(R.id.tvDaoJuNum);

        tvCailiao.setText(mJsonList.get(i).getCaiLiaoHao());
        tvDaoJuType.setText(mJsonList.get(i).getType());
        tvDaoJuNum.setText(String.valueOf(mJsonList.get(i).getNum()));
        tvDiuDaoNum.setText(String.valueOf(mJsonList.get(i).getDiudaoNum()));
        tvHuanzhuangNum.setText(String.valueOf(mJsonList.get(i).getHuanzhuangNum()));
        mLlContainer.addView(mLinearLayout);
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
                loading.show();
                //调用接口--提交一体刀修磨信息的方法

                requestData();


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
                    btnScanAuthorization.setClickable(true);
                }
            }
        }
    }

}
