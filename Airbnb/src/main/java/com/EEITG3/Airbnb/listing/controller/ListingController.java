package com.EEITG3.Airbnb.listing.controller;

import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.listing.dto.ListingWithHostDTO;
import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.listing.service.ListingService;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;
import com.EEITG3.Airbnb.users.repository.HostRepository;
import com.EEITG3.Airbnb.users.service.HostService;

@RestController
@RequestMapping("/listings")
@CrossOrigin
public class ListingController {
	
	@Autowired
	private ListRepository listRepository;
	
    @Autowired
    private ListingService listingService;
    
    private final HostRepository hostRepository;
    
    @Autowired
    private HostService hostservice;
    
    @Autowired
    public ListingController(ListRepository listRepository, HostRepository hostRepository) {
        this.listRepository = listRepository;
        this.hostRepository = hostRepository;
    }


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
    
    
 // 查詢房源（房源、圖片、設備、房東）
    @GetMapping("/{id}")
    public ResponseEntity<?> getListingWithHost(@PathVariable("id") Integer listId) {
        Optional<LisBean> optListing = listRepository.findById(listId);
        if (optListing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        LisBean listing = optListing.get();

        // 取得房東
        Optional<Host> optHost = hostRepository.findById(listing.getHostId());
        Host host = optHost.orElse(null);

        // DTO
        ListingWithHostDTO dto = new ListingWithHostDTO();
        dto.setListId(listing.getListId());
        dto.setHouseName(listing.getHouseName());
        dto.setAds(listing.getAds());
        dto.setRoom(listing.getRoom());
        dto.setBed(listing.getBed());
        dto.setPpl(listing.getPpl());
        dto.setPrice(listing.getPrice());
        dto.setDescribe(listing.getDescribe());
        dto.setTel(listing.getTel());
        dto.setReviewCount(listing.getReviewCount());

        // 圖片
        dto.setPhoto1(listing.getPhoto1());
        dto.setPhoto2(listing.getPhoto2());
        dto.setPhoto3(listing.getPhoto3());
        dto.setPhoto4(listing.getPhoto4());
        dto.setPhoto5(listing.getPhoto5());
        dto.setPhoto6(listing.getPhoto6());
        dto.setPhoto7(listing.getPhoto7());
        dto.setPhoto8(listing.getPhoto8());
        dto.setPhoto9(listing.getPhoto9());
        dto.setPhoto10(listing.getPhoto10());

     // 房東資訊 
        if (host != null) { 
        	dto.setHostName(host.getUsername()); 
        	dto.setHostAvatarURL(host.getAvatarURL());
        	}
        
        
        // 設備資訊
        if (listing.getEquipments() != null && !listing.getEquipments().isEmpty()) {
            dto.setEquipments(
                listing.getEquipments().stream().map(equip -> {
                    ListingWithHostDTO.EquipmentDTO eDto = new ListingWithHostDTO.EquipmentDTO();
                    eDto.setEquip_id(equip.getEquip_id());
                    eDto.setEquip_name(equip.getEquip_name());
                    eDto.setEquip_icon(equip.getEquip_icon());
                    eDto.setEquip_category(equip.getEquip_category());
                    return eDto;
                }).collect(Collectors.toList())
            );
        }

        return ResponseEntity.ok(dto);
    }


    // 儲存房源設備（修改後更新設備）
    @PostMapping("/{id}/equipments")
    public void saveEquipments(@PathVariable("id") Integer id, @RequestBody String[] equipIds) {
        listingService.saveEquipmentsByIds(id, equipIds);
    }

    // 新增房源（包含設備與照片）
    
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
    
    //主頁查詢房源
    @GetMapping("/search")
    public List<LisBean> searchListings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer guest,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut
    ) {
        return listingService.searchListings(location, guest, checkIn, checkOut);
    }
    
    // 根據城市取得房源
    @GetMapping("/city/{city}")
    public List<LisBean> getListingsByCity(@PathVariable String city) {
        return listingService.getListingsByCity(city);
    }

    // 根據城市取得評分最高前10筆
    @GetMapping("/city/{city}/top-rated")
    public List<LisBean> getTopRatedListingsByCity(@PathVariable String city) {
        return listingService.getTopRatedListingsByCity(city);
    }
    

}