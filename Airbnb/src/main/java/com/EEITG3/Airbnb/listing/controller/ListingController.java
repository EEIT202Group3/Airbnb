package com.EEITG3.Airbnb.listing.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.listing.service.ListingService;

@RestController
@RequestMapping("/listings")
@CrossOrigin
public class ListingController {
	private ListRepository listRepository;
	
    @Autowired
    private ListingService listingService;

    //新增房源
    @PostMapping
    public Integer createListing(@RequestBody LisBean lisBean) {
        return listingService.saveListing(lisBean);
    }

    //刪除房源
    @DeleteMapping("/{id}")
    public boolean deleteListing(@PathVariable("id") int id) {
        return listingService.deleteListing(id);
    }

    //查詢全部房源
    @GetMapping
    public List<LisBean> getAllListings() {
        return listingService.findAll();
    }

    //查詢(ID、房名、圖片1)
    @GetMapping("/basic")
    public List<Map<String,Object>>getBasicListings(){
    	List<LisBean>fullList= listRepository.findAll();
    	List<Map<String, Object>>result= new ArrayList<>();
    	for(LisBean bean : fullList) {
    		Map<String, Object>map = new HashMap<>();
    		map.put("listId",bean.getListId());
    		map.put("houseName",bean.getHouseName());
    		map.put("photo1",bean.getPhoto1());
    		result.add(map);
    	}
    	return result;
    }

    //查詢單筆房源
    @GetMapping("/{id}")
    public LisBean getListingById(@PathVariable("id") int id) {
        return listingService.getListingById(id);
    }

    //儲存房源設備 (用設備ID)
    @PostMapping("/{id}/equipments")
    public void saveEquipments(@PathVariable("id") Integer id, @RequestBody String[] equipIds) {
        listingService.saveEquipmentsByIds(id, equipIds);
    }
}