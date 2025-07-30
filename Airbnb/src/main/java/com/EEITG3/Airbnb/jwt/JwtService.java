package com.EEITG3.Airbnb.jwt;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

	//產生 JWT Token
	String generateToken(String email);
	
	//從 JWT Token 中取出 email
	String extractEmail(String token);

	//驗證 JWT Token 是否有效
	boolean validateToken(String token, UserDetails customerDetails);

}