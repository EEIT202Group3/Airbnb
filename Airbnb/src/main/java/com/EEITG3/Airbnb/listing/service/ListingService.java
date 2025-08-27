package com.EEITG3.Airbnb.listing.service;




import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.listing.dto.ListingWithHostDTO;
import com.EEITG3.Airbnb.listing.entity.EquipmentBean;
import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;
import com.EEITG3.Airbnb.users.entity.Host;
import com.EEITG3.Airbnb.users.repository.HostRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import com.EEITG3.Airbnb.listing.repository.LisBeanSpecifications;

@Service
public class ListingService {
	

    private final ListRepository listRepository;
    private final HostRepository hostRepository;
    
    @Value("${app.storage.base-dir}")
    private String baseDir;
    

    @PersistenceContext
    private EntityManager em;

    public ListingService(ListRepository listingRepository, HostRepository hostRepository) {
        this.listRepository = listingRepository;
        this.hostRepository = hostRepository;
    }
    
    public ListingWithHostDTO getListingWithHost(Integer listId) {
        Optional<LisBean> optListing = listRepository.findById(listId);
        if (optListing.isEmpty()) return null;

        LisBean listing = optListing.get();
        Optional<Host> optHost = hostRepository.findById(listing.getHostId());
        Host host = optHost.orElse(null);

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

        // 圖片欄位
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

        return dto;
    }


    //查詢全部房源
    public List<LisBean> findAll() {
        return listRepository.findAll();
    }

    //查詢單筆房源
    public Optional<LisBean> findById(Integer id) {
        return listRepository.findById(id);
    }


