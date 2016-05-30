package com.niucong.infoport.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import com.niucong.infoport.bean.StockPoolBean;
import com.niucong.infoport.bean.ViewNewBean;
import com.niucong.infoport.util.L;

public class WebServicesData {
	private static final String TAG = "WebServicesData";

	public static String URL = "http://android.cf69.com/newversion/ForAndroid.asmx";
	private static String VERSIONURL = "http://android.cf69.com/newversion/version.xml";

	/**
	 * 快速注册用户
	 * 
	 * @param phoneno
	 * @return 返回值中包含”ok:1”字样，表示注册成功
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String sendPwd2Customer(String phoneno) throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("phoneno", phoneno);
		// SoapObject soapObject = webservices("SendPwd2Customer", map);
		// return soapObject.getPropertyAsString(0);
		return sendPwd2CustomerHttp(phoneno);
	}

	/**
	 * 快速注册用户
	 * 
	 * @param phoneno
	 * @return 返回值中包含”ok:1”字样，表示注册成功
	 * @throws ProtocolException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String sendPwd2CustomerHttp(String phoneno)
			throws MalformedURLException, ProtocolException, IOException {
		String result = getRequest(URL + "/SendPwd2Customer?phoneno=" + phoneno);
		return result.substring(result.indexOf("/\">") + 3,
				result.lastIndexOf("<"));
	}

	/**
	 * 用户名验证
	 * 
	 * @param mgNO
	 * @param pwd
	 * @return string[0]用户密码,string[1]用户编号,string[2]用户名
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] selectAdmin(String mgNO, String pwd)
			throws IOException, XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("mgNO", mgNO);
		// map.put("pwd", pwd);
		// SoapObject soapObject = webservices("SelectAdmin", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2) };
		return selectAdminHttp(mgNO, pwd);
	}

	/**
	 * 用户名验证
	 * 
	 * @param mgNO
	 * @param pwd
	 * @return string[0]用户编号,string[1]姓名,string[2]性别,string[3]电话
	 * @throws ProtocolException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] selectAdminHttp(String mgNO, String pwd)
			throws MalformedURLException, ProtocolException, IOException {
		String result = getRequest(URL + "/SelectAdmin?mgNO=" + mgNO + "&pwd="
				+ pwd);
		return parseArrayOfString(result);
	}

	/**
	 * 权限验证
	 * 
	 * @param UsrSno
	 * @return string[0]；用户拥有的新闻类型权限
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] checkPermitted(String UsrSno) throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("UsrSno", UsrSno);
		// SoapObject soapObject = webservices("CheckPermitted", map);
		// int count = soapObject.getPropertyCount();
		// String[] strs = new String[count];
		// for (int i = 0; i < count; i++) {
		// strs[i] = soapObject.getPropertyAsString(i);
		// }
		// return strs;
		return checkPermittedHttp(UsrSno);
	}

	/**
	 * 权限验证
	 * 
	 * @param UsrSno
	 * @return string[0]；用户拥有的新闻类型权限
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] checkPermittedHttp(String UsrSno)
			throws IOException, XmlPullParserException, Exception {
		String result = getRequest(URL + "/CheckPermitted?UsrSno=" + UsrSno);
		return parseArrayOfString(result);
	}

	/**
	 * 读取广告图片地址
	 * 
	 * @return string[0]：广告图片地址；string[1]：广告链接地址
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] getAdInfo() throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// SoapObject soapObject = webservices("GetADInfo", map);
		// int count = soapObject.getPropertyCount();
		// String[] strs = new String[count];
		// for (int i = 0; i < count; i++) {
		// strs[i] = soapObject.getPropertyAsString(i);
		// }
		// return strs;
		return getAdInfoHttp();
	}

	/**
	 * 读取广告图片地址
	 * 
	 * @return string[0]：广告图片地址；string[1]：广告链接地址
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] getAdInfoHttp() throws IOException,
			XmlPullParserException, Exception {
		String result = getRequest(URL + "/GetADInfo");
		return parseArrayOfString(result);
	}

	/**
	 * 个人中心
	 * 
	 * @param usrsno
	 * @return string[0]：序号；string[1]：权限名称；string[2]:权限起始时间;string[3]:权限终止时间
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] getUserInfo(String usrsno)
			throws XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("usrsno", usrsno);
		// SoapObject soapObject = webservices("GetUserInfo", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2) };
		return getUserInfoHttp(usrsno);
	}

	/**
	 * 个人中心
	 * 
	 * @param usrsno
	 * @return string[0],序号；string[1]：权限名称；string[2]:权限起始时间;string[3]:权限终止时间
	 * @throws ProtocolException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] getUserInfoHttp(String usrsno)
			throws MalformedURLException, ProtocolException, IOException {
		String result = getRequest(URL + "/GetUserInfo?usrsno=" + usrsno);
		return parseArrayOfString(result);
	}

	/**
	 * 获取最新消息
	 * 
	 * @param MaxSno
	 *            最后一条查看的记录号
	 * @param UserSno
	 *            用户编号
	 * @return string[0],序号；string[1]：新闻编号；string[2]:标题;string[3]:新闻类型
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] getMsgPrompt(String MaxSno, String UserSno)
			throws XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("MaxSno", MaxSno);
		// map.put("UserSno", UserSno);
		// SoapObject soapObject = webservices("GetMsgPrompt", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2) };
		return getMsgPromptHttp(MaxSno, UserSno);
	}

	/**
	 * 获取最新消息
	 * 
	 * @param MaxSno
	 *            最后一条查看的记录号
	 * @param UserSno
	 *            用户编号
	 * @return string[0],序号；string[1]：新闻编号；string[2]:标题;string[3]:新闻类型
	 * @throws ProtocolException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] getMsgPromptHttp(String MaxSno, String UserSno)
			throws MalformedURLException, ProtocolException, IOException {
		String result = getRequest(URL + "/GetMsgPrompt?MaxSno=" + MaxSno
				+ "&UserSno=" + UserSno);
		return parseArrayOfString(result);
	}

	/**
	 * 浏览新闻列表下的新闻
	 * 
	 * 类型编号 类型名称 备注 今日头条：001106 独家视点：001103 002201 宏观信息 002202 行业信息 002203 今日策略
	 * 002204 个股信息 030801 信息港先锋版 新闻类列表显示 030802 题材起爆器 新闻类列表显示 030803 信息港体验版
	 * 新闻类列表显示 030804 信息港专业版 新闻类列表显示 030805 信息港实战版 新闻类列表显示 03080500 信息港实战池
	 * 股票池类列表显示,其权限隶属于实战版 030806 信息港组合版 新闻类列表显示 03080600 信息港组合池
	 * 股票池类列表显示,其权限隶属于组合版
	 * 
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static ArrayList<ViewNewBean> viewNews(String NewsTypeId)
			throws IOException, XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("NewsTypeId", NewsTypeId);
		// SoapObject soapObject = webservices("ViewNews ", map);
		//
		// int count = soapObject.getPropertyCount();
		// int size = count / 5;
		// ArrayList<ViewNewBean> list = new ArrayList<ViewNewBean>();
		// for (int i = 0; i < size; i++) {
		// ViewNewBean bean = new ViewNewBean();
		// bean.setNum(soapObject.getPropertyAsString(i * 5 + 0));
		// bean.setId(soapObject.getPropertyAsString(i * 5 + 1));
		// bean.setTitle(soapObject.getPropertyAsString(i * 5 + 2));
		// bean.setAddTime(soapObject.getPropertyAsString(i * 5 + 3));
		// bean.setNowTime(soapObject.getPropertyAsString(i * 5 + 4));
		// list.add(bean);
		// }
		// return list;
		return viewNewsHttp(NewsTypeId);
	}

	/**
	 * 浏览新闻列表下的新闻
	 * 
	 * 类型编号 类型名称 备注 今日头条：001106 独家视点：001103 002201 宏观信息 002202 行业信息 002203 今日策略
	 * 002204 个股信息 030801 信息港先锋版 新闻类列表显示 030802 题材起爆器 新闻类列表显示 030803 信息港体验版
	 * 新闻类列表显示 030804 信息港专业版 新闻类列表显示 030805 信息港实战版 新闻类列表显示 03080500 信息港实战池
	 * 股票池类列表显示,其权限隶属于实战版 030806 信息港组合版 新闻类列表显示 03080600 信息港组合池
	 * 股票池类列表显示,其权限隶属于组合版
	 * 
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static ArrayList<ViewNewBean> viewNewsHttp(String NewsTypeId)
			throws IOException, XmlPullParserException, Exception {
		String result = getRequest(URL + "/ViewNews?NewsTypeId=" + NewsTypeId);
		String[] strs = parseArrayOfString(result);
		ArrayList<ViewNewBean> list = new ArrayList<ViewNewBean>();
		if (strs == null) {
			return list;
		}
		int count = strs.length;
		int size = count / 5;
		for (int i = 0; i < size; i++) {
			ViewNewBean bean = new ViewNewBean();
			bean.setNum(strs[i * 5 + 0]);
			bean.setId(strs[i * 5 + 1]);
			bean.setTitle(strs[i * 5 + 2]);
			bean.setAddTime(strs[i * 5 + 3]);
			bean.setNowTime(strs[i * 5 + 4]);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 股票池
	 * 
	 * 类型编号 类型名称 备注 今日头条：001106 独家视点：001103 002201 宏观信息 002202 行业信息 002203 今日策略
	 * 002204 个股信息 030801 信息港先锋版 新闻类列表显示 030802 题材起爆器 新闻类列表显示 030803 信息港体验版
	 * 新闻类列表显示 030804 信息港专业版 新闻类列表显示 030805 信息港实战版 新闻类列表显示 03080500 信息港实战池
	 * 股票池类列表显示,其权限隶属于实战版 030806 信息港组合版 新闻类列表显示 03080600 信息港组合池
	 * 股票池类列表显示,其权限隶属于组合版
	 * 
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static ArrayList<StockPoolBean> stockPool(String NewsTypeId)
			throws IOException, XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("NewsTypeId", NewsTypeId);
		// SoapObject soapObject = webservices("GetStockPool ", map);
		// ArrayList<StockPoolBean> list = new ArrayList<StockPoolBean>();
		// if (soapObject == null) {
		// return list;
		// }
		//
		// int count = soapObject.getPropertyCount();
		// int size = count / 9;
		// for (int i = 0; i < size; i++) {
		// StockPoolBean bean = new StockPoolBean();
		// bean.setNum(soapObject.getPropertyAsString(i * 9 + 0));
		// bean.setId(soapObject.getPropertyAsString(i * 9 + 1));
		// bean.setCode(soapObject.getPropertyAsString(i * 9 + 2));
		// bean.setName(soapObject.getPropertyAsString(i * 9 + 3));
		// bean.setInTime(soapObject.getPropertyAsString(i * 9 + 4));
		// bean.setInPrice(soapObject.getPropertyAsString(i * 9 + 5));
		// bean.setOutTime(soapObject.getPropertyAsString(i * 9 + 6));
		// bean.setOutPrice(soapObject.getPropertyAsString(i * 9 + 7));
		// bean.setUpDown(soapObject.getPropertyAsString(i * 9 + 8));
		// list.add(bean);
		// }
		// return list;
		return stockPoolHttp(NewsTypeId);
	}

	/**
	 * 股票池
	 * 
	 * 类型编号 类型名称 备注 今日头条：001106 独家视点：001103 002201 宏观信息 002202 行业信息 002203 今日策略
	 * 002204 个股信息 030801 信息港先锋版 新闻类列表显示 030802 题材起爆器 新闻类列表显示 030803 信息港体验版
	 * 新闻类列表显示 030804 信息港专业版 新闻类列表显示 030805 信息港实战版 新闻类列表显示 03080500 信息港实战池
	 * 股票池类列表显示,其权限隶属于实战版 030806 信息港组合版 新闻类列表显示 03080600 信息港组合池
	 * 股票池类列表显示,其权限隶属于组合版
	 * 
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static ArrayList<StockPoolBean> stockPoolHttp(String NewsTypeId)
			throws IOException, XmlPullParserException, Exception {
		String result = getRequest(URL + "/GetStockPool?NewsTypeId="
				+ NewsTypeId);
		String[] strs = parseArrayOfString(result);
		ArrayList<StockPoolBean> list = new ArrayList<StockPoolBean>();
		if (strs == null) {
			return list;
		}
		int count = strs.length;
		int size = count / 9;
		for (int i = 0; i < size; i++) {
			StockPoolBean bean = new StockPoolBean();
			bean.setNum(strs[i * 9 + 0]);
			bean.setId(strs[i * 9 + 1]);
			bean.setCode(strs[i * 9 + 2]);
			bean.setName(strs[i * 9 + 3]);
			bean.setInTime(strs[i * 9 + 4]);
			bean.setInPrice(strs[i * 9 + 5]);
			bean.setOutTime(strs[i * 9 + 6]);
			bean.setOutPrice(strs[i * 9 + 7]);
			bean.setUpDown(strs[i * 9 + 8]);
			list.add(bean);
		}
		return list;
	}

	/**
	 * 阅读新闻
	 * 
	 * @param NewsSno
	 * @return 
	 *         string[0]；新闻编号；string[1]:新闻类型；string[2]:浏览次数；string[3]:标题：string[4
	 *         ]:新闻内容；
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] readNews(String NewsSno) throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("NewsSno", NewsSno);
		// SoapObject soapObject = webservices("ReadNews", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2),
		// soapObject.getPropertyAsString(3),
		// soapObject.getPropertyAsString(4) };
		return readNewsHttp(NewsSno);
	}

	/**
	 * 阅读新闻
	 * 
	 * @param NewsSno
	 * @return 
	 *         string[0]；新闻编号；string[1]:新闻类型；string[2]:浏览次数；string[3]:标题：string[4
	 *         ]:新闻内容；
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] readNewsHttp(String NewsSno) throws IOException,
			XmlPullParserException, Exception {
		String result = getRequest(URL + "/ReadNews?NewsSno=" + NewsSno);
		return parseArrayOfString(result);
	}

	/**
	 * 读取实战池
	 * 
	 * @param NewsSno
	 *            新闻编号
	 * @return string[0],序号；string[1]：股票代码；string[2]: 股票名称; string[3]:
	 *         投资风格;string[4], 止损参考；string[5]：入池时间； string[6]: 入池价格;string[7]:
	 *         出池时间;string[8], 出池价格； string[9]：仓位提示；string[10]: 涨跌幅度;string[11]:
	 *         投资亮点; string[12]: 调出理由
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] stockInfo(String NewsSno) throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("NewsSno", NewsSno);
		// SoapObject soapObject = webservices("GetStockInfo", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2),
		// soapObject.getPropertyAsString(3),
		// soapObject.getPropertyAsString(4),
		// soapObject.getPropertyAsString(5),
		// soapObject.getPropertyAsString(6),
		// soapObject.getPropertyAsString(7),
		// soapObject.getPropertyAsString(8),
		// soapObject.getPropertyAsString(9),
		// soapObject.getPropertyAsString(10),
		// soapObject.getPropertyAsString(11),
		// soapObject.getPropertyAsString(12) };
		return stockInfoHttp(NewsSno);
	}

	/**
	 * 读取实战池
	 * 
	 * @param NewsSno
	 *            新闻编号
	 * @return string[0],序号；string[1]：股票代码；string[2]: 股票名称; string[3]:
	 *         投资风格;string[4], 止损参考；string[5]：入池时间； string[6]: 入池价格;string[7]:
	 *         出池时间;string[8], 出池价格； string[9]：仓位提示；string[10]: 涨跌幅度;string[11]:
	 *         投资亮点; string[12]: 调出理由
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] stockInfoHttp(String NewsSno) throws IOException,
			XmlPullParserException, Exception {
		String result = getRequest(URL + "/GetStockInfo?NewsSno=" + NewsSno);
		return parseArrayOfString(result);
	}

	/**
	 * 读取组合池
	 * 
	 * @param NewsSno
	 *            新闻编号
	 * @return string[0],序号；string[1]：股票代码；string[2]: 股票名称;string[3]: 投资风格
	 *         string[4], 最新评级；string[5]：入池时间；string[6: 入池价格;string[7]: 出池时间
	 *         string[8], 出池价格；string[9]：入池逻辑；string[10]: 操作建议;string[11]: 调出理由
	 *         string[12]: 风险提示
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	public static String[] stockComInfo(String NewsSno) throws IOException,
			XmlPullParserException, Exception {
		// HashMap<String, String> map = new HashMap<String, String>();
		// map.put("NewsSno", NewsSno);
		// SoapObject soapObject = webservices("GetStockComInfo", map);
		// return new String[] { soapObject.getPropertyAsString(0),
		// soapObject.getPropertyAsString(1),
		// soapObject.getPropertyAsString(2),
		// soapObject.getPropertyAsString(3),
		// soapObject.getPropertyAsString(4),
		// soapObject.getPropertyAsString(5),
		// soapObject.getPropertyAsString(6),
		// soapObject.getPropertyAsString(7),
		// soapObject.getPropertyAsString(8),
		// soapObject.getPropertyAsString(9),
		// soapObject.getPropertyAsString(10),
		// soapObject.getPropertyAsString(11),
		// soapObject.getPropertyAsString(12) };
		return stockComInfoHttp(NewsSno);
	}

	/**
	 * 读取组合池
	 * 
	 * @param NewsSno
	 *            新闻编号
	 * @return string[0],序号；string[1]：股票代码；string[2]: 股票名称;string[3]: 投资风格
	 *         string[4], 最新评级；string[5]：入池时间；string[6: 入池价格;string[7]: 出池时间
	 *         string[8], 出池价格；string[9]：入池逻辑；string[10]: 操作建议;string[11]: 调出理由
	 *         string[12]: 风险提示
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	private static String[] stockComInfoHttp(String NewsSno)
			throws IOException, XmlPullParserException, Exception {
		String result = getRequest(URL + "/GetStockComInfo?NewsSno=" + NewsSno);
		return parseArrayOfString(result);
	}

	/**
	 * 获取版本信息
	 * 
	 * @param str
	 *            <update><version>1</version><name>gcinfoport.apk</name><
	 *            uploadurl>http://andown.cf69.com/gcinfoport.apk</uploadurl><
	 *            HttpConnSoapUrl
	 *            >http://121.15.5.252:802/ForAndroid.asmx</HttpConnSoapUrl
	 *            ></update>
	 * @return string[0]version版本号,string[1]name,string[2]uploadurl,string[3]
	 * @throws IOException
	 * @throws ProtocolException
	 * @throws MalformedURLException
	 */
	public static String[] getVersionInfo() throws MalformedURLException,
			ProtocolException, IOException {
		String str = getRequest(VERSIONURL);
		String[] strs = new String[4];
		int start0 = str.indexOf("<version>") + "<version>".length();
		int end0 = str.lastIndexOf("</version>");
		strs[0] = str.substring(start0, end0);
		int start1 = str.indexOf("<name>") + "<name>".length();
		int end1 = str.lastIndexOf("</name>");
		strs[1] = str.substring(start1, end1);
		int start2 = str.indexOf("<uploadurl>") + "<uploadurl>".length();
		int end2 = str.lastIndexOf("</uploadurl>");
		strs[2] = str.substring(start2, end2);
		int start3 = str.indexOf("<HttpConnSoapUrl>")
				+ "<HttpConnSoapUrl>".length();
		int end3 = str.lastIndexOf("</HttpConnSoapUrl>");
		strs[3] = str.substring(start3, end3);
		return strs;
	}

