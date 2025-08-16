package com.EEITG3.Airbnb.carRent.controller;

import com.EEITG3.Airbnb.carRent.entity.VehicleDamage;
import com.EEITG3.Airbnb.carRent.service.VehicleDamageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class VehicleDamageController {

    private final VehicleDamageService service;
    public VehicleDamageController(VehicleDamageService service) { this.service = service; }

    @GetMapping("/reservations/{reservationId}/damages")
    public List<VehicleDamage> list(@PathVariable Integer reservationId) {
        return service.listByReservation(reservationId);
    }

    @PostMapping(value = "/reservations/{reservationId}/damages", consumes = "multipart/form-data")
    public List<VehicleDamage> create(
            @PathVariable Integer reservationId,
            @RequestPart(value = "note", required = false) String note,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        return service.create(reservationId, note, images);
    }

    @PutMapping("/damages/{damageId}/note")
    public VehicleDamage updateNote(@PathVariable Integer damageId,
                                    @RequestBody java.util.Map<String,String> body) {
        String note = body.get("note");
        return service.updateNote(damageId, note);
    }

    @DeleteMapping("/damages/{damageId}")
    public void delete(@PathVariable Integer damageId) {
        service.delete(damageId);
    }
}
