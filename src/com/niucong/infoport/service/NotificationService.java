package com.niucong.infoport.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.niucong.infoport.R;
import com.niucong.infoport.ReadViewsActivity;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.util.AppSharedPreferences;
import com.niucong.infoport.util.L;

import java.util.Date;

public class NotificationService extends Service {
	private static final String TAG = "NotificationService";

	private AppSharedPreferences preferences = new AppSharedPreferences(this);

	// 通知栏
	private NotificationManager notificationManager = null;
	// 通知栏跳转Intent
	private Intent messageIntent = null;
	private PendingIntent messagePendingIntent = null;

	private SendRequestThread sendRequest = null;

	/** 请求消息时间间隔 */
	private long TIME_NIGHT = 5 * 60 * 1000;
	private long TIME_DAY = 10 * 1000;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		L.i(TAG, "onCreate...");
		this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);// 初始化管理器

		sendRequest = new SendRequestThread();
		super.onCreate();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		L.i(TAG, "onStart...");
		// 如果线程不是出于活动状态则开启线程
		if (!sendRequest.isAlive()) {
			sendRequest.start();
		}
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		L.i(TAG, "onDestroy...");
		notificationManager.cancelAll();
		super.onDestroy();
	}

	/**
	 * 开启消息通知的线程
	 */
	class SendRequestThread extends Thread {
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			while (true) {
				try {
					String MaxSno = preferences.getStringMessage("user",
							"MaxSno", "0");
					String usrsno = preferences.getStringMessage("user", "id",
							"");
					L.i(TAG, "SendRequestThread MaxSno=" + MaxSno + ",usrsno="
							+ usrsno);
					// string[0],序号；string[1]：新闻编号；string[2]:标题;string[3]:新闻类型
					String[] strs = WebServicesData
							.getMsgPrompt(MaxSno, usrsno);
					L.d(TAG, "SendRequestThread=" + strs[0].trim() + "="
							+ strs[1].trim() + "=" + strs[2].trim() + "="
							+ strs[3].trim());
					preferences.saveStringMessage("user", "MaxSno",
							strs[1].trim());

					String contentTitle = "信息港";
					String contentText = strs[2].trim();
					messageIntent = new Intent(NotificationService.this,
							ReadViewsActivity.class);
					messageIntent.putExtra("Title", strs[2].trim());
					messageIntent.putExtra("AddTime", strs[3].trim());
					messageIntent.putExtra("Id", strs[1].trim());
					// messageIntent.putExtra("typeId", id);
					messageIntent.putExtra("type", 0);
					messageIntent.putExtra("name", "最新消息");

//					messagePendingIntent = PendingIntent.getActivity(
//							NotificationService.this, 0, messageIntent,
//							PendingIntent.FLAG_UPDATE_CURRENT);
					messagePendingIntent = PendingIntent.getActivity(NotificationService.this, 0, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

					Notification.Builder builder = new Notification.Builder(NotificationService.this).setTicker(contentTitle)
							.setSmallIcon(R.drawable.icon);
					Notification notification = builder.setContentIntent(messagePendingIntent).setContentTitle(contentTitle).setContentText(contentText).build();


					notificationManager.cancel(null, 0);
					notificationManager.notify(0, notification);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					int hour = new Date().getHours();
					if (hour < 2 || hour > 6) {
						sleep(TIME_DAY);
					} else {
						sleep(TIME_NIGHT);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
