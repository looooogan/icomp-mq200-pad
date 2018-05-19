package com.icomp.Iswtmv10.v01c01.c01s011;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.adapter.C01S003_004Adapter;
import com.icomp.common.utils.SysApplication;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 安上设备
 *
 * @author WHY
 * @ClassName: C01S011_002Activity
 * @date 2016-3-1 下午8:54:37
 */

public class C01S011_002Activity extends CommonActivity {
    private C01S011_Params params;//序列化
    private PopupWindow popupWindow;//扫描设备popupWindow
    private LinearLayout ll_01;//选择安上设备下拉框
    private LinearLayout ll_011;//选择對應軸號下拉框
    private TextView tv_01;//合成刀编码
    private String rfidString = null;//读取的信息
    private boolean stoptj = true;//扫描停止条件
    private List<c01s011_002Entity> equipment = new ArrayList<c01s011_002Entity>();//存放设备信息
    private int num1;//保存当前选择的设备位置
    private int num2;//保存当前选择的轴位置
    private TextView tv_011, tv_0111;//安上设备显示框，初始设备显示框
    private PopupWindow mPopWindow1;//设备下拉列表框
    private List<String> equipmentNameList;//给适配器用的设备列表
    private List<String> axleCodeList;//给适配器用的轴号列表
    private List<C01s011Bean> c01s011Bean = new ArrayList<C01s011Bean>();//最终用来操作数据的集合

    private boolean isCanScan = true; //是否可以按键扫描

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SysApplication.getInstance().addActivity(this);
        setContentView(R.layout.c01s011_002activity);
        params = (C01S011_Params) getIntent().getSerializableExtra(PARAM);//后台取值
        tv_01 = (TextView) findViewById(R.id.tv_01);//合成刀编码
        ll_01 = (LinearLayout) findViewById(R.id.ll_01);//选择安上设备下拉框
        ll_011 = (LinearLayout) findViewById(R.id.ll_011);//选择對應軸號下拉框
        tv_011 = (TextView) findViewById(R.id.tv_011);//安上设备显示框
        tv_0111 = (TextView) findViewById(R.id.tv_0111);//轴号显示框
        tv_01.setText(params.synthesisParametersCode);//合成刀编码
//        num1 = 0;//保存当前选择的设备位置
//        num2 = 0;//保存当前选择的轴位置
        //把取到的集合里面的设备信息单独存到集合中
        for (int i = 0; i < params.equipmentEntity.size(); i++) {
            c01s011_002Entity item = new c01s011_002Entity();
            item.setEquipmentID(params.equipmentEntity.get(i).getEquipmentId());
            item.setEquipmentName(params.equipmentEntity.get(i).getEquipmentName());
            item.setRfidCode(params.equipmentEntity.get(i).getRfidCode());
            equipment.add(item);
        }
        //把取到的集合里面的设备信息根据设备名去重复
        for (int i = 0; i < equipment.size() - 1; i++) {
            for (int j = equipment.size() - 1; j > i; j--) {
                if (equipment.get(j).getEquipmentName().equals(equipment.get(i).getEquipmentName())) {
                    equipment.remove(j);
                }
            }
        }
        //把去重后的设备名 存到最终使用的集合 每条设备信息对应多个轴号
        for (int i = 0; i < equipment.size(); i++) {
            C01s011Bean bean = new C01s011Bean();
            bean.setRfidCode(equipment.get(i).getRfidCode());
            bean.setEquipmentName(equipment.get(i).getEquipmentName());
            bean.setEquipmentID(equipment.get(i).getEquipmentID());
            c01s011Bean.add(bean);
        }
        //给每个设备对应的轴号集合赋值
        for (int i = 0; i < c01s011Bean.size(); i++) {
            List<c01s011_002_02Entity> c01s011_002EntityList = new ArrayList<c01s011_002_02Entity>();
            for (int j = 0; j < params.equipmentEntity.size(); j++) {
                if (c01s011Bean.get(i).getEquipmentName().equals(params.equipmentEntity.get(j).getEquipmentName())) {
                    c01s011_002_02Entity entity = new c01s011_002_02Entity();
                    entity.setAxleCode(params.equipmentEntity.get(j).getAxleCode());
                    entity.setAxleID(params.equipmentEntity.get(j).getAxleID());
                    c01s011_002EntityList.add(entity);
                }
                c01s011Bean.get(i).setAxleList(c01s011_002EntityList);
            }
            //去掉轴号里的重复值
            for (int k = 0; k < c01s011Bean.get(i).getAxleList().size() - 1; k++) {
                for (int j = c01s011Bean.get(i).getAxleList().size() - 1; j > k; j--) {
                    if (c01s011Bean.get(i).getAxleList().get(j).getAxleID().equals(c01s011Bean.get(i).getAxleList().get(k).getAxleID())) {
                        c01s011Bean.get(i).getAxleList().remove(j);
                    }
                }
            }

        }
//        tv_011.setText(c01s011Bean.get(0).getEquipmentName());
//        tv_0111.setText(c01s011Bean.get(0).getAxleList().get(0).getAxleCode());


    }

    @Override
    protected void btnScan() {
        super.btnScan();
        if(isCanScan){
            isCanScan = false;
        }else{
            return;
        }
        if (popupWindow == null || !popupWindow.isShowing()) {
            showWindow(null, R.layout.c00s000_010activity);
        }
    }

    /**
     * 监听键盘扫描按键
     *
     * @return
     */
