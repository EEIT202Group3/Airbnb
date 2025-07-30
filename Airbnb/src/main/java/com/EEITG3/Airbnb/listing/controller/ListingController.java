package com.EEITG3.Airbnb.listing.controller;



import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.listing.entity.LisBean;
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


    @Autowired
    private ListingService listingService;

    // 查詢全部房源
    @GetMapping
    public List<LisBean> getAllListings() {
        return listingService.findAll();
    }

    // 查詢簡要資料（ID、房名、照片1）
    @GetMapping("/basic")
    public List<Map<String, Object>> getBasicListings() {
        return listingService.getBasicListings();
    }

    // 查詢單筆房源
    @GetMapping("/{id}")
    public LisBean getListingById(@PathVariable("id") int id) {
        return listingService.getListingById(id);
    }

    // 刪除房源
    @DeleteMapping("/{id}")
    public boolean deleteListing(@PathVariable("id") int id) {
        return listingService.deleteListing(id);
    }

    // 儲存房源設備（用於修改後更新設備）
    @PostMapping("/{id}/equipments")
    public void saveEquipments(@PathVariable("id") Integer id, @RequestBody String[] equipIds) {
        listingService.saveEquipmentsByIds(id, equipIds);
    }

    // 新增房源（包含設備與照片）
    @PostMapping(path = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createListing(
            @RequestParam("host_id") UUID hostId,
            @RequestParam("houseName") String houseName,
            @RequestParam("ads") String ads,
            @RequestParam("room") String room,
            @RequestParam("bed") String bed,
            @RequestParam("describe") String describe,
            @RequestParam("tel") String tel,
            @RequestParam("ppl") int ppl,
            @RequestParam("price") int price,
            @RequestParam("equipments") List<Integer> equipmentIds,
            @RequestParam("photos") List<MultipartFile> photos
    ) {
    	System.out.println("收到檔案數量：" + photos.size());
        for (MultipartFile photo : photos) {
            System.out.println("檔案名：" + photo.getOriginalFilename());
        }
        try {
            LisBean lisBean = new LisBean();
            lisBean.setHostId(hostId);
            lisBean.setHouseName(houseName);
            lisBean.setAds(ads);
            lisBean.setRoom(room);
            lisBean.setBed(bed);
            lisBean.setDescribe(describe);
            lisBean.setTel(tel);
            lisBean.setPpl(ppl);
            lisBean.setPrice(price);

            Integer listId = listingService.saveListingWithPhotosAndEquipments(lisBean, photos, equipmentIds);
            return ResponseEntity.ok(listId);
        } catch (MultipartException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("上傳檔案過大或格式錯誤: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("建立房源失敗: " + e.getMessage());
        }
    }
    @PostMapping("/test-multi-upload")
    public ResponseEntity<String> testMultiUpload(@RequestParam("photos") List<MultipartFile> photos) {
        StringBuilder sb = new StringBuilder("收到檔案：\n");
        for (MultipartFile file : photos) {
            sb.append(file.getOriginalFilename())
              .append(" (")
              .append(file.getSize())
              .append(" bytes)\n");
        }
        return ResponseEntity.ok(sb.toString());
    }
}