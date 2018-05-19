package com.icomp.Iswtmv10.v01c01.c01s005;
/**
 * 报废原因填写
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.icomp.Iswtmv10.v01c01.c01s003.C01S003_001Activity;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.DanPinModul;
import com.icomp.Iswtmv10.v01c01.c01s005.modul.TypeModul;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.WifiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class c01s005_004_1Activity extends CommonActivity {

    @BindView(R.id.tvSlip)
    TextView mTvSlip;
    @BindView(R.id.etTextArea)
    EditText mEtTextArea;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnSubmit)
    Button mBtnSubmit;
    private String toolCodes;
    private String scrapNumbers;
    private String rfidContainerIDs;
    private String synthesisParametersCode;
    private String toolCounts;
    private int tag;
    private Retrofit mRetrofit;
    private TypeModul mTypeModul;
    private Gson gson = new Gson();
    private String type;
    private String scrapCode;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s005_004_1);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        tag = intent.getIntExtra("TAG",0);
        scrapCode = intent.getStringExtra("scrapCode");
        switchTag(tag,intent);
        initRetrofit();
        initView();
        requestReason();
    }

    private void requestReason() {
        loading.show();
        IRequest iRquest = mRetrofit.create(IRequest.class);
        Call<String> getScrapStateList = iRquest.getScrapStateList();
        getScrapStateList.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                try {
                    if(response.raw().code() == 404){
                        createAlertDialog(c01s005_004_1Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        return;
                    }
                    JSONObject jsObject = new JSONObject(response.body().toString());
                    if(jsObject.getBoolean("success")){
                        mTypeModul = gson.fromJson(response.body().toString(),TypeModul.class);
                    }else{
                        createAlertDialog(c01s005_004_1Activity.this,jsObject.getString("message"),Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s005_004_1Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    private void initRetrofit() {
        mRetrofit = RetrofitSingle.newInstance();
    }

    private void switchTag(int num , Intent intent){
        switch (num){
            case 1:
                toolCodes = intent.getStringExtra("name");
                scrapNumbers = intent.getStringExtra("num");
                break;
            case 2:
                toolCodes = intent.getStringExtra("name");
                rfidContainerIDs = intent.getStringExtra("rifd");
                break;
            case 3:
                toolCodes = intent.getStringExtra("name");
                rfidContainerIDs = intent.getStringExtra("rifd");
                synthesisParametersCode = intent.getStringExtra("syn");
                toolCounts = intent.getStringExtra("num");
                break;
        }
    }


    private void initView() {
    }
    @OnClick({R.id.tvSlip, R.id.btnCancel, R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvSlip:
                showPopupWindow();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnSubmit:
                if("".equals(mTvSlip.getText().toString())){
                    createAlertDialog(this,"请选择报废状态  ",0);
                    return;
                }
                mBtnSubmit.setClickable(false);
                loading.show();
                requestData(tag);
                break;
        }
    }

    private void showPopupWindow() {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.item_spanner, null);
        ListView lv = (ListView) contentView.findViewById(R.id.lvSpanner);
        MyAdapter adapter = new MyAdapter();
        lv.setAdapter(adapter);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                mTvSlip.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTvSlip.setText(mTypeModul.getData().get(position).getScrapStateName());
                type = mTypeModul.getData().get(position).getScrapState();
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.showAsDropDown(mTvSlip);

    }

    private void requestData(int num){
        switch (num){
            case 1:requestDanTi();break;
            case 2:requestYiTi();break;
            case 3:requestTong();break;
        }
    }

    private void requestTong() {
        if (type == null) {
            createAlertDialog(this,"请选择报废原因",Toast.LENGTH_SHORT);
            return;
        }
        IRequest iquest = mRetrofit.create(IRequest.class);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        String handMacCode = WifiUtils.getMacAddress(getApplicationContext());
        Call<String> saveTubeScrap = iquest.saveTubeScrap(toolCodes,
                rfidContainerIDs,
                synthesisParametersCode,
                customerID,type,
                mEtTextArea.getText().toString(),
                handMacCode,toolCounts);
        saveTubeScrap.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                mBtnSubmit.setClickable(true);
                try {
                    loading.dismiss();
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s005_004_1Activity.this,c01s005_005Activity.class);
                        intent.putExtra("TAG",tag);
                        startActivity(intent);
                        finish();
                    }else{
                        createAlertDialog(c01s005_004_1Activity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                mBtnSubmit.setClickable(true);
                loading.dismiss();
                createAlertDialog(c01s005_004_1Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });

    }

    private void requestYiTi() {
        IRequest iRequest = mRetrofit.create(IRequest.class);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        String handMacCode = WifiUtils.getMacAddress(getApplicationContext());
        Call<String> saveComposeScrap = iRequest.saveComposeScrap(toolCodes,
                rfidContainerIDs,
                customerID,
                type,
                mEtTextArea.getText().toString(),handMacCode);
        saveComposeScrap.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    loading.dismiss();
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s005_004_1Activity.this,c01s005_005Activity.class);
                        intent.putExtra("TAG",tag);
                        startActivity(intent);
                        finish();
                    }else{
                        createAlertDialog(c01s005_004_1Activity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s005_004_1Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });

    }

    private void requestDanTi(){
        IRequest iRequest = mRetrofit.create(IRequest.class);
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_APPEND);
        //新建编辑器
        String customerID = sharedPreferences.getString("customerID","");
        String handMacCode = WifiUtils.getMacAddress(getApplicationContext());
        Call<String> saveFScrap = iRequest.saveFScrap(toolCodes,
                scrapNumbers,
                customerID,
                type,
                mEtTextArea.getText().toString(),
                handMacCode,
                scrapCode);
        saveFScrap.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    loading.dismiss();
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    if(jsonObject.getBoolean("success")){
                        Intent intent = new Intent(c01s005_004_1Activity.this,c01s005_005Activity.class);
                        intent.putExtra("TAG",tag);
                        startActivity(intent);
                        finish();
                    }else{
                        createAlertDialog(c01s005_004_1Activity.this,jsonObject.getString("message"), Toast.LENGTH_SHORT);
                    }
                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                } finally {
                    mBtnSubmit.setClickable(true);
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(c01s005_004_1Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });

    }

    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTypeModul.getData().size();
        }

        @Override
        public Object getItem(int position) {
            return mTypeModul.getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(c01s005_004_1Activity.this).inflate(R.layout.item_lv, null);
            TextView tv = (TextView) view.findViewById(R.id.tvCategory);
            tv.setText(mTypeModul.getData().get(position).getScrapStateName());
            return view;
        }
    }
}