//    protected boolean keycodeScanf() {
//        if (popupWindow == null || !popupWindow.isShowing()) {
//            showWindow(null, R.layout.c00s000_010activity);
//        }
//        return true;
//    }

    /**
     * 点击安上设备下拉框
     */
    public void dropdown(View view) {
        if (c01s011Bean.size() > 0) {
            int tvWidht = ll_01.getWidth();
            View contentView = LayoutInflater.from(C01S011_002Activity.this).inflate(R.layout.item_c01s020_001, null);
            mPopWindow1 = new PopupWindow(contentView, tvWidht, 200, true);
            mPopWindow1.setBackgroundDrawable(new BitmapDrawable());
            mPopWindow1.showAsDropDown(ll_01);//显示对话框
            ListView lv_01 = (ListView) contentView.findViewById(R.id.lv_01);
            equipmentNameList = new ArrayList<String>();
            for (int i = 0; i < c01s011Bean.size(); i++) {
                equipmentNameList.add(c01s011Bean.get(i).getEquipmentName());
            }
            C01S003_004Adapter adapter = new C01S003_004Adapter(C01S011_002Activity.this, equipmentNameList);
            lv_01.setAdapter(adapter);
            lv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    num1 = i;
                    num2 = 0;
                    tv_011.setText(equipmentNameList.get(i));
                    tv_0111.setText(c01s011Bean.get(num1).getAxleList().get(num2).getAxleCode());
                    mPopWindow1.dismiss();
                }
            });
        }

    }

    /**
     * 点击轴号下拉框
     */
    public void dropdown1(View view) {
        if (c01s011Bean.size() > 0&&!"".equals(tv_0111.getText().toString())) {
            int tvWidht = ll_011.getWidth();
            View contentView = LayoutInflater.from(C01S011_002Activity.this).inflate(R.layout.item_c01s020_001, null);
            mPopWindow1 = new PopupWindow(contentView, tvWidht, 200, true);
            mPopWindow1.setBackgroundDrawable(new BitmapDrawable());
            mPopWindow1.showAsDropDown(ll_011);//显示对话框
            ListView lv_01 = (ListView) contentView.findViewById(R.id.lv_01);
            axleCodeList = new ArrayList<String>();
            for (int i = 0; i < c01s011Bean.get(num1).getAxleList().size(); i++) {
                axleCodeList.add(c01s011Bean.get(num1).getAxleList().get(i).getAxleCode());
            }
            C01S003_004Adapter adapter = new C01S003_004Adapter(C01S011_002Activity.this, axleCodeList);
            lv_01.setAdapter(adapter);
            lv_01.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    tv_0111.setText(axleCodeList.get(i));
                    mPopWindow1.dismiss();
                    num2 = i;
                }
            });
        }
    }

    /**
     * 扫描按钮点击
     */
    public void scan(View view) {
        isCanScan = false;
        showWindow(null, R.layout.c00s000_010activity);
    }

    /**
     * 下一步
     */
    public void appNext(View view) {
        if (null != popupWindow && popupWindow.isShowing()) {
            stoptj = false;
            popupWindow.dismiss();
        }

        if (null != tv_011 && !"".equals(tv_011.getText().toString().trim()) && null != tv_0111 && !"".equals(tv_0111.getText().toString().trim())) {
            Intent intent = new Intent(C01S011_002Activity.this, C01S011_003Activity.class);
            intent.putExtra("synthesisParametersCode", params.synthesisParametersCode);//合成刀编码
            intent.putExtra("equipmentID", c01s011Bean.get(num1).getEquipmentID());//装入设备的设备ID
            intent.putExtra("equipmentAxisNumberID", c01s011Bean.get(num1).getAxleList().get(num2).getAxleID());//装入设备的轴号ID
            intent.putExtra("synthesisParametersRfid", params.synthesisParametersRfid);//合成刀具RFID
            intent.putExtra("equipmentName", c01s011Bean.get(num1).getEquipmentName());//设备名
            intent.putExtra("axleCode", c01s011Bean.get(num1).getAxleList().get(num2).getAxleCode());//轴
            intent.putExtra("gruantUserID", params.getGruantUserID());
            startActivity(intent);
        } else {
            createAlertDialog(C01S011_002Activity.this, "请配置生产关联或绑定对应设备标签", Toast.LENGTH_LONG);
        }

    }

    /**
     * 返回
     */
    public void appReturn(View view) {
        finish();
        stoptj = false;
    }

    /**
     * RFID扫描页面
     *
     * @param parent
     * @param pageID
     */
    protected void showWindow(View parent, int pageID) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        final View vPopupWindow = layoutInflater.inflate(pageID, null);
        popupWindow = new PopupWindow(vPopupWindow, 300, 240);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(vPopupWindow, Gravity.CENTER_VERTICAL, 0, 0);
        threadLoad = new LoadingThread();
        threadLoad.start();
    }

    /**
     * 读取RFID线程
     */
    public class LoadingThread extends Thread {
        @Override
        public void run() {
            initRFID();
            rfidString = null;
            stoptj = true;
            Date date = new Date();
            do {
                rfidString = readRfidString(true);
                if (new Date().getTime() - date.getTime() >= SCANF_TIME) {
                    rfidString = "close";
                }

            } while (rfidString == null && stoptj);
            close();
            if (null != rfidString) {

                Message message = new Message();
                message.obj = rfidString;
                handler.sendMessage(message);
            }
        }
    }

    public LoadingThread threadLoad;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnString = msg.obj.toString();
            if ("close".equals(returnString)) {
                isCanScan = true;
                createAlertDialog(C01S011_002Activity.this, getString(R.string.C01S001001_1), Toast.LENGTH_LONG);
                popupWindow.dismiss();
            } else {
                popupWindow.dismiss();
                //判断是否有对应上的
                boolean type = true;
                for (int i = 0; i < c01s011Bean.size(); i++) {
                    if (null != c01s011Bean.get(i).getRfidCode() && returnString.equals(c01s011Bean.get(i).getRfidCode())) {
                        num1 = i;
                        num2 = 0;
                        tv_011.setText(c01s011Bean.get(i).getEquipmentName());

                        tv_0111.setText(c01s011Bean.get(num1).getAxleList().get(num2).getAxleCode());
                        type = false;//判断列表中是否有跟扫描的RFID对应的设备，如果没有执行下面的If
                        break;
                    }
                }
                if (type) {
                    createAlertDialog(C01S011_002Activity.this, "扫描的设备与该合成刀具不匹配", Toast.LENGTH_LONG);
                }
                isCanScan = true;


            }
        }
    };
    /**
     * 友盟错误统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
