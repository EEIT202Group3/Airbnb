package com.EEITG3.Airbnb.chat.config;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.EEITG3.Airbnb.users.entity.CustomerDetails;
import com.EEITG3.Airbnb.users.entity.HostDetails;

import jakarta.persistence.criteria.CriteriaBuilder.Case;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


public class HttpHandshakeInterceptor implements HandshakeInterceptor{


	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
	                             WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
	    
	    System.out.println("=== WebSocket 握手開始 ===");
	    System.out.println(">> beforeHandshake() 被呼叫");

	    if (request instanceof ServletServerHttpRequest servletReq) {
	    	
	        HttpServletRequest req = servletReq.getServletRequest();
	        
	        String path = req.getRequestURI();
	        if (path != null && path.startsWith("/api/admins")) {
	        HttpSession session = req.getSession(false);
	        SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
	        if (ctx == null || !(ctx.getAuthentication() != null && ctx.getAuthentication().isAuthenticated())) {
	            System.out.println("Admin endpoint: not authenticated -> refuse");
	            return false;
	        }
	        String role = determineUserType(ctx.getAuthentication());
	        if (!"ADMIN".equals(role)) {
	            System.out.println("Admin endpoint: not ADMIN -> refuse");
	            return false;
	        }
	        attributes.put("username", ctx.getAuthentication().getName());
	        attributes.put("userType", "ADMIN");
	        attributes.put("sessionId", session.getId());
	        return true;
	    }
	        // 2) 非 admin path: 優先採用已驗證 session
	        HttpSession session = req.getSession(false);
	        if (session != null) {
	            SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
	            if (ctx != null) {
	                Authentication auth = ctx.getAuthentication();
	                if (auth != null && auth.isAuthenticated()) {
	                    attributes.put("username", auth.getName());
	                    attributes.put("userType", determineUserType(auth));
	                    attributes.put("sessionId", session.getId());
	                    return true;
	                }
	            }
	        }

	        // 3) 無認證 session -> 處理訪客（不相信前端提供的 username 做為身份）
	        return handleGuestConnection(req, attributes);
	    }
	    return false;
	}
	
	/**
	 * 統一處理訪客連接
	 */
	private boolean handleGuestConnection(HttpServletRequest req, Map<String, Object> attributes) {
	    // Debug 看看實際握手的路徑與參數
	    System.out.println("Handshake URI: " + req.getRequestURI() + "?" + req.getQueryString());
	    System.out.println(">> beforeHandshake() 被呼叫");

	    
	    // 同時支援 guest / guestInfo
	    String guestParam = req.getParameter("guest");
	    String guestId = req.getParameter("username");

	    if ("true".equalsIgnoreCase(guestParam) && guestId != null) {
	        attributes.put("username", guestId);
	        attributes.put("userType", "GUEST");
	        System.out.println("WebSocket 訪客連接: " + guestId);
	        return true;
	    }

	    System.out.println("非認證用戶且未指定訪客模式，拒絕連接");
	    return false;
	}



	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {}
	/**
     * 根據 Authentication 物件判斷用戶類型
     */
    private String determineUserType(Authentication auth) {
    	// 方法2: 根據用戶權限判斷
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();
            if ("ROLE_HOST".equals(role)) {
                return "HOST";
            } else if ("ROLE_CUSTOMER".equals(role)) {
                return "CUSTOMER";
            } else if ("ROLE_ADMIN".equals(role)) {
            	return "ADMIN";
            }
        }
        return "UNKNOWN";
    	
    	
        // 方法1: 根據 UserDetails 實作類型判斷
    	/*
        if (auth.getPrincipal() instanceof HostDetails) {
            return "HOST";
        } else if (auth.getPrincipal() instanceof CustomerDetails) {
            return "CUSTOMER";
        }
        */
        
        // 方法3: 根據用戶名格式判斷（如果有特定命名規則）
       /* 
        * String username = auth.getName();
        
        if (username.startsWith("host_")) {
            return "HOST";
        } else if (username.startsWith("customer_")) {
            return "CUSTOMER";
        }
        
        return "UNKNOWN";
        */
        
    }
    
    
    

}
