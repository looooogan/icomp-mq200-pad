package com.icomp.Iswtmv10.v01c03.c03s006;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
import com.icomp.Iswtmv10.v01c03.c03s001.C03S001Params;
import com.icomp.Iswtmv10.v01c03.c03s001.C03S001_002;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.GetItemHeight;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c03.c03s001.endpoint.SynthesisEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 筒刀初始化页面2
 * Created by FanLL on 2017/9/12.
 */

public class C03S006_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.lv_01)
    ListView lv01;
    @BindView(R.id.btnScan)
    Button btnScan;

    //扫描线程
    private scanThread scanThread;

    //合成刀具初始化参数类
    private C03S001Params params = new C03S001Params();
    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s006_002);
        ButterKnife.bind(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传递过来的参数
        params = (C03S001Params) getIntent().getSerializableExtra(PARAM);
        //将传递过来的合成刀具编码显示在TextView上
        tv01.setText(exChangeBig(params.synthesisParametersCode.trim()) + exChangeBig(params.tubeKnifeCode.trim()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyAdapter adapter = new MyAdapter(C03S006_002Activity.this, params.list);
        lv01.setAdapter(adapter);
    }

    //取消按钮处理--跳转到下一页面
    public void btnCancel(View view) {
        Intent intent = new Intent(this, C00S000_002Activity.class);
        startActivity(intent);
        finish();
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C03S006_001Activity.class);
        //将材料号返回上一页面
        intent.putExtra(PARAM1, params.synthesisParametersCode);
        intent.putExtra(PARAM2, params.tubeKnifeCode);
        startActivity(intent);
        finish();
    }

    //点击扫描按钮的方法
    @OnClick(R.id.btnScan)
    public void onViewClicked() {
        //扫描方法
        scan();
    }

    //扫描方法
    private void scan() {
        //点击扫描按钮以后，设置扫描按钮不可用
        btnScan.setClickable(false);
        //显示扫描弹框的方法
        scanPopupWindow();
        //开启扫描线程
        scanThread = new scanThread();
        scanThread.start();
    }

    //扫描线程
    private class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //单扫方法
            rfidString = singleScan();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                Message message = new Message();
                overtimeHandler.sendMessage(message);
            } else if (null != rfidString && !"close".equals(rfidString)) {
                //调用接口，验证要初始化的合成刀具标签
                IRequest iRequest = retrofit.create(IRequest.class);
                Call<String> checkInitSynthesis = iRequest.checkInitSynthesis(rfidString, exChangeBig(params.synthesisParametersCode));
                checkInitSynthesis.enqueue(new MyCallBack<String>() {
                    @Override
                    public void _onResponse(Response response) {
                        try {
                            String json = response.body().toString();
                            JSONObject jsonObject = new JSONObject(json);
                            Gson gson = new Gson();
                            if (jsonObject.getBoolean("success")) {
                                C03S001_002 c03s001002 = gson.fromJson(json, C03S001_002.class);
                                if ("".equals(c03s001002.getMessage())) {
                                    //新标签提交初始化筒刀的信息
                                    aaa();
                                } else {
                                    new AlertDialog.Builder(C03S006_002Activity.this).
                                            setTitle(R.string.prompt).
                                            setMessage(jsonObject.getString("message")).
                                            setCancelable(false).
                                            setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    //重新初始化的标签提交初始化筒刀的信息
                                                    bbb();
                                                }
                                            }).
                                            setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                }
                            } else {
                                createAlertDialog(C03S006_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            popupWindow.dismiss();
                            btnScan.setClickable(true);
                            isCanScan = true;
                        }
                    }

                    @Override
                    public void _onFailure(Throwable t) {
                        createAlertDialog(C03S006_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        popupWindow.dismiss();
                        btnScan.setClickable(true);
                        isCanScan = true;
                    }
                });
            }
        }
    }

    //新标签提交初始化筒刀的信息
    private void aaa() {
        loading.show();
        //设定用户访问信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        //用户ID
        String customerID = sharedPreferences.getString("customerID", "");
        //手持机ID
        String handSetId = sharedPreferences.getString("handsetid", "");
        //调用接口，提交初始化合成刀具RFIDCodeList
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> submitFInitSynthesis = iRequest.submitFInitSynthesis(rfidString, exChangeBig(params.synthesisParametersCode), params.createType, customerID, handSetId, ZERO, ONE, params.tubeKnifeCode);
        submitFInitSynthesis.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C03S006_002Activity.this, C03S006_003Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S006_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C03S006_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnScan.setClickable(true);
                isCanScan = true;
            }
        });
    }

    //重新初始化的标签提交初始化筒刀的信息
    private void bbb() {
        loading.show();
        //设定用户访问信息
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
        //用户ID
        String customerID = sharedPreferences.getString("customerID", "");
        //手持机ID
        String handSetId = sharedPreferences.getString("handsetid", "");
        //调用接口，提交初始化合成刀具RFIDCodeList
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> submitFInitSynthesis = iRequest.submitFInitSynthesis(rfidString, exChangeBig(params.synthesisParametersCode), params.createType, customerID, handSetId, ONE, ONE, params.tubeKnifeCode);
        submitFInitSynthesis.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response response) {
                try {
                    String json = response.body().toString();
                    JSONObject jsonObject = new JSONObject(json);
                    if (jsonObject.getBoolean("success")) {
                        Intent intent = new Intent(C03S006_002Activity.this, C03S006_003Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        createAlertDialog(C03S006_002Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                    btnScan.setClickable(true);
                    isCanScan = true;
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C03S006_002Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                btnScan.setClickable(true);
                isCanScan = true;
            }
        });
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private List<SynthesisEntity> synthesisEntityList;

        public MyAdapter(Context context, List<SynthesisEntity> list) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            synthesisEntityList = list;
        }

        @Override
        public int getCount() {
            if (null != synthesisEntityList) {
                return synthesisEntityList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (null != synthesisEntityList) {
                return synthesisEntityList.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewholder = null;
            if (null == view) {
                view = layoutInflater.inflate(R.layout.adapter_c03s001_002, null);
                viewholder = new ViewHolder();
                viewholder.tv01 = (TextView) view.findViewById(R.id.tv_01);
                viewholder.tv02 = (TextView) view.findViewById(R.id.tv_02);
                viewholder.tv03 = (TextView) view.findViewById(R.id.tv_03);
                //设置每条信息所占屏幕百分比
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (GetItemHeight.getScreenHeight(context) * 0.07));
                view.setLayoutParams(layoutParams);
                view.setTag(viewholder);
            } else {
                viewholder = (ViewHolder) view.getTag();
            }
            //显示数据
            viewholder.tv01.setText(exChangeBig(synthesisEntityList.get(i).getToolCode()));//材料号
            viewholder.tv02.setText(synthesisEntityList.get(i).getCounts() + "");//刀具数量，类型转化为String类型
            viewholder.tv03.setText(synthesisEntityList.get(i).getCutterType());//刀具类型
            return view;
        }

        class ViewHolder {
            @BindView(R.id.tv_01)
            TextView tv01;
            @BindView(R.id.tv_02)
            TextView tv02;
            @BindView(R.id.tv_03)
            TextView tv03;
        }

    }

    //重写键盘上扫描按键的方法
    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan) {
            isCanScan = false;
        } else {
            return;
        }
        //扫描方法
        scan();
    }

}
