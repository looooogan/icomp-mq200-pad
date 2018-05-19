package com.icomp.Iswtmv10.v01c02.c02s004;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import com.icomp.common.activity.CommonActivity;
import com.umeng.analytics.MobclickAgent;

public class C02S004_001Activity extends CommonActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
//		Intent intent = new Intent("/");
//		ComponentName cm = new ComponentName("com.android.settings",
//				"com.android.settings.DateTimeSettingsSetupWizard");
//		intent.setComponent(cm);
//		intent.setAction("android.intent.action.VIEW");
//		startActivityForResult(intent, 0);
//		finish();
		Intent intent =  new Intent(Settings.ACTION_DATE_SETTINGS);
		startActivity(intent);
		finish();

	}
	/**
	 * 友盟错误统计
	 */
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
