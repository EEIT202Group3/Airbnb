package com.payMent.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orderlist")
public class OrderBean implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	
		@Id
		@GeneratedValue(strategy = GenerationType.UUID)
		@Column(name = "booking_id")
	 	private String booking_id;       	    // 主鍵
		
		@Column(name = "Lisid")
	    private Integer Lisid;              // 房源ID (外來鍵)
	    
	   
		@Column(name = "customer_id")
		private String customerId;
	    
	    @Column(name = "reservation_id")
	    private Integer reservation_id;     // 租車ID (外來鍵)
	    
	    @Column(name = "username")
	    private String username;          // 房源名稱
	    
	    @Column(name = "house_name")
	    private String house_name;          // 房源名稱
	    
	    @Column(name = "address")
	    private String address;             // 房源地址
	    
	    @Column(name = "tel")
	    private String tel;                 // 房東電話
	    
	    @Column(name = "bed")
	    private String bed;                 // 房型

	    @Column(name = "people")
	    private Integer people;             // 人數

	    @Column(name = "checkin_date")
	    private Timestamp checkin_date;     // 入住/租車日期
	    
	    @Column(name = "checkout_date")
	    private Timestamp checkout_date;    // 退房/歸還日期
	    
	    @Column(name = "location_id")
	    private Integer location_id;        // 取車地址

	    @Column(name = "created_time")
	    private Timestamp created_time;     // 訂單產生日
	    
	    @Column(name = "booking_status")
	    private String booking_status;      // 訂單狀態

	    @Column(name = "payment_id")
	    private String payment_id;          // 付款編號
	    
	    @Column(name = "price")
	    private Integer price;              // 房租金額
	    
	    @Column(name = "total_amount")
	    private BigDecimal total_amount;    // 租車金額
	    
	    @Column(name = "total")
	    private BigDecimal total;              // 總金額
	    
	    @Column(name = "paid_time")
	    private Timestamp paid_time;        // 付款時間
	    
	    @Column(name = "booking_method")
	    private String booking_method;      // 付款方式
	    
	    @Column(name = "ment_status")
	    private String ment_status;         // 付款狀態
		
	    
	    
	    public OrderBean() {}
		
		
		
		public String getBooking_id() {return booking_id;}
		public void setBooking_id(String booking_id) {this.booking_id = booking_id;}
		
		public Integer getLisid() {return Lisid;}
		public void setLisid(Integer lisid) {Lisid = lisid;}
		
		public String getCustomer_id() {return customerId;}
		public void setCustomer_id(String customerId) {this.customerId = customerId;}
		
		public Integer getReservation_id() {return reservation_id;}
		public void setReservation_id(Integer reservation_id) {this.reservation_id = reservation_id;}
		
		public String getUsername() {return username;}
		public void setUsername(String username) {this.username = username;}
		
		public String getHouse_name() {return house_name;}
		public void setHouse_name(String house_name) {this.house_name = house_name;}
		
		public String getAddress() {return address;}
		public void setAddress(String address) {this.address = address;}
		
		public String getTel() {return tel;}
		public void setTel(String tel) {this.tel = tel;}
		
		public String getBed() {return bed;}
		public void setBed(String bed) {this.bed = bed;}
		
		public Integer getPeople() {return people;}
		public void setPeople(Integer people) {this.people = people;}
		
		public Timestamp getCheckin_date() {return checkin_date;}
		public void setCheckin_date(Timestamp checkin_date) {this.checkin_date = checkin_date;}
		
		public Timestamp getCheckout_date() {return checkout_date;}
		public void setCheckout_date(Timestamp checkout_date) {this.checkout_date = checkout_date;}
		
		public Integer getLocation_id() {return location_id;}
		public void setLocation_id(Integer location_id) {this.location_id = location_id;}
		
		public Timestamp getCreated_time() {return created_time;}
		public void setCreated_time(Timestamp created_time) {this.created_time = created_time;}
		
		public String getBooking_status() {return booking_status;}
		public void setBooking_status(String booking_status) {this.booking_status = booking_status;}
		
		public String getPayment_id() {return payment_id;}
		public void setPayment_id(String payment_id) {this.payment_id = payment_id;}
		
		public Integer getPrice() {return price;}
		public void setPrice(Integer price) {this.price = price;}
		
		public BigDecimal getTotal_amount() {return total_amount;}
		public void setTotal_amount(BigDecimal total_amount) {this.total_amount = total_amount;}
		
		public BigDecimal getTotal() {return total;}
		public void setTotal(BigDecimal total) {this.total = total;}
		
		public Timestamp getPaid_time() {return paid_time;}
		public void setPaid_time(Timestamp paid_time) {this.paid_time = paid_time;}
		
		public String getBooking_method() {return booking_method;}
		public void setBooking_method(String booking_method) {this.booking_method = booking_method;}
		
		public String getMent_status() {return ment_status;}
		public void setMent_status(String ment_status) {this.ment_status = ment_status;}
		

		

		
	
	
}