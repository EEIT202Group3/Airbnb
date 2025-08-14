package com.EEITG3.Airbnb.payMent.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
		
		@Column(name = "host_id")
		private String hostId;

		@Column(name = "customer_id")
		private String customerId;

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
	    private Integer roomPrice;              // 房租金額

	    @Column(name = "total_amount", precision = 10, scale = 0)
	    private BigDecimal carTotal;    // 租車金額

	    @Column(name = "total", precision = 10, scale = 0)
	    private BigDecimal grandTotal;              // 總金額

	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	    @Column(name = "paid_time")
	    private LocalDateTime paidTime;        // 付款時間

	    @Column(name = "booking_method")
	    private String bookingMethod;      // 付款方式

	    @Column(name = "ment_status")
	    private String mentStatus;         // 付款狀態

	    @PrePersist
	    protected void onCreate() {
	        if (createdTime == null) createdTime = LocalDateTime.now();
	        if (bookingStatus == null) bookingStatus = "待入住";
	        if (mentStatus == null) mentStatus = "待付款";
	    }
	    
	    

	    public Order() {}




	    public String getBookingId() { return bookingId; }
	    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

		public LisBean getListing() {return listing;}
		public void setListing(LisBean listing) {this.listing = listing;}

		public String getHostId() {return hostId;}
		public void setHostId(String hostId) {this.hostId = hostId;}
		
		public String getCustomerId() {return customerId;}
		public void setCustomerId(String customerId) {this.customerId = customerId;}

		public Integer getReservationid() {return reservationId;}
		public void setReservationid(Integer reservationId) {this.reservationId = reservationId;}

		public String getUsername() {return username;}
		public void setUsername(String username) {this.username = username;}

		public String getHouseName() {return houseName;}
		public void setHouseName(String houseName) {this.houseName = houseName;}

		public String getAddress() {return address;}
		public void setAddress(String address) {this.address = address;}

		public String getTel() {return tel;}
		public void setTel(String tel) {this.tel = tel;}

		public String getBed() {return bed;}
		public void setBed(String bed) {this.bed = bed;}

		public Integer getPeople() {return people;}
		public void setPeople(Integer people) {this.people = people;}

		public LocalDateTime getCheckinDate() {return checkinDate;}
		public void setCheckinDate(LocalDateTime checkinDate) {this.checkinDate = checkinDate;}

		public LocalDateTime getCheckoutDate() {return checkoutDate;}
		public void setCheckoutDate(LocalDateTime checkoutDate) {this.checkoutDate = checkoutDate;}

		public Integer getLocationid() {return locationId;}
		public void setLocationid(Integer locationId) {this.locationId = locationId;}

		public LocalDateTime getCreatedTime() {return createdTime;}
		public void setCreatedTime(LocalDateTime createdTime) {this.createdTime = createdTime;}

		public String getBookingStatus() {return bookingStatus;}
		public void setBookingStatus(String bookingStatus) {this.bookingStatus = bookingStatus;}

		public String getPaymentId() {return paymentId;}
		public void setPaymentId(String paymentId) {this.paymentId = paymentId;}

		public Integer getRoomPrice() {return roomPrice;}
		public void setRoomPrice(Integer roomPrice) {this.roomPrice = roomPrice;}

		public BigDecimal getCarTotal() {return carTotal;}
		public void setCarTotal(BigDecimal carTotal) {this.carTotal = carTotal;}

		public BigDecimal getGrandTotal() {return grandTotal;}
		public void setGrandTotal(BigDecimal grandTotal) {this.grandTotal = grandTotal;}

		public LocalDateTime getPaidTime() {return paidTime;}
		public void setPaidTime(LocalDateTime paidTime) {this.paidTime = paidTime;}

		public String getBookingMethod() {return bookingMethod;}
		public void setBookingMethod(String bookingMethod) {this.bookingMethod = bookingMethod;}

		public String getMentStatus() {return mentStatus;}
		public void setMentStatus(String mentStatus) {this.mentStatus = mentStatus;}


		public static final String STATUS_PENDING = "PENDING"; // 下單成功未付款
		public static final String STATUS_CONFIRMED = "CONFIRMED"; // 付款成功
		public static final String STATUS_COMPLETED = "COMPLETED"; // 履約完成（退房）
		public static final String STATUS_CANCELLED = "CANCELLED";




}