package com.EEITG3.Airbnb.carRent.service;


import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface VehicleService {
    //<車輛>

    //新增
    public Vehicle insert(Vehicle vehicle) throws Exception;

    //修改
    public Vehicle update(Vehicle vehicle) throws Exception;

    //依車牌查詢
    public Vehicle findByPlateNo(String plateNo) throws Exception;

    //依照id查詢
    Vehicle findById(Integer id);

    //依車牌加上日期查詢
    public Vehicle findReservationByPlateNoAndDate(String plateNo, LocalDate targetDate) throws Exception;

    //查詢全部
    public List<Vehicle> findAll();

    //刪除
    public Integer deleteById(Integer id) throws Exception;

    //chart.js圓餅圖
    public Map<String, Integer> getVehicleStatusSummary();

    //看車牌有無重複
    void checkPlateNoAvailable(String plateNo, Integer excludeId);

    //查符合條件的車輛
    Page<Vehicle> searchEligibleVehicle(String plateNo, Pageable pageable);
}
