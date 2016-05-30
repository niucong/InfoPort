package com.niucong.infoport.util;

import android.util.Log;

public class L {

	private static final boolean flag = true;

	public static void i(String tag, String msg) {
		if (flag) {
			if (msg != null) {
				Log.i(tag, msg);
			}
		}
	}

	public static void d(String tag, String msg) {
		if (flag) {
			if (msg != null) {
				Log.d(tag, msg);
			}
		}
	}

	public static void v(String tag, String msg) {
		if (flag) {
			if (msg != null) {
				Log.v(tag, msg);
			}
		}
	}

	/**
	 * 打印长字符串日志
	 * 
	 * @param methodName
	 * @param str
	 */
	@SuppressWarnings("unused")
	public static void getLongLog(String tag, String methodName, String str) {
		if (flag && str != null) {
			try {
				v(tag, methodName + " str.length=" + str.length());
				for (int i = 0; i < str.length() / 3000; i++) {
					v(tag,
							methodName + " str " + i + " = "
									+ str.substring(i * 3000, (i + 1) * 3000));
				}
				v(tag,
						methodName + " str  = "
								+ str.substring(str.length() / 3000 * 3000));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
