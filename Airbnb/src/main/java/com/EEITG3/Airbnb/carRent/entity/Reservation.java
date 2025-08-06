package com.EEITG3.Airbnb.carRent.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @Column(name = "RESERVATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    @Column(name = "PICKUP_DATE")
    private LocalDateTime pickupDate;

    @Column(name = "RETURN_DATE")
    private LocalDateTime returnDate;

    @Column(name = "ACTUAL_PICKUP_DATE")
    private LocalDateTime actualPickupDate;

    @Column(name = "ACTUAL_RETURN_DATE")
    private LocalDateTime actualReturnDate;

    @Column(name = "DEPOSIT")
    private BigDecimal deposit;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "LICENSE")
    private String license;

    @Column(name = "DRIVER_NAME")
    private String driverName;

    @Column(name = "DRIVER_EMAIL")
    private String driverEmail;

    @Column(name = "DRIVER_PHONE")
    private String driverPhone;

    @Column(name = "DRIVER_AGE")
    private String driverAge;

    @Column(name = "CURRENT_DISCOUNT")
    private String currentDiscount;

    @Column(name = "VEHICLE_ID")
    private Integer vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", insertable = false, updatable = false)
    @JsonIgnore
    private Vehicle vehicle;

    @Column(name = "BOOKING_ID")
    private UUID bookingId;

    @Column(name = "LOCATION_ID")
    private Integer locationId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Reservation() {
    }

    public Reservation(Integer reservationId, LocalDateTime pickupDate, LocalDateTime returnDate, LocalDateTime actualPickupDate, LocalDateTime actualReturnDate, BigDecimal deposit, BigDecimal totalAmount, String status, LocalDateTime createdAt, String license, String driverName, String driverEmail, String driverPhone, String driverAge, String currentDiscount, Integer vehicleId, Vehicle vehicle, UUID bookingId, Integer locationId, Location location) {
        this.reservationId = reservationId;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.actualPickupDate = actualPickupDate;
        this.actualReturnDate = actualReturnDate;
        this.deposit = deposit;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
        this.license = license;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.driverPhone = driverPhone;
        this.driverAge = driverAge;
        this.currentDiscount = currentDiscount;
        this.vehicleId = vehicleId;
        this.vehicle = vehicle;
        this.bookingId = bookingId;
        this.locationId = locationId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public LocalDateTime getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(LocalDateTime pickupDate) {
        this.pickupDate = pickupDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDateTime getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(LocalDateTime actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public LocalDateTime getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDateTime actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverAge() {
        return driverAge;
    }

    public void setDriverAge(String driverAge) {
        this.driverAge = driverAge;
    }

    public String getCurrentDiscount() {
        return currentDiscount;
    }

    public void setCurrentDiscount(String currentDiscount) {
        this.currentDiscount = currentDiscount;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }


    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

}
