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
        UserDetails userDetails = null;

        //從Cookies抓customer、host的JWT，優先級：host>customer
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                //先去抓jwt_host，如果有就把資料設定成host的並跳出
            	if(cookie.getName().equalsIgnoreCase("jwt_host")) {
            		token = cookie.getValue();
            		try {
						email = jwtService.extractEmail(token);
						userDetails = hostDetailsService.loadUserByUsername(email);
					} catch (Exception e) {
						
					}
            		break;
            	//沒有jwt_host，才去找jwt_customer
            	}else if (cookie.getName().equals("jwt_customer")) {
					token = cookie.getValue();
					try {
						email = jwtService.extractEmail(token);
						userDetails = customerDetailsService.loadUserByUsername(email);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
            }
        }

        //用上面抓好的資料去做驗證
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
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
