package com.EEITG3.Airbnb.listing.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.EEITG3.Airbnb.listing.entity.*;
import com.EEITG3.Airbnb.listing.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
	
	@PersistenceContext
	private EntityManager em;
	
	public EquipmentService(EquipmentRepository equipmentRepository,ListRepository listRepository) {
		
		this.equipmentRepository = equipmentRepository;
	}

		//查詢全部設備
		public List<EquipmentBean>getAllEquipments(){
			return equipmentRepository.findAll();
		}
		
				
		// 新增設備（包含名稱、圖示、分類）
		public boolean insertEquipment(String equipName, String equipIcon, String equipCategory) {
		    try {
		        EquipmentBean equip = new EquipmentBean();
		        equip.setEquip_name(equipName);
		        equip.setEquip_icon(equipIcon);
		        equip.setEquip_category(equipCategory);
		        equipmentRepository.save(equip);
		        return true;
		    } catch (Exception e) {
		        e.printStackTrace();
		        throw e; 
		    }
		}

				
		// 刪除設備
	    public boolean deleteEquipment(int equipId) {
	        try {
	            if (equipmentRepository.existsById(equipId)) {
	                equipmentRepository.deleteById(equipId);
	                return true;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return false;
	    }
	    
		//查詢設備名稱
		public List<String> getEquipmentNamesByListingId(int lisid){
			return equipmentRepository.findEquipNamesByLisid(lisid);
		}
		
		//查詢設備ID
		public List<Integer>getEquipmentIdsByListingId(int lisid){
			return equipmentRepository.findEquipIdsByLisid(lisid);
		}
		
		
}
