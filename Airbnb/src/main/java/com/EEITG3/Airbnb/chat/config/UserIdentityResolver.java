package com.EEITG3.Airbnb.chat.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.EEITG3.Airbnb.jwt.JwtService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class UserIdentityResolver {
	 private final JwtService jwtService;

	    public UserIdentityResolver(JwtService jwtService) {
	        this.jwtService = jwtService;
	    }

	    // 給 5173 用，只判斷 jwt
	    public String resolveJwtOnly(HttpServletRequest request) {
	        String hostToken = getJwtFromCookie(request, "jwt_host");
	        if (hostToken != null) {
	            try {
	                String role = jwtService.extractRole(hostToken);
	                if ("ROLE_HOST".equals(role)) {
	                    return jwtService.extractEmail(hostToken);
	                }
	            } catch (Exception e) {
	                System.out.println("⚠️ jwt_host 解析失敗：" + e.getMessage());
	            }
	        }

	        String customerToken = getJwtFromCookie(request, "jwt_customer");
	        if (customerToken != null) {
	            try {
	                String role = jwtService.extractRole(customerToken);
	                if ("ROLE_CUSTOMER".equals(role)) {
	                    return jwtService.extractEmail(customerToken);
	                }
	            } catch (Exception e) {
	                System.out.println("⚠️ jwt_customer 解析失敗：" + e.getMessage());
	            }
	        }

	        return "guest";
	    }

	    // 給 5174 用，只判斷 session
	    public String resolveSessionOnly(HttpServletRequest request) {
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
	            return "ADMIN";
	        }
	        return "ADMIN";
	    }

	    private String getJwtFromCookie(HttpServletRequest request, String name) {
	        if (request.getCookies() == null) return null;
	        for (Cookie cookie : request.getCookies()) {
	            if (name.equals(cookie.getName())) {
	                return cookie.getValue();
	            }
	        }
	        return null;
	    }
}
