package com.EEITG3.Airbnb.listing.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.EEITG3.Airbnb.listing.entity.EquipmentBean;
import com.EEITG3.Airbnb.listing.entity.LisBean;
import com.EEITG3.Airbnb.listing.repository.ListRepository;

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

    //查詢全部房源
    public List<LisBean> findAll() {
        return listRepository.findAll();
    }

    //查詢單筆房源
    public Optional<LisBean> findById(Integer id) {
        return listRepository.findById(id);
    }

    // 查詢基本資料（查詢房東現有房源）
    public List<Map<String, Object>> getBasicListings() {
        List<LisBean> fullList = listRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (LisBean bean : fullList) {
            Map<String, Object> map = new HashMap<>();
            map.put("listId", bean.getListId());
            map.put("houseName", bean.getHouseName());
            map.put("photo1", bean.getPhoto1());
            result.add(map);
        }
        return result;
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

    // 儲存設備（透過 List<EquipmentBean>）
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
    public Integer saveListingWithPhotosAndEquipments(LisBean lisBean, List<MultipartFile> photos, List<Integer> equipmentIds) throws IOException {

        // 儲存圖片
        List<String> photoUrls = new ArrayList<>();
        String storageDir = "/Users/youm/pohto/"; 

        for (int i = 0; i < photos.size() && i < 10; i++) {
            MultipartFile photo = photos.get(i);
            if (!photo.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
                File dest = new File(storageDir,fileName);
                photo.transferTo(dest);
                photoUrls.add(fileName);
            }
        }

        // 對應 10 張圖欄位
        for (int i = 0; i < photoUrls.size(); i++) {
            String url = photoUrls.get(i);
            switch (i) {
                case 0 -> lisBean.setPhoto1(url);
                case 1 -> lisBean.setPhoto2(url);
                case 2 -> lisBean.setPhoto3(url);
                case 3 -> lisBean.setPhoto4(url);
                case 4 -> lisBean.setPhoto5(url);
                case 5 -> lisBean.setPhoto6(url);
                case 6 -> lisBean.setPhoto7(url);
                case 7 -> lisBean.setPhoto8(url);
                case 8 -> lisBean.setPhoto9(url);
                case 9 -> lisBean.setPhoto10(url);
            }
        }

        // 設定設備
        List<EquipmentBean> equipList = new ArrayList<>();
        for (Integer id : equipmentIds) {
            EquipmentBean equip = em.find(EquipmentBean.class, id);
            if (equip != null) equipList.add(equip);
        }
        lisBean.setEquipments(equipList);

        // 儲存
        LisBean saved = listRepository.save(lisBean);
        return saved.getListId();
    }

}