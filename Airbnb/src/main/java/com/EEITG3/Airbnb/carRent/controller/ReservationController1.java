package com.EEITG3.Airbnb.carRent.controller;

import com.EEITG3.Airbnb.carRent.entity.Reservation;
import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import com.EEITG3.Airbnb.carRent.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations1")
public class ReservationController1 {
    private final ReservationService rService;

    public ReservationController1(ReservationService rService) {
        this.rService = rService;
    }

    @PostMapping("/insert")
    public Reservation insert(@RequestBody Reservation reservation) {
        try {
            return rService.insert(reservation);
        } catch (Exception e) {
            throw new RuntimeException("新增失敗" + e);
        }
    }

    @PutMapping("/update")
    public Reservation update(@RequestBody Reservation reservation) {
        try {
            return rService.update(reservation);
        } catch (Exception e) {
            throw new RuntimeException("修改失敗" + e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public Integer delete(@PathVariable Integer id) throws Exception {
        if (id == null) {
            throw new Exception("刪除失敗");
        }
        return rService.deleteById(id);
    }

    @GetMapping("/findall")
    public List<Reservation> findAll() {
        return rService.findAll();
    }

    @GetMapping("/phone")
    public Reservation findByDriverPhone(@RequestParam String driverPhone) throws Exception {
        return rService.findByDriverPhone(driverPhone);
    }

    @GetMapping("/license")
    public Reservation findByLicense(@RequestParam String license) throws Exception {
        return rService.findByLicense(license);
    }

    @GetMapping("/car-select")
    public List<Vehicle> findRentableVehicle(
            @RequestParam("pickupDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime pickupDateTime,
            @RequestParam("returnDateTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDateTime) {
        return rService.findRentableVehicle(pickupDateTime, returnDateTime);
    }

    @GetMapping("/search")
    public Reservation search(@RequestParam String type, @RequestParam String query) {
        try {
            if ("license".equals(type)) {
                return findByLicense(query);
            } else if ("phone".equals(type)) {
                return findByDriverPhone(query);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無此查詢類別");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "查無資料");
        }
    }
    @GetMapping("/{id}")
    public Reservation findById(@PathVariable Integer id) throws Exception {
        return rService.findById(id);
    }

    @GetMapping("/dashboard")
    public Map<String, Integer> reservationDashBoard(){
        return rService.reservationDashBoard();
    }

    @GetMapping("/search-eligible")
    public Page<Reservation> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "created") String mode,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return rService.searchEligibleReservations(keyword, from, to, mode, pageable);
    }
}
