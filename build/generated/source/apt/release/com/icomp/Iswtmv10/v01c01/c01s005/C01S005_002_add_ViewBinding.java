// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s005;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S005_002_add_ViewBinding<T extends C01S005_002_add> implements Unbinder {
  protected T target;

  private View view2131558464;

  private View view2131558465;

  private View view2131558466;

  private View view2131558469;

  private View view2131558470;

  private View view2131558467;

  private View view2131558468;

  @UiThread
  public C01S005_002_add_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tvBeiYong, "field 'mTvBeiYong' and method 'onViewClicked'");
    target.mTvBeiYong = Utils.castView(view, R.id.tvBeiYong, "field 'mTvBeiYong'", TextView.class);
    view2131558464 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tvDaiRenMo, "field 'mTvDaiRenMo' and method 'onViewClicked'");
    target.mTvDaiRenMo = Utils.castView(view, R.id.tvDaiRenMo, "field 'mTvDaiRenMo'", TextView.class);
    view2131558465 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tvDaiChuChang, "field 'mTvDaiChuChang' and method 'onViewClicked'");
    target.mTvDaiChuChang = Utils.castView(view, R.id.tvDaiChuChang, "field 'mTvDaiChuChang'", TextView.class);
    view2131558466 = view;
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
    view = Utils.findRequiredView(source, R.id.tvChangwai, "field 'mTvChangwai' and method 'onViewClicked'");
    target.mTvChangwai = Utils.castView(view, R.id.tvChangwai, "field 'mTvChangwai'", TextView.class);
    view2131558467 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tvShengchanxian, "field 'mTvShengchanxian' and method 'onViewClicked'");
    target.mTvShengchanxian = Utils.castView(view, R.id.tvShengchanxian, "field 'mTvShengchanxian'", TextView.class);
    view2131558468 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mActivityC01S005002Add = Utils.findRequiredViewAsType(source, R.id.activity_c01_s005_002_add, "field 'mActivityC01S005002Add'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mTvBeiYong = null;
    target.mTvDaiRenMo = null;
    target.mTvDaiChuChang = null;
    target.mBtnCancel = null;
    target.mBtnReturn = null;
    target.mTvChangwai = null;
    target.mTvShengchanxian = null;
    target.mActivityC01S005002Add = null;

    view2131558464.setOnClickListener(null);
    view2131558464 = null;
    view2131558465.setOnClickListener(null);
    view2131558465 = null;
    view2131558466.setOnClickListener(null);
    view2131558466 = null;
    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558470.setOnClickListener(null);
    view2131558470 = null;
    view2131558467.setOnClickListener(null);
    view2131558467 = null;
    view2131558468.setOnClickListener(null);
    view2131558468 = null;

    this.target = null;
  }
}
