package com.EEITG3.Airbnb.reviews.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.reviews.dto.ReviewWithCustomerDto;
import com.EEITG3.Airbnb.reviews.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

	
	public List<Review> findAll();
	
	Optional<Review> findByReviewId(Integer id);

	
	List<Review> findByHost_HostIdContainingIgnoreCase(String hostId);
	List<Review> findByCustomer_CustomerIdContainingIgnoreCase(String customerId);
	List<Review> findByListing_ListId(Integer listId); // 精確比對
	
	  @Query("SELECT new com.EEITG3.Airbnb.reviews.dto.ReviewWithCustomerDto(" +
	           "r.cleanScore, r.commScore, r.valueScore, r.reviewDate, " +
	           "r.cusComm, r.hostComm, c.email, c.avatarURL) " +
	           "FROM Review r JOIN r.customer c " +
	           "WHERE r.listing.listId = :listId " +
	           "ORDER BY r.reviewDate DESC")
		List<ReviewWithCustomerDto> findReviewsByListId(@Param("listId") Integer listId);
	
//	@Query("SELECT r, l.list_id, l.photo1 FROM Review r JOIN ")

}