	/**
	 * webservices请求
	 * 
	 * @param methodName
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 * @throws Exception
	 */
	// private static SoapObject webservices(String methodName,
	// HashMap<String, String> map) throws IOException,
	// XmlPullParserException, Exception {
	// L.i(TAG, "methodName=" + methodName);
	//
	// // （1）指定webservice的命名空间和调用的方法名
	// SoapObject request = new SoapObject(NAMESPACE, methodName);
	// // （2）设置调用方法的参数值，如果没有参数，可以省略
	// if (map != null && map.size() > 0) {
	// for (Iterator<?> iterator = map.keySet().iterator(); iterator
	// .hasNext();) {
	// String key = (String) iterator.next();
	// request.addProperty(key, map.get(key));
	// }
	// }
	// // （3）生成调用Webservice方法的SOAP请求信息
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// envelope.bodyOut = request;
	// envelope.dotNet = true;
	// // （4）通过HttpTransportsSE类的构造方法可以指定WebService的WSDL文档的URL
	// HttpTransportSE ht = new HttpTransportSE(URL);
	// // (5)使用call方法调用WebService方法
	// envelope.setOutputSoapObject(request);
	// ht.call(NAMESPACE + "/" + methodName, envelope);
	// // （6）使用getResponse方法获得WebService方法的返回结果
	// SoapObject result = (SoapObject) envelope.bodyIn;
	// L.i(TAG, "request=" + request);
	// L.getLongLog(TAG, "webservices", "result=" + result);
	//
	// SoapObject soapObject = (SoapObject) envelope.getResponse();
	// L.i(TAG, "soapObject=" + soapObject);
	// return soapObject;
	// }

