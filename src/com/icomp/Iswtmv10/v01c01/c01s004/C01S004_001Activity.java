package com.icomp.Iswtmv10.v01c01.c01s004;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.common.activity.CommonActivity;

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


/**
 * 补领出库
 */

public class C01S004_001Activity extends CommonActivity {

    @BindView(R.id.lvChuKu)
    ListView mLvChuKu;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnReturn)
    Button mBtnReturn;
    private long clickTime = 0;

    private List<BuLingModul> mList;

    private Retrofit retrofit;

    @Override
    protected void onRestart() {
        super.onRestart();
        requestData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s004_001);
        ButterKnife.bind(this);
        initRetrofit();
        requestData();
    }

    private void initRetrofit() {
        retrofit = RetrofitSingle.newInstance();
    }

    //网络请求 获取补领人信息列表
    private void requestData(){
        loading.show();
        mList = new ArrayList<>();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getFReplacementList = iRequest.getFReplacementList();
        getFReplacementList.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                loading.dismiss();
                String json = response.body().toString();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    if(jsonObject.getBoolean("success")){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0;i<jsonArray.length();i++){
                            BuLingModul b = new BuLingModul();
                            b.setName(jsonArray.getJSONObject(i).getString("applyUser"));
                            b.setTime(jsonArray.getJSONObject(i).getString("applyTime"));
                            b.setId(jsonArray.getJSONObject(i).getString("applyID"));
                            b.setReplacementFlag(jsonArray.getJSONObject(i).getString("replacementFlag"));
                            mList.add(b);
                        }
                        initView();
                    }else{
                        final AlertDialog.Builder builder = new AlertDialog.Builder(C01S004_001Activity.this);
                        builder.setTitle(R.string.infoMsg);// 设置标题
                        builder.setMessage(jsonObject.getString("message"));// 提示信息
                        builder.setCancelable(false);// 设置对话框不能被取消
                        // 设置正面的按钮
                        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        builder.show();  // 显示对话框
                    }
                } catch (JSONException e) {
                    loading.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S004_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    //初始化界面
    private void initView() {

        MyAdapter mAdapter = new MyAdapter();
        mLvChuKu.setAdapter(mAdapter);
        mLvChuKu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    return;
                }else{
                    //防止按钮的重复点击，出现两个画面的现象
                    Date date = new Date();
                    long tempClickTime = date.getTime();
                    if (tempClickTime-clickTime<1000){
                        clickTime = tempClickTime;
                        return;
                    }
                    clickTime = tempClickTime;
                    Intent intent = new Intent(C01S004_001Activity.this,c01s004_002Activity.class);
                    intent.putExtra("name",mList.get(position-1));
                    startActivity(intent);
                }
            }
        });
    }
    //界面按钮的点击事件
    @OnClick({R.id.btnCancel, R.id.btnReturn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                Intent intent = new Intent(this, C00S000_002Activity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btnReturn:
                finish();
                break;
            default:
        }
    }

    /**
     * 当返回画面的时候判断是否清空当前画面的信息
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
            if(bundle == null) {
                return;
            }
            boolean isClear = bundle.getBoolean("isClear",false);
            if(isClear){
               //重新获取数据
                mList.clear();
            }
        }
    }

    //补领人信息内容的List适配器
    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size()+1;
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
            View view = LayoutInflater.from(C01S004_001Activity.this).inflate(R.layout.item_buling,null);
            TextView tvShenqingRen = (TextView) view.findViewById(R.id.tvSheqingRen);
            TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
            ImageView ivLine = (ImageView) view.findViewById(R.id.ivLine);
            if (position == mList.size()){
                ivLine.setVisibility(View.GONE);
            }
            if (position == 0){
                tvShenqingRen.setText("申请人");
                tvTime.setText("日期");
            }else{
                tvShenqingRen.setText(mList.get(position-1).getName());
                tvTime.setText(mList.get(position-1).getTime());
            }
            return view;
        }
    }
}
