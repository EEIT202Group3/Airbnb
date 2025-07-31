package com.EEITG3.Airbnb.users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static void saveCookie(HttpServletResponse response, String token) {
		Cookie cookie = new Cookie("jwt",token);
		cookie.setMaxAge(60*60);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public static void deleteCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("jwt", null);
		cookie.setMaxAge(0);
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
