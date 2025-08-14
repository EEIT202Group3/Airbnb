package com.EEITG3.Airbnb.carRent.repository;

import com.EEITG3.Airbnb.carRent.entity.VehicleDamage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleDamageRepository extends JpaRepository<VehicleDamage, Integer> {
    List<VehicleDamage> findByReservationIdOrderByReportDateDesc(Integer reservationId);
}
