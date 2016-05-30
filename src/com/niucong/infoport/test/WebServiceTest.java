package com.niucong.infoport.test;

import java.util.ArrayList;

import android.test.AndroidTestCase;

import com.niucong.infoport.bean.ViewNewBean;
import com.niucong.infoport.net.WebServicesData;
import com.niucong.infoport.util.L;

public class WebServiceTest extends AndroidTestCase {
	private static final String TAG = "WebServiceTest";

	/**
	 * 注册
	 * 
	 * @throws Exception
	 */
	public void sendPwd2CustomerTest() throws Exception {
		L.i(TAG, "sendPwd2CustomerTest...");
		String str = WebServicesData.sendPwd2Customer("18610041093");
		L.d(TAG, "sendPwd2CustomerTest str=" + str);
	}

	/**
	 * 登录
	 * 
	 * @throws Exception
	 */
	public void loginTest() throws Exception {
		// L.i(TAG, "loginTest...");
		// // 13802237563  18611127763(ddd1980)
		// String[] strs = WebServicesData.selectAdminHttp("18610041095",
		// "514243");
		// // string[0]用户密码,string[1]用户编号,string[2]用户名
		// // gctz2013 12478 admin
		// L.d(TAG, "loginTest password=" + strs[0] + ",id=" + strs[1] +
		// ",name="
		// + strs[2]);
	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	public void newMsgTest() throws Exception {
		L.i(TAG, "newMsgTest...");
		String[] strs = WebServicesData.getMsgPrompt("0", "");
		L.d(TAG, "newMsgTest = " + strs[0] + " = " + strs[1] + " = " + strs[2]
				+ " = " + strs[3]);
	}

	/**
	 * 权限验证
	 * 
	 * @throws Exception
	 */
	public void CheckPermittedTest() throws Exception {
		L.i(TAG, "CheckPermittedTest...");
		String[] strs = WebServicesData.checkPermitted("12478");
		for (int i = 0; i < strs.length; i++) {
			L.d(TAG, "CheckPermittedTest password" + i + "=" + strs[i]);
		}
	}

	/**
	 * 类型编号 类型名称 备注 今日头条：001106 独家视点：001103 002201 宏观信息 002202 行业信息 002203 今日策略
	 * 002204 个股信息 030801 信息港先锋版 新闻类列表显示 030802 题材起爆器 新闻类列表显示 030803 信息港体验版
	 * 新闻类列表显示 030804 信息港专业版 新闻类列表显示 030805 信息港实战版 新闻类列表显示 03080500 信息港实战池
	 * 股票池类列表显示,其权限隶属于实战版 030806 信息港组合版 新闻类列表显示 03080600 信息港组合池
	 * 股票池类列表显示,其权限隶属于组合版
	 * 
	 * @throws Exception
	 */
	public void ViewNewsTest() throws Exception {
		ArrayList<ViewNewBean> list = WebServicesData.viewNews("001103");
		for (int i = 0; i < list.size(); i++) {
			ViewNewBean bean = list.get(i);
			L.i(TAG, "ViewNewsTest " + i + " num=" + bean.getNum() + ",id="
					+ bean.getId() + ",title=" + bean.getTitle() + ",addtime="
					+ bean.getAddTime() + ",nowtime=" + bean.getNowTime());
		}
	}

	/**
	 * 阅读新闻
	 * 
	 * @throws Exception
	 */
	public void ReadNewsTest() throws Exception {
		L.i(TAG, "ReadNewsTest...");
		String[] strs = WebServicesData.readNews("97793");
		// string[0]；新闻编号；string[1]:新闻类型；string[2]:浏览次数；string[3]:标题;
		// string[5]:新闻内容；

		L.d(TAG, "ReadNewsTest id=" + strs[0] + ",type=" + strs[1] + ",num="
				+ strs[2] + ",title=" + strs[3] + ",context=" + strs[4]);
	}

}
