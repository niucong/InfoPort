package com.niucong.infoport.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.niucong.infoport.service.NotificationService;
import com.niucong.infoport.util.AppSharedPreferences;

public class BootBroadcastReceiver extends BroadcastReceiver {

	// public static final String MSG_NUMBERS = "MSG_NUMBERS";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (new AppSharedPreferences(context).getBooleanMessage("user", "push",
				true)) {
			Intent service = new Intent(context, NotificationService.class);
			context.startService(service);
		}
	}
}
