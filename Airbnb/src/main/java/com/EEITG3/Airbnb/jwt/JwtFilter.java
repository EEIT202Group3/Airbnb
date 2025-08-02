package com.EEITG3.Airbnb.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.EEITG3.Airbnb.users.service.CustomerDetailsService;
import com.EEITG3.Airbnb.users.service.HostDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private JwtService jwtService;
	private CustomerDetailsService customerDetailsService;
	private HostDetailsService hostDetailsService;
	
	@Autowired
	public JwtFilter(JwtService jwtService, CustomerDetailsService customerDetailsService, HostDetailsService hostDetailsService) {
		this.jwtService = jwtService;
		this.customerDetailsService = customerDetailsService;
		this.hostDetailsService = hostDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
        String token = null;
        String email = null;
        String role = null;

        //從Cookies抓JWT
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    try {
                        email = jwtService.extractEmail(token);
                        role = jwtService.extractRole(token);
                    } catch (Exception e) {
                        // 無效 token（可能過期、篡改等），略過處理
                    }
                    break;
                }
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
            	UserDetails userDetails = null;
            	if("CUSTOMER".equalsIgnoreCase(role)) {
            		userDetails = customerDetailsService.loadUserByUsername(email);
            	}else if ("HOST".equalsIgnoreCase(role)) {
					userDetails = hostDetailsService.loadUserByUsername(email);
				}else {
					throw new RuntimeException("角色錯誤");
				}
            	
            	if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
			} catch (Exception e) {
				// TODO: handle exception
			}    
        }

        filterChain.doFilter(request, response);
		
	}
}
