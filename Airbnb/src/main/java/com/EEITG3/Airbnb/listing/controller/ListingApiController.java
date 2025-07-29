package com.EEITG3.Airbnb.listing.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.listing.repository.ListingRepository;

@RestController
@RequestMapping("/api/listings")
public class ListingApiController {

    @Autowired
    private ListingRepository listingRepository;

    @GetMapping("/{id}")
    public ResponseEntity<?> getListingById(@PathVariable Integer id) {
        return listingRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}