package com.icomp.Iswtmv10.v01c01.c01s004;
/**
 * 补领出库内容确认
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
import com.icomp.Iswtmv10.v01c01.c01s001.C01S001_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s004.modul.BuLingRequestModul;
import com.icomp.common.activity.CommonActivity;
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
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class c01s004_003Activity extends CommonActivity {
    @BindView(R.id.tvShenqingRen)
    TextView mTvShenqingRen;
    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    @BindView(R.id.btnSign)
    Button mBtnSign;

    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.tv_02)
    TextView tv02;

    //17413:轴、17423：齿轮、17433：壳体、17443：同步器、17473：磨刀部（其他）
    String[] a = new String[]{"轴","齿轮","壳体","同步器","磨刀部（其他）"};
    String[] b = new String[]{"17413","17423","17433","17443","17473"};


    private String jsonData;
    private String name;
    private String rfidString;
    private List<BuLingRequestModul> list = new ArrayList<>();
    private List<BuLingTableModul> table;
    //扫描弹框
    private PopupWindow popupWindow2;
    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString,userName, userPass;

    private String authorizedUsersID;//授权人ID

    public cardThread threadcard;

    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s004_003);
        ButterKnife.bind(this);
        initRetrofit();
        initView();
    }

    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }

    /**
     * 将上一画面的信息展示到当前画面，进行信息确认
     */
    private void initView() {
        jsonData = getIntent().getStringExtra("json");
        name = getIntent().getStringExtra("name");
        table = (List<BuLingTableModul>) getIntent().getSerializableExtra("table");
        mTvShenqingRen.setText("申请人："+name);
        if (jsonData == null || jsonData.equals("")) {
            return;
        }
        Gson gson = new Gson();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                BuLingRequestModul c = gson.fromJson(jsonArray.getJSONObject(i).toString(), BuLingRequestModul.class);
                list.add(c);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.show();
        for (int i = 0;i<list.size();i++){
            addLayout(table.get(i),list.get(i));
        }
        loading.dismiss();
    }

    /**
     * 添加布局
     */
    private void addLayout(BuLingTableModul table, BuLingRequestModul b) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_bulingchuku_static, null);
        TextView tvCailiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvHuoWei = (TextView) mLinearLayout.findViewById(R.id.tvHuoWei);
        TextView tvKucun = (TextView) mLinearLayout.findViewById(R.id.tvKuCun);
        TextView tvKeLing = (TextView) mLinearLayout.findViewById(R.id.tvKeLing);
        TextView tvChukushuliang = (TextView) mLinearLayout.findViewById(R.id.tvChukushuliang);
        tvCailiao.setText(exChangeBig(b.getToolCode()));
        tvHuoWei.setText(table.getHuoWei());
        tvKucun.setText(table.getKuCun());
        tvKeLing.setText(String.valueOf(b.getAppliedNumber()));
        tvChukushuliang.setText(b.getReceiveCount());
        mLlContainer.addView(mLinearLayout);
    }

    @OnClick({R.id.btnReturn, R.id.btnSign, R.id.ll_01})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReturn:
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
                showPopupWindow();
                break;
            default:
        }
    }


    private void showPopupWindow() {
        View view = LayoutInflater.from(c01s004_003Activity.this).inflate(R.layout.spinner_c03s004_001, null);
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
                tv01.setText(a[i]);
                tv02.setText(b[i]);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
    }

    //流水线的Adapter
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return a.length;
        }

        @Override
        public Object getItem(int i) {
            return a[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(c01s004_003Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(a[i]);
            return view1;
        }

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
        final Intent intent = new Intent(this,c01s004_004Activity.class);

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

    //登录方式界面，小圆×的点击处理
    @Override
    public void ivCancel(View view) {
//        bright();
        popupWindow.dismiss();
        showDialog();
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
                createAlertDialog(c01s004_003Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
                showDialog();
            } else {
                try {
//                    if ("1".equals(respons.getStateCode())) {
//                        //提示扫描登录失败的原因
//                        createAlertDialog(C01S003_002Activity.this, respons.getStateMsg(), Toast.LENGTH_SHORT);
//                    }
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
                createAlertDialog(c01s004_003Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
                loading.dismiss();
                new AlertDialog.Builder(c01s004_003Activity.this).
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
                requestData();
            } else {
                loading.dismiss();
                new AlertDialog.Builder(c01s004_003Activity.this).
                        setTitle(R.string.prompt).
                        setMessage(respons.getStateMsg()).
                        setCancelable(false).
                        setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                showDialog();
                            }
                        }).show();
//                createAlertDialog(c01s004_003Activity.this, respons.getStateMsg(), Toast.LENGTH_LONG);
//                showDialog();
            }
        }
    };

    /**
     * 将customerID填写到每一条数据上
     */
    private void addData(){
        String id = getSharedPreferences("userInfo", Context.MODE_APPEND).getString("customerID","");
        for (int i = 0;i<list.size();i++){
            list.get(i).setCustomerID(id);
            list.get(i).setGruantUserID(authorizedUsersID);
//            list.get(i).setGruantUserID(authorizedUsersID);
        }
    }

    /**
     * 将补领数据提交
     */
    private void requestData() {
        addData();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> saveFReplacementapplyInfo = iRequest.saveFReplacementapplyInfo(new Gson().toJson(list), tv02.getText().toString());
        saveFReplacementapplyInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s004_003Activity.this,c01s004_004Activity.class);
                        startActivity(intent);
                    }else{
                        createAlertDialog(c01s004_003Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s004_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 授权线程
     */
    private class shouquanThread extends Thread {
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
                loading.show();
                requestData();

            } else {
                createAlertDialog(c01s004_003Activity.this, response.getStateMsg(), Toast.LENGTH_LONG);
                loading.dismiss();
            }

        }
    };


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
            Button btnLogin = (Button) vPopupWindow.findViewById(R.id.btn_login);
            btnLogin.setText("签收");
            TextView title = (TextView) vPopupWindow.findViewById(R.id.btn_title);
            title.setText("输入签收");
            LinearLayout ll_01 = (LinearLayout) vPopupWindow.findViewById(R.id.ll_01);//目的：点击空白收起键盘
            ll_01.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    InputMethodManager inputMethodManager = (InputMethodManager) c01s004_003Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    loading.dismiss();
                    if ("".equals(userName)) {
                        createAlertDialog(c01s004_003Activity.this, getString(R.string.nameIsNotNull), Toast.LENGTH_SHORT);
                        return;
                    } else if ("".equals(userPass)) {
                        createAlertDialog(c01s004_003Activity.this, getString(R.string.passWordIsNotNull), Toast.LENGTH_SHORT);
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

    /**
     * 刷卡签收loading
     *
     * @param pageID
     */
    private void showWindow1(int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        popupWindow = new PopupWindow(vPopupWindow, 300, 240, true);
//        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        threadcard = new cardThread();
        threadcard.start();
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        showWindow1(R.layout.c00s000_010activity);
    }
}
