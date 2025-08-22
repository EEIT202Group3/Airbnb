package com.EEITG3.Airbnb.reviews.dto;

import com.EEITG3.Airbnb.reviews.entity.Review;

public class ReviewMapper {
	public static ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setReviewId(review.getReviewId());
        dto.setCustomerEmail(review.getCustomer().getEmail());
        dto.setHostEmail(review.getHost().getEmail());
        dto.setBookingId(review.getBooking().getBookingId());
        dto.setCleanScore(review.getCleanScore());
        dto.setCommScore(review.getCommScore());
        dto.setValueScore(review.getValueScore());
        dto.setReviewDate(review.getReviewDate());
        dto.setCusComm(review.getCusComm());
        dto.setHostComm(review.getHostComm());
        dto.setImage1(review.getImage1());
        dto.setImage2(review.getImage2());
        dto.setImage3(review.getImage3());
        dto.setListId(review.getListing().getListId());
        return dto;
  }
}
