// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s005;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class c01s005_004_1Activity_ViewBinding<T extends c01s005_004_1Activity> implements Unbinder {
  protected T target;

  private View view2131558508;

  private View view2131558469;

  private View view2131558535;

  @UiThread
  public c01s005_004_1Activity_ViewBinding(final T target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.tvSlip, "field 'mTvSlip' and method 'onViewClicked'");
    target.mTvSlip = Utils.castView(view, R.id.tvSlip, "field 'mTvSlip'", TextView.class);
    view2131558508 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    target.mEtTextArea = Utils.findRequiredViewAsType(source, R.id.etTextArea, "field 'mEtTextArea'", EditText.class);
    view = Utils.findRequiredView(source, R.id.btnCancel, "field 'mBtnCancel' and method 'onViewClicked'");
    target.mBtnCancel = Utils.castView(view, R.id.btnCancel, "field 'mBtnCancel'", Button.class);
    view2131558469 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btnSubmit, "field 'mBtnSubmit' and method 'onViewClicked'");
    target.mBtnSubmit = Utils.castView(view, R.id.btnSubmit, "field 'mBtnSubmit'", Button.class);
    view2131558535 = view;
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

    target.mTvSlip = null;
    target.mEtTextArea = null;
    target.mBtnCancel = null;
    target.mBtnSubmit = null;

    view2131558508.setOnClickListener(null);
    view2131558508 = null;
    view2131558469.setOnClickListener(null);
    view2131558469 = null;
    view2131558535.setOnClickListener(null);
    view2131558535 = null;

    this.target = null;
  }
}
