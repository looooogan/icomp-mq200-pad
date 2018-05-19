package com.icomp.Iswtmv10.v01c01.c01s001;
/**
 * 新刀入库
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.icomp.Iswtmv10.v01c01.c01s001.modul.JumpModul;
import com.icomp.Iswtmv10.v01c01.c01s001.modul.XinDaoModul;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class C01S001_001Activity extends CommonActivity {

    @BindView(R.id.et_01)
    EditText mEt01;
    @BindView(R.id.tvXiaLa)
    EditText mTvXiaLa;
    //2018年1月17日14:56:24
    @BindView(R.id.tvXiaLaPingGuLeiXing)
    TextView mTvXiaLaPingGuLeiXing;
    @BindView(R.id.et_03)
    EditText mEt03;
    @BindView(R.id.ll_01)
    LinearLayout ll01;
    @BindView(R.id.im_PingGuLeiXing)
    ImageView im_PingGuLeiXing;

    @BindView(R.id.im_01)
    ImageView im01;
    @BindView(R.id.et_02)
    EditText mEt02;
    @BindView(R.id.btnScan)
    Button mBtnScan;
    @BindView(R.id.btnSearch)
    Button mBtnSearch;
    @BindView(R.id.tvNumber)
    TextView mTvNumber;
    @BindView(R.id.tvPosition)
    TextView mTvPosition;
    @BindView(R.id.tvToolId)
    TextView mtvToolId;
    @BindView(R.id.tvNum)
    TextView mTvNum;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private List<XinDaoModul.DataBean.ProcuredBatchCountBean> mList;

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;
    //小号的弹框
    private PopupWindow popupWindow3;

    private Retrofit retrofit;

    private XinDaoModul mXinDaoModul;

    private String count;
    String[] pingGuLeiXing = new String[]{"JKTOOL","GCTOOL"};
    private boolean isCanScan = true;
    private boolean searchOrNot = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s001_001);
        SysApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
        initRetrofit();
        initView();
        //监听订单号改变批次
        addPiCi();
        //监听材料号按钮
        addCaiLiao();
    }

    //监听材料号按钮
    private void addCaiLiao() {
        mEt01.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchOrNot = false;
                clearAllView();

            }
        });
    }

    //监听订单号改变批次
    private void addPiCi() {
        mTvXiaLa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Date date = new Date();
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
                //通过选择的订单号结合日期生成批次
                mEt02.setText(mTvXiaLa.getText().toString().trim()+s.format(date));
            }
        });
    }

    private void initView() {
        mList = new ArrayList<>();
        mEt01.setTransformationMethod(new AllCapTransformationMethod());
    }

    /**
     * 从最后成功画面返回之后需要清除数据
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
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
                clearAllView();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    /**
     * 初始化网络请求框架
     */
    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }

    /**
     * 清空画面
     */
    private void clearEditText(){
        mTvXiaLa.setText("");
        mEt02.setText("");
        mEt01.setText("");
        mTvPosition.setText("");
        mtvToolId.setText("");
        mTvNum.setText("");
    }

    /**
     * 清空画面上所有信息
     */
    private void clearAllView(){
        mList.clear();
        mTvXiaLa.setText("");
        mEt02.setText("");
//        mEt01.clearComposingText();
        mTvNumber.setText("");
        mTvPosition.setText("");
        mTvNum.setText("");
    }

    /**
     * 按钮点击事件
     * @param view
     */
    @OnClick({R.id.tvXiaLa, R.id.btnScan, R.id.btnSearch,R.id.btnCancel,R.id.btnNext, R.id.im_01, R.id.im_PingGuLeiXing, R.id.ll_01})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                mBtnScan.setClickable(false);
                clearEditText();
                isCanScan =false;
                scan();
                break;
            case R.id.ll_01:
                //显示评估类型列表
                showPopupWindowPingGuLeiXing(ll01);
                break;
            case R.id.im_PingGuLeiXing:
                //显示评估类型列表
                showPopupWindowPingGuLeiXing(ll01);
                break;
            case R.id.btnSearch:

                mBtnSearch.setClickable(false);
                if ("".equals(mEt01.getText().toString())){
                    createAlertDialog(this,"请输入材料号或扫描标签",Toast.LENGTH_LONG);
                    clearEditText();
                    mBtnSearch.setClickable(true);
                    return;
                } else {
                    requestInfoForSearch(mEt01.getText().toString());

                }
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                if ("".equals(mTvXiaLaPingGuLeiXing.getText().toString().trim())) {
                    createAlertDialog(this, "请选择评估类型", Toast.LENGTH_SHORT);
                } else if ("".equals(mEt03.getText().toString().trim())) {
                    createAlertDialog(this, "请输入订单序号", Toast.LENGTH_SHORT);
                } else if (mEt03.getText().toString().trim().length() != 5) {
                    createAlertDialog(this, "请输入完整的订单序号", Toast.LENGTH_SHORT);
                } else if("".equals(mEt01.getText().toString().trim()) ||"".equals(mTvNumber.getText().toString()) ||"".equals(mTvNum.getText().toString())){
                    createAlertDialog(this,"请填写完数据",Toast.LENGTH_SHORT);
                } else {
                    JumpModul j = new JumpModul();
                    j.setId(mtvToolId.getText().toString());
                    j.setCailiaohao(mTvNumber.getText().toString());
                    j.setDingdanhao(mTvXiaLa.getText().toString());
                    j.setMaxSize(count);
                    j.setPici(mEt02.getText().toString());
                    String json = new Gson().toJson(j);
                    Intent intent = new Intent(this,c01s001_002Activity.class);
                    intent.putExtra("json",json);
                    intent.putExtra("DingDanXuHao",mEt03.getText().toString().trim());
                    intent.putExtra("PingGuLeiXing",mTvXiaLaPingGuLeiXing.getText().toString());
                    startActivity(intent);
                }
                break;
            case R.id.im_01:
                showPopupWindow();
                break;
            default:
        }
    }


    //显示评估类型列表
    private void showPopupWindowPingGuLeiXing(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //获取自身的长宽高
        v.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        View view = LayoutInflater.from(C01S001_001Activity.this).inflate(R.layout.spinner_c03s004_001, null);
        ListView listView = (ListView) view.findViewById(R.id.ll_spinner);
        MyAdapter1 myAdapter = new MyAdapter1();
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
                mTvXiaLaPingGuLeiXing.setText(pingGuLeiXing[i]);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
//        popupWindow.showAsDropDown(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
    }


    /**
     * 评估类型的适配器
     */
    class MyAdapter1 extends BaseAdapter {
        @Override
        public int getCount() {
            return pingGuLeiXing.length;
        }

        @Override
        public Object getItem(int position) {
            return pingGuLeiXing[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(C01S001_001Activity.this).inflate(R.layout.item_lv, null);
            TextView tv = (TextView) view.findViewById(R.id.tvCategory);
            tv.setText(pingGuLeiXing[position]);
            return view;
        }
    }



    //显示订单号列表
    private void showPopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.item_spanner, null);
        ListView lv = (ListView) contentView.findViewById(R.id.lvSpanner);
        MyAdapter adapter = new MyAdapter();
        lv.setAdapter(adapter);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                mTvXiaLa.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        //选择订单号的下拉选项
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvXiaLa.setText(mList.get(position).getToolsOrdeNO());
                Date date = new Date();
                SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd");
                //通过选择的订单号结合日期生成批次
                mEt02.setText(mList.get(position).getToolsOrdeNO()+s.format(date));
                count = mList.get(position).getProcuredCount();
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.showAsDropDown(mTvXiaLa);

    }
    /**
     * 通过扫码获取信息
     */
    private void scan() {
        //进行扫描操作
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }

    /**
     * 网络请求获取信息（扫描）
     */
    private void requestInfoForScan(String code) {
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getContentScan = iRequest.getContentScanXinDao(code);
        getContentScan.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                isCanScan = true;
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        searchOrNot = true;
                        Gson gson = new Gson();
                        mXinDaoModul = gson.fromJson(json,XinDaoModul.class);
                        mList.clear();
                        mList.addAll(mXinDaoModul.getData().getProcuredBatchCount());
                        //将网络请求返回的数据铺写到对应控件上
                        mEt01.setText(mXinDaoModul.getData().getMaterialNum());
                        mTvNumber.setText(mXinDaoModul.getData().getMaterialNum());
                        mTvNum.setText(mXinDaoModul.getData().getUnitnumber());
                        mTvPosition.setText(mXinDaoModul.getData().getLibraryCodeID());
                        mtvToolId.setText(mXinDaoModul.getData().getToolID());
                        searchOrNot = true;
                        mBtnScan.setClickable(true);
                        loading.dismiss();
//                        if (mXinDaoModul.getData().getMaterialNum().toString().contains("/")) {
//                            //将网络请求返回的数据铺写到对应控件上
//                            mEt01.setText(mXinDaoModul.getData().getMaterialNum());
//                            mTvNumber.setText(mXinDaoModul.getData().getMaterialNum());
//                            mTvNum.setText(mXinDaoModul.getData().getUnitnumber());
//                            mTvPosition.setText(mXinDaoModul.getData().getLibraryCodeID());
//                            mtvToolId.setText(mXinDaoModul.getData().getToolID());
//                            searchOrNot = true;
//                            mBtnScan.setClickable(true);
//
//                        } else {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(C01S001_001Activity.this);
//                            builder.setTitle(R.string.prompt);
//                            builder.setMessage("该材料号是否有小号？");
//                            builder.setCancelable(false);
//                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    //弹框输入小号
//                                    showDialog();
//                                }
//                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int a) {
//                                    //将网络请求返回的数据铺写到对应控件上
//                                    mEt01.setText(mXinDaoModul.getData().getMaterialNum());
//                                    mTvNumber.setText(mXinDaoModul.getData().getMaterialNum());
//                                    mTvNum.setText(mXinDaoModul.getData().getUnitnumber());
//                                    mTvPosition.setText(mXinDaoModul.getData().getLibraryCodeID());
//                                    mtvToolId.setText(mXinDaoModul.getData().getToolID());
//                                    searchOrNot = true;
//                                    mBtnScan.setClickable(true);
//                                }
//                            }).show();
//                        }

                    }else{
                        final JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        mList.clear();

                        if ("1".equals(jsonObject1.getString("code"))) {
                            searchOrNot = true;
                            //如果返回false，则将对应失败信息作为提示框显示到界面上
//                            createAlertDialog(C01S001_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                            loading.dismiss();



                            Gson gson = new Gson();
                            mXinDaoModul = gson.fromJson(json,XinDaoModul.class);
                            if (mXinDaoModul.getData().getMaterialNum().trim().contains("/")) {
                                createAlertDialog(C01S001_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                                //将网络请求返回的数据铺写到对应控件上
                                mEt01.setText(jsonObject1.getString("materialNum"));
                                mTvNumber.setText(jsonObject1.getString("materialNum"));
                                mTvNum.setText(jsonObject1.getString("unitnumber"));
                                mTvPosition.setText(jsonObject1.getString("libraryCodeID"));
                                mtvToolId.setText(jsonObject1.getString("toolID"));
                                searchOrNot = true;
                                mBtnScan.setClickable(true);

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(C01S001_001Activity.this);
                                builder.setTitle(R.string.prompt);
                                builder.setMessage(jsonObject.getString("message") + ",该材料号是否有小号？");
                                builder.setCancelable(false);
                                builder.setPositiveButton("有", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //弹框输入小号
                                        showDialog(mXinDaoModul.getData().getMaterialNum().trim());
                                    }
                                }).setNegativeButton("没有", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int a) {
                                        try {
                                            //将网络请求返回的数据铺写到对应控件上
                                            mEt01.setText(jsonObject1.getString("materialNum"));
                                            mTvNumber.setText(jsonObject1.getString("materialNum"));
                                            mTvNum.setText(jsonObject1.getString("unitnumber"));
                                            mTvPosition.setText(jsonObject1.getString("libraryCodeID"));
                                            mtvToolId.setText(jsonObject1.getString("toolID"));
                                            searchOrNot = true;
                                            mBtnScan.setClickable(true);
                                        } catch (JSONException e) {
                                            mList.clear();
                                            clearAllView();
                                            mBtnScan.setClickable(true);
                                            loading.dismiss();
                                            e.printStackTrace();
                                        }

                                    }
                                }).show();
                            }

                        } else {
                            //如果返回false，则将对应失败信息作为提示框显示到界面上
                            createAlertDialog(C01S001_001Activity.this,jsonObject.getString("message"),Toast.LENGTH_LONG);
                            mEt01.setText("");
                            mList.clear();
                            clearAllView();
                            mBtnScan.setClickable(true);
                            loading.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    mList.clear();
                    clearAllView();
                    mBtnScan.setClickable(true);
                    loading.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                loading.dismiss();
                mBtnScan.setClickable(true);
                createAlertDialog(C01S001_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }

        });

    }


    /**
     * 显示数据提示dialog
     */
    //显示材料号和修磨数量的弹框
    private void showDialog(final String abb) {
        if (null == popupWindow3 || !popupWindow3.isShowing()) {
            //点击查询按钮以后，设置扫描按钮不可用
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.dialog_c01s001_001, null);
            popupWindow3 = new PopupWindow(view, 350, 450);
            popupWindow3.setFocusable(true);
            popupWindow3.setOutsideTouchable(false);
            popupWindow3.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
            final EditText etmaterialNumber = (EditText) view.findViewById(R.id.etmaterialNumber);
            etmaterialNumber.setTransformationMethod(new AllCapTransformationMethod());
            Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
            Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow3.dismiss();
                    mBtnScan.setClickable(true);


                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("".equals(etmaterialNumber.getText().toString().trim())) {
                        createAlertDialog(C01S001_001Activity.this,"请输入小号",0);
                    } else {
                        popupWindow3.dismiss();
                        mBtnSearch.setClickable(true);
                        mBtnScan.setClickable(true);
                        loading.show();
                        String a = abb + "/" + exChangeBig(etmaterialNumber.getText().toString().trim());
                        requestInfoForSearch(a);
                    }
                }
            });
        }
    }




    /**
     * 网络请求获取信息（输入材料号查询）
     */
    private void requestInfoForSearch(String code) {
        loading.show();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getContentSearch = iRequest.getCpmtemtSearchXinDao(code);
        getContentSearch.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                mBtnSearch.setClickable(true);
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        searchOrNot = true;
                        Gson gson = new Gson();
                        mXinDaoModul = gson.fromJson(json,XinDaoModul.class);
                        mList.clear();
                        mList.addAll(mXinDaoModul.getData().getProcuredBatchCount());
                        mEt01.setText(mXinDaoModul.getData().getMaterialNum());
                        mTvNumber.setText(mXinDaoModul.getData().getMaterialNum());
                        mTvNum.setText(mXinDaoModul.getData().getUnitnumber());
                        mTvPosition.setText(mXinDaoModul.getData().getLibraryCodeID());
                    }else{
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        mList.clear();

                        if ("1".equals(jsonObject1.getString("code"))) {
                            searchOrNot = true;
                            //如果返回false，则将对应失败信息作为提示框显示到界面上
                            createAlertDialog(C01S001_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                            //将网络请求返回的数据铺写到对应控件上
                            mEt01.setText(jsonObject1.getString("materialNum"));
                            mTvNumber.setText(jsonObject1.getString("materialNum"));
                            mTvNum.setText(jsonObject1.getString("unitnumber"));
                            mTvPosition.setText(jsonObject1.getString("libraryCodeID"));
                            mtvToolId.setText(jsonObject1.getString("toolID"));
                            mBtnSearch.setClickable(true);
                            mBtnScan.setClickable(true);
                            loading.dismiss();
                        } else {
                            //如果返回false，则将对应失败信息作为提示框显示到界面上
                            createAlertDialog(C01S001_001Activity.this,jsonObject.getString("message"),Toast.LENGTH_LONG);
                            mList.clear();
                            clearAllView();
                            mBtnScan.setClickable(true);
                            loading.dismiss();
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                mBtnSearch.setClickable(true);
                loading.dismiss();
                createAlertDialog(C01S001_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });

    }

    /**
     * 扫描枪实体按钮点击事件
     */
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
            loading.show();
            popupWindow2.dismiss();
            cardString = msg.obj.toString();
            if ("close".equals(cardString)) {
                loading.dismiss();
                createAlertDialog(C01S001_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
                searchOrNot = true;
                mBtnScan.setClickable(true);
            }else{
                requestInfoForScan(cardString);
            }
        }
    };

    /**
     * 订单号内容的适配器
     */
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(C01S001_001Activity.this).inflate(R.layout.item_lv, null);
            TextView tv = (TextView) view.findViewById(R.id.tvCategory);
            tv.setText(mList.get(position).getToolsOrdeNO());
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
