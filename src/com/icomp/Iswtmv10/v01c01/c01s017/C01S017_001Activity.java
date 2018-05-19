package com.icomp.Iswtmv10.v01c01.c01s017;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.icomp.Iswtmv10.v01c00.c00s000.C00S000_002Activity;
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
 * 回厂确认页面1
 * Created by FanLL on 2017/9/15.
 */

public class C01S017_001Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.ll_01)
    LinearLayout ll01;

    //显示回厂单号的列表
    private List<String> listString = new ArrayList<>();

    //调用接口
    private Retrofit retrofit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s017_000);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //调用接口
        retrofit = RetrofitSingle.newInstance();
        //调用接口，查询所有单号列表
        getListOfOutOrdNumbers();
    }

    //返回按钮处理--跳转到系统菜单页面
    public void btnReturn(View view) {
        finish();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        if (!"".equals(tv01.getText().toString().trim())) {
            Intent intent = new Intent(this, C01S017_002Activity.class);
            intent.putExtra(PARAM, tv01.getText().toString().trim());
            startActivity(intent);
            finish();
        } else {
            createAlertDialog(C01S017_001Activity.this, getString(R.string.c01s017_000_001), Toast.LENGTH_LONG);
        }
    }

    @OnClick(R.id.ll_01)
    public void onViewClicked() {
        //显示回厂单号列表
        showPopupWindow();
    }

    //调用接口，查询所有单号列表
    private void getListOfOutOrdNumbers() {
        loading.show();
        IRequest iRequest = retrofit.create(IRequest.class);
        Call<String> getOutFactoryOutDoorNomList = iRequest.getOutFactoryOutDoorNomList();
        getOutFactoryOutDoorNomList.enqueue(new MyCallBack<String>() {
            @Override
            public void _onResponse(Response<String> response) {
                try {
                    String json = response.body();
                    JSONObject jsonObject = new JSONObject(json);
                    Gson gson = new Gson();
                    if (jsonObject.getBoolean("success")) {
                        C01S017_001 c01s017001 = gson.fromJson(json, C01S017_001.class);
                        listString.clear();
                        if (c01s017001.getData().size() == 0 ) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(C01S017_001Activity.this);
                            builder.setTitle(R.string.prompt);
                            builder.setMessage("无可回厂订单");
                            builder.setCancelable(false);
                            builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(C01S017_001Activity.this, C00S000_002Activity.class);
                                    startActivity(intent);
                                    SysApplication.getInstance().exit();
                                }
                            }).show();
                        } else {
                            for (int i = 0; i < c01s017001.getData().size(); i++) {
                                listString.add(c01s017001.getData().get(i).getOutDoorNom());
                            }
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(C01S017_001Activity.this);
                        builder.setTitle(R.string.prompt);
                        builder.setMessage(jsonObject.getString("message"));
                        builder.setCancelable(false);
                        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(C01S017_001Activity.this, C00S000_002Activity.class);
                                startActivity(intent);
                                SysApplication.getInstance().exit();
                            }
                        }).show();
//                        createAlertDialog(C01S017_001Activity.this, jsonObject.getString("message"), Toast.LENGTH_LONG);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    loading.dismiss();
                }
            }

            @Override
            public void _onFailure(Throwable t) {
                loading.dismiss();
                createAlertDialog(C01S017_001Activity.this, getString(R.string.netConnection), Toast.LENGTH_LONG);
            }
        });
    }

    //显示回厂单号列表
    private void showPopupWindow() {
        View view = LayoutInflater.from(C01S017_001Activity.this).inflate(R.layout.spinner_c03s004_001, null);
        ListView listView = (ListView) view.findViewById(R.id.ll_spinner);
        MyAdapter myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        final PopupWindow popupWindow = new PopupWindow(view, ll01.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new PaintDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv01.setText(listString.get(i));
                popupWindow.dismiss();
            }
        });
        popupWindow.showAsDropDown(ll01);
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listString.size();
        }

        @Override
        public Object getItem(int i) {
            return listString.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = LayoutInflater.from(C01S017_001Activity.this).inflate(R.layout.item_c03s004_001, null);
            TextView textView = (TextView) view1.findViewById(R.id.tv_01);
            textView.setText(listString.get(i));
            return view1;
        }

    }

}
