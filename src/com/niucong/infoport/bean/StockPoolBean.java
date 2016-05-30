package com.niucong.infoport.bean;

public class StockPoolBean {

	// 序号
	private String num;
	// 新闻编号
	private String id;
	// 股权代码
	private String code;
	// 股池名称
	private String name;
	// 入池时间
	private String inTime;
	// 入池价格
	private String inPrice;
	// 出池时间
	private String outTime;
	// 出池价格
	private String outPrice;
	// 涨跌幅
	private String upDown;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInTime() {
		return inTime.trim();
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getInPrice() {
		return inPrice;
	}

	public void setInPrice(String inPrice) {
		this.inPrice = inPrice;
	}

	public String getOutTime() {
		return outTime;
	}

	public void setOutTime(String outTime) {
		this.outTime = outTime;
	}

	public String getOutPrice() {
		return outPrice;
	}

	public void setOutPrice(String outPrice) {
		this.outPrice = outPrice;
	}

	public String getUpDown() {
		return upDown;
	}

	public void setUpDown(String upDown) {
		this.upDown = upDown;
	}

}
