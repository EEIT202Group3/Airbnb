package com.EEITG3.Airbnb.users;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static void saveCustomerCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("jwt_customer", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/customers")
                .maxAge(60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
	
	public static void deleteCustomerCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("jwt_customer", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/customers")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
	
	public static void saveHostCookie(HttpServletResponse response, String token) {
		ResponseCookie cookie = ResponseCookie.from("jwt_host", token)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/hosts")
                .maxAge(60 * 60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
	public static void deleteHostCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("jwt_host", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/hosts")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
	
}
