package com.EEITG3.Airbnb.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EEITG3.Airbnb.users.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, String> {

}
