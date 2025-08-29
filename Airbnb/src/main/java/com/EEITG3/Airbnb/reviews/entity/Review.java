package com.EEITG3.Airbnb.reviews.entity;

import org.springframework.stereotype.Component;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.payMent.entity.Order;
import com.EEITG3.Airbnb.users.entity.Customer;
import com.EEITG3.Airbnb.users.entity.Host;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Entity @Table(name = "reviews")
@Component

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Review {

	@Id @Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;
	
	// 訂單（FK: orderlist.booking_id, uniqueidentifier）
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, referencedColumnName = "booking_id")
    private Order booking;
	
    // 房東（FK: hosts.host_id, uniqueidentifier）
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "host_id", nullable = false, referencedColumnName = "host_id")
    private Host host;

	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "customer_id")
    private Customer customer;
	
	// 房源（FK: listings.list_id, int）
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "list_id", nullable = false, referencedColumnName = "list_id")
    private LisBean listing;
	
	@Column(name="clean_score")
	@NonNull
	private int cleanScore;
	
	@Column(name="comm_score")
	@NonNull
	private int commScore;
	
	@Column(name="value_score")
	@NonNull
	private int valueScore;
	
	@Column(name="cus_comm")
	private String cusComm;
	
	@Column(name="host_comm")
	private String hostComm;
	
	@Column(name="review_date")
	@NonNull
	private String reviewDate;
	
	@Column(name="image1")
	private String image1;
	
	@Column(name="image2")
	private String image2;
	
	@Column(name="image3")
	private String image3;
	
	@Column(name="report")
	private Integer report;
	
	@Column(name="is_visible")
	private Integer isVisible;

	public Integer getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Integer isVisible) {
		this.isVisible = isVisible;
	}

	public Integer getReport() {
		return report;
	}

	public void setReport(Integer report) {
		this.report = report;
	}

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public Order getBooking() {
		return booking;
	}

	public void setBooking(Order booking) {
		this.booking = booking;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LisBean getListing() {
		return listing;
	}

	public void setListing(LisBean listing) {
		this.listing = listing;
	}

	public int getCleanScore() {
		return cleanScore;
	}

	public void setCleanScore(int cleanScore) {
		this.cleanScore = cleanScore;
	}

	public int getCommScore() {
		return commScore;
	}

	public void setCommScore(int commScore) {
		this.commScore = commScore;
	}

	public int getValueScore() {
		return valueScore;
	}

	public void setValueScore(int valueScore) {
		this.valueScore = valueScore;
	}

	public String getCusComm() {
		return cusComm;
	}

	public void setCusComm(String cusComm) {
		this.cusComm = cusComm;
	}

	public String getHostComm() {
		return hostComm;
	}

	public void setHostComm(String hostComm) {
		this.hostComm = hostComm;
	}

	public String getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(String reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getImage3() {
		return image3;
	}

	public void setImage3(String image3) {
		this.image3 = image3;
	}

	
}
