package com.EEITG3.Airbnb.reviews.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.reviews.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	public Review findByReviewId(Integer id);
	
	public List<Review> findByListId(Integer id);
	
	public List<Review> findByHostId(String hostId);
	
	public List<Review> findByCustId(String custId);
	
	//@Query(value="select * from review", nativeQuery = true)
	public List<Review> findAll();
	
	@Query("SELECT r FROM Review r " +
		       "WHERE (:type = 'hostId' AND CAST(r.hostId AS string) LIKE %:keyword%) " +
		       "   OR (:type = 'custId' AND CAST(r.custId AS string) LIKE %:keyword%) " +
		       "   OR (:type = 'listId' AND CAST(r.listId AS string) LIKE %:keyword%)")
		List<Review> findByTypeAndKeyword(@Param("type") String type, @Param("keyword") String keyword);
	
//	@Query("SELECT r, l.list_id, l.photo1 FROM Review r JOIN ")

}
