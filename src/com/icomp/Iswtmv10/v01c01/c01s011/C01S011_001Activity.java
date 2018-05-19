package com.icomp.Iswtmv10.v01c01.c01s011;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;
import com.icomp.wsdl.v01c01.c01s011.C01S011Wsdl;
import com.icomp.wsdl.v01c01.c01s011.endpoint.C01S011Request;
import com.icomp.wsdl.v01c01.c01s011.endpoint.C01S011Respons;
import com.umeng.analytics.MobclickAgent;

import java.util.Date;

/**
 * 安上设备
 *
 * @author WHY
 * @ClassName: C01S011_001Activity
 * @date 2016-3-1 下午8:33:44
 */
public class C01S011_001Activity extends CommonActivity {

    private C01S011_Params params;
    private C01S011Request request = new C01S011Request();
    private C01S011Respons respons;
    private C01S011Wsdl service = new C01S011Wsdl();
    private PopupWindow popupWindow, mPopWindow1, mPopWindow2, mPopWindow3;//popupWindow:扫描时的。popupWindow1：选择授权方式。popupWindow2：输入签收授权,popupWindow3刷卡签收
    private Button btn_scan;
    private String rfidString;
    private String rfidString1;
    private boolean type = true;
    private int screenWidth, screenHeight;//屏幕宽度，高度
    private EditText et_username, et_password;//输入登录 账号，密码
    private String username, password;//保存账号，密码（String）
    private boolean isCanScan = true;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (params == null) {
            params = new C01S011_Params();
        }
        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s011_001activity);
        // 扫描点击
        btn_scan = (Button) findViewById(R.id.btn_scan);
    }

    /**
     * 扫描按钮点击事件
     */
    public void scan(View view) {
        if (popupWindow == null || !popupWindow.isShowing()) {
            isCanScan = false;
            showWindow(null, R.layout.c00s000_010activity);
        }
    }

    /**
     * 监听键盘扫描按键
     *
     * @return
     */
//    protected boolean keycodeScanf() {
//        if (popupWindow == null || !popupWindow.isShowing()) {
//            showWindow(null, R.layout.c00s000_010activity);
//        }
//        return true;
//    }
    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan){
            isCanScan = false;
        }else{
            return;
        }
            if (popupWindow == null || !popupWindow.isShowing()) {
                showWindow(null, R.layout.c00s000_010activity);
            }
    }


    //返回按钮处理
    public void appReturn(View view) {
        if (null != popupWindow) {
            popupWindow.dismiss();
            close();
            type = false;
        }
        finish();
    }

    private VisitJniThread thread;

    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            // 打开Rfid读头模块
            initRFID();
            rfidString = null;
            Date date = new Date();
            do {
                //读取Rfid
                rfidString = readRfidString(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    rfidString = "close";
                }
            } while (rfidString == null && !"close".equals(rfidString) && type);
            close();
            if (null != rfidString) {
                Message message = new Message();
                message.obj = rfidString;
                scanfmhandler.sendMessage(message);
            }

        }
    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnString = msg.obj.toString();
            isCanScan = true;
            if ("close".equals(returnString)) {
                createAlertDialog(C01S011_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
                btn_scan.setClickable(true);
            } else {
                try {
                    request.setRfidCode(returnString);//RfidCode
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);//设定用户信息
                    String customerID = sharedPreferences.getString("customerID", "");
                    String handsetid = sharedPreferences.getString("handsetid", "");
                    request.setHandSetId(handsetid);
                    request.setCustomerID(customerID);//customerID
                    request.setQueryType("2");//标签类型（0库位标签，1单品刀，2合成刀具，3员工卡，4设备，5容器，6备刀库）
                    respons = service.getSyntheticInfoEquipmentList(request);//取得合成刀信息和设备列表
                    btn_scan.setClickable(true);//扫描按钮可点击
                    if ("0".equals(respons.getStateCode())) {
                        if (params == null) {
                            params = new C01S011_Params();
                        }
//                        params.synthesisParametersID = respons.getSynthesisParametersID();//合成刀具ID
                        params.synthesisParametersCode = respons.getSynthesisParametersCode();//合成刀编码
                        //设备名称、设备ID、RFID、轴ID、轴编码等
                        params.equipmentEntity = respons.getEquipments();
                        // 合成刀具rfid
                        params.synthesisParametersRfid = returnString;
                        Intent intent = new Intent(C01S011_001Activity.this, C01S011_002Activity.class);
                        intent.putExtra(PARAM, params);
                        startActivity(intent);
                    } else if ("5".equals(respons.getStateCode())) {//重复安上授权
                        // 创建一个AlertDialog的构建者对象
                        AlertDialog.Builder builder = new AlertDialog.Builder(C01S011_001Activity.this);
                        builder.setTitle(R.string.infoMsg);// 设置标题
                        builder.setMessage(respons.getStateMsg());// 提示信息
                        builder.setCancelable(false);// 设置对话框不能被取消
                        // 设置正面的按钮
                        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shouquanPopuWindow();
                            }
                        });
                        builder.show();  // 显示对话框
                    } else {
                        createAlertDialog(C01S011_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    // 网络连接失败
                    createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
                    btn_scan.setClickable(true);
                }
            }
            popupWindow.dismiss();
        }
    };

    /**
     * 选择授权方式弹出框
     */
    public void shouquanPopuWindow() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        View contentView = LayoutInflater.from(C01S011_001Activity.this).inflate(R.layout.c01s004_004activity, null);
        mPopWindow1 = new PopupWindow(contentView, (int) (0.8 * screenWidth), (int) (0.6 * screenHeight), true);
