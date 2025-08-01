package com.EEITG3.Airbnb.payMent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.payMent.entity.Order;

import jakarta.transaction.Transactional;

public interface AdminRepository extends JpaRepository<Order, String> {
	@Query("SELECT o FROM Order o WHERE o.customerId = :customerId")
	List<Order> findByCustomerCustomerId(String customerId);

	// @Query("SELECT o FROM Order o WHERE o.booking_id = :booking_id")
	Optional<Order> findByBookingId(String bookingId);
	
	// 修改訂單狀態
	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.mentStatus = :mentStatus WHERE o.bookingId = :bookingId")
	int updateMentStatus(@Param("bookingId") String bookingId, @Param("mentStatus") String mentStatus);
	
	// 修改付款狀態
	@Modifying
	@Transactional
	@Query("UPDATE Order o SET o.bookingStatus = :bookingStatus WHERE o.bookingId = :bookingId")
	int updateBookingStatus(@Param("bookingId") String bookingId, @Param("bookingStatus") String bookingStatus);

}
