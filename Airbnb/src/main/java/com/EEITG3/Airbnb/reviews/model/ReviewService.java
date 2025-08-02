package com.EEITG3.Airbnb.reviews.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.reviews.utils.ReviewUtils;

@Service
@Transactional
public class ReviewService {
	
	/*
	 * reviews Service
	 */
	

	@Autowired
	private ReviewRepository rRepository;

	public Review findByReviewID(Integer id) {
		return rRepository.findByReviewId(id);
	}

	public List<Review> findByListId(Integer id) {
		return rRepository.findByListId(id);
	}

	public List<Review> findAll() {
		return rRepository.findAll();
	}

	public void deleteById(Integer id) {
		rRepository.deleteById(id);
	}

	public ResponseEntity<?> insertReview(Integer listId, String bookingId, String custId, String hostId,
			Integer cleanScore, Integer commScore, Integer valueScore, String custComm, List<MultipartFile> images) {
		Review insertBean = new Review();
		List<String> imageList = new ReviewUtils().uploadImg(images);
		insertBean.setListId(listId);
		insertBean.setBookingId(bookingId);
		insertBean.setCustId(custId);
		insertBean.setHostId(hostId);
		insertBean.setCleanScore(cleanScore);
		insertBean.setCommScore(commScore);
		insertBean.setValueScore(valueScore);
		insertBean.setCusComm(custComm);
		String reviewDate = new ReviewUtils().getToday();
		insertBean.setReviewDate(reviewDate);
		for (int i = 0; i < imageList.size() && i < 3; i++) {
		    switch (i) {
		        case 0 -> insertBean.setImage1(imageList.get(0));
		        case 1 -> insertBean.setImage2(imageList.get(1));
		        case 2 -> insertBean.setImage3(imageList.get(2));
		    }
		}
	 	 Review resultBean = save(insertBean);
	 	 if(resultBean != null) {
	 		return ResponseEntity.ok("成功");
	 	 } else {
	 		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
	 	 }
	}
	public Review save(Review review) {
		return rRepository.save(review);
	}

	public ResponseEntity<?> updateReview(Integer reviewId,Integer listId, String bookingId,String custId, String hostId,int cleanScore, int commScore, int valueScore,String cusComm,String hostComm) {
		Review updateBean = new Review();
		updateBean.setReviewId(reviewId);
		updateBean.setListId(listId);
		updateBean.setBookingId(bookingId);
		updateBean.setCustId(custId);
		updateBean.setHostId(hostId);
		updateBean.setCleanScore(cleanScore);
		updateBean.setCommScore(commScore);
		updateBean.setValueScore(valueScore);
		updateBean.setCusComm(cusComm);
		updateBean.setHostComm(hostComm);
		
		Review resultBean = save(updateBean);
		if(resultBean != null) {
	 		return ResponseEntity.ok("成功");
	 	 } else {
	 		 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("新增失敗");
	 	 }
	}


}
