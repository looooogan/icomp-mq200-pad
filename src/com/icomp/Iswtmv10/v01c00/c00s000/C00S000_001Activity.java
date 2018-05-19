package com.icomp.Iswtmv10.v01c00.c00s000;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.common.utils.UpdateManager;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 登录页面1
 * Created by FanLL on 2017/8/10.
 */

public class C00S000_001Activity extends CommonActivity {

    @BindView(R.id.btnInputLogin)
    Button btnInputLogin;
    @BindView(R.id.btnScanLogin)
    Button btnScanLogin;

    UpdateManager updateManager;
    //用户名、密码
    private String userName, passWord;
    //输入登录弹框
    private PopupWindow popupWindowInput;
    //检测软件更新的线程
    private visitJinThread visitJinThread;
    //输入登录线程
    private inputAuthorizationThread inputAuthorizationThread;
    //扫描登录的线程
    private scanLoginThread scanLoginThread;

    //授权参数类
    private UserRequest userRequest = null;
    private UserRespons userRespons = null;
    private C00S000Wsdl c00S000Wsdl = new C00S000Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c00s000_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //开启检测软件更新的线程
        visitJinThread = new visitJinThread();
        updateManager = new UpdateManager(C00S000_001Activity.this);
        visitJinThread.start();
    }

    @OnClick({R.id.btnInputLogin, R.id.btnScanLogin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnInputLogin:
                //点击输入登录按钮
                showInputLogin();
                break;
            case R.id.btnScanLogin:
                //点击扫描登录按钮
                showScanLogin();
                break;
            default:
        }
    }

    //检测更新版本的线程
    private class visitJinThread extends Thread {
        @Override
        public void run() {
            super.run();
            Message message = new Message();
            updateManager.checkUpdate();
            visitJinHandler.sendMessage(message);
        }
    }

    //检测更新版本的Handler
    @SuppressLint("HandlerLeak")
    private Handler visitJinHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (updateManager.isUpdateFlag()) {
                updateManager.show();
            }
        }
    };

    //点击输入登录按钮的方法
    private void showInputLogin() {
        //设置显示弹框的参数
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.c00s000_001_1activity, null);
        popupWindowInput = new PopupWindow(view, (int) (0.8*screenWidth), (int) (0.6*screenHeight), true);
        popupWindowInput.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
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
        //登录按钮
        Button btnLogin = (Button) view.findViewById(R.id.btn_login);
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
        //小圆×的点击处理
        ImageView ivCancel = (ImageView) view.findViewById(R.id.iv_cancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //输入授权弹框消失，显示授权弹框
                popupWindowInput.dismiss();
            }
        });
    }

    //输入登录线程
    protected class inputAuthorizationThread extends Thread {
        @Override
        public void run() {
            super.run();
            Message message = new Message();
            userRequest = new UserRequest();
            userRequest.setUserName(userName);//用户名
            userRequest.setUserPass(passWord);//密码
            userRequest.setLoginType(ZERO);//登录方式--输入登录
            try {
                //输入登录的接口
                userRespons = c00S000Wsdl.userLogin(userRequest);
                if (null != userRespons) {
                    message.obj = userRespons;
                    scanLoginHandler.sendMessage(message);
                } else {
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                if(loading.isShowing()) {
                    loading.dismiss();
                }
                internetErrorHandler.sendMessage(message);
                e.printStackTrace();
            }
        }
    }

    //点击扫描登录按钮的方法
    private void showScanLogin() {
        btnScanLogin.setClickable(false);
        //显示扫描弹框的方法
        scanPopupWindow();
        //扫描登录的线程
        scanLoginThread = new scanLoginThread();
        scanLoginThread.start();
    }

    //扫描登录的线程
    private class scanLoginThread extends Thread {
        @Override
        public void run() {
            super.run();
            //调用单扫方法
            rfidString = singleScan();
            if ("close".equals(rfidString)) {
                btnScanLogin.setClickable(true);
                Message message = new Message();
                overtimeHandler.sendMessage(message);
            } else if (null != rfidString && !"close".equals(rfidString)) {
                Message message = new Message();
                userRequest = new UserRequest();
                userRequest.setLoginType(ONE);//登录方式--扫卡登陆
                userRequest.setEmployeeCard(rfidString);//员工卡号
                try {
                    //输入登录的接口
                    userRespons = c00S000Wsdl.userLogin(userRequest);
                    if (null != userRespons) {
                        message.obj = userRespons;
                        scanLoginHandler.sendMessage(message);
                    } else {
                        btnScanLogin.setClickable(true);
                        internetErrorHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    btnScanLogin.setClickable(true);
                }
            }
        }
    }

    //扫描登录的Handler
    @SuppressLint("HandlerLeak")
    private Handler scanLoginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            if (null != loading && loading.isShowing()) {
                loading.dismiss();
            }
            try {
                userRespons = (UserRespons) msg.obj;
                //判断接口是否调用成功（0--成功，1--失败）
                if (ZERO.equals(userRespons.getStateCode())) {
                    //设定用户访问信息
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                    //新建编辑器
                    SharedPreferences.Editor editer = sharedPreferences.edit();
                    editer.putString("userName", userRespons.getCustomer().getUserName());//用户姓名
                    editer.putString("customerID", userRespons.getCustomer().getCustomerID());//customerID
                    editer.putString("langCode", userRespons.getLanguagetable().getLanguageCode());//语言
                    editer.putString("langValue", userRespons.getLanguagetable().getLanguageValue());
                    editer.putString("name", userRespons.getUserName());//真实姓名
                    editer.commit();//提交修改
                    //扫描登录成功，跳转到系统菜单页面
                    Intent intent = new Intent(C00S000_001Activity.this, C00S000_002Activity.class);
                    startActivity(intent);
                    SysApplication.getInstance().exit();
//                    finish();
                } else {
                    createAlertDialog(C00S000_001Activity.this, userRespons.getStateMsg(), Toast.LENGTH_LONG);
                    btnScanLogin.setClickable(true);
                }
            } catch (Exception e) {
                btnScanLogin.setClickable(true);
                e.printStackTrace();
            }
        }
    };

}
