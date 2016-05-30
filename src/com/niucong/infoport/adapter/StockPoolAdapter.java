package com.niucong.infoport.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.niucong.infoport.R;
import com.niucong.infoport.bean.StockPoolBean;
import com.niucong.infoport.util.L;

public class StockPoolAdapter extends BaseAdapter {
	private static final String TAG = "StockPoolAdapter";

	private Context mContext;
	private ArrayList<StockPoolBean> poolBeans;

	public StockPoolAdapter(Context context, ArrayList<StockPoolBean> poolBeans) {
		this.mContext = context;
		this.poolBeans = poolBeans;
	}

	@Override
	public int getCount() {
		return poolBeans.size();
	}

	@Override
	public Object getItem(int arg0) {
		return poolBeans.get(arg0);
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
					R.layout.item_pool, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.item_pool_name);
			holder.inTime = (TextView) convertView
					.findViewById(R.id.item_pool_inTime);
			holder.inPrice = (TextView) convertView
					.findViewById(R.id.item_pool_inPrice);
			holder.outTime = (TextView) convertView
					.findViewById(R.id.item_pool_outTime);
			holder.outPrice = (TextView) convertView
					.findViewById(R.id.item_pool_outPrice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final StockPoolBean bean = poolBeans.get(position);
		L.d(TAG, "getView name=" + bean.getName());
		holder.name.setText(bean.getName());
		try {
			holder.inTime.setText(getTime(bean.getInTime()));
		} catch (Exception e) {
			e.printStackTrace();
			holder.inTime.setText("");
		}
		try {
			Double.valueOf(bean.getInPrice());
			holder.inPrice.setText(bean.getInPrice());
		} catch (NumberFormatException e1) {
			holder.inPrice.setText("");
			e1.printStackTrace();
		}
		try {
			holder.outTime.setText(getTime(bean.getOutTime()));
		} catch (Exception e) {
			holder.outTime.setText("");
			e.printStackTrace();
		}
		try {
			Double.valueOf(bean.getOutPrice());
			holder.outPrice.setText(bean.getOutPrice());
		} catch (NumberFormatException e1) {
			holder.outPrice.setText("");
			e1.printStackTrace();
		}
		return convertView;
	}

	private String getTime(String time) throws Exception {
		return time.substring(0, time.indexOf(" "));
		// Date date = new Date();
		// SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// SimpleDateFormat sdf;
		// if (date.getMonth() > 9) {
		// if (date.getDate() > 9) {
		// sdf = new SimpleDateFormat("yyyy-MM-dd");
		// } else {
		// sdf = new SimpleDateFormat("yyyy-MM-d");
		// }
		// } else {
		// if (date.getDate() > 9) {
		// sdf = new SimpleDateFormat("yyyy-M-dd");
		// } else {
		// sdf = new SimpleDateFormat("yyyy-M-d");
		// }
		// }
		// Date d = sdf0.parse(time);
		// long time0 = d.getTime();
		// return sdf.format(time0);
	}

	private static class ViewHolder {
		TextView name, inTime, inPrice, outTime, outPrice;
	}
}
