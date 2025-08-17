package com.EEITG3.Airbnb.carRent.controller;

import com.EEITG3.Airbnb.carRent.entity.Location;
import com.EEITG3.Airbnb.carRent.repository.LocationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final LocationRepository locationRepository;

    public LocationController(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> one(@PathVariable Integer id) {
        return locationRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
