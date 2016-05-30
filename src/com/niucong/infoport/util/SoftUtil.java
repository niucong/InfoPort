package com.niucong.infoport.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SoftUtil {

	/**
	 * 获取当前版本号
	 * 
	 * @param context
	 * @return
	 * @throws NameNotFoundException
	 */
	public int getVersionCode(Context context) throws NameNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo pinfo = pm.getPackageInfo(context.getPackageName(),
				PackageManager.GET_CONFIGURATIONS);
		// String versionName = pinfo.versionName;
		return pinfo.versionCode;
	}

}
