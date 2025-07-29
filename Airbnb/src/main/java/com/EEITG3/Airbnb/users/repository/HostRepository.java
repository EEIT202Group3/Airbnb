package com.EEITG3.Airbnb.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EEITG3.Airbnb.users.entity.Host;

public interface HostRepository extends JpaRepository<Host, String> {

}
