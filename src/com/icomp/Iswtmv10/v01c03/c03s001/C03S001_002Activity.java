package com.icomp.Iswtmv10.v01c03.c03s001;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.icomp.Iswtmv10.R;
import com.icomp.common.activity.CommonActivity;
import com.icomp.common.utils.GetItemHeight;
import com.icomp.common.utils.SysApplication;
import com.icomp.wsdl.v01c03.c03s001.endpoint.SynthesisEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 合成刀具初始化页面2
 * Created by FanLL on 2017/6/15.
 */

public class C03S001_002Activity extends CommonActivity {

    @BindView(R.id.tv_01)
    TextView tv01;
    @BindView(R.id.lv_01)
    ListView lv01;

    //合成刀具初始化参数类
    private C03S001Params params = new C03S001Params();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c03s001_002);
        ButterKnife.bind(this);
        //创建Activity时，添加到List进行管理
        SysApplication.getInstance().addActivity(this);
        //接受上一个页面传递过来的参数
        params = (C03S001Params) getIntent().getSerializableExtra(PARAM);
        //将传递过来的合成刀具编码显示在TextView上
        tv01.setText(exChangeBig(params.synthesisParametersCode.trim()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //循环遍历list显示在列表上
        if (null != params.list && params.list.size() >= 0) {
            for (int i = 0; i < params.list.size(); i++) {
                //消耗类别(0:可刃磨钻头1可刃磨刀片2一次性刀片9辅具）
                if (ZERO == params.list.get(i).getCutterType() || params.list.get(i).getCutterType().equals(ZERO)) {
                    params.list.get(i).setCutterType(getString(R.string.c03s001_002_001));
                } else if (ONE == params.list.get(i).getCutterType() || params.list.get(i).getCutterType().equals(ONE)) {
                    params.list.get(i).setCutterType(getString(R.string.c03s001_002_002));
                } else if (TWO == params.list.get(i).getCutterType() || params.list.get(i).getCutterType().equals(TWO)) {
                    params.list.get(i).setCutterType(getString(R.string.c03s001_002_003));
                } else if (NINE == params.list.get(i).getCutterType() || params.list.get(i).getCutterType().equals(NINE)) {
                    params.list.get(i).setCutterType(getString(R.string.c03s001_002_004));
                }
            }
            MyAdapter adapter = new MyAdapter(C03S001_002Activity.this, params.list);
            lv01.setAdapter(adapter);
        }
    }

    //返回按钮处理--返回上一页面
    public void btnReturn(View view) {
        Intent intent = new Intent(this, C03S001_001Activity.class);
        //将材料号返回上一页面
        intent.putExtra(PARAM1, params.synthesisParametersCode);
        startActivity(intent);
        finish();
    }

    //下一步按钮处理--跳转到下一页面
    public void btnNext(View view) {
        Intent intent = new Intent(this, C03S001_003Activity.class);
        intent.putExtra(PARAM, params);
        startActivity(intent);
        finish();
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
                AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (GetItemHeight.getScreenHeight(context)*0.07));
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

}
