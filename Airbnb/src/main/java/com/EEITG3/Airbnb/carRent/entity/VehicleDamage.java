package com.EEITG3.Airbnb.carRent.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_damages")
public class VehicleDamage {

    @Id
    @Column(name = "DAMAGE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer damageId;

    @Column(name = "RESERVATION_ID")
    private Integer reservationId;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DAMAGE_NOTE")
    private String damageNote;

    @Column(name = "REPORT_DATE")
    private LocalDateTime reportDate;

    @PrePersist
    public void prePersist() {
        if (this.reportDate == null) {
            this.reportDate = java.time.LocalDateTime.now();
        }
    }

    public VehicleDamage() {
    }

    public VehicleDamage(Integer damageId, Integer reservationId, String imageUrl, String damageNote, LocalDateTime reportDate) {
        this.damageId = damageId;
        this.reservationId = reservationId;
        this.imageUrl = imageUrl;
        this.damageNote = damageNote;
        this.reportDate = reportDate;
    }


    public Integer getDamageId() {
        return damageId;
    }

    public void setDamageId(Integer damageId) {
        this.damageId = damageId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDamageNote() {
        return damageNote;
    }

    public void setDamageNote(String damageNote) {
        this.damageNote = damageNote;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
}
