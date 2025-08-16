package com.EEITG3.Airbnb.logging;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//這個class的功能是...
@Component
@Order(Ordered.LOWEST_PRECEDENCE-10)
public class HttpLoggingFilter extends OncePerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(HttpLoggingFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		long start = System.currentTimeMillis();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
	    MDC.put("userId", userId);
	    
	    try {
	    	filterChain.doFilter(request, response);
	    }finally {
	    	long cost = System.currentTimeMillis() - start;
            // 只記錄摘要，不含敏感資料與 Body
            log.info("{} {} status={} cost={}ms ua=\"{}\" ip={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    cost,
                    safe(request.getHeader("User-Agent")),
                    request.getRemoteAddr());
		}
	}
	
	private String safe(String s) {
        if (s == null) return "";
        // 避免換行污染日誌
        return s.replaceAll("[\\r\\n]+", " ");
    }

}
