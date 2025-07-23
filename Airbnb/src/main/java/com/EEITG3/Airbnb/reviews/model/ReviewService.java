package com.EEITG3.Airbnb.reviews.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	
	public List<Review> findAll(){
		return rRepository.findAll();
	}
	
	
	 public void deleteById(Integer id) {
	        rRepository.deleteById(id);
	    }

	public Review save(Review review) {
		return rRepository.save(review);
	}

	public Review update(Review review) {
		// TODO Auto-generated method stub
		return null;
	}

}
