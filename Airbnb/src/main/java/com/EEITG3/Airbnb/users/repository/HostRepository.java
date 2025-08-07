package com.EEITG3.Airbnb.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.users.entity.Host;

public interface HostRepository extends JpaRepository<Host, String> {

	@Query("SELECT h FROM Host h WHERE h.email = :email")
	Optional<Host> findHostByEmail(@Param("email") String email);
	
	//透過 token 找 Customer(驗證信要用)
	@Query("SELECT h FROM Host h WHERE h.verificationToken = :token")
	Optional<Host> findHostByToken(@Param("token") String token);
	
	
	//透過email模糊搜尋
	@Query("SELECT h FROM Host h WHERE h.email LIKE :email")
	List<Host> findLikeByEmail(@Param("email") String email);
		
	//透過username模糊搜尋
	@Query("SELECT h FROM Host h WHERE h.username LIKE :username")
	List<Host> findLikeByUsername(@Param("username") String username);
	
	//透過phone模糊搜尋
	@Query("SELECT h FROM Host h WHERE h.phone LIKE :phone")
	List<Host> findLikeByPhone(@Param("phone") String phone);
}
