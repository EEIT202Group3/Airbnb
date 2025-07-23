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


@Entity @Table(name = "Review")
@Component

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class Review {
	
	@Id @Column(name = "ReviewID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;
	
	@Column(name="BookingID")
	@NonNull
	private String bookingId;
	
	@Column(name="ListID")
	@NonNull
	private int listId;
	
	@Column(name="UserID")
	@NonNull
	private String userId;
	
	@Column(name="CleanScore")
	private int cleanScore;
	
	@Column(name="CommScore")
	private int commScore;
	
	@Column(name="ValueScore")
	private int valueScore;
	
	@Column(name="Comment")
	private String comment;
	
	@Column(name="ReviewDate")
	@NonNull
	private Date reviewDate;
	
	@Column(name="image1")
	private String image1;
	
	@Column(name="image2")
	private String image2;
	
	@Column(name="image3")
	private String image3;
	

}
