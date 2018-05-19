package com.icomp.Iswtmv10.v01c01.c01s003;
/**
 * 换领出库
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.icomp.Iswtmv10.v01c01.c01s003.modul.HuanLinResponseBean;
import com.icomp.common.activity.CommonActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class C01S003_001Activity extends CommonActivity implements View.OnClickListener {

    @BindView(R.id.ivAdd)
    ImageView mIvAdd;
    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnSearch)
    Button mBtnSearch;
    @BindView(R.id.btnScan)
    Button mBtnScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;


    private List<View> mViewList = new ArrayList<>();
    private List<ChuKuModul> beanList = new ArrayList<>();
    private int position = 0;
    private Map<ImageView, Integer> keyMap = new HashMap<>();
    private boolean isFull = true; //是否有剩余条目
    private String mLastEditTextString;
    private int SCAN = 0, SEARCH = 1;
    //小号的弹框
    private PopupWindow popupWindow3;

    private Retrofit retrofit;

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;
    private boolean isCanScan = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s003_001);
        ButterKnife.bind(this);
        initView();
        initRetrofit();
    }
    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }
    /**
     * 添加布局
     */
    private void addLayout() {
        if (!isFull) {
            createAlertDialog(this,"请把数据填写完再加",Toast.LENGTH_LONG);
            return;
        }
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_huanlingchuku, null);
        ChuKuModul c = new ChuKuModul();
        EditText etCailiao = (EditText) mLinearLayout.findViewById(R.id.etCailiao);
        TextView tvHuoWei = (TextView) mLinearLayout.findViewById(R.id.tvHuoWei);
        TextView tvShenqing = (TextView) mLinearLayout.findViewById(R.id.tvShenqing);
        TextView tvKuCun = (TextView) mLinearLayout.findViewById(R.id.tvKuCun);
        TextView tvChukushuliang = (TextView) mLinearLayout.findViewById(R.id.tvChukushuliang);
        TextView tvId = (TextView) mLinearLayout.findViewById(R.id.tvId);
        TextView tvToolType = (TextView) mLinearLayout.findViewById(R.id.tvToolType);
        TextView tvRfidCode = (TextView) mLinearLayout.findViewById(R.id.tvRfidCode);
        etCailiao.setTransformationMethod(new AllCapTransformationMethod());
        ImageView ivRemove = (ImageView) mLinearLayout.findViewById(R.id.ivRemove);
        mLinearLayout.setTag(position);
        position++;
        ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout child = (LinearLayout) mLlContainer.getChildAt(mLlContainer.getChildCount()-1);
                boolean isLast = false;
                if(mLinearLayout.getTag() == child.getTag()){
                    isLast = true;
                }
                if(!isFull && isLast){
                    isFull = true;
                }
                mLlContainer.removeView(mLinearLayout);

            }
        });
        mViewList.add(mLinearLayout);
        mLlContainer.addView(mViewList.get(mViewList.size() - 1));
        isFull = false;
    }

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
                mLlContainer.removeAllViews();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivAdd:
                addLayout();
                break;//添加布局点击事件
            case R.id.btnSearch:
                String num = searchLastNum();
                if(null != num && !"".equals(num)){
                    searchForCaiLiao(num,SEARCH);
                }else{
                    createAlertDialog(this,"请输入材料号",0);
                }
                break;//查询
            case R.id.btnScan:
                scanForNum();
                break;//扫描
            case R.id.btnCancel:finish();
                break;
            case R.id.btnNext:
                String temp = bianliAndToJson();
                if(temp == null){
                    temp = "";
                    return;
                }
                Intent intent = new Intent(this,C01S003_002Activity.class);
                intent.putExtra("table",temp);
                startActivity(intent);
                break;
            default:
        }
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        super.connect();
        if(isCanScan){
            isCanScan = false;
        }else{
            return;
        }
        scanForNum();
    }

    private void initView() {
        mIvAdd.setOnClickListener(this);
        mBtnSearch.setOnClickListener(this);
        mBtnScan.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);
    }

    /**
     * 添加数据
     */
    private void addData(ChuKuModul c){
        LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(mLlContainer.getChildCount()-1);
        for (int i = 0; i<mDataLin.getChildCount();i++){
            View child = mDataLin.getChildAt(i);
            if(child instanceof LinearLayout){
                int child2Coutn = ((LinearLayout) child).getChildCount();
                for (int j = 0;j<child2Coutn;j++){
                   View child2 = ((LinearLayout) child).getChildAt(j);
                    if(child2 instanceof EditText){
                        switch (child2.getId()){
                            case R.id.etCailiao:
                                ((EditText)child2).setText(exChangeBig(c.getCaiLiao()));
                                ((EditText)child2).setEnabled(false);
                                ((EditText)child2).setFocusable(false);break;
                            default:

                        }
                    }else if(child2 instanceof TextView){
                        switch (child2.getId()){
                            case R.id.tvHuoWei:
                                ((TextView)child2).setText(c.getHuoWei());break;
                            case R.id.tvShenqing:
                                ((TextView)child2).setText(c.getShenqing());break;
                            case R.id.tvKuCun:
                                ((TextView)child2).setText(c.getKucun());break;
                            case R.id.tvChukushuliang:
                                ((TextView)child2).setText(c.getChuku());break;
                            case R.id.tvId:
                                ((TextView)child2).setText(c.getToolID());break;
                            case R.id.tvToolType:
                                ((TextView)child2).setText(c.getToolType());break;
                            case R.id.tvRfidCode:
                                ((TextView)child2).setText(c.getRfidCode());break;
                            default:
                        }

                    }
                }
            }

        }
        /**
         * 最新行数据已满，可以添加下一行
         */
        isFull = true;
    }

    /**
     * 显示数据提示dialog
     */
    private void showDialog(final String num, final int Tag, final ChuKuModul c){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialog);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        View view = View.inflate(this, R.layout.dialog_huanling_c, null);
        final TextView tvCaiLiao = (TextView) view.findViewById(R.id.tvCaiLiao);
        TextView tvHuoWei = (TextView) view.findViewById(R.id.tvHuoWei);
        final TextView tvShenqing = (TextView) view.findViewById(R.id.tvShenqing);
        final TextView tvKuCun = (TextView) view.findViewById(R.id.tvKuCun);
        final EditText etMuch = (EditText) view.findViewById(R.id.etMuch);
        etMuch.setFocusable(true);
        if(!("0").equals(c.getToolType())){
            tvShenqing.setVisibility(View.GONE);
        }
        etMuch.setFocusableInTouchMode(true);
        etMuch.requestFocus();
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Button btnSure = (Button) view.findViewById(R.id.btnSure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMuch.getText().toString().isEmpty() || Integer.valueOf(etMuch.getText().toString())<=0){
                    createAlertDialog(C01S003_001Activity.this, "请填写出库数量", Toast.LENGTH_SHORT);
                    return;
                }
                int much = 0;
                if(("0").equals(c.getToolType())){

                    int shenqing = Integer.parseInt(c.getShenqing());
                    int kucun = Integer.parseInt(c.getKucun());
                    much = Integer.parseInt(etMuch.getText().toString());
                    int min = Math.min(shenqing,kucun);
                    if(much>min){
                        createAlertDialog(C01S003_001Activity.this, "数量超出限额，请重新填写", Toast.LENGTH_SHORT);
                        return;
                    }
                }else{
                    int kucun = Integer.parseInt(c.getKucun());
                    much = Integer.parseInt(etMuch.getText().toString());
                    if(much>kucun){
                        createAlertDialog(C01S003_001Activity.this, "数量超出限额，请重新填写", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                c.setChuku(String.valueOf(much));
                if(isFull && Tag == SCAN){
                    if(checkIsExit(c.getCaiLiao())){
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        createAlertDialog(C01S003_001Activity.this,"该材料号已存在",Toast.LENGTH_SHORT);
                        return;
                    }else{
                        addScanLayout(num,c,SCAN);
                    }
                }else {
                    if (isFull) {
                        if(dialog.isShowing()){
                            dialog.dismiss();
                        }
                        createAlertDialog(C01S003_001Activity.this, "请增加新的一行然后进行查询", Toast.LENGTH_SHORT);
                    }else{
                        if(checkIsExit(c.getCaiLiao())){
                            if(dialog.isShowing()){
                                dialog.dismiss();
                            }
                            createAlertDialog(C01S003_001Activity.this,"该材料号已存在",Toast.LENGTH_SHORT);
                            return;
                        }else{
                            addData(c);
                        }

                    }
                }

                if(dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        tvCaiLiao.setText("材料号："+c.getCaiLiao());
        tvHuoWei.setText("货位："+c.getHuoWei());
        tvShenqing.setText("申请数量："+c.getShenqing());
        tvKuCun.setText("库存数量："+c.getKucun());
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (screenWidth*0.8), (int) (screenHeight*0.6));
    }

    /**
     * 根据材料号进行网络请求
     */
    private void searchForCaiLiao(String num,int Tag){
            if (isFull && Tag == SEARCH) {
                isCanScan = true;
                createAlertDialog(C01S003_001Activity.this, "请增加新的一行然后进行查询", Toast.LENGTH_SHORT);
                return;
            }else{
                /**
                 * 进行网络请求
                 */
                if(Tag == SEARCH){
                    requestData(num,SEARCH);
                }else{
                    requestData(num,SCAN);
                }
            }


    }

    private String requestData(final String code, final int Tag) {
        loading.show();
        final String[] data = {null};
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getFRedemptionapplyInfo;
        if (Tag == SEARCH) {
            getFRedemptionapplyInfo = iRequest.getFRedemptionapplyInfo(code, "");
        } else {
            getFRedemptionapplyInfo = iRequest.getFRedemptionapplyInfo("", code);
        }
        getFRedemptionapplyInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                isCanScan = true;
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        data[0] = json;
                        final HuanLinResponseBean bean = new Gson().fromJson(json,HuanLinResponseBean.class);
                        if (bean == null){
                            return;
                        }
                        ChuKuModul c = new ChuKuModul();
                        if (Tag == SEARCH || bean.getData().getMaterialNum().trim().contains("/")) {

                            c.setShenqing(String.valueOf(bean.getData().getRequestNum()));
                            c.setKucun(bean.getData().getInventory());
                            c.setHuoWei(bean.getData().getLibraryCodeID());
                            c.setCaiLiao(bean.getData().getMaterialNum());
                            c.setToolType(bean.getData().getToolType());
                            c.setToolID(bean.getData().getToolID());
                            c.setRfidCode(bean.getData().getRfidCode());
                            showDialog(code,Tag,c);
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(C01S003_001Activity.this);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage("该材料号是否有小号");
                            builder.setCancelable(false);
                            builder.setPositiveButton("有", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //弹框输入小号
                                    showDialog1(bean.getData().getMaterialNum().trim());
                                }
                            }).setNegativeButton("没有", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int a) {
                                    //将网络请求返回的数据铺写到对应控件上
                                    ChuKuModul c = new ChuKuModul();
                                    c.setShenqing(String.valueOf(bean.getData().getRequestNum()));
                                    c.setKucun(bean.getData().getInventory());
                                    c.setHuoWei(bean.getData().getLibraryCodeID());
                                    c.setCaiLiao(bean.getData().getMaterialNum());
                                    c.setToolType(bean.getData().getToolType());
                                    c.setToolID(bean.getData().getToolID());
                                    c.setRfidCode(bean.getData().getRfidCode());
                                    showDialog(code,Tag,c);
                                }
                            }).show();
                        }
                    } else {
                        createAlertDialog(C01S003_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                loading.dismiss();
                createAlertDialog(C01S003_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
        return data[0];
    }

    /**
     * 显示数据提示dialog
     */
    //显示材料号和修磨数量的弹框
    private void showDialog1(final String abb) {
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
                        createAlertDialog(C01S003_001Activity.this,"请输入小号",0);
                    } else {
                        popupWindow3.dismiss();
                        mBtnSearch.setClickable(true);
                        mBtnScan.setClickable(true);
                        loading.show();
                        String a = abb + "/" + exChangeBig(etmaterialNumber.getText().toString().trim());
                        requestData1(a,SEARCH);

                    }
                }
            });
        }
    }




    //扫描后第二次查询
    private String requestData1(final String code, final int Tag) {
        loading.show();
        final String[] data = {null};
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getFRedemptionapplyInfo;
        getFRedemptionapplyInfo = iRequest.getFRedemptionapplyInfo(code, "");
        getFRedemptionapplyInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                isCanScan = true;
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        data[0] = json;
                        final HuanLinResponseBean bean = new Gson().fromJson(json,HuanLinResponseBean.class);
                        if (bean == null){
                            return;
                        }
                        ChuKuModul c = new ChuKuModul();
                        c.setShenqing(String.valueOf(bean.getData().getRequestNum()));
                        c.setKucun(bean.getData().getInventory());
                        c.setHuoWei(bean.getData().getLibraryCodeID());
                        c.setCaiLiao(bean.getData().getMaterialNum());
                        c.setToolType(bean.getData().getToolType());
                        c.setToolID(bean.getData().getToolID());
                        c.setRfidCode(bean.getData().getRfidCode());
                        showDialog(code,SCAN,c);
                    } else {
                        createAlertDialog(C01S003_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                loading.dismiss();
                createAlertDialog(C01S003_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
        return data[0];
    }




    /**
     * 查找最后一组数据的材料号
     */
    private String searchLastNum(){
        LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(mLlContainer.getChildCount()-1);
        if(mDataLin == null || isFull){
//            Toast.makeText(this,"请先增加一行，然后进行查询",Toast.LENGTH_SHORT).show();
            return null;
        }
        String lastEditTextString = null;
        for (int i = 0; i<mDataLin.getChildCount();i++){
            View child = mDataLin.getChildAt(i);
            if(child instanceof LinearLayout){
                int child2Coutn = ((LinearLayout) child).getChildCount();
                for (int j = 0;j<child2Coutn;j++){
                    View child2 = ((LinearLayout) child).getChildAt(j);
                    if(child2 instanceof EditText){
                        lastEditTextString = ((EditText) child2).getText().toString();
                        ((EditText) child2).setText("");
                    }
                }
            }
        }
        return lastEditTextString;
    }


    /**
     * 通过扫描获得材料号
     */
    private String scanForNum(){
        //进行扫描操作
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
        return "";
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
                createAlertDialog(C01S003_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                //根据员工卡rfid进行网络请求
                searchForCaiLiao(cardString,SCAN);
            }

        }
    };

    /**
     * 通过扫描添加新的一行
     */
    private void addScanLayout(String num,ChuKuModul c,int Tag){
        if (c == null){
            c = new ChuKuModul();
        }
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_huanlingchuku, null);
        EditText etCailiao = (EditText) mLinearLayout.findViewById(R.id.etCailiao);
        TextView tvHuoWei = (TextView) mLinearLayout.findViewById(R.id.tvHuoWei);
        TextView tvShenqing = (TextView) mLinearLayout.findViewById(R.id.tvShenqing);
        TextView tvKuCun = (TextView) mLinearLayout.findViewById(R.id.tvKuCun);
        TextView tvId = (TextView) mLinearLayout.findViewById(R.id.tvId);
        TextView tvToolType = (TextView) mLinearLayout.findViewById(R.id.tvToolType);
        TextView tvRfidCode = (TextView) mLinearLayout.findViewById(R.id.tvRfidCode);
        TextView tvChukushuliang = (TextView) mLinearLayout.findViewById(R.id.tvChukushuliang);
        ImageView ivRemove = (ImageView) mLinearLayout.findViewById(R.id.ivRemove);
        tvId.setText(c.getToolID());
        tvToolType.setText(c.getToolType());
        tvRfidCode.setText(c.getRfidCode());
        etCailiao.setText(c.getCaiLiao());
        etCailiao.setEnabled(false);
        etCailiao.setFocusable(false);
        tvHuoWei.setText(c.getHuoWei());
        tvShenqing.setText(c.getShenqing());
        tvKuCun.setText(c.getKucun());
        tvChukushuliang.setText(c.getChuku());
        mLinearLayout.setTag(position);
        position++;
        ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout child = (LinearLayout) mLlContainer.getChildAt(mLlContainer.getChildCount()-1);
                boolean isLast = false;
                if(mLinearLayout.getTag() == child.getTag()){
                    isLast = true;
                }
                if(!isFull && isLast){
                    isFull = true;
                }
                mLlContainer.removeView(mLinearLayout);

            }
        });
        mViewList.add(mLinearLayout);
        mLlContainer.addView(mViewList.get(mViewList.size() - 1));
    }


    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        beanList.clear();
        if(mLlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    ChuKuModul c = new ChuKuModul();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof EditText){
                        c.setCaiLiao(((EditText) child2).getText().toString());
                        }else if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvHuoWei:
                                    c.setHuoWei(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvShenqing:
                                    if("".equals(((TextView)child2).getText().toString())){
                                        createAlertDialog(this,"请填写完整最后一行或删除",0);
                                        return null;
                                    }
                                    c.setShenqing(((TextView)child2).getText().toString());break;
                                case R.id.tvKuCun:
                                    c.setKucun(((TextView)child2).getText().toString());break;
                                case R.id.tvChukushuliang:
                                    c.setChuku(((TextView)child2).getText().toString());break;
                                case R.id.tvId:
                                    c.setToolID(((TextView)child2).getText().toString());break;
                                case R.id.tvToolType:
                                    c.setToolType(((TextView)child2).getText().toString());break;
                                case R.id.tvRfidCode:
                                    c.setRfidCode(((TextView)child2).getText().toString());break;
                                default:
                            }
                        }
                    }
                    beanList.add(c);

                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(beanList);
    }


    /**
     * 遍历所有数据并转化为json
     */
    private boolean checkIsExit(String code){
        if(mLlContainer.getChildCount() == 0){
            return false;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof EditText){
                            if(((EditText) child2).getText().toString().equals(code)){
                                return true;
                            }
                        }
                    }
                }

            }
        }
        return false;
    }
}
