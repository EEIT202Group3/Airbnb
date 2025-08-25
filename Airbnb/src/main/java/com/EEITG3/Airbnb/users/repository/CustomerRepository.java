package com.EEITG3.Airbnb.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.users.dto.MonthlyRegist;
import com.EEITG3.Airbnb.users.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, String> {

	//透過 email 找 Customer
	@Query("SELECT c FROM Customer c WHERE c.email = :email")
	Optional<Customer> findCustomerByEmail(@Param("email") String email);
	
	//透過 token 找 Customer(驗證信要用)
	@Query("SELECT c FROM Customer c WHERE c.verificationToken = :token")
	Optional<Customer> findCustomerByToken(@Param("token") String token);
	
	//透過email模糊搜尋
	@Query("SELECT c FROM Customer c WHERE c.email LIKE :email")
	List<Customer> findLikeByEmail(@Param("email") String email);
	
	//透過username模糊搜尋
	@Query("SELECT c FROM Customer c WHERE c.username LIKE :username")
	List<Customer> findLikeByUsername(@Param("username") String username);
	
	//透過phone模糊搜尋
	@Query("SELECT c FROM Customer c WHERE c.phone LIKE :phone")
	List<Customer> findLikeByPhone(@Param("phone") String phone);
	
	//查詢過去12個月的註冊人數
	@Query(value="""
			;WITH bounds AS (
			  SELECT
			    DATETRUNC(month, DATEADD(month, -11, GETDATE())) AS start_m,
			    DATETRUNC(month, GETDATE())                      AS end_m
			),
			months AS (
			  SELECT start_m AS m FROM bounds
			  UNION ALL
			  SELECT DATEADD(month, 1, m)
			  FROM months, bounds
			  WHERE m < (SELECT end_m FROM bounds)
			)
			SELECT
			  FORMAT(m.m, 'yyyy-MM')        AS [month],
			  ISNULL(c.cnt, 0)              AS registrations
			FROM months m
			OUTER APPLY (
			  SELECT COUNT(*) AS cnt
			  FROM customers
			  WHERE DATETRUNC(month, created_at) = m.m
			) c
			ORDER BY m.m
			OPTION (MAXRECURSION 12);
			""", nativeQuery = true)
	public List<MonthlyRegist> getMonthlyRegist();

}
