package com.EEITG3.Airbnb.payMent.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminOrderDetailResponseDto {
	private String bookingId;
	private Integer reservationId;     
	private String username;          
	private String houseName;   
	private String address;        
	private String tel;          
	private String bed;             
	private Integer people;         
	private LocalDateTime checkinDate;    
	private LocalDateTime checkoutDate;    
	private Integer locationId;        
	private String paymentId;         
	private Integer price;        
	private BigDecimal totalAmount;
	private BigDecimal total;             
	private LocalDateTime paidTime;       
	private String bookingStatus;   
	private String bookingMethod;    
	private String mentStatus;
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public Integer getReservationId() {
		return reservationId;
	}
	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHouseName() {
		return houseName;
	}
	public void setHouseName(String houseName) {
		this.houseName = houseName;
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
	public LocalDateTime getCheckinDate() {
		return checkinDate;
	}
	public void setCheckinDate(LocalDateTime checkinDate) {
		this.checkinDate = checkinDate;
	}
	public LocalDateTime getCheckoutDate() {
		return checkoutDate;
	}
	public void setCheckoutDate(LocalDateTime checkoutDate) {
		this.checkoutDate = checkoutDate;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public LocalDateTime getPaidTime() {
		return paidTime;
	}
	public void setPaidTime(LocalDateTime paidTime) {
		this.paidTime = paidTime;
	}
	public String getBookingStatus() {
		return bookingStatus;
	}
	public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
	}
	public String getBookingMethod() {
		return bookingMethod;
	}
	public void setBookingMethod(String bookingMethod) {
		this.bookingMethod = bookingMethod;
	}
	public String getMentStatus() {
		return mentStatus;
	}
	public void setMentStatus(String mentStatus) {
		this.mentStatus = mentStatus;
	}
	
	
}