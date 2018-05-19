package com.icomp.Iswtmv10.v01c01.c01s005;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import com.icomp.common.activity.CommonActivity;

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
import retrofit2.Response;
import retrofit2.Retrofit;
/**
 * 一体刀具报废
 */
public class c01s005_002_2Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.tvScan)
    TextView mTvScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private int position = 0;
    private List<String> jsonList = new ArrayList<>();

    private List<String> rfidContainerIDList = new ArrayList<>();

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;
    private HashMap<Integer,String> rifdMap = new HashMap<>();

    private Retrofit mRetrofit;

    private boolean isCanScan = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_002_2);
        ButterKnife.bind(this);
        initRetrofit();
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    @OnClick({R.id.tvScan, R.id.btnCancel, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvScan:
                mTvScan.setClickable(false);
                scan();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                String temp = bianliAndToJson();
                Intent intent = new Intent(this,c01s005_003_2Activity.class);
                Gson gson = new Gson();
                String rifd = gson.toJson(rfidContainerIDList);
                if(temp == null || rifd == null){
                    createAlertDialog(this,"请输入数据",0);
                    return;
                }
                intent.putExtra("json",temp);
                intent.putExtra("rifd", rifd);
                startActivity(intent);
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
        mTvScan.setClickable(false);
        scan();
    }

    /**
     * 扫描
     */
    private void scan(){
        isCanScan = false;
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }

    /**
     * 添加布局
     */
    private void addLayout(String s,String r ,String laserCode) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_yiti_daojubaofei, null);
        final TextView tvCaiLiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvsingleProductCode = (TextView) mLinearLayout.findViewById(R.id.tvsingleProductCode);//单品编码
        TextView tvRfidContain = (TextView) mLinearLayout.findViewById(R.id.tvRfidContain);
        ImageView tvRemove = (ImageView) mLinearLayout.findViewById(R.id.tvRemove);
        tvCaiLiao.setTag(position);
        rifdMap.put((Integer) tvCaiLiao.getTag(),cardString);
        tvCaiLiao.setText(s);
        tvsingleProductCode.setText(laserCode);
        tvRfidContain.setText(r);
        mLinearLayout.setTag(position);
        tvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlContainer.removeView(mLinearLayout);
                rifdMap.remove(tvCaiLiao.getTag());
            }
        });
        position++;
        mLlContainer.addView(mLinearLayout);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            popupWindow2.dismiss();
            //员工卡RfidCode
            mTvScan.setClickable(true);
            cardString = msg.obj.toString();
            if ("close".equals(cardString)) {
                isCanScan = true;
                createAlertDialog(c01s005_002_2Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                if(isCheckRfid(cardString)){
                    isCanScan = true;
                    createAlertDialog(c01s005_002_2Activity.this,"已存在",1);
                }else{
                    searchForCaiLiao(cardString);
                }
            }

        }
    };

    private void searchForCaiLiao(String num) {
        loading.show();
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getComposeInfo = iRequest.getComposeInfo(num);
        getComposeInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                isCanScan = true;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        showDialog(jsonObject1.getString("synthesisParametersCode"), jsonObject1.getString("rfidContainerID"), jsonObject1.getString("laserCode"));
                    }else{
                        createAlertDialog(c01s005_002_2Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s005_002_2Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                isCanScan = true;
            }
        });
        //显示信息

    }

    /**
     * 显示数据提示dialog
     */
    private void showDialog(final String name, final String r, final String laserCode){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialog);
        final AlertDialog dialog = builder.create();

        View view = View.inflate(this, R.layout.dialog_baofei1_c, null);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvBaoFei = (TextView) view.findViewById(R.id.tvBaofeiName);
        tvBaoFei.setText("报废一体刀"+laserCode);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        Button btnConfirm = (Button) view.findViewById(R.id.btnSure);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(name, r, laserCode);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (screenWidth*0.8), (int) (screenHeight*0.6));
//        dialog.getWindow().setLayout(300, 400);
    }

    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        jsonList.clear();
        rfidContainerIDList.clear();
        if(mLlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    String c = null;
                    String r = null;
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    c = ((TextView)child2).getText().toString();
                                    break;
                                case R.id.tvRfidContain:
                                    r = ((TextView)child2).getText().toString();
                                    break;
                                default:
                            }
                        }
                    }

                    jsonList.add(c);
                    rfidContainerIDList.add(r);

                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(jsonList);
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

    private boolean isCheckRfid(String card){
        if (rifdMap.containsValue(card)){
            return true;
        }
        return false;
    }
}
