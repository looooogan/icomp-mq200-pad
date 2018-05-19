package com.icomp.Iswtmv10.v01c01.c01s008;
/**
 * 刀具拆分
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.icomp.Iswtmv10.v01c01.c01s005.c01s005_004_1Activity;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Request;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Response;
import com.icomp.Iswtmv10.v01c01.c01s010.C01S010_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s010.c01s010_003Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class C01S008_001Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.tvScan)
    TextView mTvScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private int position = 1;

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;

    private List<String> mList = new ArrayList<>();

    private HashMap<Integer, String> rfidMap = new HashMap<>();

    private Retrofit mRetrofit;
    private int rfidPosition = 0;
    private boolean isCanScan = true;

    private String codes = "";


    private Gson mGson;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s008_001);
        ButterKnife.bind(this);
        mGson = new Gson();
        initRetrofit();
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }


    @OnClick({R.id.tvScan, R.id.btnCancel, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvScan:
                isCanScan = false;
                scan();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                C01S008Request c01S008Request = bianli();
                if("".equals(c01S008Request.getRfidContainerIDs())){
                    createAlertDialog(this,"数据不能为空",Toast.LENGTH_SHORT);
                }else if(codes.contains("1")){
                    showAuthorizationWindow();
                }else{
                    requestDataForSave(c01S008Request);
                }
                break;
            default:
        }
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan){
            isCanScan = false;
        }else{
            return;
        }
        scan();
    }

    private boolean isCheckRfid(String card){
        if (rfidMap.containsValue(card)){
            return true;
        }
        return false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        rfidMap = new HashMap<>();
        Intent intent2 = getIntent();
        if(intent2 == null){
            return;
        }else{
            Bundle bundle = intent2.getExtras();
            if(bundle == null){
                return;
            }
            boolean isClear = bundle.getBoolean("isClear",false);
            if(isClear){
                position = 1;
                mList.clear();
                mLlContainer.removeAllViews();
            }
        }
    }
    /**
     * 数据提交请求
     * @param c01S008Request
     */
    private void requestDataForSave(final C01S008Request c01S008Request) {

        loading.show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveSynthesisToolSplit = iRequest.saveSynthesisToolSplit(c01S008Request.getSynthesisParametersCodes(),
                c01S008Request.getRfidContainerIDs(), c01S008Request.getToolConsumetypes(),
                customerID, codes, gruantUserID);
        saveSynthesisToolSplit.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(C01S008_001Activity.this,c01s008_002Activity.class);
                        intent.putExtra("TAG","1");//刀具拆分
                        intent.putExtra("json",c01S008Request.getSynthesisParametersCodes());
                        if(popupWindowInput != null && popupWindowInput.isShowing()){
                            popupWindowInput.dismiss();
                        }
                        if(popupWindowAuthorization != null && popupWindowAuthorization.isShowing()){
                            popupWindowAuthorization.dismiss();
                        }
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S008_001Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
//                    isCanScan = true;
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S008_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 开始扫描
     */
    private void scan(){
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }

    /**
     * 将扫描结果进行网络请求
     * @param code
     */
    private void requestData(final String code){
        if(isCheckRfid(code)){
            createAlertDialog(this,"已存在",Toast.LENGTH_SHORT);
            isCanScan = true;
            return;
        }
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getSynthesisToolSplit = iRequest.getSynthesisToolSplit(code);
        getSynthesisToolSplit.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                String re = response.body().toString();
                isCanScan = true;
                try {
                    final JSONObject jsonObject = new JSONObject(re);
                    if (jsonObject.getBoolean("success")){
                        final C01S008Response beanResponse = mGson.fromJson(re,C01S008Response.class);
                        if(!"0".equals(beanResponse.getData().getCode())){
                            AlertDialog.Builder builder = new AlertDialog.Builder(C01S008_001Activity.this);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage("重复组装拆分");
                            builder.setCancelable(false);
                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //将数据铺在画面上
                                    addLayout(beanResponse.getData().getSynthesisParametersCode(),
                                            beanResponse.getData().getRfidContainerID(),
                                            beanResponse.getData().getCreateType(),
                                            code,beanResponse.getData().getCode());
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int a) {
                                    dialogInterface.dismiss();

                                }
                            }).show();
                        }else{
                            addLayout(beanResponse.getData().getSynthesisParametersCode(),
                                    beanResponse.getData().getRfidContainerID(),
                                    beanResponse.getData().getCreateType(),
                                    code,
                                    beanResponse.getData().getCode());
                        }
                    }else{
                        createAlertDialog(C01S008_001Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                createAlertDialog(C01S008_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }


    /**
     * 添加布局
     */
    private void addLayout(String code, String rifd, String type,String card,String codes) {

        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_daojuchaifen, null);
        TextView tvNum = (TextView) mLinearLayout.findViewById(R.id.tvNum);
        TextView tvRfid = (TextView) mLinearLayout.findViewById(R.id.tvRfid);
        TextView tvType = (TextView) mLinearLayout.findViewById(R.id.tvtype);
        TextView tvCode = (TextView) mLinearLayout.findViewById(R.id.tvCode);
        tvCode.setText(codes);
        final TextView tvHeChengNum = (TextView) mLinearLayout.findViewById(R.id.tvHeChengNum);
        TextView tvDelete = (TextView) mLinearLayout.findViewById(R.id.tvDelete);
        tvHeChengNum.setTag(rfidPosition);
        rfidPosition++;
        rfidMap.put((Integer) tvHeChengNum.getTag(),card);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlContainer.removeView(mLinearLayout);
                rfidMap.remove(tvHeChengNum.getTag());
                position--;
                modifyData(position);
            }
        });
        tvNum.setText(""+position);
        tvHeChengNum.setText(code);
        tvRfid.setText(rifd);
        tvType.setText(type);
        mLlContainer.addView(mLinearLayout);
        position++;
    }

    /**
     * 修改数据
     */
    private void modifyData(int position){
        int modify = 1;
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvNum:
                                    ((TextView) child2).setText(""+modify);
                                    modify++;
                                    break;
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 遍历
     */
    private C01S008Request bianli(){
        mList.clear();
        codes = "";
        C01S008Request request = new C01S008Request();
        request.setRfidContainerIDs("");
        request.setSynthesisParametersCodes("");
        request.setToolConsumetypes("");
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvHeChengNum:
                                    mList.add(((TextView)child2).getText().toString());
                                    if (request.getSynthesisParametersCodes().equals("")){
                                        request.setSynthesisParametersCodes(((TextView)child2).getText().toString());
                                    }else{
                                        request.setSynthesisParametersCodes(request.getSynthesisParametersCodes()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                                case R.id.tvRfid:
                                    if("".equals(request.getRfidContainerIDs())){
                                        request.setRfidContainerIDs(((TextView)child2).getText().toString());
                                    }else{
                                        request.setRfidContainerIDs(request.getRfidContainerIDs()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                                case R.id.tvtype:
                                    if("".equals(request.getToolConsumetypes())){
                                        request.setToolConsumetypes(((TextView)child2).getText().toString());
                                    }else{
                                        request.setToolConsumetypes(request.getToolConsumetypes()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                                case R.id.tvCode:
                                    if(k == mLlContainer.getChildCount()-1){
                                        codes+=((TextView)child2).getText().toString();
                                    }else {
                                        codes+=((TextView)child2).getText().toString()+",";
                                    }break;
                            }
                        }
                    }
                }

            }
        }
        return  request;
    }

    //打开扫描窗体

    private void showWindow2(int pageID) {
        //在哪个页面打开窗口
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //用inflate方法调用界面
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        //从res下选择要作为背景的源文件
        vPopupWindow.setBackgroundResource(R.drawable.my_selectors);
        //设置源文件的长宽
        popupWindow2 = new PopupWindow(vPopupWindow, 300, 240);
        //从res下选择要作为背景的源文件
        //popupWindow2.setBackgroundDrawable(new BitmapDrawable());
        //设置弹框外部无法获取焦点
        popupWindow2.setFocusable(true);
        //设置popWindow的显示位置
        popupWindow2.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        //启动扫描线程
        thread = new VisitJniThread();
        thread.start();
    }

    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            scanf = true;
            //打开Rfid读头模块
            initRFID();
            //员工卡的RfidCode
            cardString = null;
            Date date = new Date();
            do {
                //读取员工卡的RfidCode
                cardString = readRfidString(false);
                //单扫设置十秒的等待时间
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    cardString = "close";
                }
            } while (cardString == null && scanf);
            //关闭扫描读头
            close();
            if (cardString != null) {
                scanf = false;
                //封装传递
                Message message = new Message();
                message.obj = cardString;
                scanfmhandler.sendMessage(message);
            }
        }

    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            popupWindow2.dismiss();
            //员工卡RfidCode
            cardString = msg.obj.toString();
            if ("close".equals(cardString)) {
                isCanScan = true;
                createAlertDialog(C01S008_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                requestData(cardString);
            }
        }
    };

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
    private String activityName = "C01S008_001Activity";
    //输入授权线程
    private inputAuthorizationThread inputAuthorizationThread;
    //扫描授权线程
    private scanAuthorizationThread scanAuthorizationThread;

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

                C01S008Request c01S008Request = bianli();
                requestDataForSave(c01S008Request);


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
