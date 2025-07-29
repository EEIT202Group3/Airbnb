package com.EEITG3.Airbnb.payMent.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


//對應資料庫中的 orderlist 資料表
@Entity
@Table(name = "orderlist")
public class Order implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		@Column(name = "booking_id")
	 	private String bookingId;       	    // 主鍵
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "list_id")
		private LisBean listing;              // 房源ID (外來鍵)
	    
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "customer_id")
		private Customer customer;
	    
	    @Column(name = "reservation_id")
	    private Integer reservationId;     // 租車ID (外來鍵)
	    
	    @Column(name = "username")
	    private String username;          // 房源名稱
	    
	    @Column(name = "house_name")
	    private String houseName;          // 房源名稱
	    
	    @Column(name = "address")
	    private String address;             // 房源地址
	    
	    @Column(name = "tel")
	    private String tel;                 // 房東電話
	    
	    @Column(name = "bed")
	    private String bed;                 // 房型

	    @Column(name = "people")
	    private Integer people;             // 人數
	    
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")			//取時間到秒
	    @Column(name = "checkin_date")
	    private LocalDateTime checkinDate;     // 入住/租車日期
	    
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	    @Column(name = "checkout_date")
	    private LocalDateTime checkoutDate;    // 退房/歸還日期
	    
	    @Column(name = "location_id")
	    private Integer locationId;        // 取車地址

	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	    @Column(name = "created_time")
	    private LocalDateTime createdTime;     // 訂單產生日
	    
	    @Column(name = "booking_status")
	    private String bookingStatus;      // 訂單狀態

	    @Column(name = "payment_id")
	    private String paymentId;          // 付款編號
	    
	    @Column(name = "price")
	    private Integer price;              // 房租金額
	    
	    @Column(name = "total_amount")
	    private BigDecimal totalAmount;    // 租車金額
	    
	    @Column(name = "total")
	    private BigDecimal total;              // 總金額
	    
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	    @Column(name = "paid_time")
	    private LocalDateTime paidTime;        // 付款時間
	    
	    @Column(name = "booking_method")
	    private String bookingMethod;      // 付款方式
	    
	    @Column(name = "ment_status")
	    private String mentStatus;         // 付款狀態
		
	    
	    
	    public Order() {}
		
		
		
		public String getBookingid() {return bookingId;}
		public void setBookingid(String bookingId) {this.bookingId = bookingId;}
		
		public LisBean getListing() {return listing;}
		public void setListing(LisBean listing) {this.listing = listing;}
		
		public Customer getCustomer() {return customer;}
		public void setCustomer(Customer customer) {this.customer = customer;}
		
		public Integer getReservationid() {return reservationId;}
		public void setReservationid(Integer reservationId) {this.reservationId = reservationId;}
		
		public String getUsername() {return username;}
		public void setUsername(String username) {this.username = username;}
		
		public String getHousename() {return houseName;}
		public void setHousename(String houseName) {this.houseName = houseName;}
		
		public String getAddress() {return address;}
		public void setAddress(String address) {this.address = address;}
		
		public String getTel() {return tel;}
		public void setTel(String tel) {this.tel = tel;}
		
		public String getBed() {return bed;}
		public void setBed(String bed) {this.bed = bed;}
		
		public Integer getPeople() {return people;}
		public void setPeople(Integer people) {this.people = people;}
		
		public LocalDateTime getCheckindate() {return checkinDate;}
		public void setCheckindate(LocalDateTime checkinDate) {this.checkinDate = checkinDate;}
		
		public LocalDateTime getCheckoutdate() {return checkoutDate;}
		public void setCheckoutdate(LocalDateTime checkoutDate) {this.checkoutDate = checkoutDate;}
		
		public Integer getLocationid() {return locationId;}
		public void setLocationid(Integer locationId) {this.locationId = locationId;}
		
		public LocalDateTime getCreatedtime() {return createdTime;}
		public void setCreatedtime(LocalDateTime createdTime) {this.createdTime = createdTime;}
		
		public String getBookingstatus() {return bookingStatus;}
		public void setBookingstatus(String bookingStatus) {this.bookingStatus = bookingStatus;}
		
		public String getPaymentid() {return paymentId;}
		public void setPaymentid(String paymentId) {this.paymentId = paymentId;}
		
		public Integer getPrice() {return price;}
		public void setPrice(Integer price) {this.price = price;}
		
		public BigDecimal getTotalamount() {return totalAmount;}
		public void setTotalamount(BigDecimal totalAmount) {this.totalAmount = totalAmount;}
		
		public BigDecimal getTotal() {return total;}
		public void setTotal(BigDecimal total) {this.total = total;}
		
		public LocalDateTime getPaidtime() {return paidTime;}
		public void setPaidtime(LocalDateTime paidTime) {this.paidTime = paidTime;}
		
		public String getBookingmethod() {return bookingMethod;}
		public void setBookingmethod(String bookingMethod) {this.bookingMethod = bookingMethod;}
		
		public String getMentstatus() {return mentStatus;}
		public void setMentstatus(String mentStatus) {this.mentStatus = mentStatus;}
		

		

		
	
	
}