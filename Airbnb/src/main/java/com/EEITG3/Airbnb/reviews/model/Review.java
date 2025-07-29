package com.EEITG3.Airbnb.reviews.model;
import java.util.Date;

import org.springframework.stereotype.Component;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	
	/*
	 * Reviews JAVA-BEAN
	 * */
	
	@Id @Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;
	
	@Column(name="booking_id")
	@NonNull
	private String bookingId;
	
	@Column(name="host_id")
	@NonNull
	private String hostId;
	
	@Column(name="user_id")
	@NonNull
	private String userId;
	
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
	
	@Column(name="cos_comm")
	private String cus_comm;
	
	@Column(name="host_comm")
	private String host_comm;
	
	@Column(name="review_date")
	@NonNull
	private Date review_date;
	
	@Column(name="image1")
	private String image1;
	
	@Column(name="image2")
	private String image2;
	
	@Column(name="image3")
	private String image3;
	

}
