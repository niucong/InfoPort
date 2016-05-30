package com.niucong.infoport.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class SpinnerProgressDialog {

	private Context context;
	private ProgressDialog pd;

	public SpinnerProgressDialog(Context context) {
		this.context = context;
	}

	/**
	 * 加载中对话框
	 * 
	 * @param context
	 * @param msg
	 * @return
	 */
	public void showProgressDialog(String msg) {
		if (pd == null) {
			pd = new ProgressDialog(context);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// pd.setIndeterminateDrawable(context.getResources().getDrawable(
			// R.drawable.drawable_progress));
		}
		pd.setMessage(msg);
		if (!pd.isShowing()) {
			pd.show();
		}
	}

	/**
	 * 提示
	 * 
	 * @param msg
	 */
	public void cancelProgressDialog(String msg) {
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}
		if (msg != null && !msg.equals("")) {
			Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		}
	}

}
