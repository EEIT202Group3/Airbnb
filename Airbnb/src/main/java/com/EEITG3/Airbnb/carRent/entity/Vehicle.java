package com.EEITG3.Airbnb.carRent.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


import java.math.BigDecimal;

@Entity
@Table(name = "vehicles")
public class Vehicle {


    @Id
    @Column(name = "VEHICLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleId;

    @Column(name = "PLATE_NO")
    private String plateNo;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "MODEL")
    private String model;


    @Column(name = "COLOR")
    private String color;

    @Column(name = "FUEL_TYPE")
    private String fuelType;

    @Column(name = "TRANSMISSION")
    private String transmission;

    @Column(name = "SEAT_CAPACITY")
    private Integer seatCapacity;

    @Column(name = "FUEL_CAPACITY")
    private BigDecimal fuelCapacity;

    @Column(name = "VEHICLE_TAX")
    private Integer vehicleTax;

    @Column(name = "DAILY_RENT")
    private Integer dailyRent;

    @Column(name = "MILEAGE")
    private Integer mileage;

    @Column(name = "LATITUDE")
    private BigDecimal latitude;

    @Column(name = "LONGITUDE")
    private BigDecimal longitude;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "IMAGE")
    private String image;

    public Vehicle() {
    }

    public Vehicle(Integer vehicleId, String plateNo, String brand, String model, String color, String fuelType, String transmission, Integer seatCapacity, BigDecimal fuelCapacity, Integer vehicleTax, Integer dailyRent, Integer mileage, BigDecimal latitude, BigDecimal longitude, String status, String image) {
        this.vehicleId = vehicleId;
        this.plateNo = plateNo;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seatCapacity = seatCapacity;
        this.fuelCapacity = fuelCapacity;
        this.vehicleTax = vehicleTax;
        this.dailyRent = dailyRent;
        this.mileage = mileage;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.image = image;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getSeatCapacity() {
        return seatCapacity;
    }

    public void setSeatCapacity(Integer seatCapacity) {
        this.seatCapacity = seatCapacity;
    }

    public BigDecimal getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(BigDecimal fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public Integer getVehicleTax() {
        return vehicleTax;
    }

    public void setVehicleTax(Integer vehicleTax) {
        this.vehicleTax = vehicleTax;
    }

    public Integer getDailyRent() {
        return dailyRent;
    }

    public void setDailyRent(Integer dailyRent) {
        this.dailyRent = dailyRent;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", plateNo='" + plateNo + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", transmission='" + transmission + '\'' +
                ", seatCapacity=" + seatCapacity +
                ", fuelCapacity=" + fuelCapacity +
                ", vehicleTax=" + vehicleTax +
                ", dailyRent=" + dailyRent +
                ", mileage=" + mileage +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status='" + status + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
