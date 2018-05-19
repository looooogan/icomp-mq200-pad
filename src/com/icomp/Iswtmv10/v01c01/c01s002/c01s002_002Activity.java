package com.icomp.Iswtmv10.v01c01.c01s002;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.Iswtmv10.v01c01.c01s001.C01S001_001Activity;
import com.icomp.common.activity.CommonActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class c01s002_002Activity extends CommonActivity {

    @BindView(R.id.tvNum)
    TextView mTvNum;
    @BindView(R.id.btnScan)
    Button mBtnScan;
    @BindView(R.id.btnStop)
    Button mBtnStop;
    @BindView(R.id.btnCancel)
    Button mBtnCancel;
    @BindView(R.id.btnNext)
    Button mBtnNext;

    private List<String> mNumList = new ArrayList<>(); // 保存扫描的标签
    private String mCategory;
    private int num = 0;
    private boolean flag = true;

    //是否正在扫描，线程的控制
    private Boolean scanf = false;
    //员工卡RfidCode
    private String cardString;

    //扫描线程
    private VisitJniThread thread;

    //扫描弹框
    private PopupWindow popupWindow2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s002_002);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        mCategory = intent.getStringExtra("skill");
    }

    @OnClick({R.id.btnScan, R.id.btnStop, R.id.btnCancel, R.id.btnNext})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                scan();
                break;
            case R.id.btnStop:
                flag = false;
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:
                Intent intent = new Intent(this,c01s002_003Activity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 扫描
     */
    private void scan(){
        flag = true;
        if (popupWindow2 == null || !popupWindow2.isShowing()) {
            showWindow2(R.layout.c00s000_010activity);
        }
    }

    //打开扫描窗体

    private void showWindow2(int pageID) {
        //在哪个页面打开窗口
//        LayoutInflater layoutInflater = LayoutInflater.from(this);
//        //用inflate方法调用界面
//        final View vPopupWindow = layoutInflater.inflate(pageID, null);
//        //从res下选择要作为背景的源文件
//        vPopupWindow.setBackgroundResource(R.drawable.my_selectors);
//        //设置源文件的长宽
//        popupWindow2 = new PopupWindow(vPopupWindow, 300, 240);
//        //从res下选择要作为背景的源文件
//        //popupWindow2.setBackgroundDrawable(new BitmapDrawable());
//        //设置弹框外部无法获取焦点
//        popupWindow2.setFocusable(true);
//        //设置popWindow的显示位置
//        popupWindow2.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        //启动扫描线程
        thread = new VisitJniThread();
        thread.start();

    }


    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            boolean isGo = true;
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
                if(flag == false){
                    isGo = false;
                    break;
                }
            } while (cardString == null && scanf);
            if(isGo == false){
                thread.interrupt();
                //关闭扫描读头
                close();
            }
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
            //员工卡RfidCode
            cardString = msg.obj.toString();
            if(!mNumList.contains(cardString)){
                mNumList.add(cardString);
                num++;
            }
            mTvNum.setText("当前扫描数量："+num);
            if ("close".equals(cardString)) {
                createAlertDialog(c01s002_002Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_SHORT);
            }
            thread = new VisitJniThread();
            thread.start();
        }
    };
}
