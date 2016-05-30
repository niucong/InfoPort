package com.niucong.infoport;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.niucong.infoport.adapter.StockPoolAdapter;
import com.niucong.infoport.adapter.ViewNewsAdapter;
import com.niucong.infoport.bean.StockPoolBean;
import com.niucong.infoport.bean.ViewNewBean;
import com.niucong.infoport.dialog.SpinnerProgressDialog;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.util.L;
import com.niucong.infoport.view.RefreshListView;
import com.niucong.infoport.view.RefreshListView.OnRefreshListener;

public class AppActivity extends UMengActivity implements OnClickListener,
		OnItemClickListener {
	protected static final String TAG = "AppActivity";

	private ImageView title_left, title_right, iv_main, iv_app;
	private TextView title_tv;
	private Button button_back;
	private LinearLayout info_buttom_main, info_buttom_app,
			info_buttom_setting, info_buttom_about, info_buttom_my,
			app_item_title;
	private RefreshListView refreshListView;

	private int type;
	private String id, name;

	private SpinnerProgressDialog progressDialog;

	private ViewNewsAdapter newsAdapter;
	private StockPoolAdapter poolAdapter;
	private ArrayList<ViewNewBean> newBeans;
	private ArrayList<StockPoolBean> poolBeans;

	public static AppActivity appActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app);
		appActivity = this;

		progressDialog = new SpinnerProgressDialog(this);

		Intent intent = getIntent();
		type = intent.getIntExtra("type", 0);
		id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");

		getView();
		setView();

		getData(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		appActivity = null;
	}

	private void getView() {
		title_left = (ImageView) findViewById(R.id.title_left);
		title_right = (ImageView) findViewById(R.id.title_right);
		title_tv = (TextView) findViewById(R.id.app_title);
		button_back = (Button) findViewById(R.id.button_back);

		info_buttom_main = (LinearLayout) findViewById(R.id.info_buttom_main);
		info_buttom_app = (LinearLayout) findViewById(R.id.info_buttom_app);
		info_buttom_setting = (LinearLayout) findViewById(R.id.info_buttom_setting);
		info_buttom_about = (LinearLayout) findViewById(R.id.info_buttom_about);
		info_buttom_my = (LinearLayout) findViewById(R.id.info_buttom_my);

		app_item_title = (LinearLayout) findViewById(R.id.app_item_title);
		refreshListView = (RefreshListView) findViewById(R.id.refreshListView);
		iv_main = (ImageView) findViewById(R.id.main_imageView);
		iv_app = (ImageView) findViewById(R.id.app_imageView);
	}

	private void setView() {
		title_tv.setText(name);

		title_left.setVisibility(View.GONE);
		title_right.setVisibility(View.GONE);

		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		button_back.setOnClickListener(this);

		info_buttom_main.setOnClickListener(this);
		info_buttom_app.setOnClickListener(this);
		info_buttom_setting.setOnClickListener(this);
		info_buttom_about.setOnClickListener(this);
		info_buttom_my.setOnClickListener(this);

		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_app.setBackgroundResource(R.drawable.more_item_b);
		iv_main.setImageResource(R.drawable.main);
		iv_app.setImageResource(R.drawable.app_b);

		if (type == 0) {
			app_item_title.setVisibility(View.GONE);
			newBeans = new ArrayList<ViewNewBean>();
			newsAdapter = new ViewNewsAdapter(this, newBeans);
			refreshListView.setAdapter(newsAdapter);
		} else if (type == 1) {
			app_item_title.setVisibility(View.VISIBLE);
			poolBeans = new ArrayList<StockPoolBean>();
			poolAdapter = new StockPoolAdapter(this, poolBeans);
			refreshListView.setAdapter(poolAdapter);
		}

		refreshListView.setOnItemClickListener(this);
		// refreshListView.setonLoadMoreListener(this);
		refreshListView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				getData(true);
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
						if (InfoPortActivity.infoPortActivity != null)
							InfoPortActivity.infoPortActivity.finish();
						AppActivity.this.finish();
					}
				}).setNegativeButton("否", null).show();
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		L.d(TAG, "onActivityResult requestCode=" + requestCode + ",resultCode="
				+ resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == InfoPortActivity.REQUEST_CODE) {
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_back:
			finish();
			break;
		case R.id.info_buttom_main:
			InfoPortActivity.BUTTOM = 1;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.info_buttom_setting:
			InfoPortActivity.BUTTOM = 2;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.info_buttom_about:
			InfoPortActivity.BUTTOM = 3;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.info_buttom_my:
			InfoPortActivity.BUTTOM = 4;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.info_buttom_app:
			InfoPortActivity.BUTTOM = 5;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		default:
			break;
		}
	}

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
					if (type == 0) {
						ArrayList<ViewNewBean> list = WebServicesData
								.viewNews(id);
						if (list != null && list.size() > 0) {
							if (isRefresh) {
								newBeans.clear();
							}
							newBeans.addAll(list);
							viewNewsHandler.sendEmptyMessage(1);
						} else {
							viewNewsHandler.sendEmptyMessage(2);
						}
					} else if (type == 1) {
						ArrayList<StockPoolBean> list = WebServicesData
								.stockPool(id);
						if (list != null && list.size() > 0) {
							L.i(TAG, "getData size=" + list.size());
							if (isRefresh) {
								poolBeans.clear();
							}
							poolBeans.addAll(list);
							viewNewsHandler.sendEmptyMessage(1);
						} else {
							viewNewsHandler.sendEmptyMessage(2);
						}
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
	}

	@SuppressLint("HandlerLeak")
	private Handler viewNewsHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// refreshListView.showFinishLoadFooter();
			progressDialog.cancelProgressDialog(null);
			switch (msg.what) {
			case 0:
				Toast.makeText(AppActivity.this, "读取失败！", Toast.LENGTH_LONG)
						.show();
				break;
			case 1:
				if (type == 0) {
					newsAdapter.notifyDataSetChanged();
				} else if (type == 1) {
					poolAdapter.notifyDataSetChanged();
				}
				break;
			case 2:
				Toast.makeText(AppActivity.this, "暂无数据！", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent i = new Intent(this, ReadViewsActivity.class);
		if (type == 0) {
			ViewNewBean bean = newBeans.get(arg2
					- refreshListView.getHeaderViewsCount());
			i.putExtra("Title", bean.getTitle());
			i.putExtra("AddTime", bean.getAddTime());
			i.putExtra("Id", bean.getId());
		} else if (type == 1) {
			StockPoolBean bean = poolBeans.get(arg2
					- refreshListView.getHeaderViewsCount());
			i.putExtra("Id", bean.getId());
		}
		i.putExtra("isMain", false);
		i.putExtra("typeId", id);
		i.putExtra("type", type);
		i.putExtra("name", name);
		startActivityForResult(i, InfoPortActivity.REQUEST_CODE);
	}

}
