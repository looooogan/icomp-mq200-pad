package com.icomp.Iswtmv10.v01c01.c01s003;
/**
 * 换领内容填写
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.icomp.Iswtmv10.v01c01.c01s003.modul.bean;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.WifiUtils;
import com.icomp.wsdl.v01c00.c00s000.endpoint.UserRespons;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class C01S003_002Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnSign)
    Button mBtnSign;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.tv_02)
    TextView tv02;

    private String jsonString;
    private List<ChuKuTableModul> list = new ArrayList<>();

    //17413:轴、17423：齿轮、17433：壳体、17443：同步器、17473：磨刀部（其他）
    String[] wwwwww = new String[]{"轴","齿轮","壳体","同步器","磨刀部（其他）"};
    String[] b = new String[]{"17413","17423","17433","17443","17473"};

    private String rfidString;

    //扫描弹框
    private PopupWindow popupWindow2;
    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString,userName, userPass;

    private List<bean> redemptionApplyList = new ArrayList<>();

    private String authorizedUsersID;//授权人ID

    public cardThread threadcard;

    private Retrofit retrofit;

//    private boolean isCanScan = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s003_002);
        ButterKnife.bind(this);
        initView();
        initRetrofit();
    }

    /**
     * 初始化界面，将上一画面的数据铺到当前画面
     */
    private void initView() {
        Intent intent = getIntent();
        jsonString = intent.getStringExtra("table");
        Gson gson  =  new Gson();
        try {

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i=0;i<jsonArray.length();i++){
                ChuKuTableModul c = gson.fromJson(jsonArray.getJSONObject(i).toString(),ChuKuTableModul.class);
                list.add(c);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.show();
        for (int i = 0;i<list.size();i++){
            addLayout(i);
        }
        loading.dismiss();
        String handMacCode = WifiUtils.getMacAddress(getApplicationContext());
        for (int i = 0;i<list.size();i++){
            bean b = new bean();
            b.setRfidCode(list.get(i).getRfidCode());
            b.setToolType(list.get(i).getToolType());
            b.setToolID(list.get(i).getToolID());
            b.setCustomerID(getSharedPreferences("userInfo", Context.MODE_APPEND).getString("customerID",""));

            b.setHandSetID(handMacCode);
            b.setInventory(list.get(i).getKucun());
            b.setLibraryCodeID(list.get(i).getHuoWei());
            b.setMaterialNum(list.get(i).getCaiLiao());
            b.setReceiveCount(list.get(i).getChuku());
            b.setRequestNum(list.get(i).getShenqing());
            redemptionApplyList.add(b);
        }
    }

    @OnClick({R.id.btnCancel, R.id.btnSign, R.id.ll_01})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSign:
                if (tv01.getText().toString().trim() == null || "".equals(tv01.getText().toString().trim())) {
                    createAlertDialog(this, "请选择材料的出库去向" ,Toast.LENGTH_LONG);
                } else {
                showDialog();
                }
                break;
            case R.id.ll_01:
                //显示流水线列表
                showPopupWindow(ll01);
                break;
            default:
        }
    }


    private void showPopupWindow(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //获取自身的长宽高
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        View view = LayoutInflater.from(C01S003_002Activity.this).inflate(R.layout.spinner_c03s004_001, null);
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
                tv01.setText(wwwwww[i]);
                tv02.setText(b[i]);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
//        popupWindow.showAsDropDown(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
    }

    //流水线的Adapter
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return wwwwww.length;
        }

        @Override
        public Object getItem(int i) {
            return wwwwww[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(C01S003_002Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(wwwwww[i]);
            return view1;
        }

    }


    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }

    /**
     * 添加布局
     */
    private void addLayout(int i) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_huanlingchuku2, null);
        ChuKuModul c = new ChuKuModul();
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvHuoWei = (TextView) mLinearLayout.findViewById(R.id.tvHuoWei);
        TextView tvShenqing = (TextView) mLinearLayout.findViewById(R.id.tvShenqing);
        TextView tvKuCun = (TextView) mLinearLayout.findViewById(R.id.tvKuCun);
        TextView tvChukushuliang = (TextView) mLinearLayout.findViewById(R.id.tvChukushuliang);
        TextView tvId = (TextView) mLinearLayout.findViewById(R.id.tvId);
        TextView tvToolType = (TextView) mLinearLayout.findViewById(R.id.tvToolType);
        TextView tvRfidCode = (TextView) mLinearLayout.findViewById(R.id.tvRfidCode);
        ImageView ivRemove = (ImageView) mLinearLayout.findViewById(R.id.ivRemove);
        tvCailiao.setText(list.get(i).getCaiLiao());
        tvHuoWei.setText(list.get(i).getHuoWei());
        tvShenqing.setText(list.get(i).getShenqing());
        tvKuCun.setText(list.get(i).getKucun());
        tvChukushuliang.setText(list.get(i).getChuku());
        mLlContainer.addView(mLinearLayout);
    }

    /**
     * 显示数据提示dialog
     */
    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_sign, null);
        TextView tvShuru = (TextView) view.findViewById(R.id.tvShuru);
        TextView tvShuaka = (TextView) view.findViewById(R.id.tvShuaKa);
        TextView tvFanhui = (TextView) view.findViewById(R.id.tvFanhui);
        tvShuru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow2 != null) {
                    popupWindow2.dismiss();
                }
                scanf = false;
                close();
                showWindow(R.layout.c00s000_001_1activity);
                dialog.dismiss();
            }
        });
        tvShuaka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showWindow1(R.layout.c00s000_010activity);
                dialog.dismiss();
            }
        });
        tvFanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (screenWidth*0.8), (int) (screenHeight*0.6));
    }

    /**
     * 刷卡签收loading
     *
     * @param pageID
     */
    private void showWindow1(int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        popupWindow = new PopupWindow(vPopupWindow, 300, 240, true);
        popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        threadcard = new cardThread();
        threadcard.start();
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        showWindow1(R.layout.c00s000_010activity);
    }

    //登录方式界面，小圆×的点击处理
    @Override
    public void ivCancel(View view) {
//        bright();
        popupWindow.dismiss();
        showDialog();
    }

    //弹出输入方式的弹框
    public void showWindow(int pageID) {
        //在哪个页面打开窗口
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //用inflate方法调用界面
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        //从res下选择要作为背景的源文件
        //vPopupWindow.setBackgroundResource(R.drawable.login);
        //设置其背景
        //popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow = new PopupWindow(vPopupWindow, (int) (0.8 * screenWidth), (int) (0.6 * screenHeight), true);
        //背景置灰
//        dark();
        //设置弹框外部无法获取焦点
        popupWindow.setFocusable(true);
        //扫卡登录
        if (pageID == R.layout.c00s000_001_2activity) {
            //设置弹框外部无法获取焦点
            popupWindow.setFocusable(true);
            //设置popWindow的显示位置
            popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
            //弹框上的扫描按钮
            final Button btnScan = (Button) vPopupWindow.findViewById(R.id.btn_scan);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出扫描悬框
                    if (popupWindow2 == null || !popupWindow2.isShowing()) {
                        showWindow2(R.layout.c00s000_010activity);
                    }
                }
            });
            //输入登录
        } else if (pageID == R.layout.c00s000_001_1activity) {
            //设置弹框外部无法获取焦点
            popupWindow.setFocusable(true);
            //设置popWindow的显示位置
            popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
            //弹框上的登录按钮
            TextView tvTitle = (TextView) vPopupWindow.findViewById(R.id.btn_title);

            Button btnLogin = (Button) vPopupWindow.findViewById(R.id.btn_login);
            tvTitle.setText("输入签收");
            btnLogin.setText("签收");
            LinearLayout ll_01 = (LinearLayout) vPopupWindow.findViewById(R.id.ll_01);//目的：点击空白收起键盘
            ll_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager inputMethodManager = (InputMethodManager) C01S003_002Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            });
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //弹出等待对话框
                    loading.show();
                    //弹框弹出十秒未响应，弹框消失
                    Date date = new Date();
                    if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                        loading.dismiss();
                    }
                    //用户名
                    EditText txtUserName = (EditText) vPopupWindow.findViewById(R.id.et_username);
                    userName = txtUserName.getText().toString();
                    //密码
                    EditText txtUserPass = (EditText) vPopupWindow.findViewById(R.id.et_password);
                    userPass = txtUserPass.getText().toString();
                    //判断用户名或密码是否为空
                    if ("".equals(userName)) {
                        loading.dismiss();
                        createAlertDialog(C01S003_002Activity.this, getString(R.string.nameIsNotNull), Toast.LENGTH_SHORT);
                        return;
                    } else if ("".equals(userPass)) {
                        loading.dismiss();
                        createAlertDialog(C01S003_002Activity.this, getString(R.string.passWordIsNotNull), Toast.LENGTH_SHORT);
                        return;
                    }

                    shouquan = new shouquanThread();
                    shouquan.start();
                }
            });

            //弹框上的重置按钮
            Button btnClearUser = (Button) vPopupWindow.findViewById(R.id.btn_reset);
            btnClearUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //用户名
                    EditText txtUserName = (EditText) vPopupWindow.findViewById(R.id.et_username);
                    //密码
                    EditText txtUserPass = (EditText) vPopupWindow.findViewById(R.id.et_password);
                    //用户名置空
                    txtUserName.setText("");
                    //密码置空
                    txtUserPass.setText("");
                }
            });
        }
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

    //扫描线程
    private VisitJniThread thread;

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
            //员工卡RfidCode
            popupWindow2.dismiss();
            cardString = msg.obj.toString();
            if ("close".equals(cardString)) {
                createAlertDialog(C01S003_002Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            } else {
                try {

//                    new cardThread().start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //网络连接失败
                    createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_SHORT);
                    loading.dismiss();
                }
            }
        }
    };

    /**
     * 刷卡签收线程
     */
    public class cardThread extends Thread {
        @Override
        public void run() {
            initRFID();
            Date date = new Date();
            rfidString = null;
            do {
                rfidString = readRfidString(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    rfidString = "close";
                }
            } while (rfidString == null);
            close();
            if (null != rfidString) {
                Message message = new Message();
                message.obj = rfidString;
                cardhandler.sendMessage(message);
            }

        }
    }


    @SuppressLint("HandlerLeak")
    Handler cardhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                new AlertDialog.Builder(C01S003_002Activity.this).
                        setTitle(R.string.prompt).
                        setMessage(getString(R.string.C01S001001_1)).
                        setCancelable(false).
                        setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showDialog();
                            }
                        }).show();
            } else {
                //调用刷卡签收授权
                loading.show();
                threadShuaka = new ShuakaThread();
                threadShuaka.start();
            }
            popupWindow.dismiss();
        }

    };

    /**
     * 刷卡签收授权线程
     */
    public class ShuakaThread extends Thread {
        @Override
        public void run() {
            UserRespons respons = authorize("C01S003_002Activity", rfidString);//刷卡授权
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

    public ShuakaThread threadShuaka;
    Handler threadShuakaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UserRespons respons = (UserRespons) msg.obj;
            if ("0".equals(respons.getStateCode())) {
                authorizedUsersID = respons.getCustomer().getCustomerID();//返回授权人ID
                loading.show();
                for (int i = 0;i<redemptionApplyList.size();i++){
                    redemptionApplyList.get(i).setReceiveUser(authorizedUsersID);
                }
                requestData();

            } else {
                new AlertDialog.Builder(C01S003_002Activity.this).
                        setTitle(R.string.prompt).
                        setMessage(respons.getStateMsg()).
                        setCancelable(false).
                        setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showDialog();
                            }
                        }).show();
                loading.dismiss();
            }
        }
    };

    /**
     * 授权线程
     */
    public class shouquanThread extends Thread {
        @Override
        public void run() {
            UserRespons response = authorize(userName, userPass, "C01S003_002Activity");//返回值是授权用户ID
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
                authorizedUsersID = response.getCustomer().getCustomerID();//返回授权人Id
                for (int i = 0;i<redemptionApplyList.size();i++){
                    redemptionApplyList.get(i).setReceiveUser(authorizedUsersID);
                }
                requestData();

            } else {
                createAlertDialog(C01S003_002Activity.this, response.getStateMsg(), Toast.LENGTH_LONG);
                loading.dismiss();
            }
        }
    };

    /**
     * 提交换领数据
     */
    private void requestData(){
        loading.show();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> saveFRedemptionapplyInfo = iRequest.saveFRedemptionapplyInfo(new Gson().toJson(redemptionApplyList), tv02.getText().toString().trim());
        saveFRedemptionapplyInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        loading.dismiss();
                        Intent intent = new Intent(C01S003_002Activity.this, c01s003_003Activity.class);
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S003_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S003_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }
}
