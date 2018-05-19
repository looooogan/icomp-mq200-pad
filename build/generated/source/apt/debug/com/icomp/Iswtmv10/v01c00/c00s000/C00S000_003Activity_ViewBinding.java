// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c00.c00s000;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C00S000_003Activity_ViewBinding<T extends C00S000_003Activity> implements Unbinder {
  protected T target;

  private View view2131558569;

  @UiThread
  public C00S000_003Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    target.mScrollLayout = Utils.findRequiredViewAsType(source, R.id.ScrollLayout, "field 'mScrollLayout'", ViewPager.class);
    view = Utils.findRequiredView(source, R.id.btn_return, "field 'btnReturn' and method 'onClick'");
    target.btnReturn = Utils.castView(view, R.id.btn_return, "field 'btnReturn'", Button.class);
    view2131558569 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick();
      }
    });
    target.tv01 = Utils.findRequiredViewAsType(source, R.id.tv_01, "field 'tv01'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mScrollLayout = null;
    target.btnReturn = null;
    target.tv01 = null;

    view2131558569.setOnClickListener(null);
    view2131558569 = null;

    this.target = null;
  }
}
