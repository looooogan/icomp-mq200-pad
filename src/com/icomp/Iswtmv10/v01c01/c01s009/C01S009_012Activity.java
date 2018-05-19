package com.icomp.Iswtmv10.v01c01.c01s009;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.internet.IRequest;
import com.icomp.Iswtmv10.internet.MyCallBack;
import com.icomp.Iswtmv10.internet.RetrofitSingle;
import com.icomp.Iswtmv10.v01c01.c01s008.modul.C01S008Request;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.C01S009Response;
import com.icomp.Iswtmv10.v01c01.c01s009.modul.FuJuModul;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2017/11/17.
 */

public class C01S009_012Activity extends CommonActivity {

    @BindView(R.id.btnScan)
    Button btnScan;

    //扫描线程
    private scanThread scanThread;

    C01S008Request params = new C01S008Request();

    private Retrofit mRetrofit;
    private Gson mGson = new Gson();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s009_012);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        mRetrofit = RetrofitSingle.newInstance();
    }

    //取消按钮处理--跳转到系统菜单页面
    public void btnCancel(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        //防止点击扫描后点击此按钮
        stopScan();
        finish();
    }

    //扫描按钮处理
    @OnClick(R.id.btnScan)
    public void onViewClicked() {
        //点击扫描按钮的方法
        scan();
    }

    //点击扫描按钮的方法
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
    public class scanThread extends Thread {
        @Override
        public void run() {
            super.run();
            //调用单扫方法
            rfidString = singleScan();
            Message message = new Message();
            if ("close".equals(rfidString)) {
                btnScan.setClickable(true);
                isCanScan = true;
                overtimeHandler.sendMessage(message);
            } else if(null != rfidString && !"close".equals(rfidString)) {
                //刀具组装-扫码查询合成刀信息
                IRequest iRequest = mRetrofit.create(IRequest.class);
                Call<String> getSynthesisToolInstall = iRequest.getSynthesisToolInstall(rfidString,"0");
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
                                createAlertDialog(C01S009_012Activity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT);
                                if (popupWindow != null && popupWindow.isShowing()) {
                                    popupWindow.dismiss();
                                }
                                btnScan.setClickable(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void _onFailure(Throwable t) {
                        isCanScan = true;
                        createAlertDialog(C01S009_012Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
                        btnScan.setClickable(true);
                    }
                });
            }
        }
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
        MyAdapter myAdapter = new C01S009_012Activity.MyAdapter(list);
        lvZuZhuang.setAdapter(myAdapter);
        tvDaoJuCode.setText("合成刀具编码：" + code);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.setRfidString(rfidString);
                params.setSynthesisParametersCodes(code);
                params.setRfidContainerIDs(rfid);
                params.setToolConsumetypes(type);
                //跳转页面，传值 rfid,新材料号，costomerID
                dialog.dismiss();
                Intent intent = new Intent(C01S009_012Activity.this,C01S009_013Activity.class);
                intent.putExtra(PARAM, params);
                startActivity(intent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                btnScan.setClickable(true);
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
            View view = LayoutInflater.from(C01S009_012Activity.this).inflate(R.layout.item_dialog_fujulist,null);
            TextView tvFunJu = (TextView) view.findViewById(R.id.tvFunJu);
            tvFunJu.setText(list.get(position).getName()+"   "+list.get(position).getNum()+"把");
            return view;
        }

        public List<FuJuModul> getList(){
            return list;
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
