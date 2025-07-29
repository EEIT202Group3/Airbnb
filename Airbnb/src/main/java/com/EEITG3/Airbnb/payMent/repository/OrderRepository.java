package com.EEITG3.Airbnb.payMent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.EEITG3.Airbnb.payMent.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
	// @Query("SELECT o FROM Order o WHERE o.customer.customer_id = :customer_id")
	 List<Order> findByCustomerCustomerId(String customer_id);
	 
	// @Query("SELECT o FROM Order o WHERE o.booking_id = :booking_id")
	 Optional<Order> findByBookingId(String bookingId);
}

