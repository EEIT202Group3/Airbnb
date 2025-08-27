package com.EEITG3.Airbnb.users.dto;

import java.math.BigDecimal;

public class YearlyRevenue {
	private String month;
	private BigDecimal revenue;
	public YearlyRevenue() {
		super();
	}
	public YearlyRevenue(String month, BigDecimal revenue) {
		super();
		this.month = month;
		this.revenue = revenue;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public BigDecimal getRevenue() {
		return revenue;
	}
	public void setRevenue(BigDecimal revenue) {
		this.revenue = revenue;
	}
	@Override
	public String toString() {
		return "MonthlyRevenue [month=" + month + ", revenue=" + revenue + "]";
	}
}
