package com.icomp.common.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ReplacementTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.constat.Constat;
import com.icomp.common.utils.CardRead;
import com.icomp.common.utils.DialogFactory;
import com.icomp.wsdl.v01c00.c00s000.C00S000Wsdl;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRequest;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;
import com.rodinbell.uhf.serialport.SerialPort;
import com.t_epc.UHFApplication;
import com.t_epc.reader.model.ISO180006BOperateTagBuffer;
import com.t_epc.reader.model.InventoryBuffer;
import com.t_epc.reader.model.OperateTagBuffer;
import com.t_epc.reader.model.ReaderSetting;
import com.t_epc.reader.server.ReaderBase;
import com.t_epc.reader.server.ReaderHelper;
import com.t_epc.utils.M10_GPIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class CommonActivity extends Activity {
    @SuppressLint("SimpleDateFormat")
    public SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);//设定格式
    public PopupWindow popupWindow;
    private PopupWindow popupWindow2;//登录按钮
    protected String url = "";
    protected Context packageContext;
    protected String gruantUserID;// 授权用户ID
    protected String gruantUserName;// 授权用户姓名
    protected String userID;// 交接人ID
    protected String activityName;
    private ImageView tvBatteryChanged;
    private ImageView tvBatteryWifi;
    private TextView txtBatteryChanged;
    public static int SCANF_TIME = 10000;
    public static final String UHF_POWERON_ACTION = "com.android.uhf.POWERON";
    public static final int SUCCESS = 0;
    IntentFilter wifiIntentFilter;    // wifi监听器
    public String stateCode;
    public String customerID, handSetId;
    public boolean transferConfirn = false;
    public DialogFactory loading = null;
    // 取得用户登录信息
    //  public SharedPreferences preferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);

    /**
     * 丢刀授权开关 开 ：true 关：false
     */
    public static final Boolean GIVE_ABLE = true;
    //参数
    public static final String PARAM = "param";
    public static final String PARAM1 = "param1";
    public static final String PARAM2 = "param2";
    public static final String PARAM3 = "param3";
    public static final String ZERO = "0";
    public static final String ONE = "1";
    public static final String TWO = "2";
    public static final String THREE = "3";
    public static final String FOUR = "4";
    public static final String FIVE = "5";
    public static final String NINE = "9";

    private final int SCANKEY_LEFT = 300;
    private final int SCANKEY_RIGHT = 301;
    private final int SCANKEY_CENTER = 302;
    public InputMethodManager manager;

    /**
     * PopupWindow是否显示 true:显示 false:不显示
     */
    public static Boolean IS_SHOW = false;
    //0-33 最大值为33
    public byte rfidFrequency = 32;//rfid频率

    /** 判断是否上电的标示*/
//    protected boolean mSwitchFlag = false;
//    private VirtualKeyListenerBroadcastReceiver mVirtualKeyListenerBroadcastReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath().build());
        super.onCreate(savedInstanceState);
        wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        ((UHFApplication) getApplication()).addActivity(this);
        loading = new DialogFactory(this, R.layout.c00s000_015activity, R.style.DialogTheme);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    //    @Override
