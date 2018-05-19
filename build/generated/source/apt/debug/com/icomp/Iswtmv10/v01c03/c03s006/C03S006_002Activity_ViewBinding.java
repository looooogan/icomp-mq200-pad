// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c03.c03s006;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C03S006_002Activity_ViewBinding<T extends C03S006_002Activity> implements Unbinder {
  protected T target;

  private View view2131558487;

  @UiThread
  public C03S006_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.tv01 = Utils.findRequiredViewAsType(source, R.id.tv_01, "field 'tv01'", TextView.class);
    target.lv01 = Utils.findRequiredViewAsType(source, R.id.lv_01, "field 'lv01'", ListView.class);
    view = Utils.findRequiredView(source, R.id.btnScan, "field 'btnScan' and method 'onViewClicked'");
    target.btnScan = Utils.castView(view, R.id.btnScan, "field 'btnScan'", Button.class);
    view2131558487 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.tv01 = null;
    target.lv01 = null;
    target.btnScan = null;

    view2131558487.setOnClickListener(null);
    view2131558487 = null;

    this.target = null;
  }
}
