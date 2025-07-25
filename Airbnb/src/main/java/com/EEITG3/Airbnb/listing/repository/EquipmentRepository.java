package com.EEITG3.Airbnb.listing.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.EEITG3.Airbnb.listing.entity.*;

@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentBean, Integer>{
	
	    //查詢指定房源的所有設備名稱(equip_name)
	    @Query("SELECT e.equip_name FROM LisBean l JOIN l.equipments e WHERE l.lisid = :lisid")
	    List<String> findEquipNamesByLisid(int lisid);

	    //查詢指定房源的所有設備ID(equip_id)
	    @Query("SELECT e.equip_id FROM LisBean l JOIN l.equipments e WHERE l.lisid = :lisid")
	    List<Integer> findEquipIdsByLisid(int lisid);
	}

