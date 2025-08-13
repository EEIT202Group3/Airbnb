package com.EEITG3.Airbnb.payMent.dto;
import java.time.LocalDate;


public class OrderRequestDto {

	//前端傳來的訂單資料

	    private Integer listid;
	    private String hostid;
	    private String customerid;
	    private String username;
	    private String housename;
	    private String address;
	    private String tel;
	    private String bed;
	    private LocalDate checkindate;
	    private LocalDate checkoutdate;
	    private int people;
	    private int roomprice;
	    private int days;
	    private int cartotal;
	    private int grandtotal;
	    private String bookingmethod;
		public Integer getListid() {
			return listid;
		}
		public void setListid(Integer listid) {
			this.listid = listid;
		}
		public String getHostid() {
			return hostid;
		}
		public void setHostid(String hostid) {
			this.hostid = hostid;
		}
		public String getCustomerid() {
			return customerid;
		}
		public void setCustomerid(String customerid) {
			this.customerid = customerid;
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
		public LocalDate getCheckindate() {
			return checkindate;
		}
		public void setCheckindate(LocalDate checkindate) {
			this.checkindate = checkindate;
		}
		public LocalDate getCheckoutdate() {
			return checkoutdate;
		}
		public void setCheckoutdate(LocalDate checkoutdate) {
			this.checkoutdate = checkoutdate;
		}
		public int getPeople() {
			return people;
		}
		public void setPeople(int people) {
			this.people = people;
		}
		public int getDays() {
			return days;
		}
		public void setDays(int days) {
			this.days = days;
		}
		public int getRoomprice() {
			return roomprice;
		}
		public void setRoomprice(int roomprice) {
			this.roomprice = roomprice;
		}
		public int getCartotal() {
			return cartotal;
		}
		public void setCartotal(int cartotal) {
			this.cartotal = cartotal;
		}
		public int getGrandtotal() {
			return grandtotal;
		}
		public void setGrandtotal(int grandtotal) {
			this.grandtotal = grandtotal;
		}
		public String getBookingmethod() {
			return bookingmethod;
		}
		public void setBookingmethod(String bookingmethod) {
			this.bookingmethod = bookingmethod;
		}



	}