package com.EEITG3.Airbnb.carRent.service;


import com.EEITG3.Airbnb.carRent.entity.Vehicle;

import java.time.LocalDate;
import java.util.List;

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

}
