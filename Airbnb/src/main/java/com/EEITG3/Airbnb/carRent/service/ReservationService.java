package com.EEITG3.Airbnb.carRent.service;


import com.EEITG3.Airbnb.carRent.entity.Reservation;
import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReservationService {
//<訂單>

    //新增
    public Reservation insert(Reservation reservation) throws Exception;

    //修改
    public Reservation update(Reservation reservation) throws Exception;

    //依ID查詢
    Reservation findById(Integer id);

    //依電話查詢
    public Reservation findByDriverPhone(String driverPhone) throws Exception;

    //依駕照查詢
    public Reservation findByLicense(String license) throws Exception;

    //依時間查詢可用車輛
    public List<Vehicle> findRentableVehicle(LocalDateTime pickupDateTime, LocalDateTime returnDateTime);

    //查詢全部
    public List<Reservation> findAll();

    //刪除
    public Integer deleteById(Integer id) throws Exception;

    //租車時間重複檢查
    public void checkVehicleAvailable(Integer vehicleId, LocalDateTime pickup, LocalDateTime ret, Integer reservationId);

    //預約儀錶板
    public Map<String, Integer> reservationDashBoard();

//    時間+模糊查詢查符合預約
    public Page<Reservation> searchEligibleReservations(String keyword, LocalDate from, LocalDate to, String mode, Pageable pageable);

}
