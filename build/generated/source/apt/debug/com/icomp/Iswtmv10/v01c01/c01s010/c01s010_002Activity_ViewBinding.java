// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s010;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class c01s010_002Activity_ViewBinding<T extends c01s010_002Activity> implements Unbinder {
  protected T target;

  private View view2131558470;

  private View view2131558477;

  @UiThread
  public c01s010_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.mTlContainer = Utils.findRequiredViewAsType(source, R.id.tlContainer, "field 'mTlContainer'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.btnReturn, "field 'mBtnReturn' and method 'onViewClicked'");
    target.mBtnReturn = Utils.castView(view, R.id.btnReturn, "field 'mBtnReturn'", Button.class);
    view2131558470 = view;
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

    target.mTlContainer = null;
    target.mBtnReturn = null;
    target.mBtnNext = null;

    view2131558470.setOnClickListener(null);
    view2131558470 = null;
    view2131558477.setOnClickListener(null);
    view2131558477 = null;

    this.target = null;
  }
}
