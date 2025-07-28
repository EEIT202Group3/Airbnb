package com.EEITG3.Airbnb.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.users.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

	//透過 email 找 Customer
	@Query("SELECT c FROM Customer c WHERE c.email = :email")
	Customer findCustomerByEmail(@Param("email") String email);
}
