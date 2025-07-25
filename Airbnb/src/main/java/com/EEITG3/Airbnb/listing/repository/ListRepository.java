package com.EEITG3.Airbnb.listing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.EEITG3.Airbnb.listing.entity.*;


@Repository
public interface ListRepository extends JpaRepository<LisBean,Integer>{
	
	 //查詢指定房源的ID,房名,圖片一(房東管理房源列表顯示)
	 @Query("SELECT new com.listing.model.LisBean(l.lisid, l.house_name, l.photo1) FROM LisBean l")
	    List<LisBean> findBasicListingInfo();
	 
	 //根據list_id查詢LisBean的整個資料(修改房源用)
	 @Query("SELECT l FROM LisBean l WHERE l.lisid = :lisid")
	    LisBean findByLisid(int lisid);
	 
}
