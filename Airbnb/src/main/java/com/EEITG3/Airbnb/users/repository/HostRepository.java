package com.EEITG3.Airbnb.users.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.users.entity.Host;

public interface HostRepository extends JpaRepository<Host, String> {

	@Query("SELECT h FROM Host h WHERE h.email = :email")
	Optional<Host> findHostByEmail(@Param("email") String email);
}
