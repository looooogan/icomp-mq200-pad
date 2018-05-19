package com.icomp.Iswtmv10.v01c01.c01s009;
/**
 * 合成刀具组装
 */
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.icomp.Iswtmv10.v01c01.c01s008.c01s008_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Request;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.C01S009Response;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.FuJuModul;
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
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class C01S009_002Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.tvScan)
    TextView mTvScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private int position = 1;
    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;
    //扫描线程
    private VisitJniThread thread;
    //扫描弹框
    private PopupWindow popupWindow2;
    private List<String> mList = new ArrayList<>();
    private Retrofit mRetrofit;
    private Gson mGson = new Gson();
    private HashMap<Integer, String> rfidMap = new HashMap<>();
    private int rfidPosition = 0;
    private boolean isCanScan = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s009_002);
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
                isCanScan = false;
                scan();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                C01S008Request c01S008Request = bianli();
                if("".equals(c01S008Request.getRfidContainerIDs())){
                    createAlertDialog(this,"数据不能为空",Toast.LENGTH_SHORT);
                }else {
                    requestDataForSave(c01S008Request);
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
        }else{
            return;
        }
        scan();
    }

    private boolean isCheckRfid(String card){
        if (rfidMap.containsValue(card)){
            return true;
        }
        return false;
    }

    private void requestDataForSave(final C01S008Request request) {
        loading.show();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveSynthesisToolInstall = iRequest.saveSynthesisToolInstall(request.getSynthesisParametersCodes(),
                request.getRfidContainerIDs(),
                request.getToolConsumetypes(),
                customerID,
                "");
        saveSynthesisToolInstall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(C01S009_002Activity.this,c01s008_002Activity.class);
                        intent.putExtra("TAG","2");//刀具拆分
                        intent.putExtra("json",request.getSynthesisParametersCodes());
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S009_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    loading.dismiss();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S009_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        rfidMap = new HashMap<>();
        Intent intent2 = getIntent();
        if(intent2 == null){
            return;
        }else{
            Bundle bundle = intent2.getExtras();
            if (bundle == null){return;}
            boolean isClear = bundle.getBoolean("isClear",false);
            if(isClear){
                mList.clear();
                mLlContainer.removeAllViews();
            }
        }
    }

    /**
     * 开始扫描
     */
    private void scan(){
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
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
                createAlertDialog(C01S009_002Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                requestData(cardString);
            }
        }
    };
    /**
     * 将扫描结果进行网络请求
     * @param code
     */
    private void requestData(String code){
        if(isCheckRfid(code)){
            isCanScan = true;
            createAlertDialog(this,"已存在",Toast.LENGTH_SHORT);
            return;
        }
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getSynthesisToolInstall = iRequest.getSynthesisToolInstall(code,"0");
        getSynthesisToolInstall.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                isCanScan = true;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        C01S009Response c01S009Response = mGson.fromJson(response.body().toString(),C01S009Response.class);
                        List<FuJuModul> list = new ArrayList<>();
                        for (int i = 0; i <c01S009Response.getData().getToolList().size() ; i++) {
                            FuJuModul f1 = new FuJuModul();
                            f1.setName(c01S009Response.getData().getToolList().get(i).getToolCode());
                            f1.setNum(Integer.valueOf(c01S009Response.getData().getToolList().get(i).getToolCount()));
                            list.add(f1);
                        }
                        showDialog(c01S009Response.getData().getSynthesisParametersCode(),
                                list,
                                c01S009Response.getData().getRfidContainerID(),
                                c01S009Response.getData().getCreateType());

                    }else {
                        createAlertDialog(C01S009_002Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                createAlertDialog(C01S009_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }


    /**
     * 添加布局
     */
    private void addLayout(String code, String rfid, String type) {
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_daojuchaifen, null);
        TextView tvNum = (TextView) mLinearLayout.findViewById(R.id.tvNum);
        final TextView tvHeChengNum = (TextView) mLinearLayout.findViewById(R.id.tvHeChengNum);
        TextView tvRfid = (TextView) mLinearLayout.findViewById(R.id.tvRfid);
        TextView tvType = (TextView) mLinearLayout.findViewById(R.id.tvtype);
        TextView tvDelete = (TextView) mLinearLayout.findViewById(R.id.tvDelete);
        tvHeChengNum.setTag(rfidPosition);
        rfidPosition++;
        rfidMap.put((Integer) tvHeChengNum.getTag(),cardString);
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlContainer.removeView(mLinearLayout);
                rfidMap.remove((Integer) tvHeChengNum.getTag());
                position--;
                modifyData(position);
            }
        });
        tvRfid.setText(rfid);
        tvType.setText(type);
        tvNum.setText(""+position);
        tvHeChengNum.setText(code);
        mLlContainer.addView(mLinearLayout);
        position++;
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

    /**
     * 遍历
     */
    private C01S008Request bianli(){
        C01S008Request request = new C01S008Request();
        request.setRfidContainerIDs("");
        request.setSynthesisParametersCodes("");
        request.setToolConsumetypes("");
        mList.clear();
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
                                case R.id.tvHeChengNum:
                                    mList.add(((TextView)child2).getText().toString());
                                    if (request.getSynthesisParametersCodes().equals("")){
                                        request.setSynthesisParametersCodes(((TextView)child2).getText().toString());
                                    }else{
                                        request.setSynthesisParametersCodes(request.getSynthesisParametersCodes()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                                case R.id.tvRfid:
                                    if("".equals(request.getRfidContainerIDs())){
                                        request.setRfidContainerIDs(((TextView)child2).getText().toString());
                                    }else{
                                        request.setRfidContainerIDs(request.getRfidContainerIDs()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                                case R.id.tvtype:
                                    if("".equals(request.getToolConsumetypes())){
                                        request.setToolConsumetypes(((TextView)child2).getText().toString());
                                    }else{
                                        request.setToolConsumetypes(request.getToolConsumetypes()+","+((TextView)child2).getText().toString());
                                    }
                                    break;
                            }
                        }
                    }
                }

            }
        }
        return  request;
    }

    /**
     * 显示静态提示dialog
     */
    private void showDialogstatic(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.dialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_daojuzuzhuang_c, null);
        dialog.setView(view);

        Button btnConfirm = (Button) view.findViewById(R.id.btnSure);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 显示静态提示dialog
     */
    private void showDialog(final String code, List<FuJuModul> list, final String rfid, final String type){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialog);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_daojuzuzhuang_data_c, null);

        Button btnConfirm = (Button) view.findViewById(R.id.btnSure);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        TextView tvDaoJuCode = (TextView) view.findViewById(R.id.tvDaoJuCode);
        ListView lvZuZhuang = (ListView) view.findViewById(R.id.lvZuZhuang);
//        TextView tvFuzhuNum = (TextView) view.findViewById(R.id.tvFuzhuNum);
        MyAdapter myAdapter = new MyAdapter(list);
        lvZuZhuang.setAdapter(myAdapter);
//        tvFuzhuNum.setText("辅具"+list.size()+"个");
        tvDaoJuCode.setText("合成刀具编码："+code);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLayout(code, rfid,type);
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
        dialog.setContentView(view);
        dialog.getWindow().setLayout((int) (screenWidth*0.8), (int) (screenHeight*0.6));
    }

    class MyAdapter extends BaseAdapter {
        private List<FuJuModul> list;

        public MyAdapter(List<FuJuModul> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(C01S009_002Activity.this).inflate(R.layout.item_dialog_fujulist,null);
            TextView tvFunJu = (TextView) view.findViewById(R.id.tvFunJu);
            tvFunJu.setText(list.get(position).getName()+"   "+list.get(position).getNum()+"把");
            return view;
        }

        public List<FuJuModul> getList(){
            return list;
        }
    }
}
