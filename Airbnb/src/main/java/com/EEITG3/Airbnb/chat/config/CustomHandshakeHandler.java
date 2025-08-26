package com.EEITG3.Airbnb.chat.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomHandshakeHandler  extends DefaultHandshakeHandler{

    private final OnlineUserManager onlineUserManager;
	
	 private final UserIdentityResolver identityResolver;

	    public CustomHandshakeHandler(UserIdentityResolver identityResolver, OnlineUserManager onlineUserManager) {
	        this.identityResolver = identityResolver;
	        this.onlineUserManager = onlineUserManager;
	    }


	    @Override
	    protected Principal determineUser(ServerHttpRequest request,
	                                      WebSocketHandler wsHandler,
	                                      Map<String, Object> attributes) {

	        if (request instanceof ServletServerHttpRequest servletRequest) {
	            HttpServletRequest httpRequest = servletRequest.getServletRequest();
	            String origin = httpRequest.getHeader("Origin");
	            String username;

	            if (origin != null && origin.contains("5174")) {
	                // 只處理 JWT
	                username = identityResolver.resolveSessionOnly(httpRequest);
	                System.out.println("determainUser:" + username);
	            } else if (origin != null && origin.contains("5173")) {
	                // 只處理 JSESSIONID
	                username = identityResolver.resolveJwtOnly(httpRequest);
	            } else {
	                username = "guest";
	            }
	            onlineUserManager.addUser(username);
	            System.out.println("✅ WebSocket 認證來源: " + origin + " -> 使用者: " + username);
	            final String finalUsername = username;
	            return () -> finalUsername;
	        }

	        return () -> "guest";
	    }

}
