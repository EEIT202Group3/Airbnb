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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private JwtService jwtService;
	private CustomerDetailsService customerDetailsService;
	
	@Autowired
	public JwtFilter(JwtService jwtService, CustomerDetailsService customerDetailsService) {
		this.jwtService = jwtService;
		this.customerDetailsService = customerDetailsService;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
        String token = null;
        String email = null;

        //從Cookies抓JWT
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    try {
                        email = jwtService.extractEmail(token);
                    } catch (Exception e) {
                        // 無效 token（可能過期、篡改等），略過處理
                    }
                    break;
                }
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails customerDetails = customerDetailsService.loadUserByUsername(email);
            if (jwtService.validateToken(token, customerDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(customerDetails, null, customerDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
		
	}
}
