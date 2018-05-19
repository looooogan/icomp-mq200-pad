// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s003;

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

public class C01S003_002Activity_ViewBinding<T extends C01S003_002Activity> implements Unbinder {
  protected T target;

  private View view2131558469;

  private View view2131558515;

  private View view2131558479;

  @UiThread
  public C01S003_002Activity_ViewBinding(final T target, View source) {
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
    view = Utils.findRequiredView(source, R.id.btnSign, "field 'mBtnSign' and method 'onViewClicked'");
    target.mBtnSign = Utils.castView(view, R.id.btnSign, "field 'mBtnSign'", Button.class);
    view2131558515 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.ll_01, "field 'll01' and method 'onViewClicked'");
    target.ll01 = Utils.castView(view, R.id.ll_01, "field 'll01'", LinearLayout.class);
    view2131558479 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.tv01 = Utils.findRequiredViewAsType(source, R.id.tv_01, "field 'tv01'", TextView.class);
    target.tv02 = Utils.findRequiredViewAsType(source, R.id.tv_02, "field 'tv02'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mLlContainer = null;
    target.mBtnCancel = null;
    target.mBtnSign = null;
    target.ll01 = null;
    target.tv01 = null;
    target.tv02 = null;

    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558515.setOnClickListener(null);
    view2131558515 = null;
    view2131558479.setOnClickListener(null);
    view2131558479 = null;

    this.target = null;
  }
}
