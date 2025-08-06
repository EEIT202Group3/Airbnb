package com.EEITG3.Airbnb.carRent.repository;

import com.EEITG3.Airbnb.carRent.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
