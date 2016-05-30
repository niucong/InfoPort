package com.niucong.infoport;

import java.util.List;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.niucong.infoport.service.NotificationService;
import com.umeng.analytics.MobclickAgent;

public class UMengActivity extends FragmentActivity {

	protected Intent service;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		MobclickAgent.onError(this);

		service = new Intent(UMengActivity.this, NotificationService.class);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 通过Service的类名来判断是否启动某个服务
	 * 
	 * @return
	 */
	protected boolean notificationServiceIsStart() {
		ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager
				.getRunningServices(30);
		for (int i = 0; i < mServiceList.size(); i++) {
			if (NotificationService.class.getName().equals(
					mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

}