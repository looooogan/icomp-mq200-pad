// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s018;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S018_002Activity_ViewBinding<T extends C01S018_002Activity> implements Unbinder {
  protected T target;

  private View view2131558547;

  @UiThread
  public C01S018_002Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.iv_01, "field 'iv01' and method 'onViewClicked'");
    target.iv01 = Utils.castView(view, R.id.iv_01, "field 'iv01'", ImageView.class);
    view2131558547 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
    target.ll01 = Utils.findRequiredViewAsType(source, R.id.ll_01, "field 'll01'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.iv01 = null;
    target.ll01 = null;

    view2131558547.setOnClickListener(null);
    view2131558547 = null;

    this.target = null;
  }
}
