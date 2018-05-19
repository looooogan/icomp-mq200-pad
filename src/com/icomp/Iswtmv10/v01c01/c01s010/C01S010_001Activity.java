package com.icomp.Iswtmv10.v01c01.c01s010;
/**
 * 刀具换装
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

public class C01S010_001Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private int position = 1;
    List<String> mList = new ArrayList<>();

    private HashMap<Integer,String> ridfMap = new HashMap<>();

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;

    private int rifdPositon = 0;

    private Retrofit mRetrofit;
    private boolean isShouQuan = false;

    private String synthesisParametersCodes = "";  //合成刀具编码s
    private String rfidContainerIDs = ""; //RFID载体ID
    private String authorizationFlgs = "";
    private boolean isCanScan = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s010_001);
        ButterKnife.bind(this);
        initRetrofit();
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    @OnClick({R.id.btnCancel, R.id.btnNext,R.id.tvScan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                Intent intent = new Intent(this,c01s010_002Activity.class);
                String json = bianliAndToJson();
                if(json == null || ("").equals(json)){
                    return;
                }
                intent.putExtra("json",json);
                intent.putExtra("synthesisParametersCodes",synthesisParametersCodes);
                intent.putExtra("rfidContainerIDs",rfidContainerIDs);
                intent.putExtra("authorizationFlgs",authorizationFlgs);
                startActivity(intent);
                break;
            case R.id.tvScan:
                if (rifdPositon == 1) {
                    createAlertDialog(C01S010_001Activity.this, "每次只可操作一把合成刀具，点击删除或者下一把", Toast.LENGTH_SHORT);
                } else {
                    isCanScan = false;
                    scan();
                }
                break;
            default:
        }
    }

    @Override
    protected void btnScan() {
        super.btnScan();
        if (isCanScan){
            isCanScan = false;
        }else {return;}
        scan();
    }

    /**
     * 开始扫描
     */
    private void scan(){
        //判断是否可以组装
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Intent intent2 = getIntent();
        position = 1;
        rifdPositon = 0;
        ridfMap = new HashMap<>();
        if(intent2 == null){
            return;
        }else{
            Bundle bundle = intent2.getExtras();
            if(bundle == null){
                return;
            }
            boolean isClear = bundle.getBoolean("isClear",false);
            if(isClear){
                mList.clear();
                mLlContainer.removeAllViews();
            }
        }
    }



    /**
     *     打开扫描窗体
     */
    private void showWindow2(int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);         //在哪个页面打开窗口
        final View vPopupWindow = layoutInflater.inflate(pageID, null);    //用inflate方法调用界面
        vPopupWindow.setBackgroundResource(R.drawable.my_selectors);       //从res下选择要作为背景的源文件
        popupWindow2 = new PopupWindow(vPopupWindow, 300, 240);            //设置源文件的长宽
        //popupWindow2.setBackgroundDrawable(new BitmapDrawable());        //从res下选择要作为背景的源文件
        popupWindow2.setFocusable(true);                                    //设置弹框外部无法获取焦点
        popupWindow2.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);//设置popWindow的显示位置
        thread = new VisitJniThread();                                      //启动扫描线程
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
                createAlertDialog(C01S010_001Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                if(isCheckRfid(cardString)){
                    isCanScan = true;
                    createAlertDialog(C01S010_001Activity.this,"该标签已存在",Toast.LENGTH_SHORT);
                }else{
                    loading.show();
                    requestData(cardString);
                }
            }
        };
    };


    /**
     * 将扫描结果进行网络请求
     * @param code
     */
    private void requestData(final String code){
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getSynthesisToolOneKnifeInfo = iRequest.getSynthesisToolOneKnifeInfo(code);
        getSynthesisToolOneKnifeInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                isCanScan = true;
                String re = response.body().toString();
                try {
                    final JSONObject jsonObject = new JSONObject(re);
                    if (jsonObject.getBoolean("success")){
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        if("0".equals(jsonObject1.getString("code"))){
                            AlertDialog.Builder builder = new AlertDialog.Builder(C01S010_001Activity.this);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage("该合成刀具已经换装是否继续换装");
                            builder.setCancelable(false);
                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    JSONObject js = null;
                                    String synthesisParametersCode = null;
                                    String rfidContainerID = null;
                                    try {
                                        js = jsonObject.getJSONObject("data");
                                        synthesisParametersCode = js.getString("synthesisParametersCode");
                                        rfidContainerID = js.getString("rfidContainerID");

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    addLayout(code,rfidContainerID,synthesisParametersCode,"1");
                                }
                            }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int a) {
                                    dialogInterface.dismiss();
//                                    btnScan.setClickable(true);
                                }
                            }).show();
                        }else{
                            JSONObject js = jsonObject.getJSONObject("data");
                            String synthesisParametersCode = js.getString("synthesisParametersCode");
                            String rfidContainerID = js.getString("rfidContainerID");
                            addLayout(code,rfidContainerID,synthesisParametersCode,"0");
                        }
                    }else{
                        createAlertDialog(C01S010_001Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                createAlertDialog(C01S010_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                loading.dismiss();
            }
        });
    }

    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        synthesisParametersCodes = "";
        rfidContainerIDs = "";
        authorizationFlgs = "";
        mList.clear();
        if(mLlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        switch (child2.getId()){
                            case R.id.tvHeChengNum:
                                mList.add(((TextView)child2).getText().toString());
                                if(i == mDataLin.getChildCount()-1){
                                    synthesisParametersCodes+=((TextView)child2).getText().toString();
                                }else {
                                    synthesisParametersCodes+=((TextView)child2).getText().toString()+",";
                                }break;
                            case R.id.tvRfid:
                                if(i == mDataLin.getChildCount()-1){
                                    rfidContainerIDs+=((TextView)child2).getText().toString();
                                }else {
                                    rfidContainerIDs+=((TextView)child2).getText().toString()+",";
                                }break;
                            case R.id.tvShouQuan:
                                if(i == mDataLin.getChildCount()-1){
                                    authorizationFlgs+=((TextView)child2).getText().toString();
                                }else {
                                    authorizationFlgs+=((TextView)child2).getText().toString()+",";
                                }break;
                            default:

                        }

                    }

                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(mList);
    }



    /**
     * 添加布局
     */
    private void addLayout(String rfidcode,String code,String synthesisParametersCode,String shouquan) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_daojuchaifen, null);
        final TextView tvNum = (TextView) mLinearLayout.findViewById(R.id.tvNum);
        TextView tvHeChengNum = (TextView) mLinearLayout.findViewById(R.id.tvHeChengNum);
        TextView tvDelete = (TextView) mLinearLayout.findViewById(R.id.tvDelete);
        TextView tvRfid = (TextView) mLinearLayout.findViewById(R.id.tvRfid);
        TextView tvShouQuan = (TextView) mLinearLayout.findViewById(R.id.tvShouQuan);
        tvRfid.setText(code);
        tvShouQuan.setText(shouquan);
        tvNum.setTag(rifdPositon);
        ridfMap.put((Integer) tvNum.getTag(),rfidcode);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlContainer.removeView(mLinearLayout);
                rifdPositon--;
                position--;
                modifyData(position);
                ridfMap.remove(tvNum.getTag());
            }
        });
        tvNum.setText(""+position);
        tvHeChengNum.setText(synthesisParametersCode);
        mLlContainer.addView(mLinearLayout);
        position++;
        rifdPositon++;
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

    private boolean isCheckRfid(String card){
        return  ridfMap.containsValue(card);
    }

}
