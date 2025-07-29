package com.EEITG3.Airbnb.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.EEITG3.Airbnb.users.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {


Customer findByUsername(String username);
}