    // 刪除房源
    public boolean deleteListing(int listId) {
        try {
            if (listRepository.existsById(listId)) {
                listRepository.deleteById(listId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 根據 listId 取得房源（含設備）
    public LisBean getListingById(int lisid) {
        return listRepository.findByLisid(lisid);
    }

    // 儲存設備（透過 ID 陣列）
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

    // 儲存設備
    public void saveEquipments(Integer lisid, List<EquipmentBean> equipments) {
        Optional<LisBean> optional = listRepository.findById(lisid);
        if (optional.isPresent()) {
            LisBean lis = optional.get();
            lis.setEquipments(equipments);
            listRepository.save(lis);
        }
    }

    // 新增房源（最多 10 張圖片與設備關聯）
    @Transactional
    public Integer saveListingWithPhotosAndEquipments(
            LisBean lisBean,
            List<MultipartFile> photos,
            List<Integer> equipmentIds) throws IOException {

        // 1) 建立儲存目錄：{baseDir}/listings
        Path storageDir = Paths.get(baseDir, "listings").toAbsolutePath().normalize();
        Files.createDirectories(storageDir);

        // 2) 寫檔（僅用一種方式：Files.copy），並收集檔名
        List<String> photoFileNames = new ArrayList<>();
        int limit = Math.min(photos != null ? photos.size() : 0, 10);

        for (int i = 0; i < limit; i++) {
            MultipartFile photo = photos.get(i);
            if (photo == null || photo.isEmpty()) continue;

            String original = photo.getOriginalFilename();
            String safeName = sanitizeFileName(original);
            String fileName = System.currentTimeMillis() + "_" + safeName;

            Path targetPath = storageDir.resolve(fileName).normalize();

            // 保護：避免 ../ 走出 storageDir
            if (!targetPath.startsWith(storageDir)) {
                throw new IOException("Invalid file path: " + fileName);
            }

            // 寫入檔案（覆蓋同名）
            Files.copy(photo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            photoFileNames.add(fileName);
        }

        // 3) 寫回到 lisBean 的 photo1~photo10 欄位
        applyPhotoFields(lisBean, photoFileNames);

        // 4) 關聯設備
        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            List<EquipmentBean> equipList = new ArrayList<>();
            for (Integer id : equipmentIds) {
                EquipmentBean equip = em.find(EquipmentBean.class, id);
                if (equip != null) equipList.add(equip);
            }
            lisBean.setEquipments(equipList);
        }

        // 5) 儲存
        LisBean saved = listRepository.save(lisBean);
        return saved.getListId();
    }

    /** 只保留英數、點、底線、連字號；其他轉為底線，避免奇怪路徑或 OS 不支援字元 */
    private String sanitizeFileName(String name) {
        if (name == null) return "unknown";
        String base = Paths.get(name).getFileName().toString(); // 去掉路徑成分
        return base.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    /** 把前面收到的檔名依序塞入 photo1~photo10 */
    private void applyPhotoFields(LisBean lisBean, List<String> names) {
        for (int i = 0; i < names.size(); i++) {
            String n = names.get(i);
            switch (i) {
                case 0 -> lisBean.setPhoto1(n);
                case 1 -> lisBean.setPhoto2(n);
                case 2 -> lisBean.setPhoto3(n);
                case 3 -> lisBean.setPhoto4(n);
                case 4 -> lisBean.setPhoto5(n);
                case 5 -> lisBean.setPhoto6(n);
                case 6 -> lisBean.setPhoto7(n);
                case 7 -> lisBean.setPhoto8(n);
                case 8 -> lisBean.setPhoto9(n);
                case 9 -> lisBean.setPhoto10(n);
                default -> { /* 超出 10 張就忽略 */ }
            }
        }
    }
//    @Transactional
//    public Integer saveListingWithPhotosAndEquipments(LisBean lisBean, List<MultipartFile> photos, List<Integer> equipmentIds) throws IOException {
//
//        
//        List<String> photoUrls = new ArrayList<>();
//        
//        
//        Path storageDir = Paths.get(baseDir,"listings");
//        
////        String storageDir = "/Users/youm/Documents/GitHub/Airbnb/Airbnb/photo/listing";
////        String storageDir = "C:/upload/photo/";
////        
////        File dir = new File(storageDir);
////        if(!dir.exists()) {
////        	dir.mkdirs();
////        }  
//        
//        Files.createDirectories(storageDir);
//
//        for (int i = 0; i < photos.size() && i < 10; i++) {
//            MultipartFile photo = photos.get(i);
//            if (!photo.isEmpty()) {
//                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
////                File dest = new File(storageDir,fileName);
//                
//                Files.copy(photo.getInputStream(), storageDir.resolve(fileName),StandardCopyOption.REPLACE_EXISTING);
//                photo.transferTo(storageDir);
//                photoUrls.add(fileName);
//               
//            }
//        }
//
//        
//        for (int i = 0; i < photoUrls.size(); i++) {
//            String url = photoUrls.get(i);
//            switch (i) {
//                case 0 -> lisBean.setPhoto1(url);
//                case 1 -> lisBean.setPhoto2(url);
//                case 2 -> lisBean.setPhoto3(url);
//                case 3 -> lisBean.setPhoto4(url);
//                case 4 -> lisBean.setPhoto5(url);
//                case 5 -> lisBean.setPhoto6(url);
//                case 6 -> lisBean.setPhoto7(url);
//                case 7 -> lisBean.setPhoto8(url);
//                case 8 -> lisBean.setPhoto9(url);
//                case 9 -> lisBean.setPhoto10(url);
//            }
//        }
//
//        
//        List<EquipmentBean> equipList = new ArrayList<>();
//        for (Integer id : equipmentIds) {
//            EquipmentBean equip = em.find(EquipmentBean.class, id);
//            if (equip != null) equipList.add(equip);
//        }
//        lisBean.setEquipments(equipList);
//
//        
//        LisBean saved = listRepository.save(lisBean);
//        return saved.getListId();
//    }
    
   
     // 編輯房源（更新基本資料、設備、照片）

    public void updateListingWithPhotosAndEquipments(
            LisBean lisBean,
            List<MultipartFile> photos,
            List<Integer> equipmentIds
    ) throws IOException {

        // 建立儲存目錄
        Path storageDir = Paths.get(baseDir, "listings").toAbsolutePath().normalize();
        Files.createDirectories(storageDir);

        // 有新照片 → 先清空舊的 → 再上傳新的
        if (photos != null && !photos.isEmpty()) {
            clearPhotoFields(lisBean); // 

            List<String> photoFileNames = new ArrayList<>();
            int limit = Math.min(photos.size(), 10); // 最多10張

            for (int i = 0; i < limit; i++) {
                MultipartFile photo = photos.get(i);
                if (photo == null || photo.isEmpty()) continue;

                // 檔名處理
                String safeName = sanitizeFileName(photo.getOriginalFilename());
                String fileName = System.currentTimeMillis() + "_" + safeName;
                Path targetPath = storageDir.resolve(fileName).normalize();

                // 確保不跳脫目錄
                if (!targetPath.startsWith(storageDir)) {
                    throw new IOException("Invalid file path: " + fileName);
                }

                // 儲存檔案
                Files.copy(photo.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                photoFileNames.add(fileName);
            }

            applyPhotoFields(lisBean, photoFileNames);
        }

        // 更新設備
        if (equipmentIds != null) {
            List<EquipmentBean> equipList = new ArrayList<>();
            for (Integer id : equipmentIds) {
                EquipmentBean equip = em.find(EquipmentBean.class, id);
                if (equip != null) equipList.add(equip);
            }
            lisBean.setEquipments(equipList);
        }

        // 儲存
        listRepository.save(lisBean);
    }

    
     //清空 LisBean 中的 photo1 ~ photo10 欄位
     
    private void clearPhotoFields(LisBean lisBean) {
        for (int i = 1; i <= 10; i++) {
            try {
                Field field = LisBean.class.getDeclaredField("photo" + i);
                field.setAccessible(true);
                field.set(lisBean, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //下架房源
    public LisBean updatePublishedStatus(Integer id, boolean status) {
        LisBean listing = listRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("房源不存在"));
        listing.setPublished(status);
        return listRepository.save(listing);
    }
    
    //重新上架房源
    public boolean republishListing(Integer id) {
        return listRepository.findById(id).map(listing -> {
            listing.setPublished(true);  // 設為重新上架
            listRepository.save(listing);
            return true;
        }).orElse(false);
    }
    
    
    //前台房源卡
    public List<LisBean> findApprovedAndPublished() {
        return listRepository.findByApprovedTrueAndPublishedTrue();
    }
    
//    //主頁 查詢地點 人數
//    public List<LisBean> searchListings(String location, Integer guestCount) {
//        Specification<LisBean> spec = Specification
//                .where(LisBeanSpecifications.isApprovedAndPublished())
//                .and(LisBeanSpecifications.locationLike(location))
//                .and(LisBeanSpecifications.guestCountAtLeast(guestCount));
//
//        return listRepository.findAll(spec);
//    }
    
 // 主頁查詢欄查詢 （三個選一都可查詢）
    public List<LisBean> searchListings(String location, Integer guestCount, LocalDate checkIn, LocalDate checkOut) {
        Specification<LisBean> spec = Specification
                .where(LisBeanSpecifications.isApprovedAndPublished())
                .and(LisBeanSpecifications.locationLike(location))
                .and(LisBeanSpecifications.guestCountAtLeast(guestCount))
                .and(LisBeanSpecifications.availableBetween(
                        checkIn != null ? checkIn.atStartOfDay() : null,
                        checkOut != null ? checkOut.atStartOfDay() : null
                ));

        return listRepository.findAll(spec);
    }
    
 // 根據城市取得所有符合條件的房源
    public List<LisBean> getListingsByCity(String city) {
        return listRepository.findByCity(city);
    }

    // 取得該城市評分最高前10筆
    public List<LisBean> getTopRatedListingsByCity(String city) {
        int offset = 0;
        int limit = 10;
        return listRepository.findTopRatedByCity(city, offset, limit);
    }

    
}
