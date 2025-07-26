package com.EEITG3.Airbnb.listing.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EEITG3.Airbnb.listing.entity.*;


@Repository
public interface ListRepository extends JpaRepository<LisBean,Integer>{
	 //根據list_id查詢LisBean的整個資料(修改房源用)
	 @Query("SELECT l FROM LisBean l WHERE l.listId = :lisid")
	    LisBean findByLisid(int lisid);
	 
}
