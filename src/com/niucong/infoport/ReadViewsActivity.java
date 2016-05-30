package com.niucong.infoport;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.niucong.infoport.dialog.SpinnerProgressDialog;
import com.niucong.infoport.net.DownLoadFileThread;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.util.AppSharedPreferences;
import com.niucong.infoport.util.L;

@SuppressLint("SetJavaScriptEnabled")
public class ReadViewsActivity extends UMengActivity implements OnClickListener {
	protected static final String TAG = "ReadViewsActivity";

	private ImageView title_left, title_right, iv_main, iv_app;
	private LinearLayout info_buttom_main, info_buttom_app,
			info_buttom_setting, info_buttom_about, info_buttom_my;
	private WebView context_wv;
	private TextView title_tv, data_tv;
	// tv0, tv1, tv2, tv3, tv4, tv5,
	private TextView title2_tv;
	private Button button_back;

	private String id, name;
	private int type;
	private String typeId;
	private String str = "";
	private boolean isMain = true;
	private boolean isVideo = false;

	private SpinnerProgressDialog progressDialog;

	private BootReceiver receiver;
	private IntentFilter intentFilter;
	private boolean isShow = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_view);

		progressDialog = new SpinnerProgressDialog(this);

		getView();
		setView();
		setData();
	}

	private void getView() {
		title_left = (ImageView) findViewById(R.id.title_left);
		title_right = (ImageView) findViewById(R.id.title_right);

		info_buttom_main = (LinearLayout) findViewById(R.id.info_buttom_main);
		info_buttom_app = (LinearLayout) findViewById(R.id.info_buttom_app);
		info_buttom_setting = (LinearLayout) findViewById(R.id.info_buttom_setting);
		info_buttom_about = (LinearLayout) findViewById(R.id.info_buttom_about);
		info_buttom_my = (LinearLayout) findViewById(R.id.info_buttom_my);

		iv_main = (ImageView) findViewById(R.id.main_imageView);
		iv_app = (ImageView) findViewById(R.id.app_imageView);

		title2_tv = (TextView) findViewById(R.id.app_title);
		button_back = (Button) findViewById(R.id.button_back);

		// tv0 = (TextView) findViewById(R.id.info_textView0);
		// tv1 = (TextView) findViewById(R.id.info_textView1);
		// tv2 = (TextView) findViewById(R.id.info_textView2);
		// tv3 = (TextView) findViewById(R.id.info_textView3);
		// tv4 = (TextView) findViewById(R.id.info_textView4);
		// tv5 = (TextView) findViewById(R.id.info_textView5);

		title_tv = (TextView) findViewById(R.id.read_title);
		data_tv = (TextView) findViewById(R.id.read_data);
		context_wv = (WebView) findViewById(R.id.read_context);
	}

	private void setView() {
		title_left.setVisibility(View.GONE);
		title2_tv.setText(name);
		button_back.setOnClickListener(this);
		title_right.setOnClickListener(this);

		AppSharedPreferences preferences = new AppSharedPreferences(this);
		if (!"".equals(preferences.getStringMessage("user", "account", ""))
				&& !"".equals(preferences.getStringMessage("user", "password",
						""))) {
			title_right.setVisibility(View.GONE);
		}

		if (!isMain) {
			info_buttom_main.setBackgroundResource(R.drawable.more_item);
			info_buttom_app.setBackgroundResource(R.drawable.more_item_b);
			iv_main.setImageResource(R.drawable.main);
			iv_app.setImageResource(R.drawable.app_b);
		}

		info_buttom_main.setOnClickListener(this);
		info_buttom_app.setOnClickListener(this);
		info_buttom_setting.setOnClickListener(this);
		info_buttom_about.setOnClickListener(this);
		info_buttom_my.setOnClickListener(this);

		context_wv.getSettings().setJavaScriptEnabled(true);

		// tv0.setOnClickListener(this);
		// tv1.setOnClickListener(this);
		// tv2.setOnClickListener(this);
		// tv3.setOnClickListener(this);
		// tv4.setOnClickListener(this);
		// tv5.setOnClickListener(this);
	}

	private void setData() {
		Intent i = getIntent();
		String title = i.getStringExtra("Title");
		String time = i.getStringExtra("AddTime");
		L.i(TAG, "setData title=" + title + ",time=" + time);
		title_tv.setText(title.trim());
		data_tv.setText(time.trim());
		id = i.getStringExtra("Id");
		type = i.getIntExtra("type", 0);
		typeId = i.getStringExtra("typeId");
		name = i.getStringExtra("name");
		isMain = i.getBooleanExtra("isMain", true);
		isVideo = i.getBooleanExtra("isVideo", false);

		if (type == 1) {
			title_tv.setVisibility(View.GONE);
			data_tv.setVisibility(View.GONE);
		}

		progressDialog.showProgressDialog("正在获取中...");
		new Thread() {
			public void run() {
				try {
					if (type == 0) {
						String[] strs = WebServicesData.readNews(id);
						if (strs != null) {
							Message msg = new Message();
							msg.what = 1;
							msg.obj = strs[4];
							readHandler.sendMessage(msg);
						} else {
							readHandler.sendEmptyMessage(0);
						}
					} else if (type == 1) {
						String[] strs = null;
						if ("03080500".equals(typeId)) {
							strs = WebServicesData.stockInfo(id);
						} else if ("03080600".equals(typeId)) {
							strs = WebServicesData.stockComInfo(id);
						}
						if (strs != null) {
							L.d(TAG, "setData strs=" + strs.length);
							Message msg = new Message();
							msg.what = 1;
							msg.obj = strs;
							readHandler.sendMessage(msg);
						} else {
							readHandler.sendEmptyMessage(0);
						}
					}
				} catch (IOException e) {
					readHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					readHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					readHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		isShow = true;
		if (isVideo && str != null && !"".equals(str)) {
			addVedio();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		context_wv.pauseTimers();
		// if (isFinishing()) {
		// context_wv.loadUrl("about:blank");
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		context_wv.resumeTimers();
	}

	@Override
	protected void onStop() {
		super.onStop();
		isShow = false;
	}

	private void callHiddenWebViewMethod(String name) {
		if (context_wv != null) {
			try {
				Method method = WebView.class.getMethod(name);
				method.invoke(context_wv);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler readHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(ReadViewsActivity.this, "读取失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				if (type == 0) {
					str = (String) msg.obj;
				} else if (type == 1) {
					String[] strs = (String[]) msg.obj;
					String[] strs0 = null;
					if ("03080500".equals(typeId)) {
						// string[0],序号；string[1]：股票代码；string[2]:
						// 股票名称;string[3]:投资风格;string[4],
						// 止损参考；string[5]：入池时间；string[6:
						// 入池价格;string[7]:出池时间;string[8],
						// 出池价格；string[9]：仓位提示；string[10]:涨跌幅度;string[11]:投资亮点;string[12]:
						// 调出理由
						strs0 = new String[] { "序号：%s<br><br>",
								"股票代码：%s<br><br>", "股票名称：%s<br><br>",
								"投资风格：%s<br><br>", "止损参考：%s<br><br>",
								"入池时间：%s<br><br>", "入池价格：%s<br><br>",
								"出池时间：%s<br><br>", "出池价格：%s<br><br>",
								"仓位提示：%s<br><br>", "涨跌幅度：%s<br><br>",
								"投资亮点：%s<br><br>", "调出理由：%s<br><br>" };
					} else if ("03080600".equals(typeId)) {
						// string[0],序号；string[1]：股票代码；string[2]:
						// 股票名称;string[3]: 投资风格
						// string[4], 最新评级；string[5]：入池时间；string[6:
						// 入池价格;string[7]: 出池时间
						// string[8], 出池价格；string[9]：入池逻辑；string[10]:
						// 操作建议;string[11]: 调出理由
						// string[12]: 风险提示
						strs0 = new String[] { "序号：%s<br><br>",
								"股票代码：%s<br><br>", "股票名称：%s<br><br>",
								"投资风格：%s<br><br>", "最新评级：%s<br><br>",
								"入池时间：%s<br><br>", "入池价格：%s<br><br>",
								"出池时间：%s<br><br>", "出池价格：%s<br><br>",
								"入池逻辑：%s<br><br>", "操作建议：%s<br><br>",
								"调出理由：%s<br><br>", "风险提示：%s<br><br>" };
					}
					for (int i = 1; i < strs.length; i++) {
						try {
							str += String.format(strs0[i], strs[i]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (strs.length < strs0.length) {
						for (int i = 0; i < strs0.length - strs.length; i++) {
							try {
								str += String
										.format(strs0[strs.length + i], "");
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				L.getLongLog(TAG, "readHandler", str);
				if (isVideo) {
					addVedio();
				} else {
					context_wv.getSettings().setSupportZoom(true);
					context_wv.getSettings().setBuiltInZoomControls(true);
					context_wv.getSettings().setDefaultFontSize(15);
					context_wv.setWebViewClient(getWebViewClient());

					str = str.replaceAll("&lt;", "<");
					str = str.replaceAll("&gt;", ">");
					str = str.replaceAll("&amp;nbsp;", " ");
					context_wv.loadDataWithBaseURL(null, str, "text/html",
							"utf-8", null);
				}
				break;
			default:
				break;
			}
		};
	};

	@SuppressWarnings("deprecation")
	private void addVedio() {
		if (check()) {
			// context_wv.loadUrl(str);

			// context_wv.getSettings().setPluginsEnabled(true);
			context_wv.getSettings().setPluginState(PluginState.ON);
			String htmlPre = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\"></head><body style='margin:0; pading:0; background-color: black;'>";
			String htmlCode = "<embed style='width:100%; height:100%' src='@VIDEO@' "
					+ "autoplay='true' "
					+ "quality='high' bgcolor='#000000' "
					+ "name='VideoPlayer' align='middle'"
					+ // width='640' height='480'
					"allowScriptAccess='*' allowFullScreen='true'"
					+ "type='application/x-shockwave-flash' "
					+ "pluginspage='http://www.macromedia.com/go/getflashplayer' />"
					+ "";
			String htmlPost = "</body></html>";
			htmlCode = htmlCode.replaceAll("@VIDEO@", str);
			context_wv.loadDataWithBaseURL("fake://fake/fake", htmlPre
					+ htmlCode + htmlPost, "text/html", "UTF-8", null);
		} else {
			new AlertDialog.Builder(ReadViewsActivity.this)
					.setTitle("提示")
					.setMessage("播放此视频需要Adobe Flash Player插件，确定下载此插件吗？")
					.setPositiveButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new DownLoadFileThread(
											ReadViewsActivity.this,
											"http://andown.cf69.com/adobe_flash_player.apk",
											0, "adobe_flash_player.apk")
											.start();
									receiver = new BootReceiver();
									intentFilter = new IntentFilter();
									intentFilter
											.addAction("android.intent.action.PACKAGE_ADDED");
									registerReceiver(receiver, intentFilter);
								}
							}).setNegativeButton("否", null).show();
		}
	}

	private boolean check() {
		PackageManager pm = getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {
			if ("com.adobe.flashplayer".equals(info.packageName)) {
				return true;
			}
		}
		return false;
	}

	// private void install() {
	// Intent installIntent = new Intent("android.intent.action.VIEW");
	// installIntent.setData(Uri
	// .parse("market://details?id=com.adobe.flashplayer"));
	// startActivity(installIntent);
	// }
	class BootReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收安装广播
			if (intent.getAction()
					.equals("android.intent.action.PACKAGE_ADDED")) {
				String packageName = intent.getDataString();
				L.i(TAG, "BootReceiver 安装了:" + packageName + "包名的程序");
				if ("com.adobe.flashplayer".contains(packageName)) {
					if (isShow) {
						addVedio();
					}
					unregisterReceiver(receiver);
				}
			}
			// 接收卸载广播
			// if (intent.getAction().equals(
			// "android.intent.action.PACKAGE_REMOVED")) {
			// String packageName = intent.getDataString();
			// System.out.println("卸载了:" + packageName + "包名的程序");
			// }
		}
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	public WebViewClient getWebViewClient() {
		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// if (isVideo) {
				// view.loadUrl(url);
				// } else {
				showUrlRedirect(view.getContext(), url);
				// }
				return true;
			}
		};
	}

	/**
	 * url跳转
	 * 
	 * @param context
	 * @param url
	 */
	public static void showUrlRedirect(Context context, String url) {
		openBrowser(context, url);
	}

	/**
	 * 打开浏览器
	 * 
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			// ToastMessage(context, "无法浏览此网页", 500);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 0, 1, "退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("退出后，你将收不到新的消息。确定要退出？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (notificationServiceIsStart())
							stopService(service);
						if (InfoPortActivity.infoPortActivity != null)
							InfoPortActivity.infoPortActivity.finish();
						if (AppActivity.appActivity != null)
							AppActivity.appActivity.finish();
						ReadViewsActivity.this.finish();
					}
				}).setNegativeButton("否", null).show();
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.title_right:
			InfoPortActivity.BUTTOM = 0;
			closeActivity();
			break;
		case R.id.info_buttom_main:
			InfoPortActivity.BUTTOM = 1;
			closeActivity();
			break;
		case R.id.info_buttom_setting:
			InfoPortActivity.BUTTOM = 2;
			closeActivity();
			break;
		case R.id.info_buttom_about:
			InfoPortActivity.BUTTOM = 3;
			closeActivity();
			break;
		case R.id.info_buttom_my:
			InfoPortActivity.BUTTOM = 4;
			closeActivity();
			break;
		case R.id.info_buttom_app:
			InfoPortActivity.BUTTOM = 5;
			closeActivity();
			break;
		// case R.id.info_textView0:// 今天头条
		// InfoPortActivity.TOP = 0;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		// case R.id.info_textView1:// 宏观信息
		// InfoPortActivity.TOP = 1;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		// case R.id.info_textView2:// 国诚策略
		// InfoPortActivity.TOP = 2;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		// case R.id.info_textView3:// 独家视点
		// InfoPortActivity.TOP = 3;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		// case R.id.info_textView4:// 行业信息
		// InfoPortActivity.TOP = 4;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		// case R.id.info_textView5:// 个股信息
		// InfoPortActivity.TOP = 5;
		// setResult(RESULT_OK, new Intent());
		// finish();
		// break;
		default:
			break;
		}
	}

	private void closeActivity() {
		if (InfoPortActivity.infoPortActivity != null) {
			setResult(RESULT_OK);
			finish();
		} else {
			startActivity(new Intent(this, InfoPortActivity.class));
		}
	}

}
