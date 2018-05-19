package com.icomp.Iswtmv10.v01c02.c02s003;

import android.content.Intent;
import android.os.Bundle;
import com.icomp.common.activity.CommonActivity;
import com.umeng.analytics.MobclickAgent;

public class C02S003_001Activity extends CommonActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent wifiIntent = new Intent(
				android.provider.Settings.ACTION_SETTINGS);
		startActivity(wifiIntent);
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
