// Generated code from Butter Knife. Do not modify!
package com.icomp.Iswtmv10.v01c01.c01s003;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.icomp.Iswtmv10.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class C01S003_001Activity_ViewBinding<T extends C01S003_001Activity> implements Unbinder {
  protected T target;

  @UiThread
  public C01S003_001Activity_ViewBinding(T target, View source) {
    this.target = target;

    target.mIvAdd = Utils.findRequiredViewAsType(source, R.id.ivAdd, "field 'mIvAdd'", ImageView.class);
    target.mLlContainer = Utils.findRequiredViewAsType(source, R.id.llContainer, "field 'mLlContainer'", LinearLayout.class);
    target.mBtnSearch = Utils.findRequiredViewAsType(source, R.id.btnSearch, "field 'mBtnSearch'", Button.class);
    target.mBtnScan = Utils.findRequiredViewAsType(source, R.id.btnScan, "field 'mBtnScan'", Button.class);
    target.mBtnCancel = Utils.findRequiredViewAsType(source, R.id.btnCancel, "field 'mBtnCancel'", Button.class);
    target.mBtnNext = Utils.findRequiredViewAsType(source, R.id.btnNext, "field 'mBtnNext'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.mIvAdd = null;
    target.mLlContainer = null;
    target.mBtnSearch = null;
    target.mBtnScan = null;
    target.mBtnCancel = null;
    target.mBtnNext = null;

    this.target = null;
  }
}
