package com.EEITG3.Airbnb.listing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.EEITG3.Airbnb.listing.entity.*;

@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentBean, Integer>{
	
	    //查詢指定房源的所有設備名稱(equip_name)
		@Query("SELECT e.equip_name FROM LisBean l JOIN l.equipments e WHERE l.listId = :lisid")
		List<String> findEquipNamesByLisid(@Param("lisid") int lisid);

		@Query("SELECT e.equip_id FROM LisBean l JOIN l.equipments e WHERE l.listId = :lisid")
		List<Integer> findEquipIdsByLisid(@Param("lisid") int lisid);
	}

