package com.niucong.infoport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileUtil {
	private static final String TAG = "FileUtil";

	/**
	 * 创建文件夹
	 * 
	 * @param filePath
	 */
	public static void createFile(String filePath) {
		// L.i(TAG, "createFile filePath = " + filePath);
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 创建一个新文件
	 * 
	 * @param filePath
	 * @return true 创建成功 false 创建失败或文件已存在
	 */
	public boolean createNewFile(String filePath) {
		File file = new File(filePath);
		boolean result = false;
		if (!file.exists()) {
			try {
				file.createNewFile();
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 复制文件
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param rewrite
	 */
	public void copyfile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return;
		}
		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		L.i(TAG, "copyfile fromFile=" + fromFile.getPath() + ",toFile="
				+ toFile.getPath());

		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}
		try {
			FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			fosfrom.close();
			fosto.close();

			L.d(TAG, "copyfile toFile=" + toFile.length());
		} catch (Exception e) {
			e.fillInStackTrace();
		}

	}

	/**
	 * 打开文件
	 * 
	 * @param context
	 * @param file
	 */
	public void openFile(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 设置intent的Action属性
		intent.setAction(Intent.ACTION_VIEW);
		// 获取文件file的MIME类型
		String type = getMIMEType(file);
		L.i(TAG, "openFile type = " + type);
		// 设置intent的data和Type属性。
		intent.setDataAndType(Uri.fromFile(file), type);
		try {
			// 跳转
			context.startActivity(intent);
		} catch (Exception e) {
			type = "*/*";
			intent.setDataAndType(Uri.fromFile(file), type);
			context.startActivity(intent);
		}
	}

	/**
	 * 删除文件夹、文件
	 * 
	 * @param filePath
	 */
	public static void deleteFile(String filePath) {
		L.i(TAG, "deleteFile filePath = " + filePath);
		File file = null;
		try {
			file = new File(filePath);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (file != null && file.exists()) {
			// file.delete();
			try {
				Runtime.getRuntime().exec("rm -r " + filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 计算文件大小
	 * 
	 * @param len
	 * @return
	 */
	public static String computeFileSize(long len) {
		String size = "1K";
		float len2 = len;
		DecimalFormat df = new DecimalFormat("#0.#");
		if (len > 1024) {
			len2 = len2 / 1024;
		} else {
			return size;
		}
		if (len2 > 1024) {
			len2 = len2 / 1024;
		} else {
			size = df.format(len2) + "K";
			return size;
		}
		if (len2 > 1024) {
			len2 = len2 / 1024;
			size = df.format(len2) + "G";
		} else {
			size = df.format(len2) + "M";
		}
		return size;
	}

	/**
	 * 根据文件后缀名获得对应的MIME类型。
	 * 
	 * @param file
	 */
	public static String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		// 获取后缀名前的分隔符"."在fName中的位置。
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		/* 获取文件的后缀名 */
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		// 在MIME和文件类型的匹配表中找到对应的MIME类型。
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

	private static final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ ".3gp", "video/3gpp" },
			{ ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" },
			{ ".avi", "video/x-msvideo" },
			{ ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" },
			{ ".c", "text/plain" },
			{ ".class", "application/octet-stream" },
			{ ".conf", "text/plain" },
			{ ".cpp", "text/plain" },
			{ ".doc", "application/msword" },
			{ ".docx",
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx",
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" },
			{ ".gif", "image/gif" },
			{ ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" },
			{ ".h", "text/plain" },
			{ ".htm", "text/html" },
			{ ".html", "text/html" },
			{ ".jar", "application/java-archive" },
			{ ".java", "text/plain" },
			{ ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" },
			{ ".js", "application/x-javascript" },
			{ ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" },
			{ ".m4a", "audio/mp4a-latm" },
			{ ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" },
			{ ".m4u", "video/vnd.mpegurl" },
			{ ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" },
			{ ".mp2", "audio/x-mpeg" },
			{ ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" },
			{ ".mpc", "application/vnd.mpohun.certificate" },
			{ ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" },
			{ ".mpg", "video/mpeg" },
			{ ".mpg4", "video/mp4" },
			{ ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" },
			{ ".ogg", "audio/ogg" },
			{ ".pdf", "application/pdf" },
			{ ".png", "image/png" },
			{ ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx",
					"application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" },
			// {".rar", "application/octet-stream"},
			{ ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" },
			{ ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" },
			{ ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" },
			{ ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" },
			// {".xml", "text/xml"},
			{ ".xml", "text/plain" }, { ".z", "application/x-compress" },
			{ ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

}
