package com.EEITG3.Airbnb.logging;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//這個class的功能在於...
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

	private static final String MDC_REQUEST_ID = "requestId";
	private static final String HEADER_REQUEST_ID = "X-Request-Id";
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestId = request.getHeader(HEADER_REQUEST_ID);
		if(requestId == null||requestId.isBlank()) {
			requestId = UUID.randomUUID().toString();
		}
		MDC.put(MDC_REQUEST_ID, requestId);
		response.setHeader(HEADER_REQUEST_ID, requestId);
		
		try {
			filterChain.doFilter(request, response);
		}finally {
			MDC.remove(MDC_REQUEST_ID);
			MDC.remove("userId");
		}
	}

}
