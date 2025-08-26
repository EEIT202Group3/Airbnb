package com.EEITG3.Airbnb.chat.controller;


import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;

import com.EEITG3.Airbnb.chat.entity.ChatMessage;

@Controller
public class ChatController {
	
	private final SimpMessagingTemplate template;

	@Autowired
    private SimpUserRegistry simpUserRegistry;
    public ChatController(SimpMessagingTemplate template) {
        this.template = template;
    }

    // 廣播訊息（所有訂閱 /topic/public 的人都收到）
    @MessageMapping("/sendMessage")
    @SendTo("/topic/public")
    public ChatMessage broadcast(ChatMessage message) {
        System.out.println("📣 廣播收到：sender=" + message.getSender() + ", content=" + message.getContent());
        return message;
    }

    // 發送給管理員訊息
    @MessageMapping("/privateMessageToAdmin")
    public void sendPrivateToAdmin(@Payload ChatMessage message, Principal principal) {
        // 確保訊息完整性
        message.setType("PRIVATE");
        message.setTimestamp(new Date());
        System.out.println("🟢 訊息來自使用者: " + principal.getName() + message.getSender());
        // Log 發送對象
        System.out.println("🟡 要推送給: " + message.getReceiver());

        System.out.println("🟢 目前所有連線使用者:");
        simpUserRegistry.getUsers().forEach(user -> System.out.println(" - " + user.getName()));
        
        
        // 發送給管理員
        template.convertAndSendToUser(
            "ADMIN",           // 接收者 username
            "/queue/messages", // 目標路徑
            message
        );
        
        
        // 調試日誌
        System.out.println(message.toString());
    }
    
    
    
    @MessageMapping("/adminReply")
    public void adminReply(ChatMessage message, Principal principal) {
        message.setType("ADMIN_REPLY");
        message.setSender(principal.getName()); // 由後端保證 sender 正確
        message.setTimestamp(new Date());
        
        if (message.getReceiver() == null || message.getReceiver().trim().isEmpty()) {
            System.err.println("❌ 錯誤: 接收者為空");
            return;
        }
        
        String receiver = "eszrdx417@gmail.com";
        System.out.println("adminReply" + receiver);
        message.setReceiver(receiver);
        
        // 確保這裡的 receiver 是正確的用戶名
        System.out.println("發送訊息給: " + message.getReceiver());
        System.out.println(message.toString());
        template.convertAndSendToUser(
        	receiver,    // 這裡應該是客戶的用戶名，不是 "ADMIN"
            "/queue/messages",        // 目標路徑
            message
        );
    }

  


}
