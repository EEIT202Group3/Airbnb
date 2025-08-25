package com.EEITG3.Airbnb.users.dto;

public class MonthlyRegist {
	private String month;
	private Integer registrations;
	public MonthlyRegist() {
		super();
	}
	public MonthlyRegist(String month, Integer registrations) {
		super();
		this.month = month;
		this.registrations = registrations;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Integer getRegistrations() {
		return registrations;
	}
	public void setRegistrations(Integer registrations) {
		this.registrations = registrations;
	}
	@Override
	public String toString() {
		return "MonthlyRegist [month=" + month + ", Registrations=" + registrations + "]";
	}
}
