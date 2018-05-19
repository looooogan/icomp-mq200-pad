package com.icomp.Iswtmv10.v01c01.c01s002;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c01.c01s005.C01S005Wsdl;
import com.icomp.wsdl.v01c01.c01s005.endpoint.C01S005Request;
import com.icomp.wsdl.v01c01.c01s005.endpoint.C01S005Respons;

import java.util.ArrayList;

/**
 * 清空RFID标签
 *
 * @author Taoyy
 * @ClassName: C01S002_001Activity
 * @date 2016年9月4日21:14:53
 */


public class C01S005_102Activity extends CommonActivity {
    //向下个页面传递数据
    private C01S005_Params params;

    private TextView tv_01;
    private Button btn_scan, btn_stop, btn_next;
    //是否正在扫描，线程的终止条件
    private boolean scanf = false;
    //RfidCode
    private String rfidString;
    //判断是否可以点击扫描
    private boolean isScan;
    private boolean isCanScan = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c01s005_102activity);
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传递的数据
        params = (C01S005_Params) getIntent().getSerializableExtra(PARAM);
        //防止程序崩溃
        if (params == null) {
            params = new C01S005_Params();
        }

        //当前初始化的数量:params.scanNumber，初始状态下扫描数量为 0
        tv_01 = (TextView) findViewById(R.id.tv_01);
        params.scanNumber = "0";
        isScan = true;
        tv_01.setText("当前扫描的数量:" + params.scanNumber);

        //扫描，停止，下一步按钮
        btn_scan = (Button) findViewById (R.id.btn_scan);
        btn_stop = (Button) findViewById (R.id.btn_stop);
        btn_next = (Button) findViewById (R.id.btn_next);

        //未扫描之前，停止按钮、下一步按钮都不可用
        btn_stop.setEnabled(false);
        btn_next.setEnabled(false);





        //扫描按钮
        if (btn_scan != null) {
            btn_scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isScan) {
                        isScan = false;
                        scanf = true;
                        //打开读头
                        initRFID();
                        //扫描按钮点击后，扫描按钮置为不可用，背景变暗，字体对应改变
                        btn_scan.setEnabled(false);
                        btn_scan.setBackgroundResource(R.color.baseColor);
                        btn_scan.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.textColor));
                        //扫描按钮点击后，停止按钮可用，提交按钮仍然不可用
                        btn_stop.setEnabled(true);
                        //第二次点击扫描按钮后，停止按钮的状态对应改变
                        btn_stop.setBackgroundResource(R.drawable.border);
                        btn_stop.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
                        //设置点击扫描按钮后，提交按钮状态及可用(此处考虑第二次点击扫描按钮的情况)
                        btn_next.setEnabled(false);
                        btn_next.setBackgroundResource(R.color.hintcolor);
                        //启动扫描线程
                        thread = new VisitJniThread();
                        thread.start();
                    }

                }
            });
        }




        //停止按钮
        if (btn_stop != null) {
            isCanScan = true;
            btn_stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //停止读取rfid线程
                    scanf = false;
                    isScan = true;
                    //关闭读头
                    close();
                    //停止按钮点击后，扫描按钮可用，背景、字体对应改变
                    btn_scan.setEnabled(true);
                    btn_scan.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
                    btn_scan.setBackgroundResource(R.drawable.border);
                    //停止按钮点击后，停止按钮置为不可用，背景变暗，字体对应改变
                    btn_stop.setEnabled(false);
                    btn_stop.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.textColor));
                    btn_stop.setBackgroundResource(R.color.baseColor);
                    //停止按钮点击后，提交按钮可用
                    btn_next.setEnabled(true);
                    btn_next.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
                    btn_next.setBackgroundResource(R.drawable.border);
                }
            });
        }

        //下一步按钮
        if (btn_next != null) {
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    //再次关闭群扫，防止线程未杀死
                    scanf = false;
                    close();
                    //点击提交按钮后，停止按钮状态恢复，但扫描与停止按钮仍不可用
                    btn_scan.setEnabled(false);
                    btn_stop.setEnabled(false);
                    btn_stop.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
                    btn_stop.setBackgroundResource(R.drawable.border);
                    //点击提交后，提交按钮置为不可用，按钮状态改变
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.color.hintcolor);
                    if (Integer.parseInt(params.scanNumber) == 0) {
                        createAlertDialog(C01S005_102Activity.this, getString(R.string.C03S004_2), Toast.LENGTH_SHORT);
                        btn_scan.setEnabled(true);
                    } else {
                        //提示用户
                        new AlertDialog.Builder(C01S005_102Activity.this).
                                setTitle(getString(R.string.infoMsg)).
                                setMessage("当前扫描的RFID标签即将被清空，将无法找回，您确定要清空吗？").
                                setPositiveButton(getString(R.string.confirm),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //弹出等待悬框
                                                loading.show();
                                                nextThread = new NextThread();
                                                nextThread.start();
                                            }
                                        }).
                                setNegativeButton(getString(R.string.cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                btn_scan.setEnabled(true);
                                                btn_next.setEnabled(true);
                                            }
                                        }).show();
                    }
                }
            });
        }
    }

    /**
     * 扫描按键
     */

    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan){
            isCanScan = false;
        }else{
            return;
        }
        if (isScan) {
            isScan = false;
            scanf = true;
            //打开读头
            initRFID();
            //扫描按钮点击后，扫描按钮置为不可用，背景变暗，字体对应改变
            btn_scan.setEnabled(false);
            btn_scan.setBackgroundResource(R.color.baseColor);
            btn_scan.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.textColor));
            //扫描按钮点击后，停止按钮可用，提交按钮仍然不可用
            btn_stop.setEnabled(true);
            //第二次点击扫描按钮后，停止按钮的状态对应改变
            btn_stop.setBackgroundResource(R.drawable.border);
            btn_stop.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
            //设置点击扫描按钮后，提交按钮状态及可用(此处考虑第二次点击扫描按钮的情况)
            btn_next.setEnabled(false);
            btn_next.setBackgroundResource(R.color.hintcolor);
            //启动扫描线程
            thread = new VisitJniThread();
            thread.start();
        }
    }

    //返回按钮处理
    public void appReturn(View view) {
        //再次关闭群扫，防止线程未杀死
        scanf = false;
        close();
        Intent intent = new Intent(this, C01S002_001Activity.class);
        //将合成刀具编码返回到上一个页面
        startActivity(intent);
        finish();
    }

    //扫描按钮处理
    private VisitJniThread thread;

    private class VisitJniThread extends Thread {
        @Override
        public void run() {
            //每次需为RfidString置空
            rfidString = null;
            while (rfidString == null && scanf) {
                //读取Rfid
                rfidString = readRfidString(true);
            }
            if (rfidString != null) {
                //close();
                //封装传递
                Message message = new Message();
                message.obj = rfidString;
                scanfmhandler.sendMessage(message);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler scanfmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //传递数据
            rfidString = msg.obj.toString();
            //如果没有Rfid列表，新建Rfid列表，防止程序崩坏
            if (params.rfidLists == null) {
                params.rfidLists = new ArrayList<String>();
            }
            if (!params.rfidLists.contains(rfidString)) {
                //放入List集合扫描一个数量加1
                params.rfidLists.add(rfidString);
                //当前初始化的数量:params.scanNumber
                params.scanNumber = String.valueOf(Integer.parseInt(params.scanNumber) + 1);
                tv_01.setText("当前扫描的数量:" + params.scanNumber);
            }
            //重新启动线程
            thread = new VisitJniThread();
            thread.start();
        }
    };

    //下一步线程
    private NextThread nextThread;

    private class NextThread extends Thread {
        @Override
        public void run() {
            Message message = new Message();
            try {
                //设定访问用户信息
                SharedPreferences sharedPreferences = getSharedPreferences("userInfo", CommonActivity.MODE_APPEND);
                String customerID = sharedPreferences.getString("customerID", "");
                String handsetid = sharedPreferences.getString("handsetid", "");
                //请求参数类，向后台传递数据，需新建
                C01S005Request request = new C01S005Request();

                //调用接口,需新建
                C01S005Wsdl service = new C01S005Wsdl();
                request.setHandSetId(handsetid);
                //用户ID
                request.setCustomerID(customerID);
                request.setToolType(params.containerCarrierType + "");
                request.setRfidCodeList(params.rfidLists);
                //清空当前扫描的标签信息
                C01S005Respons respons = service.delRfidCodeIsNull(request);
                if (respons != null) {
                    //封装传递数据
                    message.obj = respons;
                    nextmhandler.sendMessage(message);
                } else {
                    respons = new C01S005Respons();
                    respons.setStateMsg(ONE);
                    respons.setStateMsg("提交失败");
                    internetErrorHandler.sendMessage(message);
                }
            } catch (Exception e) {
                //网络连接失败
                internetErrorHandler.sendMessage(message);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler nextmhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //传递数据
            C01S005Respons respons = (C01S005Respons) msg.obj;
            loading.dismiss();
            if (ZERO.equals(respons.getStateCode())) {
                startActivity(new Intent(C01S005_102Activity.this, C01S005_103Activity.class));
                finish();
            } else {
                //错误消息提示
                createAlertDialog(C01S005_102Activity.this, respons.getStateMsg(), Toast.LENGTH_SHORT);
                btn_next.setEnabled(true);
                btn_next.setBackgroundResource(R.drawable.border);
            }
        }
    };

    @Override
    //键盘扫描按钮
    protected boolean keycodeScanf() {
        if (isScan) {
            Log.v("scan", "start");
            //防止再次点击键盘扫描
            isScan = false;
            scanf = true;
            //扫描按钮点击后，扫描按钮置为不可用，背景变暗，字体对应改变
            btn_scan.setEnabled(false);
            btn_scan.setBackgroundResource(R.color.baseColor);
            btn_scan.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.textColor));
            //扫描按钮点击后，停止按钮可用，提交按钮仍然不可用
            btn_stop.setEnabled(true);
            //第二次点击扫描按钮后，停止按钮的状态对应改变
            btn_stop.setBackgroundResource(R.drawable.border);
            btn_stop.setTextColor(C01S005_102Activity.this.getResources().getColorStateList(R.color.baseColor));
            //设置点击扫描按钮后，提交按钮状态及可用(此处考虑第二次点击扫描按钮的情况)
            btn_next.setEnabled(false);
            btn_next.setBackgroundResource(R.color.hintcolor);
            //打开读头
            initRFID();
            //启动扫描线程
            thread = new VisitJniThread();
            thread.start();
        }

        return true;
    }

    /**
     * 处理网络异常的handler
     */
    @SuppressLint("HandlerLeak")
    Handler internetErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            createAlertDialog(null, getString(R.string.netConnection), Toast.LENGTH_LONG);
            loading.dismiss();
        }
    };
}