//        dark();
        mPopWindow1.showAtLocation(contentView, Gravity.CENTER_VERTICAL, 0, 0);
        mPopWindow1.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                bright();
            }
        });
        //获取按钮
        Button btn_01 = (Button) contentView.findViewById(R.id.btn_01);//输入签收
        Button btn_02 = (Button) contentView.findViewById(R.id.btn_02);//刷卡确认
        Button btn_return = (Button) contentView.findViewById(R.id.btn_return);//返回
        btn_01.setText(getString(R.string.inputauthorization));
        btn_02.setText("扫卡授权");
        //输入签收点击事件
        btn_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuruPopuwindow();
            }
        });
        //刷卡签收点击事件
        btn_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isShuaKa = true;
                shuakaPopuwindow(null, R.layout.c00s000_010activity);
            }
        });
        //返回点击事件
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow1.dismiss();
            }
        });
    }

    /**
     * 输入授权popuWindow
     */
    public void shuruPopuwindow() {
        View contentView = LayoutInflater.from(C01S011_001Activity.this).inflate(R.layout.c00s000_001_1activity, null);//找到对话框布局文件
        mPopWindow2 = new PopupWindow(contentView, (int) (0.8 * screenWidth), (int) (0.6 * screenHeight), true);
        mPopWindow2.showAtLocation(contentView, Gravity.CENTER_VERTICAL, 0, 0);//显示对话框
        //获取文本输入框
        et_username = (EditText) contentView.findViewById(R.id.et_username);//用户名
        et_password = (EditText) contentView.findViewById(R.id.et_password);//密码
        LinearLayout ll_01 = (LinearLayout) contentView.findViewById(R.id.ll_01);
        ll_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) C01S011_001Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        //获取按钮
        TextView btn_title = (TextView) contentView.findViewById(R.id.btn_title);
        Button btn_login = (Button) contentView.findViewById(R.id.btn_login);//登录
        Button btn_reset = (Button) contentView.findViewById(R.id.btn_reset);//重置
        btn_login.setText("授权");
        btn_title.setText("输入授权");
        //关闭键
        ImageView imageView = (ImageView) contentView.findViewById(R.id.iv_cancel);
        //输入签收点击事件→user登录按钮
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = et_username.getText().toString().trim();//用户名
                password = et_password.getText().toString().trim();//密码
                if (null == username || "".equals(username)) {
                    Toast.makeText(C01S011_001Activity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                } else if (null == password || "".equals(password)) {
                    Toast.makeText(C01S011_001Activity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    //调用共通授权
                    loading.show();
                    shouquan = new shouquanThread();
                    shouquan.start();
                }

            }
        });
        //输入签收点击事件→重置按钮
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_username.setText("");
                et_password.setText("");


            }
        });
        //关闭
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopWindow2.dismiss();
            }
        });
    }

    /**
     * 授权线程
     */

    public class shouquanThread extends Thread {
        @Override
        public void run() {
            UserRespons response = authorize(username, password, "C01S013_003Activity");//返回值是授权用户ID
            if (null != response) {
                Message message = new Message();
                message.obj = response;
                shouquanHandler.sendMessage(message);
            } else {
                Message message = new Message();
                internetErrorHandler.sendMessage(message);
            }

        }
    }

    public shouquanThread shouquan;//授权线程
    @SuppressLint("HandlerLeak")
    Handler shouquanHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UserRespons response = (UserRespons) msg.obj;
            if ("0".equals(response.getStateCode())) {
                loading.show();
                try {
                    request.setRfidCode(rfidString);//RfidCode
                    request.setGruantUserID(response.getCustomer().getCustomerID());
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);//设定用户信息
                    String customerID = sharedPreferences.getString("customerID", "");
                    String handsetid = sharedPreferences.getString("handsetid", "");
                    request.setHandSetId(handsetid);
                    request.setCustomerID(customerID);//customerID
                    request.setQueryType(TWO);//标签类型（0库位标签，1单品刀，2合成刀具，3员工卡，4设备，5容器，6备刀库）
                    respons = service.getSyntheticInfoEquipmentList(request);//取得合成刀信息和设备列表
                    btn_scan.setClickable(true);//扫描按钮可点击
                    if (ZERO.equals(respons.getStateCode())) {
                        if (params == null) {
                            params = new C01S011_Params();
                        }
//                        params.synthesisParametersID = respons.getSynthesisParametersID();//合成刀具ID
                        params.synthesisParametersCode = respons.getSynthesisParametersCode();//合成刀编码
                        //设备名称、设备ID、RFID、轴ID、轴编码等
                        params.equipmentEntity = respons.getEquipments();
                        // 合成刀具rfid
                        params.synthesisParametersRfid = rfidString;
                        Intent intent = new Intent(C01S011_001Activity.this, C01S011_002Activity.class);
                        intent.putExtra(PARAM, params);
                        startActivity(intent);
                    } else if (FIVE.equals(respons.getStateCode())) {//重复安上授权
                        // 创建一个AlertDialog的构建者对象
                        AlertDialog.Builder builder = new AlertDialog.Builder(C01S011_001Activity.this);
                        builder.setTitle(R.string.infoMsg);// 设置标题
                        builder.setMessage(respons.getStateMsg());// 提示信息
                        builder.setCancelable(false);// 设置对话框不能被取消
                        // 设置正面的按钮
                        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shouquanPopuWindow();
                            }
                        });
                        builder.show();  // 显示对话框
                    } else {
                        createAlertDialog(C01S011_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    // 网络连接失败
                    createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
                }
                mPopWindow1.dismiss();
                mPopWindow2.dismiss();
            } else {
                createAlertDialog(C01S011_001Activity.this, response.getStateMsg(), Toast.LENGTH_LONG);

            }
            loading.dismiss();
        }
    };

    /**
     * 打开扫描Rfid页面
     *
     * @param view
     * @param pageID void
     * @title showWindow
     */
    protected void showWindow(View view, int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        popupWindow = new PopupWindow(vPopupWindow, 300, 240);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        btn_scan.setClickable(false);
        thread = new VisitJniThread();
        thread.start();
    }

    /**
     * 刷卡授权popuwindow
     *
     * @param parent
     * @param pageID
     */
    private void shuakaPopuwindow(View parent, int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View vPopupWindow = layoutInflater.inflate(pageID, null);
        mPopWindow3 = new PopupWindow(vPopupWindow, 300, 240, true);
//        mPopWindow3.setBackgroundDrawable(new BitmapDrawable());
        mPopWindow3.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        threadcard = new cardThread();
        threadcard.start();
    }

    /**
     * 刷卡签收线程
     */
    public class cardThread extends Thread {
        @Override
        public void run() {
            initRFID();
            rfidString1 = null;
            Date date = new Date();
            do {
                rfidString1 = readRfidString(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    rfidString1 = "close";
                }
            } while (rfidString1 == null);
            close();
            if (null != rfidString1) {
                Message message = new Message();
                message.obj = rfidString1;
                cardhandler.sendMessage(message);
            }

        }
    }

    public cardThread threadcard;
    @SuppressLint("HandlerLeak")
    Handler cardhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                createAlertDialog(C01S011_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
            } else {
                //调用刷卡签收授权
                loading.show();
                threadShuaka = new ShuakaThread();
                threadShuaka.start();
            }
            mPopWindow3.dismiss();

        }
    };

    /**
     * 刷卡签收授权线程
     */
    public class ShuakaThread extends Thread {
        @Override
        public void run() {
            UserRespons respons = authorize("C01S013_003Activity", rfidString1);//刷卡授权
            if (null != respons) {
                Message message = new Message();
                message.obj = respons;
                threadShuakaHandler.sendMessage(message);
            } else {
                Message message = new Message();
                internetErrorHandler.sendMessage(message);
            }
        }
    }


    @SuppressWarnings("HandlerLeak")
    public ShuakaThread threadShuaka;
    Handler threadShuakaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UserRespons response = (UserRespons) msg.obj;
            if (ZERO.equals(response.getStateCode())) {
                gruantUserID = response.getCustomer().getCustomerID();
                //request.setGruantUserID(gruantUserID);
                loading.show();
                try {
                    request.setRfidCode(rfidString);//RfidCode
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);//设定用户信息
                    String customerID = sharedPreferences.getString("customerID", "");
                    String handsetid = sharedPreferences.getString("handsetid", "");
                    request.setHandSetId(handsetid);
                    request.setCustomerID(customerID);//customerID
                    request.setGruantUserID(gruantUserID);
                    request.setQueryType("2");//标签类型（0库位标签，1单品刀，2合成刀具，3员工卡，4设备，5容器，6备刀库）
                    respons = service.getSyntheticInfoEquipmentList(request);//取得合成刀信息和设备列表
                    btn_scan.setClickable(true);//扫描按钮可点击
                    if (ZERO.equals(respons.getStateCode())) {
                        if (params == null) {
                            params = new C01S011_Params();
                        }
//                        params.synthesisParametersID = respons.getSynthesisParametersID();//合成刀具ID
                        params.synthesisParametersCode = respons.getSynthesisParametersCode();//合成刀编码
                        //设备名称、设备ID、RFID、轴ID、轴编码等
                        params.equipmentEntity = respons.getEquipments();
                        // 合成刀具rfid
                        params.synthesisParametersRfid = rfidString;
                        Intent intent = new Intent(C01S011_001Activity.this, C01S011_002Activity.class);
                        intent.putExtra(PARAM, params);
                        startActivity(intent);
                    } else if (FIVE.equals(respons.getStateCode())) {//重复安上授权
                        // 创建一个AlertDialog的构建者对象
                        AlertDialog.Builder builder = new AlertDialog.Builder(C01S011_001Activity.this);
                        builder.setTitle(R.string.infoMsg);// 设置标题
                        builder.setMessage(respons.getStateMsg());// 提示信息
                        builder.setCancelable(false);// 设置对话框不能被取消
                        // 设置正面的按钮
                        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                shouquanPopuWindow();
                            }
                        });
                        builder.show();  // 显示对话框
                    } else {
                        createAlertDialog(C01S011_001Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    // 网络连接失败
                    createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
                }
                if (mPopWindow1 != null) {
                    mPopWindow1.dismiss();
                }
                if (mPopWindow2 != null) {
                    mPopWindow2.dismiss();
                }
            } else {
                createAlertDialog(C01S011_001Activity.this, response.getStateMsg(), Toast.LENGTH_LONG);

            }
            loading.dismiss();
        }
    };
    /**
     * 处理网络异常的handler
     */
    @SuppressLint("HandlerLeak")
    Handler internetErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loading.dismiss();
            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);


        }
    };
    /**
     * 友盟错误统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}