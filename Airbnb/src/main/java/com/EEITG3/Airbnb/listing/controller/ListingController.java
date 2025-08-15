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
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.listing.service.ListingService;

@RestController
@RequestMapping("/listings")
@CrossOrigin
public class ListingController {
	
	@Autowired
	private ListRepository listRepository;
	
    @Autowired
    private ListingService listingService;


    //刪除房源
    @DeleteMapping("/{listId}")
    public boolean deleteListing(@PathVariable("listId") int listId) {
        return listingService.deleteListing(listId);
        
    }

    //查詢全部房源
    @GetMapping
    public List<LisBean> getfindAll() {
        return listingService.findAll();
    }


    //根據host_id查詢(ID、房名、圖片1)
    @GetMapping("/host/{hostId}")
    public List<Map<String, Object>> getListingsByHostId(@PathVariable UUID hostId) {
        List<LisBean> list = listRepository.findByHostId(hostId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LisBean bean : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("listId", bean.getListId());
            map.put("houseName", bean.getHouseName());
            map.put("photo1", bean.getPhoto1());
            map.put("approved", bean.getApproved());  
            map.put("published", bean.getPublished());
            result.add(map);
        }
        return result;
    }
    
    //顯示房源卡用的
    @GetMapping("/simple")
    public List<Map<String, Object>> getSimpleListings() {
        List<LisBean> approvedPublishedListings = listingService.findApprovedAndPublished();

        List<Map<String, Object>> result = new ArrayList<>();
        for (LisBean lis : approvedPublishedListings) {
            Map<String, Object> item = new HashMap<>();
            item.put("listId", lis.getListId());
            item.put("ads", lis.getAds());
            item.put("reviewCount", lis.getReviewCount());
            item.put("price", lis.getPrice());
            item.put("room", lis.getRoom());
            item.put("photo1", lis.getPhoto1());
            result.add(item);
        }
        return result;
    }

 

    //查詢單筆房源(房源基本資料)
    @GetMapping("/{id}/basic")
    public Optional<LisBean> getfindById(@PathVariable("id") int id) {
        return listingService.findById(id);
    }
    
    //查詢單筆房源（房源資料與設備）
    @GetMapping("/{id}")
    public LisBean getListingById(@PathVariable("id") int id) {
        return listingService.getListingById(id);
    }


    // 儲存房源設備（修改後更新設備）
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

            // 新增時強制狀態為待審核
            lisBean.setApproved(null); 
          
            lisBean.setApproved(null); // Boolean 欄位
            // 或 lisBean.setAuditStatus("PENDING"); // String 欄位

            Integer listId = listingService.saveListingWithPhotosAndEquipments(lisBean, photos, equipmentIds);
            return ResponseEntity.ok(listId);
        } catch (MultipartException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("上傳檔案過大或格式錯誤: " + e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
            return ResponseEntity.internalServerError().body("建立房源失敗: " + e.getMessage());
        }
    }
  //編輯房源
    @PutMapping(path = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateListing(
            @PathVariable("id") Integer id,
            @RequestParam("houseName") String houseName,
            @RequestParam("ads") String ads,
            @RequestParam("room") String room,
            @RequestParam("bed") String bed,
            @RequestParam("describe") String describe,
            @RequestParam("tel") String tel,
            @RequestParam("ppl") int ppl,
            @RequestParam("price") int price,
            @RequestParam(value = "approved", required = false) String approved,
            @RequestParam("equipments") List<Integer> equipmentIds,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos
    ) {
        try {
            LisBean original = listingService.getListingById(id);
            if (original == null) return ResponseEntity.notFound().build();

            // 處理 approved 的邏輯（Boolean 可為 null）
            Boolean approvedValue = null;
            if (approved != null && !approved.trim().isEmpty()) {
                approvedValue = Boolean.valueOf(approved); // "true"/"false"
            }

            // 判斷是否有改地址
            boolean addressChanged = !ads.equals(original.getAds());
//
//            // 判斷是否有改其他重要欄位（不包含地址）
//            boolean otherFieldsChanged = 
//                    !houseName.equals(original.getHouseName()) ||
//                    !room.equals(original.getRoom()) ||
//                    !bed.equals(original.getBed()) ||
//                    !describe.equals(original.getDescribe()) ||
//                    !tel.equals(original.getTel()) ||
//                    ppl != original.getPpl() ||
//                    price != original.getPrice();

            // 如果原本是審核失敗（false），不論改什麼都設成待審核 (null)
            if (Boolean.FALSE.equals(original.getApproved())) {
                approvedValue = null;
            }
            // 如果原本是審核通過（true）
            else if (Boolean.TRUE.equals(original.getApproved())) {
                // 只有地址改了，才變成待審核 (null)
                if (addressChanged) {
                    approvedValue = null;
                } else {
                    // 其他欄位改了，不改變審核狀態
                    approvedValue = true;
                }
            }
            // 原本是待審核(null)，保持不變
            else {
                approvedValue = null;
            }

            original.setHouseName(houseName);
            original.setAds(ads);
            original.setRoom(room);
            original.setBed(bed);
            original.setDescribe(describe);
            original.setTel(tel);
            original.setPpl(ppl);
            original.setPrice(price);
            original.setApproved(approvedValue); 

            // 更新房源的圖片和設備
            listingService.updateListingWithPhotosAndEquipments(original, photos, equipmentIds);

            return ResponseEntity.ok("房源更新成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("房源更新失敗: " + e.getMessage());
        }
    }



    //查詢未審核的房源
    @GetMapping("/pending")
    public List<LisBean> getPendingListings() {
        return listRepository.findPendingListings();
    }
    
    //通過房源功能
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveListing(@PathVariable Integer id) {
        Optional<LisBean> optionalListing = listRepository.findById(id);
        if (optionalListing.isPresent()) {
            LisBean listing = optionalListing.get();
            listing.setApproved(true);
            listRepository.save(listing);
            return ResponseEntity.ok("房源已通過審核");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("房源不存在");
    }
    //標記未通過房源
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectListing(@PathVariable Integer id) {
        Optional<LisBean> optional = listRepository.findById(id);
        if(optional.isPresent()) {
            LisBean listing = optional.get();
            listing.setApproved(false);  // 標記為資訊錯誤
            listRepository.save(listing);
            return ResponseEntity.ok("標記資訊錯誤成功");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該房源");
    }
    
 // 下架房源 
    @PutMapping("/{id}/unpublish")
    public ResponseEntity<LisBean> unpublishListing(@PathVariable Integer id) {
        LisBean listing = listingService.updatePublishedStatus(id, false);
        return ResponseEntity.ok(listing);
    }
    
 // 重新上架單筆房源 
    @PutMapping("/{id}/publish")
    public ResponseEntity<String> republishListing(@PathVariable Integer id) {
        boolean success = listingService.republishListing(id);
        if (success) {
            return ResponseEntity.ok("房源已重新上架");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("找不到該房源");
        }
    }

    

}