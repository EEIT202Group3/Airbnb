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
	        HttpSession session = req.getSession(false);
	        
	        // 先處理已認證用戶
	        if (session != null) {
	            SecurityContext context = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");
	            if (context != null) {
	                Authentication auth = context.getAuthentication();
	                if (auth != null && auth.isAuthenticated()) {
	                    String username = auth.getName();
	                    String userType = determineUserType(auth);
	                    
	                    // 只處理明確的用戶角色
	                    if ("HOST".equals(userType) || "CUSTOMER".equals(userType) || "ADMIN".equals(userType)) {
	                        attributes.put("username", username);
	                        attributes.put("userType", userType);
	                        attributes.put("sessionId", session.getId());
	                        
	                        switch (userType) {
	                            case "HOST":
	                                attributes.put("hostId", username);
	                                System.out.println("WebSocket主機用戶登入:" + username);
	                                break;
	                            case "CUSTOMER":
	                                attributes.put("customerId", username);
	                                System.out.println("WebSocket客戶登入:" + username);
	                                break;
	                            case "ADMIN":
	                                attributes.put("adminId", username);
	                                System.out.println("WebSocket管理員登入:" + username);
	                                break;
	                        }
	                        
	                        System.out.println("WebSocket 連接驗證成功: " + userType + " - " + username);
	                        return true;
	                    }
	                    // 如果 determineUserType 返回 "UNKNOWN" 或其他值，則繼續下面的訪客處理
	                }
	            }
	        }
	        
	        // 統一的訪客處理邏輯
	        return handleGuestConnection(req, attributes);
	    }
	    
	    System.out.println("WebSocket 握手驗證失敗 - 無有效認證或訪客標識");
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
			Exception exception) {
		
	}
	/**
	 * 獲取客戶端 IP
	 */
	private String getClientIpAddress(HttpServletRequest request) {
	    String xForwardedFor = request.getHeader("X-Forwarded-For");
	    if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
	        return xForwardedFor.split(",")[0].trim();
	    }
	    
	    String xRealIp = request.getHeader("X-Real-IP");
	    if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
	        return xRealIp;
	    }
	    
	    return request.getRemoteAddr();
	}

	/**
	 * 生成訪客 ID
	 */
	private String generateGuestId(String seed) {
	    return Math.abs(seed.hashCode()) + "_" + (System.currentTimeMillis() % 100000);
	}
	
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
