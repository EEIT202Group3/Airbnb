package com.EEITG3.Airbnb.reviews.model;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	/*
	 * reveiws Repository
	 */
	
	public Review findByReviewId(Integer id);
	
	//@Query(value="select * from review", nativeQuery = true)
	public List<Review> findAll();
	
	public List<Review> findByListId(Integer id);
	
	
}