//    public void onResume() {
    //      //计算电池电量
    //  tvBatteryChanged = (ImageView) this.findViewById(R.id.tvBatteryChanged);
    //  //注册一个接受广播类型
    //  //计算信号强度
    ////  tvBatteryWifi = (ImageView) this.findViewById(R.id.tvBatteryWifi);
    //txtBatteryChanged = (TextView) this.findViewById(R.id.txtBatteryChanged);
    //  if (tvBatteryWifi != null && tvBatteryChanged != null) {
    //      registerReceiver(new BatteryBroadcastReceiver(), new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    //  }
    //  super.onResume();
    // }


    /**
     * 点击功能菜单,用户再次登录
     *
     * @return
     */
    protected void reLogin(View parent, int pageID) {

        // 取得当前手持机登录验证机制
        // 保存用户登录信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        String loginStauts = sharedPreferences.getString("loginStauts", "1");
        if (loginStauts.equals("1")) {
            Intent intent = new Intent();
            try {
                intent.setClass(packageContext, Class.forName("com.icomp.Iswtmv10." + url));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }// 从哪里跳到哪里
            packageContext.startActivity(intent);
        } else {

            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View vPopupWindow = layoutInflater.inflate(pageID, null);
            vPopupWindow.setBackgroundResource(R.drawable.rounded_corners_view);
            popupWindow = new PopupWindow(vPopupWindow, 400, 280);
            //            popupWindow.setFocusable(true);
            //            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            // 设置其背景
            // popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
            //  Button btnInputLogin = (Button) vPopupWindow.findViewById(R.id.btnInputLogin);
            //   Button btnCardLogin = (Button) vPopupWindow.findViewById(R.id.btnCardLogin);
            //  UserBtn userBtn = new UserBtn();
            //  btnInputLogin.setOnClickListener(userBtn);
            // CardBtn cardBtn = new CardBtn();
            // btnCardLogin.setOnClickListener(cardBtn);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("haha", Activity.MODE_PRIVATE);
        //旧手持机安装屏幕关闭的问题
//        setScreenBrightness(sp.getInt("light", 5));
    }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    private void setScreenBrightness(int paramInt) {
        Window localWindow = getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow
                .getAttributes();
        float f = paramInt / 2550.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    private VisitJniThread thread;

    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            CardRead.initRead(CommonActivity.this);// 打开Rfid读头模块
            String cardString = null;
            Date date = new Date();
            do {
                cardString = CardRead.readCard(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    cardString = "close";
                }
            } while (cardString == null && !"close".equals(cardString));
            CardRead.close();
            Message message = new Message();
            message.obj = cardString;
            scanfmhandler.sendMessage(message);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                createAlertDialog(CommonActivity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);

            } else {
                try {
                    UserRequest request = new UserRequest();
                    request.setEmployeeCard(returnString);
                    C00S000Wsdl service = new C00S000Wsdl();
                    UserRespons respons = service.userLogin(request);
                    if (respons.getMessageText() == null) {
                        // 保存用户登录信息
                        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
                        Editor editor = sharedPreferences.edit();// 获取编辑器
                        editor.putString("userName", respons.getCustomer().getUserName());
                        editor.putString("customerID", respons.getCustomer().getCustomerID());
                        editor.putString("langCode", respons.getLanguagetable().getLanguageCode());
                        editor.putString("langValue", respons.getLanguagetable().getLanguageValue());
                        editor.commit();// 提交修改

                        try {

                            Intent intent = new Intent();
                            intent.setClass(packageContext, Class.forName("com.icomp.Iswtmv10." + url));// 从哪里跳到哪里
                            packageContext.startActivity(intent);

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        popupWindow2.dismiss();
                        popupWindow.dismiss();
                    } else {
                        createAlertDialog(CommonActivity.this, respons.getMessageText(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            popupWindow2.dismiss();
            popupWindow.dismiss();
        }
    };

    private GruantThread thread2;

    private class GruantThread extends Thread {
        @Override
        public void run() {
            CardRead.initRead(CommonActivity.this);// 打开Rfid读头模块
            String cardString = null;
            Date date = new Date();
            do {
                cardString = CardRead.readCard(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    cardString = "close";
                }
            } while (cardString == null && !"close".equals(cardString));
            CardRead.close();
            Message message = new Message();
            message.obj = cardString;
            gruantmhandler.sendMessage(message);
        }
    }

    Handler gruantmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                createAlertDialog(CommonActivity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
            } else {
                try {
                    /*
                    CardRead.initRead(CommonActivity.this);// 启动读卡器
                    String cardString = null;
                    do {
                        cardString = CardRead.readCard();
                    } while (cardString == null);
                    */
                    UserRequest request = new UserRequest();
                    // 设定访问用户信息
                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                    String customerID = sharedPreferences.getString("customerID", "");
                    String langCode = sharedPreferences.getString("langCode", "");
                    String langValue = sharedPreferences.getString("langValue", "zh_en");// 语言值（zh_en）
                    String handSetId = sharedPreferences.getString("handsetid", null);// 手持机Id
                    request.setCustomerID(customerID);
                    request.setLanguageCode(langCode);
                    request.setLanguageValue(langValue);
                    request.setEmployeeCard(returnString);
                    request.setActivityName(activityName);
                    C00S000Wsdl service = new C00S000Wsdl();
                    UserRespons respons = service.userGruant(request);
                    if (respons.getStatus() == SUCCESS) {
                        // 保存用户登录信息
                        gruantUserID = respons.getCustomer().getCustomerID();
                        gruantUserName = respons.getUserDetailName();
                    } else {
                        createAlertDialog(CommonActivity.this, respons.getMessageText(), Toast.LENGTH_LONG);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            popupWindow2.dismiss();
            popupWindow.dismiss();
        }
    };


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        boolean superFlag = false;
//        if (superFlag) {
//            return super.onKeyDown(keyCode, event);
//        }
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_MENU: // F1按键 82
//                superFlag = keycodeMenu();
//                break;
//            case KeyEvent.KEYCODE_BACK:// F3按键 4
//                superFlag = keycodeBack();
//                break;
//            case KeyEvent.KEYCODE_F4:// F4按键 134
//                superFlag = keycodeF4();
//                break;
//            case KeyEvent.KEYCODE_DPAD_UP:// down 按键 19
//                superFlag = keycodeUp();
//                break;
//            case KeyEvent.KEYCODE_DPAD_DOWN:// UP 按键 20
//                superFlag = keycodeDown();
//                break;
//            case KeyEvent.KEYCODE_DPAD_LEFT:// <-按键 21
//                superFlag = keycodeLeft();
//                break;
//            case KeyEvent.KEYCODE_DPAD_RIGHT:// ->按键 22
//                superFlag = keycodeRight();
//                break;
//            case KeyEvent.KEYCODE_ENTER:// Enter按键 66
//                superFlag = keycodeEnter();
//                break;
//            case SUCCESS:// 黄色按键 扫描
//                superFlag = keycodeScanf();
//                break;
//            case 213:// 黄色按键 扫描
//                superFlag = keycodeScanf();
//                break;
//            default:
//                superFlag = true;
//                break;
//        }
//        if (superFlag) {
//            return super.onKeyDown(keyCode, event);
//        } else {
//            return false;
//        }
//    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == SCANKEY_LEFT)
                || (keyCode == SCANKEY_RIGHT)
                || (keyCode == SCANKEY_CENTER)
                || (keyCode == SUCCESS)
                || (keyCode == 213)
            ) {
            btnScan();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            btnReturn();
        }

        return super.onKeyDown(keyCode, event);
    }

    protected void btnScan() {
        connect();
    }

    protected void btnReturn() {
        keycodeBack();
    }

    /**
     * 8.17
     */
    protected void connect(){

        //  手持机一个包-------------------------------------------------------
        try {
            M10_GPIO.R1000_PowerOn();
//            mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
            mSerialPort = new SerialPort(new File("/dev/ttySAC2"), 115200, 0);
        } catch (IOException e) {
//            e.printStackTrace();
            try {
                mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
            } catch (Exception e1) {
                e.printStackTrace();
                try {
                    mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
                } catch (Exception er) {
                    er.printStackTrace();
                }

            }
        }
        //  手持机一个包-------------------------------------------------------



//  旧手持机-------------------------------------------------------
//        try {
//            M10_GPIO.R1000_PowerOn();
//            mSerialPort = new SerialPort(new File("/dev/ttySAC2"), 115200, 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
        //  旧手持机-------------------------------------------------------



        try {
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
            mReader = mReaderHelper.getReader();
            m_curReaderSetting = mReaderHelper.getCurReaderSetting();
            m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
            m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
            m_curOperateTagISO18000Buffer = mReaderHelper.getCurOperateTagISO18000Buffer();
            Intent intent_poweron = null;
            intent_poweron = new Intent(UHF_POWERON_ACTION);
            getBaseContext().sendBroadcast(intent_poweron);
        } catch (Exception e) {
            e.printStackTrace();

            return;
        }
//        ControlGPIO.newInstance().JNIwriteGPIO(1);
    }


    /**
     * 黄色按键 扫描
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeScanf() {
        return false;
    }

    /**
     * F1按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeMenu() {
        return false;
    }

    /**
     * F3按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeBack() {
        return false;
    }

    /**
     * F4按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeF4() {
        return false;
    }

    /**
     * down 按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeUp() {
        return true;
    }

    /**
     * UP 按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeDown() {
        return true;
    }

    /**
     * <-按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeLeft() {
        return true;
    }

    /**
     * ->按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeRight() {
        return true;
    }

    /**
     * Enter按键
     *
     * @return true:事件继续执行,false:事件停止
     */
    protected boolean keycodeEnter() {
        return false;
    }

    /**
     * 取消按钮处理
     */
    protected void appCancel() {
    }

    /**
     * 返回按钮处理
     */
    protected void appReturn() {

    }

    protected byte[] objectToBytes(Object obj) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        ObjectOutputStream sOut = new ObjectOutputStream(out);

        sOut.writeObject(obj);

        sOut.flush();

        byte[] bytes = out.toByteArray();

        return bytes;

    }

    protected Object bytesToObject(byte[] bytes) throws Exception {

        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        ObjectInputStream sIn = new ObjectInputStream(in);

        return sIn.readObject();

    }

    /**
     * 创建一个提示信息的对话框
     *
     * @param activity 要显示在那activity中
     * @param contry   提示内容
     */

    public void createAlertDialog(Context activity, CharSequence contry, int i) {
        // 创建一个AlertDialog的构建者对象
        AlertDialog.Builder builder = new AlertDialog.Builder(CommonActivity.this);
        builder.setTitle(R.string.infoMsg);// 设置标题
        if (contry == null) {
            contry = getString(R.string.CommonActivity_1);
        }
        builder.setMessage(contry);// 提示信息
        builder.setCancelable(false);// 设置对话框不能被取消
        // 设置正面的按钮
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();  // 显示对话框
    }


    /*****************************************************************tyy**********2015年8月4日 12:41:50 ****end****/
    /**********************
     * new RFID************************************tyy**********2015年8月4日 12:41:50 ****begin
     ****/
    private static SerialPort mSerialPort = null;
    private ReaderBase mReader;
    private ReaderSetting m_curReaderSetting;
    private InventoryBuffer m_curInventoryBuffer;
    private OperateTagBuffer m_curOperateTagBuffer;
    private ISO180006BOperateTagBuffer m_curOperateTagISO18000Buffer;
    private ReaderHelper mReaderHelper;

    public void initRFID() {

        //2017年11月21日10:05:35手机及一个包问题---------------------------------------
//        //模块上电
        try {
            M10_GPIO.R1000_PowerOn();
            //判断手持机串口号
            mSerialPort = new SerialPort(new File("/dev/ttySAC2"), 115200, 0);
        } catch (SecurityException e1) {
            e1.printStackTrace();
            try {
                mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            try {
                mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 115200, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //2017年11月21日10:05:35手机及一个包问题---------------------------------------


//  旧手持机-------------------------------------------------------
//        //模块上电
//        try {
//            M10_GPIO.R1000_PowerOn();
//            //判断手持机串口号
//            mSerialPort = new SerialPort(new File("/dev/ttySAC2"), 115200, 0);
//        } catch (SecurityException e1) {
//            e1.printStackTrace();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//
//        }
//  旧手持机-------------------------------------------------------



        try {
            //启动RFID读头模块
            mReaderHelper = ReaderHelper.getDefaultHelper();
            mReaderHelper.setReader(mSerialPort.getInputStream(), mSerialPort.getOutputStream());
            mReader = mReaderHelper.getReader();
            m_curReaderSetting = mReaderHelper.getCurReaderSetting();
            m_curInventoryBuffer = mReaderHelper.getCurInventoryBuffer();
            m_curOperateTagBuffer = mReaderHelper.getCurOperateTagBuffer();
            m_curOperateTagISO18000Buffer = mReaderHelper.getCurOperateTagISO18000Buffer();
            Intent intent_poweron = null;
            intent_poweron = new Intent(UHF_POWERON_ACTION);
            getBaseContext().sendBroadcast(intent_poweron);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public String readRfidString(boolean scanf) {
        Constat.RFID_CODE_STRING = null;
        m_curInventoryBuffer.clearInventoryPar();
        m_curInventoryBuffer.lAntenna.add((byte) 0x01);

        m_curInventoryBuffer.bLoopInventoryReal = true;
        m_curInventoryBuffer.btRepeat = 0;

        m_curInventoryBuffer.btRepeat = (byte) 1;

        m_curInventoryBuffer.bLoopCustomizedSession = false;

        m_curInventoryBuffer.clearInventoryRealResult();
        mReaderHelper.setInventoryFlag(true);
        mReaderHelper.clearInventoryTotal();
        byte btWorkAntenna;
        if (null == m_curInventoryBuffer || null == m_curInventoryBuffer.lAntenna) {
            return null;
        }
        btWorkAntenna = m_curInventoryBuffer.lAntenna.get(m_curInventoryBuffer.nIndexAntenna);
        if (btWorkAntenna < 0) {
            btWorkAntenna = 0;
        }
        mReader.setWorkAntenna(m_curReaderSetting.btReadId, btWorkAntenna);
        SharedPreferences preferences = getSharedPreferences("rfidFrequency", CommonActivity.MODE_APPEND);
        String paramInt = preferences.getString("paramInt", "18");
        byte frequency = (byte) Integer.parseInt(paramInt);
        mReader.setOutputPower(m_curReaderSetting.btReadId, frequency);//调节射频频率  0-33
        return Constat.RFID_CODE_STRING;
    }

    private Handler mLoopHandler = new Handler();

    public void close() {
        if (mReaderHelper != null) {
            mReaderHelper.setInventoryFlag(false);
            m_curInventoryBuffer.bLoopInventory = false;
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mVirtualKeyListenerBroadcastReceiver = new VirtualKeyListenerBroadcastReceiver();
//        IntentFilter intentFilter=new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//        this.registerReceiver(mVirtualKeyListenerBroadcastReceiver,intentFilter);
//        if (mSwitchFlag) {
//            ControlGPIO.newInstance().JNIwriteGPIO(ControlGPIO.ON);
//        }
//
//    }

    /**
     * 背景变暗方法
     */
    public void dark() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.4f;
        getWindow().setAttributes(lp);
    }

    /**
     * 恢复背景亮度
     */
    public void bright() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }


    //*****************************************************************************

    /**
     * 授权接口,输入授权
     */
    public UserRespons authorize(String username, String password, String activityName) {
        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        customerID = sharedPreferences.getString("customerID", "");
        UserRequest request = new UserRequest();
        C00S000Wsdl service = new C00S000Wsdl();
        request.setUserName(username);//用户名
        request.setUserPass(password);//密码
        request.setLoginType("0");//输入授权0  扫卡1
        request.setActivityName(activityName);//页面名
        request.setCustomerID(customerID);//用户ID
        try {
            UserRespons response = service.userGruant(request);
            if (null != response) {
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 授权---扫卡授权
     */
    public UserRespons authorize(String activityName, String employeeCard) {
        // 设定访问用户信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        customerID = sharedPreferences.getString("customerID", "");
        UserRequest request = new UserRequest();
        request.setCustomerID(customerID);//用户Id
        request.setLoginType("1");//授权类型（0:输入授权。1：刷卡授权）
        request.setActivityName(activityName);//页面名称
        request.setEmployeeCard(employeeCard);//RFID
        C00S000Wsdl service = new C00S000Wsdl();
        try {
            UserRespons respons = service.userGruant(request);
            if (null != respons) {
                return respons;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //小圆×的点击处理
    public void ivCancel(View view) {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    /**********
     * FanLL
     **********/

    //扫描或停止条件
    protected boolean scanOrStop = false;
    //控制键盘扫描按键连续点击的布尔变量
    protected boolean isCanScan = true;
    //rfidString
    protected String rfidString;
    //是否有空行
    protected boolean fullOrNot = true;
    public static int P_X = 300;
    public static int P_Y = 240;
    //屏幕的宽度、高度
    protected int screenWidth, screenHeight;

    //把字符串中的小写转为大写
    public static String exChangeBig(String str) {
        StringBuffer sb = new StringBuffer();
        if (str != null) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLowerCase(c)) {
                    sb.append(Character.toUpperCase(c));
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    //将输入的字符串大写显示
    public class AllCapTransformationMethod extends ReplacementTransformationMethod {
        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] aa = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return aa;
        }
    }

    //显示扫描弹框的方法
    public void scanPopupWindow() {
        if (null == popupWindow || !popupWindow.isShowing()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.c00s000_010activity, null);
            popupWindow = new PopupWindow(view, P_X, P_Y);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            popupWindow.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    //单扫方法
    public String singleScan() {
        //设置扫描或停止条件为true
        scanOrStop = true;
        //打开读头
        initRFID();
        //声明data，用来控制读头打开的时间
        Date date = new Date();
        do {
            rfidString = readRfidString(scanOrStop);
            //判断超过扫描时间（10秒），读头关闭
            if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                rfidString = "close";
                scanOrStop = false;
            }
        } //读头不关闭的条件：1、rfidString为空2、rfidString ！= close（没超过扫描时间）3、扫描条件为true
        while (rfidString == null && scanOrStop);
        //关闭读头
        close();
        return rfidString;
    }

    //防止点击扫描后点击此按钮
    public void stopScan() {
        //设置扫描或停止条件为false
        scanOrStop = false;
        //关闭读头
        close();
        //关闭弹框
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    //超过扫描时间的Handler
    @SuppressLint("HandlerLeak")
    public Handler overtimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            //关闭扫描弹框
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            createAlertDialog(null, getString(R.string.overtimeToast), Toast.LENGTH_LONG);
        }
    };

    //处理网络异常的Handler
    @SuppressLint("HandlerLeak")
    public Handler internetErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (null != popupWindow && popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
            if (null != loading && loading.isShowing()) {
                loading.dismiss();
            }
            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
        }
    };

    /**********FanLL**********/

     /*
     * 点击收起软键盘
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            //获取输入框当前的location位置  
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 8.17
     */
    /** 监听homekey and recentapps*/
//    private class VirtualKeyListenerBroadcastReceiver extends BroadcastReceiver {
//        private final String SYSTEM_REASON = "reason";
//        //Home键
//        private final String SYSTEM_HOME_KEY = "homekey";
//        //最近使用的应用键
//        private final String SYSTEM_RECENT_APPS = "recentapps";
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//                String systemReason = intent.getStringExtra(SYSTEM_REASON);
//                if (systemReason != null) {
//                    if (systemReason.equals(SYSTEM_HOME_KEY)) {
//                        System.out.println("按下HOME键");
//                    } else if (systemReason.equals(SYSTEM_RECENT_APPS)) {
//                        System.out.println("按下RECENT_APPS键");
//                        ControlGPIO.newInstance().JNIwriteGPIO(ControlGPIO.OFF);
//                        mSwitchFlag = true;
//                    }
//                }
//            }
//        }
//
//    }

}