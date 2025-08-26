package com.EEITG3.Airbnb.chat.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class PresenceEventListener {
	
	private final OnlineUserManager userManager;
    private final SimpMessagingTemplate messagingTemplate;

    public PresenceEventListener(OnlineUserManager userManager,
                                 SimpMessagingTemplate messagingTemplate) {
        this.userManager = userManager;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String username = sha.getUser().getName();
        userManager.addUser(username);
        System.out.println("監聽器成功啟用" +username);

        // 廣播最新用戶列表
        messagingTemplate.convertAndSend("/topic/users", userManager.getOnlineUsers());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() != null) {
            String username = sha.getUser().getName();
            userManager.removeUser(username);

            // 廣播最新用戶列表
            messagingTemplate.convertAndSend("/topic/users", userManager.getOnlineUsers());
        }
    }

}
