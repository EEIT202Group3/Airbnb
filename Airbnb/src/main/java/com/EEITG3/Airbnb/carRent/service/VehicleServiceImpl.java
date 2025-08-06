package com.EEITG3.Airbnb.carRent.service;

import com.EEITG3.Airbnb.carRent.entity.Reservation;
import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import com.EEITG3.Airbnb.carRent.repository.ReservationRepository;
import com.EEITG3.Airbnb.carRent.repository.VehicleRepository;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vRepository;
    private final ReservationRepository rRepository;

    public VehicleServiceImpl(VehicleRepository vRepository, ReservationRepository rRepository, ReservationService reservationService) {
        this.vRepository = vRepository;
        this.rRepository = rRepository;
    }

    @Override
    public Vehicle insert(Vehicle vehicle) throws Exception {
        Integer id = vehicle.getVehicleId();
        if (id != null && vRepository.existsById(id)) {
            throw new Exception("此車輛已存在");
        }
        checkPlateNoAvailable(vehicle.getPlateNo(), null);
        return vRepository.save(vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) throws Exception {
        Integer id = vehicle.getVehicleId();
        if (id == null || !vRepository.existsById(id)) {
            throw new Exception("查無此車輛資料");
        }
        Vehicle original = vRepository.findById(id).orElseThrow(() -> new Exception("找不到車輛"));
        checkPlateNoAvailable(vehicle.getPlateNo(), id);
        original.setPlateNo(vehicle.getPlateNo());
        original.setBrand(vehicle.getBrand());
        original.setModel(vehicle.getModel());
        original.setColor(vehicle.getColor());
        original.setFuelType(vehicle.getFuelType());
        original.setTransmission(vehicle.getTransmission());
        original.setSeatCapacity(vehicle.getSeatCapacity());
        original.setFuelCapacity(vehicle.getFuelCapacity());
        original.setVehicleTax(vehicle.getVehicleTax());
        original.setDailyRent(vehicle.getDailyRent());
        original.setMileage(vehicle.getMileage());
        original.setLatitude(vehicle.getLatitude());
        original.setLongitude(vehicle.getLongitude());
        original.setStatus(vehicle.getStatus());
        original.setImage(vehicle.getImage());
        return vRepository.save(original);
    }


    @Override
    public Vehicle findByPlateNo(String plateNo) throws Exception {
        return vRepository.findByPlateNo(plateNo)
                .orElseThrow(() -> new Exception("查無此車牌資料"));
    }

    @Override
    public Vehicle findById(Integer id) {
        return vRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "查無此資料"));
    }

    @Transactional(readOnly = true)
    @Override
    public Vehicle findReservationByPlateNoAndDate(String plateNo, LocalDate targetDate) throws Exception {
        List<Reservation> reservations = rRepository.findByPlateNoAndDate(plateNo, targetDate);
        if (reservations.isEmpty()) {
            throw new RuntimeException("找不到對應的 reservation");
        }

        Vehicle proxyVehicle = reservations.get(0).getVehicle();
        Vehicle realVehicle = (proxyVehicle instanceof HibernateProxy)
                ? (Vehicle) Hibernate.unproxy(proxyVehicle)
                : proxyVehicle;

        return realVehicle;
    }

    @Override
    public List<Vehicle> findAll() {
        return vRepository.findAll();
    }

    @Override
    public Integer deleteById(Integer id) throws Exception {
        if (id == null || !vRepository.existsById(id)) {
            throw new Exception("查無此ID，無法刪除");
        }
        vRepository.deleteById(id);
        System.out.println("成功刪除ID:" + id);
        return id;
    }

    @Override
    public Map<String, Integer> getVehicleStatusSummary() {
        List<Vehicle> vehicles = vRepository.findAll();

        Map<String, Integer> summary = new LinkedHashMap<>(); // 保持順序
        summary.put("可租用", 0);
        summary.put("維修中", 0);
        summary.put("下架", 0);

        for (Vehicle v : vehicles) {
            String status = v.getStatus();
            if (status != null && summary.containsKey(status)) {
                summary.put(status, summary.get(status) + 1);
            }
        }
        return summary;
    }

    public void checkPlateNoAvailable(String plateNo, Integer excludeId) {
        Optional<Vehicle> conflicts = vRepository.findByPlateNo(plateNo);
        if (conflicts.isPresent()) {
            Vehicle found = conflicts.get();
            if (excludeId == null || !found.getVehicleId().equals(excludeId)) {
                throw new RuntimeException("該車牌已被登入");
            }
        }
    }
}
