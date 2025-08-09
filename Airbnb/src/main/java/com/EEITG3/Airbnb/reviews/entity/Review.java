package com.EEITG3.Airbnb.reviews.entity;

import org.springframework.stereotype.Component;

import com.EEITG3.Airbnb.listing.entity.LisBean;

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
	
	@Column(name="booking_id")
	@NonNull
	private String bookingId;
	
	@Column(name="host_id")
	@NonNull
	private String hostId;
	
	@Column(name="customer_id")
	@NonNull
	private String custId;
	
	@Column(name="list_id")
	@NonNull
	private int listId;
	
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

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
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
