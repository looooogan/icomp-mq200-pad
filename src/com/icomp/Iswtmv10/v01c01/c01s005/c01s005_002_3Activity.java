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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.icomp.Iswtmv10.v01c01.c01s001.C01S001_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.TongDaoModul;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.TongResponseModul;
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

public class c01s005_002_3Activity extends CommonActivity {

    @BindView(R.id.llContainer)
    LinearLayout mLlContainer;
    @BindView(R.id.tvScan)
    TextView mTvScan;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;
    private int position = 0;
    private List<TongDaoModul> jsonList = new ArrayList<>();

    private HashMap<Integer,String> rifdMap = new HashMap<>();

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;
    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;

    private Retrofit mRetrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_002_3);
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
                scan();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                String json = bianliAndToJson();
                if(json == null || json.equals("")){
                    return;
                }
                Intent intent = new Intent(this,c01s005_003_3Activity.class);
                intent.putExtra("json",json);
                startActivity(intent);
                break;
        }
    }

    /**
     * 显示数据提示dialog
     */
    private void showDialog(final List<TongDaoModul> list){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_baofei2_c, null);
        dialog.setView(view,0,0,0,0);
        ListView lv = (ListView) view.findViewById(R.id.lvBaoFei);
        final MyAdapter myAdapter = new MyAdapter(list);
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
                boolean ischeck = false;
                List<TongDaoModul> listTemp = myAdapter.getList();
                if (listTemp!=null){
                    for (int i = 0; i<listTemp.size();i++){
                        if(listTemp.get(i).isCheck()){
                            ischeck = true;
                            break;
                        }
                    }
                }
                if(ischeck){
                    if (listTemp!=null){
                        for (int i = 0; i<listTemp.size();i++){
                            addLayout(listTemp.get(i));
                        }
                    }
                }else{
                    createAlertDialog(c01s005_002_3Activity.this,"请选择要报废的材料",1);
                }

                dialog.dismiss();
            }


        });
        dialog.show();
    }

    private void addLayout(TongDaoModul modul) {
        if(!modul.isCheck()){
            return;
        }
        final View mLinearLayout = LayoutInflater.from(this).inflate(R.layout.item_tongdao_daojubaofei, null);
        final TextView tvCaiLiao = (TextView) mLinearLayout.findViewById(R.id.tvCailiao);
        TextView tvBaofeishuliang = (TextView) mLinearLayout.findViewById(R.id.tvBaofeishuliang);
        TextView tvsynthesisParametersCode = (TextView) mLinearLayout.findViewById(R.id.tvsynthesisParametersCode);
        TextView tvrFID = (TextView) mLinearLayout.findViewById(R.id.tvrFID);

        tvCaiLiao.setTag(position);
        rifdMap.put((Integer) tvCaiLiao.getTag(),modul.getrFID());

        tvCaiLiao.setText(modul.getCaiLiao());
        tvBaofeishuliang.setText(modul.getGroupNum());
        tvsynthesisParametersCode.setText(modul.getSynthesisParametersCode());
        tvrFID.setText(modul.getrFID());

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
                mLlContainer.removeView(mLinearLayout);

                rifdMap.remove(tvCaiLiao.getTag());

            }
        });
        mLlContainer.addView(mLinearLayout);
    }

    /**
     * 遍历所有数据并转化为json
     */
    private String bianliAndToJson(){
        jsonList.clear();
        if(mLlContainer.getChildCount() == 0){
            return null;
        }
        for (int k = 0; k<mLlContainer.getChildCount();k++){
            LinearLayout mDataLin = (LinearLayout) mLlContainer.getChildAt(k);
            for (int i = 0; i<mDataLin.getChildCount();i++){
                View child = mDataLin.getChildAt(i);
                if(child instanceof LinearLayout){
                    int child2Coutn = ((LinearLayout) child).getChildCount();
                    TongDaoModul c = new TongDaoModul();
                    for (int j = 0;j<child2Coutn;j++){
                        View child2 = ((LinearLayout) child).getChildAt(j);
                        if(child2 instanceof TextView){
                            switch (child2.getId()){
                                case R.id.tvCailiao:
                                    c.setCaiLiao(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvBaofeishuliang:
                                    c.setGroupNum(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvsynthesisParametersCode:
                                    c.setSynthesisParametersCode(((TextView)child2).getText().toString());
                                    break;
                                case R.id.tvrFID:
                                    c.setrFID(((TextView)child2).getText().toString());
                                    break;
                            }
                        }
                    }
                    jsonList.add(c);

                }

            }
        }
        Gson gson = new Gson();
        return gson.toJson(jsonList);
    }

    /**
     * 扫描
     */
    private void scan(){
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }

    }

    /**
     * 网络请求查询数据
     */
    private void requestData(String cardString){
        loading.show();
        IRequest iRequest = mRetrofit.create(IRequest.class);
        Call<String> getTubeInfo = iRequest.getTubeInfo(cardString);
//        getTubeInfo.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });

        getTubeInfo.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                try {
                    if(response == null){
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Gson gson = new Gson();
                        TongResponseModul t = gson.fromJson(response.body().toString(),TongResponseModul.class);
                        List<TongDaoModul> list = new ArrayList<TongDaoModul>();
                        for (int i = 0; i<t.getData().size();i++){
                            TongDaoModul tong = new TongDaoModul();
                            tong.setrFID(t.getData().get(i).getRFID());
                            tong.setSynthesisParametersCode(t.getData().get(i).getSynthesisParametersCode());
                            tong.setGroupNum(String.valueOf(t.getData().get(i).getToolCount()));
                            tong.setCaiLiao(t.getData().get(i).getToolCode());
                            list.add(tong);
                        }
                        if(isCheckRfid(t.getData().get(0).getRFID())){
                            createAlertDialog(c01s005_002_3Activity.this,"已存在",1);
                        }else{
                            showDialog(list);
                        }

                    }else{
                        createAlertDialog(c01s005_002_3Activity.this,jsonObject.getString("message"),1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s005_002_3Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    class MyAdapter extends BaseAdapter{
        private List<TongDaoModul> list;

        public MyAdapter(List<TongDaoModul> list) {
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
            View view = LayoutInflater.from(c01s005_002_3Activity.this).inflate(R.layout.item_dialog_list,null);
            CheckBox c = (CheckBox) view.findViewById(R.id.cb);
            TextView tvCaiLiao = (TextView) view.findViewById(R.id.tvCaiLiao);
            TextView tvGroupNum = (TextView) view.findViewById(R.id.tvGroupNum);
            c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(position).setCheck(isChecked);
                }
            });
            tvCaiLiao.setText(list.get(position).getCaiLiao());
            tvGroupNum.setText(list.get(position).getGroupNum());
            return view;
        }

        public List<TongDaoModul> getList(){
            return list;
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
                createAlertDialog(c01s005_002_3Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }else{
                requestData(cardString);
            }
        }
    };


    private boolean isCheckRfid(String card){
        if (rifdMap.containsValue(card)){
            return true;
        }
        return false;
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

}
