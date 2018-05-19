// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s013;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S013_004Activity_ViewBinding<T extends C01S013_004Activity> implements Unbinder {
  protected T target;

  private View view2131558483;

  private View view2131558486;

  private View view2131558549;

  private View view2131558593;

  private View view2131558479;

  private View view2131558548;

  private View view2131558601;

  private View view2131558600;

  @UiThread
  public C01S013_004Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.et_01, "field 'et_01' and method 'onViewClicked'");
    target.et_01 = Utils.castView(view, R.id.et_01, "field 'et_01'", EditText.class);
    view2131558483 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.et_02, "field 'et_02' and method 'onViewClicked'");
    target.et_02 = Utils.castView(view, R.id.et_02, "field 'et_02'", EditText.class);
    view2131558486 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.tv_01 = Utils.findRequiredViewAsType(source, R.id.tv_01, "field 'tv_01'", TextView.class);
    target.tv_02 = Utils.findRequiredViewAsType(source, R.id.tv_02, "field 'tv_02'", TextView.class);
    view = Utils.findRequiredView(source, R.id.tv_03, "field 'tv_03' and method 'onViewClicked'");
    target.tv_03 = Utils.castView(view, R.id.tv_03, "field 'tv_03'", TextView.class);
    view2131558549 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_04, "field 'tv_04' and method 'onViewClicked'");
    target.tv_04 = Utils.castView(view, R.id.tv_04, "field 'tv_04'", TextView.class);
    view2131558593 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.tv_05 = Utils.findRequiredViewAsType(source, R.id.tv_05, "field 'tv_05'", TextView.class);
    target.tv_06 = Utils.findRequiredViewAsType(source, R.id.tv_06, "field 'tv_06'", TextView.class);
    view = Utils.findRequiredView(source, R.id.ll_01, "field 'll_01' and method 'onViewClicked'");
    target.ll_01 = Utils.castView(view, R.id.ll_01, "field 'll_01'", LinearLayout.class);
    view2131558479 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.ll_02, "field 'll_02' and method 'onViewClicked'");
    target.ll_02 = Utils.castView(view, R.id.ll_02, "field 'll_02'", LinearLayout.class);
    view2131558548 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.ll_03, "field 'll_03' and method 'onViewClicked'");
    target.ll_03 = Utils.castView(view, R.id.ll_03, "field 'll_03'", LinearLayout.class);
    view2131558601 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.ll_04, "field 'll_04' and method 'onViewClicked'");
    target.ll_04 = Utils.castView(view, R.id.ll_04, "field 'll_04'", LinearLayout.class);
    view2131558600 = view;
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

    target.et_01 = null;
    target.et_02 = null;
    target.tv_01 = null;
    target.tv_02 = null;
    target.tv_03 = null;
    target.tv_04 = null;
    target.tv_05 = null;
    target.tv_06 = null;
    target.ll_01 = null;
    target.ll_02 = null;
    target.ll_03 = null;
    target.ll_04 = null;

    view2131558483.setOnClickListener(null);
    view2131558483 = null;
    view2131558486.setOnClickListener(null);
    view2131558486 = null;
    view2131558549.setOnClickListener(null);
    view2131558549 = null;
    view2131558593.setOnClickListener(null);
    view2131558593 = null;
    view2131558479.setOnClickListener(null);
    view2131558479 = null;
    view2131558548.setOnClickListener(null);
    view2131558548 = null;
    view2131558601.setOnClickListener(null);
    view2131558601 = null;
    view2131558600.setOnClickListener(null);
    view2131558600 = null;

    this.target = null;
  }
}
