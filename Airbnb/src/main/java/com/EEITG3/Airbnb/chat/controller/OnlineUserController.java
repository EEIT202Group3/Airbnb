package com.EEITG3.Airbnb.chat.controller;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EEITG3.Airbnb.chat.config.OnlineUserManager;

@RestController
@RequestMapping("/api")
public class OnlineUserController {
	
	private final OnlineUserManager userManager;

    public OnlineUserController(OnlineUserManager userManager) {
        this.userManager = userManager;
    }

    @GetMapping("/online-users")
    public Set<String> getOnlineUsers() {
    	System.out.println("onlineusers" + userManager.getOnlineUsers());
        return userManager.getOnlineUsers(); // 例如: ["guest_123", "user_456", "ADMIN"]
    }

}
