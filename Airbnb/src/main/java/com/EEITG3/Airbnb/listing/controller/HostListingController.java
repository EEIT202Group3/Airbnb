package com.EEITG3.Airbnb.listing.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.listing.service.ListingService;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.entity.HostDetails;
import com.EEITG3.Airbnb.users.service.HostService;

@RestController
@RequestMapping("/api/hosts")
public class HostListingController {
    
	@Autowired
    private HostService hostservice;
	
	@Autowired
	private ListRepository listRepository;
	
	 @Autowired
	 private ListingService listingService;
	
	//根據host_id查詢(ID、房名、圖片1)
    @GetMapping("/getListing")
    public List<Map<String, Object>> getListingsByHostId(@AuthenticationPrincipal HostDetails hostdetails) {
    	Host host = hostservice.currentHost(hostdetails);
    	String hostId = host.getHostId();
    	List<LisBean> list = listRepository.findByHostId(hostId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (LisBean bean : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("listId", bean.getListId());
            map.put("houseName", bean.getHouseName());
            map.put("ads", bean.getAds());
            map.put("photo1", bean.getPhoto1());
            map.put("approved", bean.getApproved());  
            map.put("published", bean.getPublished());
            result.add(map);
        }
        return result;
    }
    
    @PostMapping(path = "/listings/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createListing(
    		@AuthenticationPrincipal HostDetails hostdetails,
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
    	Host host = hostservice.currentHost(hostdetails);
    	String hostId = host.getHostId();
    	System.out.print(hostId);
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
}
