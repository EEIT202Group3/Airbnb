package com.EEITG3.Airbnb.carRent.repository;

import com.EEITG3.Airbnb.carRent.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    Optional<Vehicle> findByPlateNo(String plateNo);

    @Query("""
                SELECT v FROM Vehicle v
                WHERE v.vehicleId NOT IN (
                    SELECT v.vehicleId FROM Reservation r
                    WHERE NOT (
                        r.returnDate <= :pickupDateTime OR r.pickupDate >= :returnDateTime
                    )
                )
            """)
    List<Vehicle> findRentableVehicle(
            @Param("pickupDateTime") LocalDateTime pickupDateTime,
            @Param("returnDateTime") LocalDateTime returnDateTime
    );
}
