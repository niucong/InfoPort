package com.niucong.infoport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.tsz.afinal.FinalBitmap;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.niucong.infoport.adapter.ViewNewsAdapter;
import com.niucong.infoport.bean.ViewNewBean;
import com.niucong.infoport.dialog.SpinnerProgressDialog;
import com.niucong.infoport.net.DownLoadFileThread;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.service.NotificationService;
import com.niucong.infoport.util.AppSharedPreferences;
import com.niucong.infoport.util.CharUtil;
import com.niucong.infoport.util.L;
import com.niucong.infoport.util.SoftUtil;
import com.niucong.infoport.view.RefreshListView;
import com.niucong.infoport.view.RefreshListView.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("HandlerLeak")
public class InfoPortActivity extends UMengActivity implements OnClickListener,
		OnItemClickListener {
	private static final String TAG = "InfoPortActivity";

	private ImageView title_left, title_right, iv_main, iv_app, iv_setting,
			iv_about, iv_my, iv_tianyanban, iv_xianfengban, iv_qibaoqi,
			iv_zhuanyeban, iv_shizhanban, iv_shizhanci, iv_zuheban, iv_zuheci,
			ad_imageView;
	private TextView tv0, tv1, tv2, tv3, tv4, tv5, my_name, service_type;
	private Button startRegist, start_pay, regist_btn, login_btn, btn_exit,
			setting_version;
	private LinearLayout login, login_layout, regist_layout, info_buttom_main,
			info_buttom_app, info_buttom_setting, info_buttom_about,
			info_buttom_my, app, my, setting, about, passwod_layout, ad_layout;
	private HorizontalScrollView scrollView;
	private EditText account_et, password_et;
	private RefreshListView refreshListView;
	private CheckBox setting_push;

	private ViewNewsAdapter newsAdapter0, newsAdapter1, newsAdapter2,
			newsAdapter3, newsAdapter4, newsAdapter5;
	private ArrayList<ViewNewBean> newBeans0, newBeans1, newBeans2, newBeans3,
			newBeans4, newBeans5;

	private String account_str, password_str;
	/**
	 * 0：登录、1：首页、2：我的设置、3：关于信息港、4：我、5：应用
	 */
	public static int BUTTOM = 1;
	/**
	 * 0：今日头条、1：宏观信息、2：国诚策略、3：独家视点、4：行业信息、5：个股信息
	 */
	public static int TOP = 0;

	private String NewsTypeId;
	private int cut = 0;

	private AppSharedPreferences preferences;
	private SpinnerProgressDialog progressDialog;

	public final static int REQUEST_CODE = 0;

	public static InfoPortActivity infoPortActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_port);
		infoPortActivity = this;

		preferences = new AppSharedPreferences(this);
		progressDialog = new SpinnerProgressDialog(this);

		getView();
		setView();

		newBeans0 = new ArrayList<ViewNewBean>();
		newsAdapter0 = new ViewNewsAdapter(this, newBeans0);
		refreshListView.setAdapter(newsAdapter0);
		NewsTypeId = "001106";

		account_str = preferences.getStringMessage("user", "account", "");
		password_str = preferences.getStringMessage("user", "password", "");
		if (!"".equals(account_str) && !"".equals(password_str)) {
			title_right.setVisibility(View.GONE);
		}

		if (preferences.getBooleanMessage("user", "push", true)) {
			service = new Intent(this, NotificationService.class);
			L.d(TAG, "onCreate " + notificationServiceIsStart());
			if (!notificationServiceIsStart())
				startService(service);
			L.d(TAG, "onCreate " + notificationServiceIsStart());
		}

		if (BUTTOM == 0) {
			login();
		} else if (BUTTOM == 2) {
			setting();
		} else if (BUTTOM == 3) {
			about();
		} else if (BUTTOM == 4) {
			String ids = preferences.getStringMessage("user", "id", "");
			if ("".equals(ids)) {
				Toast.makeText(InfoPortActivity.this, "请先登录！",
						Toast.LENGTH_LONG).show();
				login();
			} else {
				String type = preferences.getStringMessage("user", "userInfo",
						"");
				if ("".equals(type)) {
					getUserInfo(ids);
				} else {
					my();
				}
			}
		} else if (BUTTOM == 5) {
			app();
		} else {
			getData(true);
			getAdInfo();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BUTTOM = 1;
		infoPortActivity = null;
	}

	private void getView() {
		ad_layout = (LinearLayout) ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.ad, null);
		ad_imageView = (ImageView) ad_layout.findViewById(R.id.ad_imageView);

		title_right = (ImageView) findViewById(R.id.title_right);
		title_left = (ImageView) findViewById(R.id.title_left);

		scrollView = (HorizontalScrollView) findViewById(R.id.horizontalScrollView);
		tv0 = (TextView) findViewById(R.id.info_textView0);
		tv1 = (TextView) findViewById(R.id.info_textView1);
		tv2 = (TextView) findViewById(R.id.info_textView2);
		tv3 = (TextView) findViewById(R.id.info_textView3);
		tv4 = (TextView) findViewById(R.id.info_textView4);
		tv5 = (TextView) findViewById(R.id.info_textView5);

		login = (LinearLayout) findViewById(R.id.login);
		startRegist = (Button) findViewById(R.id.start_regist);
		login_layout = (LinearLayout) findViewById(R.id.login_layout);
		regist_layout = (LinearLayout) findViewById(R.id.regist_layout);
		regist_btn = (Button) findViewById(R.id.regist_button);
		login_btn = (Button) findViewById(R.id.start_login);
		account_et = (EditText) findViewById(R.id.user_name);
		password_et = (EditText) findViewById(R.id.user_passwod);
		passwod_layout = (LinearLayout) findViewById(R.id.passwod_layout);

		info_buttom_main = (LinearLayout) findViewById(R.id.info_buttom_main);
		info_buttom_app = (LinearLayout) findViewById(R.id.info_buttom_app);
		info_buttom_setting = (LinearLayout) findViewById(R.id.info_buttom_setting);
		info_buttom_about = (LinearLayout) findViewById(R.id.info_buttom_about);
		info_buttom_my = (LinearLayout) findViewById(R.id.info_buttom_my);
		iv_main = (ImageView) findViewById(R.id.main_imageView);
		iv_app = (ImageView) findViewById(R.id.app_imageView);
		iv_setting = (ImageView) findViewById(R.id.setting_imageView);
		iv_about = (ImageView) findViewById(R.id.about_imageView);
		iv_my = (ImageView) findViewById(R.id.my_imageView);

		refreshListView = (RefreshListView) findViewById(R.id.refreshListView);

		app = (LinearLayout) findViewById(R.id.app);
		iv_tianyanban = (ImageView) findViewById(R.id.app_tiyanban);
		iv_xianfengban = (ImageView) findViewById(R.id.app_xianfengban);
		iv_qibaoqi = (ImageView) findViewById(R.id.app_qibaoqi);
		iv_zhuanyeban = (ImageView) findViewById(R.id.app_zhuanyeban);
		iv_shizhanban = (ImageView) findViewById(R.id.app_shizhanban);
		iv_shizhanci = (ImageView) findViewById(R.id.app_shizhanci);
		iv_zuheban = (ImageView) findViewById(R.id.app_zuheban);
		iv_zuheci = (ImageView) findViewById(R.id.app_zuheci);

		setting = (LinearLayout) findViewById(R.id.setting);
		setting_push = (CheckBox) findViewById(R.id.setting_push);
		setting_version = (Button) findViewById(R.id.setting_version);

		about = (LinearLayout) findViewById(R.id.about);

		my = (LinearLayout) findViewById(R.id.my);
		start_pay = (Button) findViewById(R.id.start_pay);
		btn_exit = (Button) findViewById(R.id.my_exit);
		my_name = (TextView) findViewById(R.id.my_name);
		service_type = (TextView) findViewById(R.id.service_type);
		// service_date = (TextView) findViewById(R.id.service_date);
	}

	private void setView() {
		tv0.setOnClickListener(this);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		tv4.setOnClickListener(this);
		tv5.setOnClickListener(this);

		title_left.setVisibility(View.GONE);
		title_right.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		startRegist.setOnClickListener(this);
		regist_btn.setOnClickListener(this);

		info_buttom_main.setOnClickListener(this);
		info_buttom_app.setOnClickListener(this);
		info_buttom_setting.setOnClickListener(this);
		info_buttom_about.setOnClickListener(this);
		info_buttom_my.setOnClickListener(this);

		iv_tianyanban.setOnClickListener(this);
		iv_xianfengban.setOnClickListener(this);
		iv_qibaoqi.setOnClickListener(this);
		iv_zhuanyeban.setOnClickListener(this);
		iv_shizhanban.setOnClickListener(this);
		iv_shizhanci.setOnClickListener(this);
		iv_zuheban.setOnClickListener(this);
		iv_zuheci.setOnClickListener(this);

		start_pay.setOnClickListener(this);
		btn_exit.setOnClickListener(this);

		refreshListView.addHeaderView(ad_layout, null, false);
		refreshListView.setAdapter(newsAdapter0);
		// refreshListView.setonLoadMoreListener(this);
		refreshListView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				getData(true);
				getAdInfo();
			}
		});

		refreshListView.setOnItemClickListener(this);

		setting_version.setOnClickListener(this);
		setting_push.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				preferences.saveBooleanMessage("user", "push", isChecked);
				if (isChecked) {
					if (!notificationServiceIsStart())
						startService(service);
				} else {
					if (notificationServiceIsStart())
						stopService(service);
				}
			}
		});
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
						InfoPortActivity.this.finish();
					}
				}).setNegativeButton("否", null).show();
		return false;
	}

	/**
	 * 获取广告信息
	 */
	private void getAdInfo() {
		new Thread() {
			public void run() {
				try {
					String[] strs = WebServicesData.getAdInfo();
					if (strs[0] != null) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = strs;
						getAdInfoHandler.sendMessage(msg);
					} else {
						getAdInfoHandler.sendEmptyMessage(0);
					}
				} catch (MalformedURLException e) {
					getAdInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (ProtocolException e) {
					getAdInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (IOException e) {
					getAdInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					getAdInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					getAdInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	private Handler getAdInfoHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:
				try {
					final String[] strs = (String[]) msg.obj;
					FinalBitmap.create(InfoPortActivity.this).display(
							ad_imageView, strs[0]);
					ad_imageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Uri uri = Uri.parse(strs[1]);
							Intent it = new Intent(Intent.ACTION_VIEW, uri);
							startActivity(it);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 注册
	 */
	private void sendPwd2Customer() {
		account_str = account_et.getEditableText().toString();
		if (account_str.equals("")) {
			Toast.makeText(InfoPortActivity.this, "手机号不能为空！ ",
					Toast.LENGTH_LONG).show();
			return;
		} else if (!CharUtil.isValidPhone(account_str)) {
			Toast.makeText(InfoPortActivity.this, "手机号不合法！ ", Toast.LENGTH_LONG)
					.show();
			return;
		}

		progressDialog.showProgressDialog("正在请求中...");
		new Thread() {
			public void run() {
				try {
					String str = WebServicesData.sendPwd2Customer(account_str);

					if (str != null) {
						if (str.contains("ok:1")) {
							registHandler.sendEmptyMessage(1);
						} else if ("该手机号码已注册,请用其它手机注册！".equals(str)) {
							registHandler.sendEmptyMessage(2);
						} else {
							registHandler.sendEmptyMessage(0);
						}
					} else {
						registHandler.sendEmptyMessage(0);
					}
				} catch (MalformedURLException e) {
					registHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (ProtocolException e) {
					registHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (IOException e) {
					registHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					registHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					registHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	private Handler registHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "注册失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(InfoPortActivity.this, "注册成功！注册成功,密码已通过短信发送给您！",
						Toast.LENGTH_LONG).show();
				// preferences.saveStringMessage("user", "phone", phone_str);
				// preferences.saveStringMessage("user", "password",
				// password_str);
				// refreshListView.setVisibility(View.VISIBLE);
				// login.setVisibility(View.GONE);
				login();
				password_et.requestFocus();
				break;
			case 2:
				Toast.makeText(InfoPortActivity.this, "该手机号码已注册,请用其它手机注册！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 登陆
	 */
	private void selectAdmin() {
		account_str = account_et.getEditableText().toString();
		password_str = password_et.getEditableText().toString();
		if (account_str.equals("") || password_str.equals("")) {
			Toast.makeText(InfoPortActivity.this, "用户名、密码均不能为空！ ",
					Toast.LENGTH_LONG).show();
			return;
			// } else if (!CharUtil.isValidPhone(phone_str)) {
			// Toast.makeText(InfoPortActivity.this, "用户名不合法！ ",
			// Toast.LENGTH_LONG)
			// .show();
			// return;
		}
		progressDialog.showProgressDialog("正在登录中...");
		new Thread() {
			public void run() {
				try {
					String[] strs = WebServicesData.selectAdmin(account_str,
							password_str);
					if (strs != null) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = strs;
						loginHandler.sendMessage(msg);
					} else {
						loginHandler.sendEmptyMessage(0);
					}
				} catch (MalformedURLException e) {
					loginHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (ProtocolException e) {
					loginHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (IOException e) {
					loginHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					loginHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					loginHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	private Handler loginHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "登录失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				String[] strs = (String[]) msg.obj;
				// string[0]用户编号,string[1]姓名,string[2]性别,string[3]电话
				Toast.makeText(InfoPortActivity.this, "登录成功！",
						Toast.LENGTH_LONG).show();
				preferences.saveStringMessage("user", "account", account_str);
				preferences.saveStringMessage("user", "password", password_str);
				preferences.saveStringMessage("user", "id", strs[0]);
				preferences.saveStringMessage("user", "name", strs[1]);
				preferences.saveStringMessage("user", "sex", strs[2]);
				preferences.saveStringMessage("user", "phone", strs[3]);

				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(InfoPortActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				title_right.setVisibility(View.GONE);
				main();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 获取个人类型
	 */
	private void getUserInfo(final String id) {
		progressDialog.showProgressDialog("正在加载中...");
		new Thread() {
			public void run() {
				try {
					String[] strs = WebServicesData.getUserInfo(id);
					if (strs[0] != null) {
						Message msg = new Message();
						msg.what = 1;
						msg.obj = strs;
						getUsetInfoHandler.sendMessage(msg);
					} else {
						getUsetInfoHandler.sendEmptyMessage(0);
					}
				} catch (MalformedURLException e) {
					getUsetInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (ProtocolException e) {
					getUsetInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (IOException e) {
					getUsetInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					getUsetInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					getUsetInfoHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	private Handler getUsetInfoHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "加载失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				String[] strs = (String[]) msg.obj;
				// string[0],序号；string[1]：权限名称；string[2]:权限起始时间;string[3]:权限终止时间
				int count = strs.length;
				int size = count / 4;
				String userInfo = "";
				for (int i = 0; i < size; i++) {
					userInfo += strs[i * 4 + 0] + "#" + strs[i * 4 + 1] + "#"
							+ strs[i * 4 + 2] + "#" + strs[i * 4 + 3] + "&";
				}
				userInfo = userInfo.substring(0, userInfo.length() - 1);
				preferences.saveStringMessage("user", "userInfo", userInfo);
				my();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 获取新闻列表
	 * 
	 * @param isRefresh
	 */
	private void getData(final boolean isRefresh) {
		// refreshListView.showLoadFooter();
		refreshListView.onRefreshComplete();
		progressDialog.showProgressDialog("正在获取中...");
		new Thread() {
			public void run() {
				try {
					ArrayList<ViewNewBean> list = WebServicesData
							.viewNews(NewsTypeId);
					if (list != null && list.size() > 0) {
						L.i(TAG, "getData NewsTypeId=" + NewsTypeId + ",size="
								+ list.size() + ",cut=" + cut);
						if (cut == 0) {
							if (isRefresh) {
								newBeans0.clear();
							}
							newBeans0.addAll(list);
						} else if (cut == 1) {
							if (isRefresh) {
								newBeans1.clear();
							}
							newBeans1.addAll(list);
						} else if (cut == 2) {
							if (isRefresh) {
								newBeans2.clear();
							}
							newBeans2.addAll(list);
						} else if (cut == 3) {
							if (isRefresh) {
								newBeans3.clear();
							}
							newBeans3.addAll(list);
						} else if (cut == 4) {
							if (isRefresh) {
								newBeans4.clear();
							}
							newBeans4.addAll(list);
						} else if (cut == 5) {
							if (isRefresh) {
								newBeans5.clear();
							}
							newBeans5.addAll(list);
						}
						viewNewsHandler.sendEmptyMessage(1);
					} else {
						viewNewsHandler.sendEmptyMessage(2);
					}
				} catch (IOException e) {
					viewNewsHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					viewNewsHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					viewNewsHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();

		MobclickAgent.onEvent(this, "" + cut);
	}

	private Handler viewNewsHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// refreshListView.showFinishLoadFooter();
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "读取失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				if (cut == 0) {
					newsAdapter0.notifyDataSetChanged();
				} else if (cut == 1) {
					newsAdapter1.notifyDataSetChanged();
				} else if (cut == 2) {
					newsAdapter2.notifyDataSetChanged();
				} else if (cut == 3) {
					newsAdapter3.notifyDataSetChanged();
				} else if (cut == 4) {
					newsAdapter4.notifyDataSetChanged();
				} else if (cut == 5) {
					newsAdapter5.notifyDataSetChanged();
				}
				break;
			case 2:
				Toast.makeText(InfoPortActivity.this, "暂无数据！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		ViewNewBean bean = null;
		String name = "";// 0：今日头条、1：宏观信息、2：国诚策略、3：独家视点、4：行业信息、5：个股信息
		if (cut == 0) {
			bean = newBeans0.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "今日头条";
		} else if (cut == 1) {
			bean = newBeans1.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "宏观信息";
		} else if (cut == 2) {
			bean = newBeans2.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "国诚策略";
		} else if (cut == 3) {
			bean = newBeans3.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "独家视点";
		} else if (cut == 4) {
			bean = newBeans4.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "行业信息";
		} else if (cut == 5) {
			bean = newBeans5.get(arg2 - refreshListView.getHeaderViewsCount());
			name = "个股信息";
		}

		Intent i = new Intent(this, ReadViewsActivity.class);
		i.putExtra("Title", bean.getTitle());
		i.putExtra("AddTime", bean.getAddTime());
		i.putExtra("Id", bean.getId());
		i.putExtra("type", 0);
		i.putExtra("name", name);
		if ("005503".equals(NewsTypeId)) {
			i.putExtra("isVideo", true);
		}
		startActivityForResult(i, REQUEST_CODE);
	}

	/**
	 * 获取webviewClient对象
	 * 
	 * @return
	 */
	public static WebViewClient getWebViewClient() {
		return new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				showUrlRedirect(view.getContext(), url);
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

	/**
	 * 今日头条
	 */
	private void topLine() {
		NewsTypeId = "001106";
		setTextViewBackground(R.id.info_textView0);
		cut = 0;
		if (newsAdapter0 == null) {
			if (newBeans0 == null)
				newBeans0 = new ArrayList<ViewNewBean>();
			newsAdapter0 = new ViewNewsAdapter(this, newBeans0);
			refreshListView.setAdapter(newsAdapter0);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter0);
		}
	}

	/**
	 * 宏观信息
	 */
	private void macroInfo() {
		NewsTypeId = "002201";
		setTextViewBackground(R.id.info_textView1);
		cut = 1;
		if (newsAdapter1 == null) {
			if (newBeans1 == null)
				newBeans1 = new ArrayList<ViewNewBean>();
			newsAdapter1 = new ViewNewsAdapter(this, newBeans1);
			refreshListView.setAdapter(newsAdapter1);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter1);
		}
	}

	/**
	 * 国诚策略
	 */
	private void strategy() {
		NewsTypeId = "030807";
		setTextViewBackground(R.id.info_textView2);
		cut = 2;
		if (newsAdapter2 == null) {
			if (newBeans2 == null)
				newBeans2 = new ArrayList<ViewNewBean>();
			newsAdapter2 = new ViewNewsAdapter(this, newBeans2);
			refreshListView.setAdapter(newsAdapter2);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter2);
		}
	}

	/**
	 * 独家视点
	 */
	private void viewPoint() {
		// NewsTypeId = "001103";
		NewsTypeId = "005503";
		setTextViewBackground(R.id.info_textView3);
		cut = 3;
		if (newsAdapter3 == null) {
			if (newBeans3 == null)
				newBeans3 = new ArrayList<ViewNewBean>();
			newsAdapter3 = new ViewNewsAdapter(this, newBeans3);
			refreshListView.setAdapter(newsAdapter3);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter3);
		}
	}

	/**
	 * 行业信息
	 */
	private void industryInfo() {
		NewsTypeId = "002202";
		setTextViewBackground(R.id.info_textView4);
		cut = 4;
		if (newsAdapter4 == null) {
			if (newBeans4 == null)
				newBeans4 = new ArrayList<ViewNewBean>();
			newsAdapter4 = new ViewNewsAdapter(this, newBeans4);
			refreshListView.setAdapter(newsAdapter4);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter4);
		}
	}

	/**
	 * 个股信息
	 */
	private void stockInfo() {
		NewsTypeId = "002204";
		setTextViewBackground(R.id.info_textView5);
		cut = 5;
		if (newsAdapter5 == null) {
			if (newBeans5 == null)
				newBeans5 = new ArrayList<ViewNewBean>();
			newsAdapter5 = new ViewNewsAdapter(this, newBeans5);
			refreshListView.setAdapter(newsAdapter5);
			getData(true);
		} else {
			refreshListView.setAdapter(newsAdapter5);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (regist_btn.isShown()) {
				login();
			} else if (login_btn.isShown()) {
				L.d(TAG, "onKeyDown BUTTOM=" + BUTTOM);
				switch (BUTTOM) {
				case 1:
					main();
					break;
				case 2:
					setting();
					break;
				case 3:
					about();
					break;
				case 4:
					my();
					break;
				case 5:
					app();
					break;
				default:
					break;
				}
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.info_textView0:// 今天头条
			topLine();
			break;
		case R.id.info_textView1:// 宏观信息
			macroInfo();
			break;
		case R.id.info_textView2:// 国诚策略
			strategy();
			break;
		case R.id.info_textView3:// 独家视点
			viewPoint();
			break;
		case R.id.info_textView4:// 行业信息
			industryInfo();
			break;
		case R.id.info_textView5:// 个股信息
			stockInfo();
			break;
		case R.id.title_right:
			login();
			break;
		case R.id.start_regist:
			login_layout.setVisibility(View.GONE);
			passwod_layout.setVisibility(View.GONE);
			regist_layout.setVisibility(View.VISIBLE);
			break;
		case R.id.regist_button:
			sendPwd2Customer();
			break;
		case R.id.start_login:
			selectAdmin();
			break;
		case R.id.info_buttom_main:
			main();
			break;
		case R.id.info_buttom_app:
			app();
			break;
		case R.id.info_buttom_setting:
			setting();
			break;
		case R.id.info_buttom_about:
			about();
			break;
		case R.id.my_exit:
			exit();
			break;
		case R.id.start_pay:
			startActivityForResult(new Intent(this, PaymentActivity.class), 1);
			break;
		case R.id.info_buttom_my:
			String ids = preferences.getStringMessage("user", "id", "");
			if ("".equals(ids)) {
				Toast.makeText(InfoPortActivity.this, "请先登录！",
						Toast.LENGTH_LONG).show();
				login();
			} else {
				String type = preferences.getStringMessage("user", "userInfo",
						"");
				if ("".equals(type)) {
					getUserInfo(ids);
				} else {
					my();
				}
			}
			break;
		case R.id.setting_version:
			checkVersion();
			break;
		case R.id.app_tiyanban:
			enterApp(0, "030803", "体验版");
			MobclickAgent.onEvent(this, "" + 7);
			break;
		case R.id.app_xianfengban:
			enterApp(0, "030801", "先锋版");
			MobclickAgent.onEvent(this, "" + 8);
			break;
		case R.id.app_qibaoqi:
			enterApp(0, "030802", "起爆器");
			MobclickAgent.onEvent(this, "" + 9);
			break;
		case R.id.app_zhuanyeban:
			enterApp(0, "030804", "专业版");
			MobclickAgent.onEvent(this, "" + 10);
			break;
		case R.id.app_shizhanban:
			enterApp(0, "030805", "实战版");
			MobclickAgent.onEvent(this, "" + 11);
			break;
		case R.id.app_shizhanci:
			enterApp(1, "03080500", "实战池");
			MobclickAgent.onEvent(this, "" + 12);
			break;
		case R.id.app_zuheban:
			enterApp(0, "030806", "组合版");
			MobclickAgent.onEvent(this, "" + 13);
			break;
		case R.id.app_zuheci:
			enterApp(1, "03080600", "组合池");
			MobclickAgent.onEvent(this, "" + 14);
			break;
		default:
			break;
		}
	}

	private void checkVersion() {
		String uploadname = preferences.getStringMessage("user", "uploadname",
				"");
		String uploadurl = preferences
				.getStringMessage("user", "uploadurl", "");
		if (!"".equals(uploadname) && "".equals(uploadurl)) {
			String[] strs = new String[] { "", uploadname, uploadurl, "" };
			Message msg = new Message();
			msg.obj = strs;
			handler.sendMessage(msg);
		} else {
			progressDialog.showProgressDialog("正在检查新版本...");
			new Thread() {
				@Override
				public void run() {
					try {
						String[] strs = WebServicesData.getVersionInfo();
						if (strs != null) {
							int versionCode = Integer.valueOf(strs[0]);
							if (versionCode > new SoftUtil()
									.getVersionCode(InfoPortActivity.this)) {
								Message msg = new Message();
								msg.what = 1;
								msg.obj = strs;
								handler.sendMessage(msg);
							} else {
								handler.sendEmptyMessage(2);
							}
						} else {
							handler.sendEmptyMessage(0);
						}
					} catch (MalformedURLException e) {
						handler.sendEmptyMessage(0);
						e.printStackTrace();
					} catch (ProtocolException e) {
						handler.sendEmptyMessage(0);
						e.printStackTrace();
					} catch (IOException e) {
						handler.sendEmptyMessage(0);
						e.printStackTrace();
					} catch (NameNotFoundException e) {
						handler.sendEmptyMessage(0);
						e.printStackTrace();
					}
				}
			}.start();
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			progressDialog.cancelProgressDialog("");
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "网络异常，请稍后再试！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				final String[] strs = (String[]) msg.obj;
				new AlertDialog.Builder(InfoPortActivity.this)
						.setTitle("检测到新版本")
						.setCancelable(false)
						.setPositiveButton("马上升级",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										new SpinnerProgressDialog(
												InfoPortActivity.this)
												.showProgressDialog("正在更新版本...");
										new DownLoadFileThread(
												InfoPortActivity.this, strs[2],
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
									}
								}).show();
				break;
			case 2:
				Toast.makeText(InfoPortActivity.this, "当前已是最新版本！",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 
	 * @param type
	 *            类型：0、ViewNews新闻类列表 1、GetStockPool股票池
	 * @param id
	 * @param name
	 */
	private void enterApp(int type, String id, String name) {
		String ida = preferences.getStringMessage("user", "id", "");
		if ("".equals(ida)) {
			Toast.makeText(InfoPortActivity.this, "请先登录！", Toast.LENGTH_LONG)
					.show();
			login();
		} else {
			String Permitted = preferences.getStringMessage("user",
					"Permitted", "");
			if (!"".equals(Permitted)) {
				enterAppItem(Permitted, id, type, name);
			} else {
				checkPermitted(preferences.getStringMessage("user", "id", ""),
						id, type, name);
			}
		}
	}

	/**
	 * 获取应用权限
	 */
	private void checkPermitted(final String UsrSno, final String id,
			final int type, final String name) {
		progressDialog.showProgressDialog("正在加载中...");
		new Thread() {
			public void run() {
				try {
					String[] strs = WebServicesData.checkPermitted(UsrSno);
					if (strs[0] != null) {
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = type;
						String Permitted = "";
						for (String str : strs) {
							Permitted += str + "#";
						}
						preferences.saveStringMessage("user", "Permitted",
								Permitted);
						msg.obj = new String[] { Permitted, id, name };
						checkPermittedHandler.sendMessage(msg);
					} else {
						checkPermittedHandler.sendEmptyMessage(0);
					}
				} catch (MalformedURLException e) {
					checkPermittedHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (ProtocolException e) {
					checkPermittedHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (IOException e) {
					checkPermittedHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					checkPermittedHandler.sendEmptyMessage(0);
					e.printStackTrace();
				} catch (Exception e) {
					checkPermittedHandler.sendEmptyMessage(0);
					e.printStackTrace();
				}
			};
		}.start();
	}

	private Handler checkPermittedHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(InfoPortActivity.this, "加载失败！",
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				String[] strs = (String[]) msg.obj;
				enterAppItem(strs[0], strs[1], msg.arg1, strs[2]);
				break;
			default:
				break;
			}
		};
	};

	private void enterAppItem(String Permitted, String id, int type, String name) {
		String idd = id;
		if (idd.length() == 8 && idd.endsWith("00")) {
			idd = idd.substring(0, 6);
		}
		if (Permitted.contains(idd)) {
			Intent i = new Intent(this, AppActivity.class);
			i.putExtra("type", type);
			i.putExtra("id", id);
			i.putExtra("name", name);
			startActivityForResult(i, REQUEST_CODE);
		} else {
			Toast.makeText(InfoPortActivity.this, "您没有该版本的权限！",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		L.d(TAG, "onActivityResult requestCode=" + requestCode + ",resultCode="
				+ resultCode + ",BUTTOM=" + BUTTOM + ",TOP=" + TOP);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				switch (BUTTOM) {
				case 0:// 登陆
					login();
					break;
				case 1:// 首页
					main();
					break;
				case 2:// 我的设置
					setting();
					return;
					// break;
				case 3:// 关于信息港
					about();
					return;
					// break;
				case 4:// 我
					my();
					return;
					// break;
				case 5:// 应用
					app();
					return;
					// break;
				default:
					break;
				}
				switch (TOP) {
				case 0:
					topLine();
					break;
				case 1:
					macroInfo();
					break;
				case 2:
					strategy();
					break;
				case 3:
					viewPoint();
					break;
				case 4:
					industryInfo();
					break;
				case 5:
					stockInfo();
					break;
				default:
					break;
				}
			}
		}
	}

	private void login() {
		refreshListView.setVisibility(View.GONE);
		login.setVisibility(View.VISIBLE);
		passwod_layout.setVisibility(View.VISIBLE);
		my.setVisibility(View.GONE);
		about.setVisibility(View.GONE);
		password_et.setText("");
		account_str = preferences.getStringMessage("user", "account", "");
		if (account_str != null && !"".equals(account_str)) {
			account_et.setText(account_str);
			password_et.requestFocus();
		}

		login_layout.setVisibility(View.VISIBLE);
		regist_layout.setVisibility(View.GONE);
	}

	private void logout() {
		title_right.setVisibility(View.VISIBLE);
	}

	private void main() {
		setTextViewBackground(R.id.info_textView0);
	}

	private void app() {
		BUTTOM = 5;
		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_app.setBackgroundResource(R.drawable.more_item_b);
		info_buttom_setting.setBackgroundResource(R.drawable.more_item);
		info_buttom_about.setBackgroundResource(R.drawable.more_item);
		info_buttom_my.setBackgroundResource(R.drawable.more_item);
		iv_main.setImageResource(R.drawable.main);
		iv_app.setImageResource(R.drawable.app_b);
		iv_setting.setImageResource(R.drawable.setting);
		iv_about.setImageResource(R.drawable.about);
		iv_my.setImageResource(R.drawable.my);

		refreshListView.setVisibility(View.GONE);
		scrollView.setVisibility(View.VISIBLE);
		login.setVisibility(View.GONE);
		app.setVisibility(View.VISIBLE);
		setting.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		about.setVisibility(View.GONE);
	}

	private void setting() {
		BUTTOM = 2;
		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_app.setBackgroundResource(R.drawable.more_item);
		info_buttom_setting.setBackgroundResource(R.drawable.more_item_b);
		info_buttom_about.setBackgroundResource(R.drawable.more_item);
		info_buttom_my.setBackgroundResource(R.drawable.more_item);
		iv_main.setImageResource(R.drawable.main);
		iv_app.setImageResource(R.drawable.app);
		iv_setting.setImageResource(R.drawable.setting_b);
		iv_about.setImageResource(R.drawable.about);
		iv_my.setImageResource(R.drawable.my);

		refreshListView.setVisibility(View.GONE);
		scrollView.setVisibility(View.VISIBLE);
		login.setVisibility(View.GONE);
		setting.setVisibility(View.VISIBLE);
		app.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		about.setVisibility(View.GONE);

		setting_push.setChecked(preferences.getBooleanMessage("user", "push",
				true));
	}

	private void my() {
		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_app.setBackgroundResource(R.drawable.more_item);
		info_buttom_setting.setBackgroundResource(R.drawable.more_item);
		info_buttom_about.setBackgroundResource(R.drawable.more_item);
		info_buttom_my.setBackgroundResource(R.drawable.more_item_b);
		iv_main.setImageResource(R.drawable.main);
		iv_app.setImageResource(R.drawable.app);
		iv_setting.setImageResource(R.drawable.setting);
		iv_about.setImageResource(R.drawable.about);
		iv_my.setImageResource(R.drawable.my_b);

		refreshListView.setVisibility(View.GONE);
		scrollView.setVisibility(View.GONE);
		login.setVisibility(View.GONE);
		app.setVisibility(View.GONE);
		setting.setVisibility(View.GONE);
		my.setVisibility(View.VISIBLE);
		about.setVisibility(View.GONE);

		String name = preferences.getStringMessage("user", "name", "");
		my_name.setText("欢迎" + name);
		String userInfo = preferences.getStringMessage("user", "userInfo", "");
		String[] types = userInfo.split("&");
		userInfo = "";
		for (String type : types) {
			String[] tps = type.split("#");
			String tp = tps[1].trim();
			userInfo += tp.substring(tp.length() - 3, tp.length()) + "：起始日期"
					+ tps[2].substring(0, tps[2].indexOf(" ")) + "   终止日期："
					+ tps[3].substring(0, tps[3].indexOf(" ")) + "\n\n";
		}
		service_type.setText(userInfo);
		// try {
		// int d = getSurplusdate(preferences.getStringMessage("user",
		// "toTime", ""));
		// d = d < 0 ? 0 : d;
		// service_date.setText("余" + d + "天");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * 计算剩余天数
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	@SuppressLint("SimpleDateFormat")
	private int getSurplusdate(String date) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d0 = new Date();
		Date d1 = df.parse(date);
		long time0 = d0.getTime();
		long time1 = d1.getTime();
		return (int) ((time1 - time0) / (1000 * 60 * 60 * 24));
	}

	private void about() {
		BUTTOM = 3;
		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_app.setBackgroundResource(R.drawable.more_item);
		info_buttom_setting.setBackgroundResource(R.drawable.more_item);
		info_buttom_about.setBackgroundResource(R.drawable.more_item_b);
		info_buttom_my.setBackgroundResource(R.drawable.more_item);
		iv_main.setImageResource(R.drawable.main);
		iv_app.setImageResource(R.drawable.app);
		iv_setting.setImageResource(R.drawable.setting);
		iv_about.setImageResource(R.drawable.about_b);
		iv_my.setImageResource(R.drawable.my);

		refreshListView.setVisibility(View.GONE);
		login.setVisibility(View.GONE);
		app.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		setting.setVisibility(View.GONE);
		about.setVisibility(View.VISIBLE);
	}

	private void exit() {
		preferences.saveStringMessage("user", "phone", "");
		preferences.saveStringMessage("user", "password", "");
		preferences.saveStringMessage("user", "id", "");
		preferences.saveStringMessage("user", "name", "");
		preferences.saveStringMessage("user", "sex", "");
		preferences.saveStringMessage("user", "userInfo", "");
		preferences.saveStringMessage("user", "Permitted", "");

		main();
		logout();
	}

	private void setTextViewBackground(int id) {
		BUTTOM = 1;
		refreshListView.setVisibility(View.VISIBLE);
		scrollView.setVisibility(View.VISIBLE);
		account_str = preferences.getStringMessage("user", "phone", "");
		password_str = preferences.getStringMessage("user", "password", "");
		login.setVisibility(View.GONE);
		app.setVisibility(View.GONE);
		my.setVisibility(View.GONE);
		setting.setVisibility(View.GONE);
		about.setVisibility(View.GONE);

		info_buttom_main.setBackgroundResource(R.drawable.more_item_b);
		info_buttom_app.setBackgroundResource(R.drawable.more_item);
		info_buttom_setting.setBackgroundResource(R.drawable.more_item);
		info_buttom_about.setBackgroundResource(R.drawable.more_item);
		info_buttom_my.setBackgroundResource(R.drawable.more_item);
		iv_main.setImageResource(R.drawable.main_b);
		iv_app.setImageResource(R.drawable.app);
		iv_setting.setImageResource(R.drawable.setting);
		iv_about.setImageResource(R.drawable.about);
		iv_my.setImageResource(R.drawable.my);

		int[] ids = { R.id.info_textView0, R.id.info_textView1,
				R.id.info_textView2, R.id.info_textView3, R.id.info_textView4,
				R.id.info_textView5 };
		for (int i : ids) {
			if (i == id) {
				findViewById(i).setBackgroundResource(R.drawable.bar_textview);
			} else {
				findViewById(i).setBackgroundResource(0);
			}
		}
	}

}
