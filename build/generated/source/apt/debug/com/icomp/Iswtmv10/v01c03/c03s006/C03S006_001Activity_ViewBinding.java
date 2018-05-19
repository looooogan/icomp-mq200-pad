// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c03.c03s006;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C03S006_001Activity_ViewBinding<T extends C03S006_001Activity> implements Unbinder {
  protected T target;

  private View view2131558487;

  private View view2131558488;

  @UiThread
  public C03S006_001Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.et01 = Utils.findRequiredViewAsType(source, R.id.et_01, "field 'et01'", EditText.class);
    target.et02 = Utils.findRequiredViewAsType(source, R.id.et_02, "field 'et02'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnScan, "field 'btnScan' and method 'onViewClicked'");
    target.btnScan = Utils.castView(view, R.id.btnScan, "field 'btnScan'", Button.class);
    view2131558487 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnSearch, "field 'btnSearch' and method 'onViewClicked'");
    target.btnSearch = Utils.castView(view, R.id.btnSearch, "field 'btnSearch'", Button.class);
    view2131558488 = view;
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

    target.et01 = null;
    target.et02 = null;
    target.btnScan = null;
    target.btnSearch = null;

    view2131558487.setOnClickListener(null);
    view2131558487 = null;
    view2131558488.setOnClickListener(null);
    view2131558488 = null;

    this.target = null;
  }
}
