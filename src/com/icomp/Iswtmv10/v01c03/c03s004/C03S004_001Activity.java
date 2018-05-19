package com.icomp.Iswtmv10.v01c03.c03s004;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c03.c03s000.C03S000_003Activity;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;
import com.icomp.wsdl.v01c03.c03s002.C03S002Wsdl;
import com.icomp.wsdl.v01c03.c03s002.endpoint.C03S002Request;
import com.icomp.wsdl.v01c03.c03s002.endpoint.C03S002Respons;
import com.icomp.wsdl.v01c03.c03s002.endpoint.Equipments;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 修磨设备初始化1
 * Created by FanLL on 2017/6/29.
 */

public class C03S004_001Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.btnScan)
    Button btnScan;

    //获取修磨设备列表的线程
    private findGrindEquipmentThread findGrindEquipmentThread;
    //刃磨设备列表
    private List<Equipments> list;
    //显示修磨备列表
    private List<String> listString = new ArrayList<>();
    //设备ID
    private String equipmentID;
    //扫描线程
    private scanThread scanThread;

    //设备初始化参数类
    private C03S002Request request = new C03S002Request();
    private C03S002Respons respons = new C03S002Respons();
    private C03S002Wsdl wsdl = new C03S002Wsdl();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s004_001);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        loading.show();
        //获取修磨设备列表的线程
        findGrindEquipmentThread = new findGrindEquipmentThread();
        findGrindEquipmentThread.start();
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //返回按钮处理--返回设备初始化菜单页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        Intent intent = new Intent(this, C03S000_003Activity.class);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.ll_01, R.id.btnScan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //点击下拉列表处理
            case R.id.ll_01:
                //显示修磨设备列表
                showPopupWindow();
                break;
            //扫描按钮处理
            case R.id.btnScan:
                //扫描方法
                scan();
                break;
        }
    }

    //获取修磨设备列表的线程
    private class findGrindEquipmentThread extends Thread {
        @Override
        public void run() {
            super.run();
            //设定用户访问信息
            SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
            //用户ID
            String customerID = sharedPreferences.getString("customerID", "");
            request.setCustomerID(customerID);
            //手持机ID
            String handSetId = sharedPreferences.getString("handsetid", "");
            request.setHandSetId(handSetId);
            Message message = new Message();
            try {
                //获取修磨设备列表
                respons = wsdl.findGrindEquipment(request);
                if (null != respons) {
                    message.obj = respons;
                    findGrindEquipmentHandler.sendMessage(message);
                } else {
                    btnScan.setClickable(true);
                    isCanScan = true;
                    ll01.setClickable(true);
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
                ll01.setClickable(true);
            }
        }
    }

    //获取修磨设备列表的Handler
    @SuppressLint("HandlerLeak")
    public Handler findGrindEquipmentHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loading.dismiss();
            respons = (C03S002Respons) msg.obj;
            if (ZERO.equals(respons.getStateCode())) {
                if (null == respons.getNocequipmentList() || respons.getNocequipmentList().size() == 0) {
                    new AlertDialog.Builder(C03S004_001Activity.this).
                            setTitle(R.string.prompt).
                            setMessage(R.string.c03s004_001_002).
                            setCancelable(false).
                            setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //返回设备初始化菜单页面
                                    Intent intent = new Intent(C03S004_001Activity.this, C03S000_003Activity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).show();
                } else {
                    for (int i = 0; i < respons.getNocequipmentList().size(); i++) {
                        listString.add(respons.getNocequipmentList().get(i).getEquipmentName());
                    }
                    list = respons.getNocequipmentList();
                }
            } else {
                createAlertDialog(C03S004_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                btnScan.setClickable(true);
                isCanScan = true;
                ll01.setClickable(true);
            }
        }
    };

    //显示修磨备列表
    private void showPopupWindow() {
        View view = LayoutInflater.from(C03S004_001Activity.this).inflate(R.layout.spinner_c03s004_001, null);
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
                tv01.setText(listString.get(i));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return respons.getNocequipmentList().size();
        }

        @Override
        public Object getItem(int i) {
            return respons.getNocequipmentList().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(C03S004_001Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(listString.get(i));
            return view1;
        }

    }

    //扫描方法
    private void scan() {
        //点击扫描按钮以后，设置扫描按钮不可用
        btnScan.setClickable(false);
        //扫描时选择弹框不可用
        ll01.setClickable(false);
        if (null == tv01.getText() || "".equals(tv01.getText())) {
            createAlertDialog(C03S004_001Activity.this, getString(R.string.c03s004_001_004), Toast.LENGTH_LONG);
            btnScan.setClickable(true);
            isCanScan = true;
            ll01.setClickable(true);
        } else {
            //取设备ID
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getEquipmentName().equals(tv01.getText())) {
                    equipmentID =list.get(i).getEquipmentID();
                }
            }
            //显示扫描弹框的方法
            scanPopupWindow();
            //开启扫描线程
            scanThread = new scanThread();
            scanThread.start();
        }
    }

    //扫描线程
    private class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //单扫方法
            singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                ll01.setClickable(true);
                overtimeHandler.sendMessage(message);
            } else if(null != rfidString && !"close".equals(rfidString)) {
                //设定用户访问信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                //用户ID
                String customerID = sharedPreferences.getString("customerID", "");
                request.setCustomerID(customerID);
                //手持机ID
                String handSetId = sharedPreferences.getString("handsetid", "");
                request.setHandSetId(handSetId);
                //rfidString
                request.setRfidCode(rfidString);
                //标签类型（4--设备标签）
                request.setQueryType(FOUR);
                //设备ID
                request.setEquipmentID(equipmentID);
                //绑定类型（0--加工设备，1--修磨设备）
                request.setEquipmentType(ONE);
                try {
                    //提交修磨设备标签绑定数据
                    respons = wsdl.submitEquipmentRifdCode(request);
                    if (null != respons) {
                        message.obj = respons;
                        scanhandler.sendMessage(message);
                    } else {
                        btnScan.setClickable(true);
                        isCanScan = true;
                        ll01.setClickable(true);
                        internetErrorHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    internetErrorHandler.sendMessage(message);
                    btnScan.setClickable(true);
                    isCanScan = true;
                    ll01.setClickable(true);
                }
            }
        }
    }

    //正常提交数据的Handler
    @SuppressLint("HandlerLeak")
    public Handler scanhandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            respons = (C03S002Respons) message.obj;
            try{
                //取值成功，跳转页面
                if (ZERO.equals(respons.getStateCode())) {
                    Intent intent = new Intent(C03S004_001Activity.this, C03S004_002Activity.class);
                    startActivity(intent);
                    finish();
                } else if (TWO.equals(respons.getStateCode())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(C03S004_001Activity.this);
                    builder.setTitle(R.string.prompt);
                    builder.setMessage(R.string.c03s004_001_003);
                    builder.setCancelable(false);
                    builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //授权弹框
                            showAuthorizationWindow();
                        }
                    }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int a) {
                            dialogInterface.dismiss();
                            btnScan.setClickable(true);
                            isCanScan = true;
                            ll01.setClickable(true);
                        }
                    }).show();
                } else {
                    //取值失败，提示返回的错误消息
                    createAlertDialog(C03S004_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    btnScan.setClickable(true);
                    isCanScan = true;
                    ll01.setClickable(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
                ll01.setClickable(true);
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
    private String activityName = "C03S002_006Activity";
    //输入授权线程
    private inputAuthorizationThread inputAuthorizationThread;
    //扫描授权线程
    private scanAuthorizationThread scanAuthorizationThread;
    //已经初始化的设备重新初始化的提交线程
    private submitThread submitThread;

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
                //放开下拉框和扫描按钮
                ll01.setClickable(true);
                btnScan.setClickable(true);
                isCanScan = true;
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
                submitThread = new submitThread();
                submitThread.start();
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
                        btnScanAuthorization.setClickable(true);
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

    //已经初始化的修磨设备重新初始化的线程
    private class submitThread extends Thread {
        @Override
        public void run() {
            super.run();
            //授权ID
            request.setGruantUserID(gruantUserID);
            Message message = new Message();
            try {
                //提交修磨设备标签绑定数据
                respons = wsdl.submitEquipmentRifdCode(request);
                if (null != respons) {
                    message.obj = respons;
                    scanhandler.sendMessage(message);
                } else {
                    btnScan.setClickable(true);
                    isCanScan = true;
                    ll01.setClickable(true);
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                internetErrorHandler.sendMessage(message);
                btnScan.setClickable(true);
                isCanScan = true;
                ll01.setClickable(true);
            }
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
