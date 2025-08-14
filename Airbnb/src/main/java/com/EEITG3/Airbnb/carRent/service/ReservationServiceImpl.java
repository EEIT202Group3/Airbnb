package com.EEITG3.Airbnb.carRent.service;

import com.EEITG3.Airbnb.carRent.entity.Reservation;
import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import com.EEITG3.Airbnb.carRent.repository.ReservationRepository;
import com.EEITG3.Airbnb.carRent.repository.VehicleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository rRepository;
    private final VehicleRepository vRepository;

    public ReservationServiceImpl(ReservationRepository rRepository, VehicleRepository vRepository) {
        this.rRepository = rRepository;
        this.vRepository = vRepository;
    }

    @Override
    public Reservation insert(Reservation reservation) throws Exception {
        Integer id = reservation.getReservationId();
        if (id != null && rRepository.existsById(id)) {
            throw new Exception("此預約已存在");
        }
        checkVehicleAvailable(
                reservation.getVehicleId(),
                reservation.getPickupDate(),
                reservation.getReturnDate(),
                reservation.getReservationId()
        );
        return rRepository.save(reservation);
    }

    @Override
    public Reservation update(Reservation reservation) throws Exception {
        Integer id = reservation.getReservationId();
        if (id == null || !rRepository.existsById(id)) {
            throw new Exception("查無此預約資料");
        }
        checkVehicleAvailable(
                reservation.getVehicleId(),
                reservation.getPickupDate(),
                reservation.getReturnDate(),
                reservation.getReservationId()
        );
        return rRepository.save(reservation);
    }

    @Override
    public Reservation findById(Integer id) {
        return rRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此預約資料"));
    }

    @Override
    public Reservation findByDriverPhone(String driverPhone) throws ResponseStatusException {
        Reservation res = rRepository.findByDriverPhone(driverPhone);
        return Optional.ofNullable(rRepository.findByDriverPhone(driverPhone))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此電話資料"));
    }

    @Override
    public Reservation findByLicense(String license) throws ResponseStatusException {
        return Optional.ofNullable(rRepository.findByLicense(license))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此駕照資料"));
    }

    @Override
    public List<Vehicle> findRentableVehicle(LocalDateTime pickupDateTime, LocalDateTime returnDateTime) {
        if (pickupDateTime == null || returnDateTime == null || !pickupDateTime.isBefore(returnDateTime)) {
            throw new IllegalArgumentException("時間區間不正確");
        }
        return vRepository.findRentableVehicle(pickupDateTime, returnDateTime);
    }

    @Override
    public List<Reservation> findAll() {
        return rRepository.findAll();
    }

    @Override
    public Integer deleteById(Integer id) throws Exception {

        if (id == null || !rRepository.existsById(id)) {
            throw new Exception("查無此ID，無法刪除");
        }
        rRepository.deleteById(id);
        System.out.println("成功刪除ID:" + id);
        return id;
    }

    @Override
    public void checkVehicleAvailable(Integer vehicleId, LocalDateTime pickup, LocalDateTime ret, Integer reservationId) {
        List<Reservation> conflicts = rRepository.findConflictingReservations(vehicleId, pickup, ret, reservationId);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("車輛在所選時間已被預約");
        }
    }

    public Map<String, Integer> reservationDashBoard() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("已預定", Optional.ofNullable(rRepository.countUpcomingReservations()).orElse(0));
        map.put("出租中", Optional.ofNullable(rRepository.countOngoingReservations()).orElse(0));
        map.put("已還車", Optional.ofNullable(rRepository.countReturnedToday()).orElse(0));
        map.put("逾期", Optional.ofNullable(rRepository.countOverdueReservations()).orElse(0));
        map.put("取消", Optional.ofNullable(rRepository.countCancelledReservations()).orElse(0));
        return map;
    }

    @Override
    public Page<Reservation> searchEligibleReservations(String keyword, LocalDate from, LocalDate to, String mode, Pageable pageable) {
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt = (to != null) ? to.plusDays(1).atStartOfDay() : null;
        if("period".equalsIgnoreCase(mode)) {
            return rRepository.searchByPeriodOverlap(kw,fromDt, toDt, pageable);
        }
        return rRepository.searchByCreatedAt(kw, fromDt, toDt, pageable);
    }
}
