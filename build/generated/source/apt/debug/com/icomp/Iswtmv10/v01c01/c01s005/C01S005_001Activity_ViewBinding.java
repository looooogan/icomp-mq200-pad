// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s005;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S005_001Activity_ViewBinding<T extends C01S005_001Activity> implements Unbinder {
  protected T target;

  private View view2131558524;

  private View view2131558525;

  private View view2131558473;

  private View view2131558469;

  private View view2131558470;

  @UiThread
  public C01S005_001Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tvDanPin, "field 'mTvDanPin' and method 'onViewClicked'");
    target.mTvDanPin = Utils.castView(view, R.id.tvDanPin, "field 'mTvDanPin'", TextView.class);
    view2131558524 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tvYiTi, "field 'mTvYiTi' and method 'onViewClicked'");
    target.mTvYiTi = Utils.castView(view, R.id.tvYiTi, "field 'mTvYiTi'", TextView.class);
    view2131558525 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tvTongDao, "field 'mTvTongDao' and method 'onViewClicked'");
    target.mTvTongDao = Utils.castView(view, R.id.tvTongDao, "field 'mTvTongDao'", TextView.class);
    view2131558473 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnCancel, "field 'mBtnCancel' and method 'onViewClicked'");
    target.mBtnCancel = Utils.castView(view, R.id.btnCancel, "field 'mBtnCancel'", Button.class);
    view2131558469 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnReturn, "field 'mBtnReturn' and method 'onViewClicked'");
    target.mBtnReturn = Utils.castView(view, R.id.btnReturn, "field 'mBtnReturn'", Button.class);
    view2131558470 = view;
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

    target.mTvDanPin = null;
    target.mTvYiTi = null;
    target.mTvTongDao = null;
    target.mBtnCancel = null;
    target.mBtnReturn = null;

    view2131558524.setOnClickListener(null);
    view2131558524 = null;
    view2131558525.setOnClickListener(null);
    view2131558525 = null;
    view2131558473.setOnClickListener(null);
    view2131558473 = null;
    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558470.setOnClickListener(null);
    view2131558470 = null;

    this.target = null;
  }
}
