package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderAllResponseDto {
	private String username;
    private String housename;
    private String address;
    private String tel;
    private String bed;
    private int people;
    private String bookingstatus;
    private LocalDateTime checkindate;
    private LocalDateTime checkoutdate;
    private BigDecimal totalamount;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHousename() {
		return housename;
	}
	public void setHousename(String housename) {
		this.housename = housename;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getBed() {
		return bed;
	}
	public void setBed(String bed) {
		this.bed = bed;
	}
	public int getPeople() {
		return people;
	}
	public void setPeople(int people) {
		this.people = people;
	}
	public String getBookingstatus() {
		return bookingstatus;
	}
	public void setBookingstatus(String bookingstatus) {
		this.bookingstatus = bookingstatus;
	}
	public LocalDateTime getCheckindate() {
		return checkindate;
	}
	public void setCheckindate(LocalDateTime checkindate) {
		this.checkindate = checkindate;
	}
	public LocalDateTime getCheckoutdate() {
		return checkoutdate;
	}
	public void setCheckoutdate(LocalDateTime checkoutdate) {
		this.checkoutdate = checkoutdate;
	}
	public BigDecimal getTotalamount() {
		return totalamount;
	}
	public void setTotalamount(BigDecimal totalamount) {
		this.totalamount = totalamount;
	}

}