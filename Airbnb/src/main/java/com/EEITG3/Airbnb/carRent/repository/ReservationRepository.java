package com.EEITG3.Airbnb.carRent.repository;

import com.EEITG3.Airbnb.carRent.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    Reservation findByDriverPhone(String driverPhone);


    Reservation findByLicense(String license);


    @Query(value = """
                SELECT r.* FROM reservations r
                JOIN vehicles v ON r.vehicle_id = v.vehicle_id
                WHERE v.plate_no = :plateNo
                  AND :targetDate BETWEEN CAST(r.pickup_date AS date) AND CAST(r.return_date AS date)
            """, nativeQuery = true)
    List<Reservation> findByPlateNoAndDate(
            @Param("plateNo") String plateNo,
            @Param("targetDate") LocalDate targetDate
    );


    @Query(value = """
                SELECT * FROM reservations r
                WHERE r.vehicle_id = :vehicleId
                AND r.pickup_date <= :returnDate
                AND r.return_date >= :pickupDate
                AND (:reservationId IS NULL OR r.reservation_id != :reservationId)
            """, nativeQuery = true)
    List<Reservation> findConflictingReservations(
            @Param("vehicleId") Integer vehicleId,
            @Param("pickupDate") LocalDateTime pickupDate,
            @Param("returnDate") LocalDateTime returnDate,
            @Param("reservationId") Integer reservationId
    );


    @Query(value = """  
                SELECT COUNT(*) FROM reservations 
                WHERE pickup_date > CURRENT_TIMESTAMP 
                AND status NOT IN ('取消', '完成訂單')
            """, nativeQuery = true)
    Integer countUpcomingReservations();


    @Query(value = """
                SELECT COUNT(*) FROM reservations  
                WHERE pickup_date <= CURRENT_TIMESTAMP 
                AND return_date >= CURRENT_TIMESTAMP
                AND status NOT IN ('取消', '完成訂單')
            """, nativeQuery = true)
    Integer countOngoingReservations();


    @Query(value = """
                SELECT COUNT(*) FROM reservations
                WHERE return_date <= CURRENT_TIMESTAMP
                AND status = '完成訂單'
            """, nativeQuery = true)
    Integer countReturnedToday();


    @Query(value = """
                SELECT COUNT(*) FROM reservations
                WHERE return_date < CURRENT_TIMESTAMP
                AND status NOT IN ('完成訂單', '取消')
            """, nativeQuery = true)
    Integer countOverdueReservations();


    @Query(value = """
                SELECT COUNT(*) FROM reservations
                WHERE status = '取消'
            """, nativeQuery = true)
    Integer countCancelledReservations();
}
