package com.niucong.infoport.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharUtil {
	// private static final String TAG = "CharUtil";

	/**
	 * 去除手机号码前的国家代码和IP号
	 * 
	 * @param phone
	 * @return
	 */
	public static String cleanIP(String phone) {
		String[] ips = { "+86", "12593", "17951", "17911", "10193" };
		for (String ip : ips) {
			if (phone.startsWith(ip)) {
				phone = phone.replace(ip, "");
			}
		}
		return phone;
	}

	/**
	 * 验证邮箱合法性
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean isValidEmail(String paramString) {
		// L.i(TAG, "isValidEmail Email=" + paramString);
		if (paramString == null || paramString.equals(""))
			return false;
		return paramString.matches("\\w+[\\w]*@[\\w]+\\.[\\w]+$")
				|| paramString.matches("\\w+[\\w]*@[\\w]+\\.[\\w]+\\.[\\w]+$");
		// return paramString
		// .matches("[a-zA-Z0-9._-]*@([a-zA-Z0-9-_]+\\.)+(com|gov|net|org|com\\.cn|edu\\.cn)$");
	}

	/**
	 * 验证密码是否合法
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean isValidPassword(String paramString) {
		// L.i(TAG, "isValidPassword Password=" + paramString);
		if (paramString == null || paramString.equals(""))
			return false;
		// return paramString.matches("[a-zA-Z0-9]{6,16}+$");
		return paramString.matches("[^\u4e00-\u9fa5]{6,16}+$");
	}

	/**
	 * 手机号是否合法
	 * 
	 * @param paramString
	 * @return
	 */
	public static boolean isValidPhone(String paramString) {
		// L.i(TAG, "isValidPhone phone=" + paramString);
		if (paramString == null || paramString.equals(""))
			return false;
		Pattern p = Pattern.compile("^((1))\\d{10}$");
		Matcher m = p.matcher(paramString);
		return m.matches();
	}

	public static String getUrl(String strContent) {
		HashMap<String, String> map = new HashMap<String, String>();
		Pattern pattern = Pattern
				.compile("(http|ftp|https|)://[\\S\\.]+[:\\d]?[/\\S]+\\??[\\S=\\S&?]+[^\u4e00-\u9fa5]");
		Matcher matcher = pattern.matcher(strContent);
		while (matcher.find()) {
			String url = matcher.group();
			map.put(url, "<a href=\"" + url + "\">" + url + "</a>");
		}
		for (Iterator<?> iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			strContent = strContent.replace(key, map.get(key));
		}
		return strContent;
	}

	/**
	 * 页面过滤 标签，javascript，style
	 * 
	 * @param inputString
	 * @return
	 */
	public static String filterText(String inputString) {
		String htmlStr = inputString; // 含html标签的字符串
		if (htmlStr == null) {
			htmlStr = "";
			return "";
		}
		String textStr = "";
		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;
		Pattern p_html1;
		Matcher m_html1;
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			String regEx_html1 = "<[^>]+";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			//
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签

			p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
			m_html1 = p_html1.matcher(htmlStr);
			htmlStr = m_html1.replaceAll(""); // 过滤html标签

			textStr = htmlStr;

		} catch (Exception e) {
		}

		return textStr;// 返回文本字符串
	}

	/**
	 * 把html中的&nbsp;替换为空格
	 * 
	 * @param content
	 * @return
	 */
	public static String formartResultContent(String content) {
		if (null == content || "null".equals(content.trim())) {
			content = "";
		}
		return content.replace("&nbsp;", " ");
	}

}
