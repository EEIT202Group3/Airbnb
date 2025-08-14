package com.EEITG3.Airbnb.carRent.service;

import com.EEITG3.Airbnb.carRent.entity.VehicleDamage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleDamageService {
    String saveToFrontPublic(MultipartFile file) throws IOException;

    void deletePhysicalByUrl(String imageUrl);

    List<VehicleDamage> listByReservation(Integer reservationId);

    @Transactional
    List<VehicleDamage> create(Integer reservationId, String note, List<MultipartFile> images) throws IOException;

    @Transactional
    VehicleDamage updateNote(Integer damageId, String note);

    @Transactional
    void delete(Integer damageId);
}
