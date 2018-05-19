package com.icomp.Iswtmv10.v01c01.c01s018;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.SysApplication;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 厂内修磨页面2
 * Created by FanLL on 2017/6/16.
 */

public class C01S018_002Activity extends CommonActivity {

    @BindView(R.id.iv_01)
    ImageView iv01;
    @BindView(R.id.ll_01)
    LinearLayout ll01;

    //查询弹框
    private PopupWindow popupWindow2;
    //厂外修磨参数类
    private C01S018Params params = new C01S018Params();
    //向下一个页面传递参数的List
    private List<C01S018Params> list = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c01s018_002);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        ButterKnife.bind(this);
    }

    //返回按钮处理--跳转到上一页（厂内修磨菜单页面）
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C01S018_001Activity.class);
        startActivity(intent);
        finish();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        if (ll01.getChildCount() == 0) {
            createAlertDialog(C01S018_002Activity.this, getString(R.string.c01s018_002_001), Toast.LENGTH_LONG);
        } else if (ll01.getChildCount() == 1) {
            //如果只有一行判断是否为空
            C01S018Params params1 = new C01S018Params();
            LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(0);
            //遍历每一行中的每一个控件
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                View view1 = linearLayout.getChildAt(i);
                if (view1 instanceof TextView) {
                    switch (view1.getId()) {
                        case R.id.tvmaterialNumber:
                            params1.setMaterialNumber(((TextView) view1).getText().toString().trim());
                            break;
                        case R.id.tvgrindingQuantity:
                            params1.setGrindingQuantity(((TextView) view1).getText().toString().trim());
                            break;
                    }
                }
            }
            if (null != params1.getMaterialNumber() && !"".equals(params1.getMaterialNumber())
                    && null != params1.getGrindingQuantity() && !"".equals(params1.getGrindingQuantity())) {
                list.clear();
                list.add(params1);
                Gson gosn = new Gson();
                Intent intent = new Intent(this, C01S018_003Activity.class);
                intent.putExtra(PARAM, gosn.toJson(list));
                startActivity(intent);
            } else {
                createAlertDialog(C01S018_002Activity.this, getString(R.string.c01s018_002_001), Toast.LENGTH_LONG);
            }
        } else {
            //遍历页面列表数据传递到下一个页面
            list.clear();
            //遍历每一行
            for (int i = 0; i < ll01.getChildCount(); i++) {
                C01S018Params params1 = new C01S018Params();
                LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(i);
                //遍历每一行中的每一个控件
                for (int j = 0; j < linearLayout.getChildCount() - 1; j++) {
                    View view1 = linearLayout.getChildAt(j);
                    if (view1 instanceof TextView) {
                        switch (view1.getId()) {
                            case R.id.tvmaterialNumber:
                                params1.setMaterialNumber(exChangeBig(((TextView) view1).getText().toString().trim()));
                                break;
                            case R.id.tvgrindingQuantity:
                                params1.setGrindingQuantity(((TextView) view1).getText().toString().trim());
                                break;
                        }
                    }
                }
                if (null != params1.getMaterialNumber() && !"".equals(params1.getMaterialNumber())
                        && null != params1.getGrindingQuantity() && !"".equals(params1.getGrindingQuantity())) {
                    list.add(params1);
                }
            }
            Gson gosn = new Gson();
            Intent intent = new Intent(this, C01S018_003Activity.class);
            intent.putExtra(PARAM, gosn.toJson(list));
            startActivity(intent);
        }
    }

    //点击加号增加一行的方法
    @OnClick(R.id.iv_01)
    public void onViewClicked() {
        //控制只可以添加一个空行
        if (!fullOrNot) {
            createAlertDialog(this, getString(R.string.c01s019_001_002), Toast.LENGTH_LONG);
            return;
        } else {
            //加一行
            final View addRow = LayoutInflater.from(this).inflate(R.layout.item_c01s019_001, null);
            ImageView delete = (ImageView) addRow.findViewById(R.id.iv_01);//减号
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout child = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
                    boolean lastOrNot = false;
                    //如果addRow的位置和ll01最后一行的位置相同，设置lastOrNot = true
                    if (addRow.getTag() == child.getTag()) {
                        lastOrNot = true;
                    }
                    //如果删除的是最后一行并且为空，设置fullOrNot = true
                    if (!fullOrNot && lastOrNot) {
                        fullOrNot = true;
                    }
                    ll01.removeView(addRow);
                }
            });
            TextView materialNumber = (TextView) addRow.findViewById(R.id.tvmaterialNumber);//材料号
            materialNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //显示弹框
                    showDialog();
                }
            });
            //显示在列表上
            ll01.addView(addRow);
            fullOrNot = false;
        }
    }

    //显示材料号和修磨数量的弹框
    private void showDialog() {
        if (null == popupWindow2 || !popupWindow2.isShowing()) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            final View view = layoutInflater.inflate(R.layout.dialog_c01s019_001, null);
            popupWindow2 = new PopupWindow(view, (int) (0.8 * screenWidth), (int) (0.6 * screenHeight), true);
            popupWindow2.setFocusable(true);
            popupWindow2.setOutsideTouchable(false);
            popupWindow2.showAtLocation(view, Gravity.CENTER_VERTICAL, 0, 0);
            final EditText etmaterialNumber = (EditText) view.findViewById(R.id.etmaterialNumber);
            etmaterialNumber.setTransformationMethod(new AllCapTransformationMethod());
            final EditText etgrindingQuantity = (EditText) view.findViewById(R.id.etgrindingQuantity);
            Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow2.dismiss();
                }
            });
            Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("".equals(etmaterialNumber.getText().toString().trim())) {
                        createAlertDialog(C01S018_002Activity.this, getString(R.string.c01s019_001_003), Toast.LENGTH_LONG);
                    } else if ("".equals(etgrindingQuantity.getText().toString().trim())) {
                        createAlertDialog(C01S018_002Activity.this, getString(R.string.c01s019_001_004), Toast.LENGTH_LONG);
                    } else if (0 == Integer.parseInt(etgrindingQuantity.getText().toString().trim())) {
                        createAlertDialog(C01S018_002Activity.this, getString(R.string.c01s018_012_005), Toast.LENGTH_LONG);
                    } else {
                        params.setMaterialNumber(exChangeBig(etmaterialNumber.getText().toString().trim()));
                        params.setGrindingQuantity(etgrindingQuantity.getText().toString().trim());
                        popupWindow2.dismiss();
                        addData(params);
                    }
                }
            });
        }
    }

    //将数据放到表格上
    private void addData(C01S018Params params) {
        //取列表的最后一行
        LinearLayout linearLayout = (LinearLayout) ll01.getChildAt(ll01.getChildCount() - 1);
        //遍历最后一行里的所有控件
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            View view = linearLayout.getChildAt(i);
            if (view instanceof TextView) {
                switch (view.getId()) {
                    case R.id.tvmaterialNumber:
                        ((TextView) view).setText(params.getMaterialNumber());
                        view.setClickable(false);
                        break;
                    case R.id.tvgrindingQuantity:
                        ((TextView) view).setText(params.getGrindingQuantity());
                        view.setClickable(false);
                        break;
                }
            }
        }
        fullOrNot = true;
    }

}
