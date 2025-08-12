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
	@Query("SELECT new com.EEITG3.Airbnb.payMent.dto.HostOrderAggDto(" + "  o.hostId, " + "  o.listing.listId, " + // 這裡經由關聯拿房源的
																													// listId
			"  o.bookingId, " + "  o.totalAmount" + // 金額欄位用 totalAmount（你的欄位名）
			") " + "FROM Order o " + "WHERE o.paidTime IS NOT NULL " + // 已付款的依據（或改用 o.mentStatus 判斷）
			"  AND o.paidTime >= :start " + "  AND o.paidTime <  :end")
	List<HostOrderAggDto> findPaidOrdersForPayout(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

	

}
