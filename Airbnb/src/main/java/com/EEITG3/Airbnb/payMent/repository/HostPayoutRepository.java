package com.EEITG3.Airbnb.payMent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EEITG3.Airbnb.payMent.entity.HostPayout;

public interface HostPayoutRepository extends JpaRepository<HostPayout, String> {
	
	boolean existsByHostIdAndPayoutMonth(String hostId, String payoutMonth);
	List<HostPayout> findByPayoutMonth(String payoutMonth);
	List<HostPayout> findByHostIdAndPayoutMonth(String hostId, String payoutMonth);
	
}


