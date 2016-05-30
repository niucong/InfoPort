package com.niucong.infoport.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.niucong.infoport.R;
import com.niucong.infoport.bean.ViewNewBean;
import com.niucong.infoport.util.L;

public class ViewNewsAdapter extends BaseAdapter {
	private static final String TAG = "ViewNewsAdapter";

	private Context mContext;
	private ArrayList<ViewNewBean> newBeans;

	public ViewNewsAdapter(Context context, ArrayList<ViewNewBean> newBeans) {
		this.mContext = context;
		this.newBeans = newBeans;
	}

	@Override
	public int getCount() {
		return newBeans.size();
	}

	@Override
	public Object getItem(int arg0) {
		return newBeans.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_info, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.context = (TextView) convertView.findViewById(R.id.context);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ViewNewBean bean = newBeans.get(position);
		holder.title.setText(bean.getTitle());
		holder.context.setVisibility(View.GONE);
		String time = bean.getAddTime();
		holder.time.setText(time);
		L.d(TAG, "getView time=" + time + ",nowTime=" + getNowDate());
		if (time.startsWith(getNowDate())) {
			holder.title.setTextColor(mContext.getResources().getColor(
					R.color.red));
		} else {
			holder.title.setTextColor(mContext.getResources().getColor(
					R.color.black));
		}
		return convertView;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	private String getNowDate() {
		Date date = new Date();
		SimpleDateFormat sdf;// = new SimpleDateFormat("yyyy-MM-dd");
		if (date.getMonth() > 9) {
			if (date.getDate() > 9) {
				sdf = new SimpleDateFormat("yyyy/MM/dd");
			} else {
				sdf = new SimpleDateFormat("yyyy/MM/d");
			}
		} else {
			if (date.getDate() > 9) {
				sdf = new SimpleDateFormat("yyyy/M/dd");
			} else {
				sdf = new SimpleDateFormat("yyyy/M/d");
			}
		}
		return sdf.format(date);
	}

	private static class ViewHolder {
		TextView title, context, time;
	}
}
