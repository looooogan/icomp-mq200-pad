// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s023;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S023_002Activity_ViewBinding<T extends C01S023_002Activity> implements Unbinder {
  protected T target;

  private View view2131558479;

  private View view2131558487;

  @UiThread
  public C01S023_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.tv01 = Utils.findRequiredViewAsType(source, R.id.tv_01, "field 'tv01'", TextView.class);
    view = Utils.findRequiredView(source, R.id.ll_01, "field 'll01' and method 'onViewClicked'");
    target.ll01 = Utils.castView(view, R.id.ll_01, "field 'll01'", LinearLayout.class);
    view2131558479 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.tv02 = Utils.findRequiredViewAsType(source, R.id.tv_02, "field 'tv02'", TextView.class);
    target.et01 = Utils.findRequiredViewAsType(source, R.id.et_01, "field 'et01'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnScan, "field 'btnScan' and method 'onViewClicked'");
    target.btnScan = Utils.castView(view, R.id.btnScan, "field 'btnScan'", Button.class);
    view2131558487 = view;
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

    target.tv01 = null;
    target.ll01 = null;
    target.tv02 = null;
    target.et01 = null;
    target.btnScan = null;

    view2131558479.setOnClickListener(null);
    view2131558479 = null;
    view2131558487.setOnClickListener(null);
    view2131558487 = null;

    this.target = null;
  }
}
