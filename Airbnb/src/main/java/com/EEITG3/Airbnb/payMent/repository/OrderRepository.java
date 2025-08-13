package com.EEITG3.Airbnb.payMent.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.EEITG3.Airbnb.payMent.dto.HostOrderAggDto;
import com.EEITG3.Airbnb.payMent.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	@Query("SELECT o FROM Order o WHERE o.customerId = :customerId")
	List<Order> findByCustomerId(String customerId);

	// @Query("SELECT o FROM Order o WHERE o.booking_id = :booking_id")
	Optional<Order> findByBookingId(String bookingId);

	Optional<Order> findByPaymentId(String paymentId);

	//JBQL
	@Query("SELECT new com.EEITG3.Airbnb.payMent.dto.HostOrderAggDto(" +
		       "  o.hostId, " +
		       "  o.listing.listId, " +
		       "  o.bookingId, " +
		       "  o.grandTotal) " +  // 這裡改為 grandTotal
		       "FROM Order o " +
		       "WHERE o.paidTime IS NOT NULL " +
		       "AND o.paidTime >= :start " +
		       "AND o.paidTime <  :end")
		List<HostOrderAggDto> findPaidOrdersForPayout(@Param("start") LocalDateTime start,
		                                               @Param("end") LocalDateTime end);
}
