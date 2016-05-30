package com.niucong.infoport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PaymentActivity extends UMengActivity implements OnClickListener {

	private ImageView title_left, title_mid, title_right, iv_main, iv_my;
	private TextView title_tv, prompt_tv;
	private EditText user_name, user_pay, user_phone;
	private Button button;
	private LinearLayout info_buttom_main, info_buttom_app,
			info_buttom_setting, info_buttom_about, info_buttom_my;

	private boolean isConfirm = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);

		getView();
		setView();
	}

	private void getView() {
		title_left = (ImageView) findViewById(R.id.title_left);
		title_mid = (ImageView) findViewById(R.id.title_mid);
		title_right = (ImageView) findViewById(R.id.title_right);
		title_tv = (TextView) findViewById(R.id.title_tv);
		prompt_tv = (TextView) findViewById(R.id.prompt_tv);
		button = (Button) findViewById(R.id.button);

		user_name = (EditText) findViewById(R.id.user_name);
		user_pay = (EditText) findViewById(R.id.user_pay);
		user_phone = (EditText) findViewById(R.id.user_phone);

		info_buttom_main = (LinearLayout) findViewById(R.id.info_buttom_main);
		info_buttom_app = (LinearLayout) findViewById(R.id.info_buttom_app);
		info_buttom_setting = (LinearLayout) findViewById(R.id.info_buttom_setting);
		info_buttom_about = (LinearLayout) findViewById(R.id.info_buttom_about);
		info_buttom_my = (LinearLayout) findViewById(R.id.info_buttom_my);
		iv_main = (ImageView) findViewById(R.id.main_imageView);
		iv_my = (ImageView) findViewById(R.id.my_imageView);
	}

	private void setView() {
		title_mid.setVisibility(View.GONE);
		title_tv.setText("订单填写");

		title_left.setVisibility(View.GONE);
		title_right.setVisibility(View.GONE);

		title_left.setOnClickListener(this);
		title_right.setOnClickListener(this);
		button.setOnClickListener(this);

		info_buttom_main.setOnClickListener(this);
		info_buttom_app.setOnClickListener(this);
		info_buttom_setting.setOnClickListener(this);
		info_buttom_about.setOnClickListener(this);
		info_buttom_my.setOnClickListener(this);

		info_buttom_main.setBackgroundResource(R.drawable.more_item);
		info_buttom_my.setBackgroundResource(R.drawable.more_item_b);
		iv_main.setImageResource(R.drawable.main);
		iv_my.setImageResource(R.drawable.my_b);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left:
			if (isConfirm) {
				isConfirm = false;
				title_tv.setText("订单填写");
				button.setText("下一步");
				prompt_tv.setVisibility(View.GONE);

				user_name.setEnabled(true);
				user_pay.setEnabled(true);
				user_phone.setEnabled(true);
			} else {
				finish();
			}
			break;
		case R.id.title_right:
			InfoPortActivity.BUTTOM = 0;
			setResult(RESULT_OK, new Intent());
			finish();
			break;
		case R.id.button:
			if (isConfirm) {

			} else {
				isConfirm = true;
				title_tv.setText("订单确认");
				button.setText("确认订单，购买");
				prompt_tv.setVisibility(View.VISIBLE);

				user_name.setEnabled(false);
				user_pay.setEnabled(false);
				user_phone.setEnabled(false);
			}
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

}
