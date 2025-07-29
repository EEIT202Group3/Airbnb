package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class OrderDetailResponseDto {

 	private String bookingid;       	
    private Integer reservationid;     
    private String username;          
    private String housename;   
    private String address;        
    private String tel;          
    private String bed;             
    private Integer people;         
    private LocalDateTime checkindate;    
    private LocalDateTime checkoutdate;    
    private Integer locationid;        
    private String paymentid;         
    private Integer price;             
    private BigDecimal total;             
    private LocalDateTime paidtime;       
    private String bookingstatus;   
    private String bookingmethod;    
    private String mentstatus;
	public String getBookingid() {
		return bookingid;
	}
	public void setBookingid(String bookingid) {
		this.bookingid = bookingid;
	}
	public Integer getReservationid() {
		return reservationid;
	}
	public void setReservationid(Integer reservationid) {
		this.reservationid = reservationid;
	}
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
	public Integer getPeople() {
		return people;
	}
	public void setPeople(Integer people) {
		this.people = people;
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
	public Integer getLocationid() {
		return locationid;
	}
	public void setLocationid(Integer locationid) {
		this.locationid = locationid;
	}
	public String getPaymentid() {
		return paymentid;
	}
	public void setPaymentid(String paymentid) {
		this.paymentid = paymentid;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public LocalDateTime getPaidtime() {
		return paidtime;
	}
	public void setPaidtime(LocalDateTime paidtime) {
		this.paidtime = paidtime;
	}
	public String getBookingstatus() {
		return bookingstatus;
	}
	public void setBookingstatus(String bookingstatus) {
		this.bookingstatus = bookingstatus;
	}
	public String getBookingmethod() {
		return bookingmethod;
	}
	public void setBookingmethod(String bookingmethod) {
		this.bookingmethod = bookingmethod;
	}
	public String getMentstatus() {
		return mentstatus;
	}
	public void setMentstatus(String mentstatus) {
		this.mentstatus = mentstatus;
	}
}