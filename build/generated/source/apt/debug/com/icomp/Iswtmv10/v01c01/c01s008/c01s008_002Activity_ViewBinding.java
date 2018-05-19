// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s008;

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

public class c01s008_002Activity_ViewBinding<T extends c01s008_002Activity> implements Unbinder {
  protected T target;

  private View view2131558505;

  private View view2131558506;

  @UiThread
  public c01s008_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.mTvTitle = Utils.findRequiredViewAsType(source, R.id.tvTitle, "field 'mTvTitle'", TextView.class);
    target.mTvContent = Utils.findRequiredViewAsType(source, R.id.tvContent, "field 'mTvContent'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btnGoOn, "field 'mBtnGoOn' and method 'onViewClicked'");
    target.mBtnGoOn = Utils.castView(view, R.id.btnGoOn, "field 'mBtnGoOn'", Button.class);
    view2131558505 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnComplete, "field 'mBtnComplete' and method 'onViewClicked'");
    target.mBtnComplete = Utils.castView(view, R.id.btnComplete, "field 'mBtnComplete'", Button.class);
    view2131558506 = view;
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

    target.mTvTitle = null;
    target.mTvContent = null;
    target.mBtnGoOn = null;
    target.mBtnComplete = null;

    view2131558505.setOnClickListener(null);
    view2131558505 = null;
    view2131558506.setOnClickListener(null);
    view2131558506 = null;

    this.target = null;
  }
}
