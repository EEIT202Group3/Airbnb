package com.EEITG3.Airbnb.users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static void saveCustomerCookie(HttpServletResponse response, String token) {
		Cookie cookie = new Cookie("jwt_customer",token);
		cookie.setMaxAge(60*60);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static void deleteCustomerCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("jwt_customer", null);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static void saveHostCookie(HttpServletResponse response, String token) {
		Cookie cookie = new Cookie("jwt_host",token);
		cookie.setMaxAge(60*60);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static void deleteHostCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("jwt_host", null);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
