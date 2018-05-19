package com.icomp.Iswtmv10.v01c01.c01s009;
/**
 * 筒刀组装
 */

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.icomp.Iswtmv10.v01c01.c01s008.c01s008_002Activity;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Request;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.C01S009Response;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.TongDaoZuZhuang;
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

public class C01S009_003Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.tvScan)
    TextView mTvScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;

    private int position = 1;
    private boolean scanf = false;  //是否正在扫描，线程的控制
    private String cardString;      //员工卡RfidCode
    private VisitJniThread thread;  //扫描线程
    private PopupWindow popupWindow2; //扫描弹框
    private Retrofit mRetrofit;
    private Gson mGson = new Gson();
    private HashMap<Integer, String> rfidMap = new HashMap<>();
    private int rfidPosition = 0;
    private boolean isCanScan = true;
    private MyAdapter myAdapter;
    private List<String> tongDaoList = new ArrayList<>();
    private List<TongDaoZuZhuang> mAllList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01_s009_003);
        initRetrofit();
        ButterKnife.bind(this);
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    @OnClick({R.id.llContainer, R.id.tvScan, R.id.btnCancel, R.id.btnNext})
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

    private String getInstallParams(){
        String installParams = "";
        for (int i = 0;i<mAllList.size();i++){
            if(i == mAllList.size()-1){
                installParams = installParams+mAllList.get(i).getHeChengdaoNum()+"&"+
                        mAllList.get(i).getRfidContainerIDs()+"&"+
                        mAllList.get(i).getCaiLiao()+"&"+
                        mAllList.get(i).getNum();
            }else{
                installParams = installParams+mAllList.get(i).getHeChengdaoNum()+"&"+
                        mAllList.get(i).getRfidContainerIDs()+"&"+
                        mAllList.get(i).getCaiLiao()+"&"+
                        mAllList.get(i).getNum()+",";
            }
        }
        return installParams;
    }

    private void requestDataForSave(final C01S008Request request) {
        loading.show();
        String installparams = getInstallParams();
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> saveSynthesisToolInstall = iRequest.saveSynthesisToolInstall(request.getSynthesisParametersCodes(),
                request.getRfidContainerIDs(),
                request.getToolConsumetypes(),
                customerID,
                installparams);
        saveSynthesisToolInstall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(C01S009_003Activity.this,c01s008_002Activity.class);
                        intent.putExtra("TAG","3");//刀具拆分
                        intent.putExtra("json",request.getSynthesisParametersCodes());
                        startActivity(intent);
                    }else{
                        createAlertDialog(C01S009_003Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
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
                createAlertDialog(C01S009_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);

            }
        });
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
            cardString = msg.obj.toString();
            if ("close".equals(cardString)) {
                isCanScan = true;
                createAlertDialog(C01S009_003Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                requestData(cardString);
            }
        }
    };

    /**
     * 根据RFID进行查询
     * @param code
     */
    private void requestData(String code) {
        if(isCheckRfid(code)){
            isCanScan = true;
            createAlertDialog(this,"已存在",Toast.LENGTH_SHORT);
            return;
        }
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getSynthesisToolInstall = iRequest.getSynthesisToolInstall(code,"1");
        getSynthesisToolInstall.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                isCanScan = true;
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        C01S009Response c01S009Response = mGson.fromJson(response.body().toString(),C01S009Response.class);
                        List<TongDaoZuZhuang> list = new ArrayList<>();
                        for (int i = 0; i <c01S009Response.getData().getToolList().size() ; i++) {
                            TongDaoZuZhuang f1 = new TongDaoZuZhuang();
                            f1.setCaiLiao(c01S009Response.getData().getToolList().get(i).getToolCode());
                            f1.setHeChengdaoNum(c01S009Response.getData().getSynthesisParametersCode());
                            //默认将返回的韧磨次数加1
                            //// TODO: 2017/12/13 不加1 
                            f1.setNum(Integer.valueOf(c01S009Response.getData().getToolList().get(i).getGrindingsum())+"");
                            f1.setRfidContainerIDs(c01S009Response.getData().getRfidContainerID());
                            list.add(f1);
                        }
                        if(list.size() != 0){
                            showDialog(list,c01S009Response.getData().getSynthesisParametersCode(),
                                    c01S009Response.getData().getRfidContainerID(),
                                    c01S009Response.getData().getCreateType());
                        }else{
                            createAlertDialog(C01S009_003Activity.this,"请重新操作",1);
                        }

                    }else {
                        createAlertDialog(C01S009_003Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                isCanScan = true;
                createAlertDialog(C01S009_003Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    /**
     * 开始扫描
     */
    private void scan(){
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }

    /**
     * 显示数据提示dialog
     */
    private void showDialog(final List<TongDaoZuZhuang> list, final String synthesisParametersCodes, final String rfidContainerID, final String toolConsumetypes){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_baofei2_c, null);
        dialog.setView(view,0,0,0,0);
        ListView lv = (ListView) view.findViewById(R.id.lvBaoFei);
        myAdapter = new MyAdapter(list);
        lv.setAdapter(myAdapter);
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

                //2017年11月27日修改 FanLL
                //如果为空提示用户
                for (int i = 0; i < myAdapter.getCount(); i++) {
                    if ("".equals(myAdapter.getList().get(i).getNum().trim())) {
                        createAlertDialog(C01S009_003Activity.this,"修磨次数不能为空",Toast.LENGTH_SHORT);
                        return;
                    }
                }
                mAllList.addAll(myAdapter.getList());
                addLayout(synthesisParametersCodes,rfidContainerID,toolConsumetypes);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //将材料号从集合中删除，防止删除后不能添加
    private void removeCaiLiao(String hechengNum){
        for (int i = 0;i<mAllList.size();i++){
            if(hechengNum.equals(mAllList.get(i).getHeChengdaoNum())){
                mAllList.remove(i);
            }else{
                continue;
            }
        }
    }


    /**
     * 添加布局
     */
    private void addLayout(final String code, String rfid, String type) {
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
                rfidMap.remove(tvHeChengNum.getTag());
                removeCaiLiao(code);
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
//                mList.clear();
                mAllList.clear();
                mLlContainer.removeAllViews();
            }
        }
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
                                default:
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
//        mList.clear();
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
//                                    mList.add(((TextView)child2).getText().toString());
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
                                default:
                            }
                        }
                    }
                }

            }
        }
        return  request;
    }

    class MyAdapter extends BaseAdapter{
        private List<TongDaoZuZhuang> list;

        public MyAdapter(List<TongDaoZuZhuang> list) {
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
            View view = getLayoutInflater().inflate(R.layout.item_daojuzuzhuang,null);
            TextView tvCaiLiao = (TextView) view.findViewById(R.id.tvCaiLiao);
            TextView tvadd = (TextView) view.findViewById(R.id.tv_add);
            TextView tvdel = (TextView) view.findViewById(R.id.tv_del);
            final EditText evXiuMo = (EditText) view.findViewById(R.id.etXiuMo);
            tvCaiLiao.setText(list.get(position).getCaiLiao());
            tvadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    evXiuMo.setText(String.valueOf(Integer.parseInt(evXiuMo.getText().toString())+1));
                    if (evXiuMo.getText().toString().equals(list.get(position).getNum())) {
                        evXiuMo.setTextColor(C01S009_003Activity.this.getResources().getColorStateList(R.color.itemtextColor));
                    }
                }
            });
            tvdel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Integer.parseInt(evXiuMo.getText().toString()) <= 0) {
                        createAlertDialog(C01S009_003Activity.this, "修磨次数不能小于0次", Toast.LENGTH_LONG);
                    } else {
                        evXiuMo.setText(String.valueOf(Integer.parseInt(evXiuMo.getText().toString())-1));
                        if (evXiuMo.getText().toString().equals(list.get(position).getNum())) {
                            evXiuMo.setTextColor(C01S009_003Activity.this.getResources().getColorStateList(R.color.itemtextColor));
                        }
                    }
                }
            });

            evXiuMo.setText(list.get(position).getNum());
            evXiuMo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(position).setNum(s.toString());
                }
            });
            return view;
        }

        public List<TongDaoZuZhuang> getList(){
            return list;
        }
    }
}
