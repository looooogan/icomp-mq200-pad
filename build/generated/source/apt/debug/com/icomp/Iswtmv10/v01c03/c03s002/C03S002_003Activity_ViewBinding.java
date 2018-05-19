// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c03.c03s002;

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

public class C03S002_003Activity_ViewBinding<T extends C03S002_003Activity> implements Unbinder {
  protected T target;

  private View view2131558535;

  @UiThread
  public C03S002_003Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.et01 = Utils.findRequiredViewAsType(source, R.id.et_01, "field 'et01'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnSubmit, "field 'btnSubmit' and method 'onViewClicked'");
    target.btnSubmit = Utils.castView(view, R.id.btnSubmit, "field 'btnSubmit'", Button.class);
    view2131558535 = view;
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

    target.et01 = null;
    target.btnSubmit = null;

    view2131558535.setOnClickListener(null);
    view2131558535 = null;

    this.target = null;
  }
}
