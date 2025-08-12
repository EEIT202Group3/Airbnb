package com.EEITG3.Airbnb.payMent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EEITG3.Airbnb.payMent.entity.PayoutOrder;

public interface PayoutOrderRepository extends JpaRepository<PayoutOrder, String>{

	 List<PayoutOrder> findByHostPayout_PayoutId(String payoutId);
}
