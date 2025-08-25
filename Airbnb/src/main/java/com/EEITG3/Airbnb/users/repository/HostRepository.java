package com.EEITG3.Airbnb.users.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EEITG3.Airbnb.users.dto.MonthlyRegist;
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
			  FROM hosts
			  WHERE DATETRUNC(month, created_at) = m.m
			) c
			ORDER BY m.m
			OPTION (MAXRECURSION 12);
			""", nativeQuery = true)
	public List<MonthlyRegist> getMonthlyRegist();
	
	//查全部房東
	@Query(value="""
			SELECT COUNT(*) AS total_hosts
			FROM hosts;
			""",nativeQuery=true)
	public Integer getTotalHosts();
	
	//查已驗證客戶
	@Query(value="""
			SELECT COUNT(*) AS verified_hosts
			FROM hosts
			WHERE is_verified = 1
			""",nativeQuery=true)
	public Integer getVerifiedHosts();
}
