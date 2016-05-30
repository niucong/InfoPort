package com.niucong.infoport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.niucong.infoport.dialog.SpinnerProgressDialog;
import com.niucong.infoport.net.DownLoadFileThread;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.util.AppSharedPreferences;
import com.niucong.infoport.util.SoftUtil;

public class WelcomeActivity extends UMengActivity {

	private AppSharedPreferences preferences;

	private long lon0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		preferences = new AppSharedPreferences(this);

		lon0 = System.currentTimeMillis();
		new Thread() {
			@Override
			public void run() {
				try {
					String[] strs = WebServicesData.getVersionInfo();
					if (strs != null) {
						int versionCode = Integer.valueOf(strs[0]);
						if (versionCode > new SoftUtil()
								.getVersionCode(WelcomeActivity.this)) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = strs;
							handler.sendMessage(msg);
							return;
						}
					}
					preferences.saveStringMessage("user", "HttpConnSoapUrl",
							strs[3]);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				String HttpConnSoapUrl = preferences.getStringMessage("user",
						"HttpConnSoapUrl", "");
				if (!"".equals(HttpConnSoapUrl)) {
					WebServicesData.URL = HttpConnSoapUrl;
				}

				long lon1 = System.currentTimeMillis();
				int lon = (int) (lon1 - lon0);
				if (lon < (3 * 1000)) {
					try {
						sleep(3 * 1000 - lon);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				enterInfo();
			}
		}.start();
	}

	private void enterInfo() {
		startActivity(new Intent(WelcomeActivity.this, InfoPortActivity.class));
		WelcomeActivity.this.finish();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				final String[] strs = (String[]) msg.obj;
				new AlertDialog.Builder(WelcomeActivity.this)
						.setTitle("检测到新版本")
						.setCancelable(false)
						.setPositiveButton("马上升级",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										new SpinnerProgressDialog(
												WelcomeActivity.this)
												.showProgressDialog("正在更新版本...");
										new DownLoadFileThread(
												WelcomeActivity.this, strs[2],
												0, strs[1]).start();
									}
								})
						.setNegativeButton("稍后再说",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (!"".equals(strs[3])) {
											WebServicesData.URL = strs[3];
											preferences.saveStringMessage(
													"user", "HttpConnSoapUrl",
													strs[3]);
										}
										preferences.saveStringMessage("user",
												"uploadname", strs[1]);
										preferences.saveStringMessage("user",
												"uploadurl", strs[2]);
										enterInfo();
									}
								}).show();

				break;

			default:
				break;
			}
		};
	};
}
