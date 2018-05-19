// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s004;

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

public class c01s004_002Activity_ViewBinding<T extends c01s004_002Activity> implements Unbinder {
  protected T target;

  private View view2131558469;

  private View view2131558477;

  @UiThread
  public c01s004_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.mLlContainer = Utils.findRequiredViewAsType(source, R.id.llContainer, "field 'mLlContainer'", LinearLayout.class);
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
    target.mTvShenqingRen = Utils.findRequiredViewAsType(source, R.id.tvShenqingRen, "field 'mTvShenqingRen'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mLlContainer = null;
    target.mBtnCancel = null;
    target.mBtnNext = null;
    target.mTvShenqingRen = null;

    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558477.setOnClickListener(null);
    view2131558477 = null;

    this.target = null;
  }
}
