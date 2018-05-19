// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s001;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S001_001Activity_ViewBinding<T extends C01S001_001Activity> implements Unbinder {
  protected T target;

  private View view2131558484;

  private View view2131558479;

  private View view2131558481;

  private View view2131558485;

  private View view2131558487;

  private View view2131558488;

  private View view2131558469;

  private View view2131558477;

  @UiThread
  public C01S001_001Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.mEt01 = Utils.findRequiredViewAsType(source, R.id.et_01, "field 'mEt01'", EditText.class);
    view = Utils.findRequiredView(source, R.id.tvXiaLa, "field 'mTvXiaLa' and method 'onViewClicked'");
    target.mTvXiaLa = Utils.castView(view, R.id.tvXiaLa, "field 'mTvXiaLa'", EditText.class);
    view2131558484 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mTvXiaLaPingGuLeiXing = Utils.findRequiredViewAsType(source, R.id.tvXiaLaPingGuLeiXing, "field 'mTvXiaLaPingGuLeiXing'", TextView.class);
    target.mEt03 = Utils.findRequiredViewAsType(source, R.id.et_03, "field 'mEt03'", EditText.class);
    view = Utils.findRequiredView(source, R.id.ll_01, "field 'll01' and method 'onViewClicked'");
    target.ll01 = Utils.castView(view, R.id.ll_01, "field 'll01'", LinearLayout.class);
    view2131558479 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.im_PingGuLeiXing, "field 'im_PingGuLeiXing' and method 'onViewClicked'");
    target.im_PingGuLeiXing = Utils.castView(view, R.id.im_PingGuLeiXing, "field 'im_PingGuLeiXing'", ImageView.class);
    view2131558481 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.im_01, "field 'im01' and method 'onViewClicked'");
    target.im01 = Utils.castView(view, R.id.im_01, "field 'im01'", ImageView.class);
    view2131558485 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mEt02 = Utils.findRequiredViewAsType(source, R.id.et_02, "field 'mEt02'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnScan, "field 'mBtnScan' and method 'onViewClicked'");
    target.mBtnScan = Utils.castView(view, R.id.btnScan, "field 'mBtnScan'", Button.class);
    view2131558487 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnSearch, "field 'mBtnSearch' and method 'onViewClicked'");
    target.mBtnSearch = Utils.castView(view, R.id.btnSearch, "field 'mBtnSearch'", Button.class);
    view2131558488 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mTvNumber = Utils.findRequiredViewAsType(source, R.id.tvNumber, "field 'mTvNumber'", TextView.class);
    target.mTvPosition = Utils.findRequiredViewAsType(source, R.id.tvPosition, "field 'mTvPosition'", TextView.class);
    target.mtvToolId = Utils.findRequiredViewAsType(source, R.id.tvToolId, "field 'mtvToolId'", TextView.class);
    target.mTvNum = Utils.findRequiredViewAsType(source, R.id.tvNum, "field 'mTvNum'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btnCancel, "field 'mBtnCancel' and method 'onViewClicked'");
    target.mBtnCancel = Utils.castView(view, R.id.btnCancel, "field 'mBtnCancel'", Button.class);
    view2131558469 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnNext, "field 'mBtnNext' and method 'onViewClicked'");
    target.mBtnNext = Utils.castView(view, R.id.btnNext, "field 'mBtnNext'", Button.class);
    view2131558477 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mEt01 = null;
    target.mTvXiaLa = null;
    target.mTvXiaLaPingGuLeiXing = null;
    target.mEt03 = null;
    target.ll01 = null;
    target.im_PingGuLeiXing = null;
    target.im01 = null;
    target.mEt02 = null;
    target.mBtnScan = null;
    target.mBtnSearch = null;
    target.mTvNumber = null;
    target.mTvPosition = null;
    target.mtvToolId = null;
    target.mTvNum = null;
    target.mBtnCancel = null;
    target.mBtnNext = null;

    view2131558484.setOnClickListener(null);
    view2131558484 = null;
    view2131558479.setOnClickListener(null);
    view2131558479 = null;
    view2131558481.setOnClickListener(null);
    view2131558481 = null;
    view2131558485.setOnClickListener(null);
    view2131558485 = null;
    view2131558487.setOnClickListener(null);
    view2131558487 = null;
    view2131558488.setOnClickListener(null);
    view2131558488 = null;
    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558477.setOnClickListener(null);
    view2131558477 = null;

    this.target = null;
  }
}
