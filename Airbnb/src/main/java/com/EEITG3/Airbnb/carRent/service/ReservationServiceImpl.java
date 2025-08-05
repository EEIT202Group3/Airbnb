package com.EEITG3.Airbnb.carRent.service;

import com.EEITG3.Airbnb.carRent.entity.Reservation;
import com.EEITG3.Airbnb.carRent.repository.ReservationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository rRepository;

    public ReservationServiceImpl(ReservationRepository rRepository) {
        this.rRepository = rRepository;
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
        if(!conflicts.isEmpty()) {
            throw  new RuntimeException("車輛在所選時間已被預約");
        }
    }
}
