package com.EEITG3.Airbnb.listing.repository;


import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import com.EEITG3.Airbnb.listing.entity.*;


@Repository
public interface ListRepository extends JpaRepository<LisBean,Integer>,JpaSpecificationExecutor<LisBean> {
	 //根據list_id查詢LisBean的整個資料
	 @Query("SELECT l FROM LisBean l WHERE l.listId = :lisid")
	    LisBean findByLisid(int lisid);
	 
	 @Query("SELECT l FROM LisBean l WHERE l.host_Id = :hostId")
	 List<LisBean> findByHostId(@Param("hostId") String hostId);
	 
	 List<LisBean> findByApprovedFalse();
	 
	 @Query("SELECT l FROM LisBean l WHERE l.approved = false OR l.approved IS NULL")
	 
	 List<LisBean> findPendingListings();
	 
	 List<LisBean> findByPublishedTrue();
	 
	 List<LisBean> findByApprovedTrueAndPublishedTrue();
	 
	  // 依照地址的城市分類房源
	 @Query("SELECT l FROM LisBean l WHERE l.ads LIKE %:city% AND l.approved = true AND l.published = true")
	 List<LisBean> findByCity(@Param("city") String city);

	  // 撈出該城市中評分最高的前10比
	 @Query(value = "SELECT * FROM listings WHERE ads LIKE '%' + :city + '%' AND approved = 1 AND published = 1 ORDER BY review_count DESC OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY", nativeQuery = true)
	    List<LisBean> findTopRatedByCity(@Param("city") String city, @Param("offset") int offset, @Param("limit") int limit);
	}

