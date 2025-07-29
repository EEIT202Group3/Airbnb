package com.EEITG3.Airbnb.listing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.EEITG3.Airbnb.listing.entity.LisBean;	

@Repository
public interface ListingRepository extends JpaRepository<LisBean, Integer> {
	    // 暫時不用寫任何方法，基本CRUD都內建了
	}