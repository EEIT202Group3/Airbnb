package com.EEITG3.Airbnb.listing.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.EEITG3.Airbnb.listing.service.EquipmentService;
import com.EEITG3.Airbnb.listing.entity.EquipmentBean;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    //查詢全部設備
    @GetMapping("/all")
    public List<EquipmentBean> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    //新增設備
    @PostMapping("/add")
    public String addEquipment(@RequestParam String equipName) {
    	System.out.println("成功新增的設備:"+equipName);
        boolean result = equipmentService.insertEquipment(equipName);
        return result ? "新增成功" : "新增失敗";
    }

    //刪除設備
    @DeleteMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable("id") int id) {
    	System.out.println("成功刪除商品ID:"+id);
        boolean result = equipmentService.deleteEquipment(id);
        return result ? "刪除成功" : "刪除失敗";
    }


    //查詢房源設備名稱
    @GetMapping("/names/{lisid}")
    public List<String> getEquipNames(@PathVariable("lisid") int lisid) {
        return equipmentService.getEquipmentNamesByListingId(lisid);
    }

    //查詢房源設備 ID
    @GetMapping("/ids/{lisid}")
    public List<Integer> getEquipIds(@PathVariable("lisid") int lisid) {
        return equipmentService.getEquipmentIdsByListingId(lisid);
    }
}
