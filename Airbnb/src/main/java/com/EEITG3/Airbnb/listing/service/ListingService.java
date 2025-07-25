package com.EEITG3.Airbnb.listing.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.EEITG3.Airbnb.listing.entity.*;
import com.EEITG3.Airbnb.listing.repository.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ListingService {

	private final ListRepository listRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	
	public ListingService(ListRepository listRepository) {
		this.listRepository = listRepository;
		
	}
	
	//新增房源回傳ID
	@Transactional
	public Integer saveListing(LisBean lisBean) {
		LisBean savedBean = listRepository.save(lisBean);
		return savedBean.getListId();
		
	}
	
	// 刪除房源
    public boolean deleteListing(int lisId) {
       try {
            if (listRepository.existsById(lisId)) {
            	listRepository.deleteById(lisId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	
	//查詢全部
	public  List<LisBean> findAll(){
		return listRepository.findAll();
	}
	
	//查詢單筆
	public Optional<LisBean> findById(Integer id){
		return listRepository.findById(id);
	}
	
	//查詢部分欄位 (ID,圖片一,房源名稱) #房東查詢房源欄用
	    public List<LisBean> getfindBasicListingInfo() {
	        return listRepository.findBasicListingInfo();
	    }

	@Transactional
	
	//儲存設備(傳入List)
    public void saveEquipments(Integer lisid, List<EquipmentBean> equipments) {
        Optional<LisBean> optional = listRepository.findById(lisid);
        if (optional.isPresent()) {
            LisBean lis = optional.get();
            lis.setEquipments(equipments);
            listRepository.save(lis);
        }
    }
	// 儲存設備（傳入ID 字串）
    @Transactional
    public void saveEquipmentsByIds(Integer lisid, String[] equipIds) {
        if (equipIds == null || equipIds.length == 0) return;

        List<EquipmentBean> equips = new ArrayList<>();
        for (String idStr : equipIds) {
            int eid = Integer.parseInt(idStr);
            EquipmentBean equip = em.find(EquipmentBean.class, eid);
            if (equip != null) equips.add(equip);
        }

        saveEquipments(lisid, equips);
    }
    
    //根據list_id查詢LisBean的整個資料(修改房源用)
    public LisBean getListingById(int lisid) {
        return listRepository.findByLisid(lisid);
    }
    
	
}