	/**
	 * 客户端调用API的GET请求方式
	 * 
	 * @param urlstr
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	private static String getRequest(String urlstr)
			throws MalformedURLException, ProtocolException, IOException {
		L.d(TAG, "getRequest : " + urlstr.length() + ",urlstr = " + urlstr);

		HttpURLConnection conn = null;
		InputStream is = null;

		URL url = new URL(urlstr);
		conn = (HttpURLConnection) url.openConnection();
		// 设置连接超时时间
		conn.setConnectTimeout(10 * 1000);
		// 设置数据读取超时时间
		conn.setReadTimeout(25 * 1000);
		conn.setRequestMethod("GET");// 以get方式发起请求
		int code = conn.getResponseCode();
		L.i(TAG, "getRequest : code = " + code);
		L.d(TAG, "getRequest : length = " + conn.getContentLength());

		is = conn.getInputStream();// 得到网络返回的输入流
		return readData(is);
	}

	/**
	 * 读取请求数据
	 * 
	 * @param inSream
	 * @param charsetName
	 * @return
	 * @throws IOException
	 */
	private static String readData(InputStream inSream) throws IOException {
		ByteArrayOutputStream outStream = null;
		String str = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = inSream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			byte[] data = outStream.toByteArray();
			str = new String(data, "utf-8");
		} catch (OutOfMemoryError e) {
		} finally {
			if (outStream != null)
				outStream.close();
			if (inSream != null)
				inSream.close();
		}
		return str;
	}

	/**************
	 * 解析ArrayOfString
	 * 
	 * @param str
	 * @return
	 */
	private static String[] parseArrayOfString(String str) {
		L.d(TAG, "parseArrayOfString str=" + str);
		if (str != null && str.contains("<string>")
				&& str.contains("</string>")) {
			// str = str.replace("\n", "");
			// str = str.replace(" ", "");
			// str = str.replace("	", "");
			// str = str.trim();
			L.i(TAG, "parseArrayOfString str=" + str);
			int start = str.indexOf("<string>") + "<string>".length();
			int end = str.lastIndexOf("</string>");
			str = str.substring(start, end);
			str = str.replace("</string>", "");
			str = str.replace("<string />", "<string>");
			if (str.contains("<string>")) {
				return str.split("<string>");
			} else {
				return new String[] { str };
			}
		} else {
			return null;
		}
	}

	// public static Document getRSS(Context context, String url)
	// throws ParserConfigurationException, ClientProtocolException,
	// IOException, IllegalStateException, SAXException {
	// Document doc;
	// DocumentBuilder builder = DocumentBuilderFactory.newInstance()
	// .newDocumentBuilder();
	// DefaultHttpClient client = new DefaultHttpClient();
	// HttpGet request = new HttpGet(url);
	// HttpResponse response = client.execute(request);
	// doc = builder.parse(response.getEntity().getContent());
	// return doc;
	// }
	//
	// public static ArrayList<String> getEpisodesFromRSS(Context context,
	// Document feed) {
	// ArrayList<String> episodes = new ArrayList<String>();
	// NodeList items = feed.getElementsByTagName("ArrayOfString");
	// for (int i = 0; i < items.getLength(); i++) {
	// Element el = (Element) items.item(i);
	// NodeList node = el.getElementsByTagName("String");
	// episodes.add(node.item(0).getFirstChild().getNodeValue());
	// }
	// return episodes;
	// }
}
