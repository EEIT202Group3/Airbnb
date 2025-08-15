package com.EEITG3.Airbnb.chat.controller;


import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.EEITG3.Airbnb.chat.entity.ChatMessage;


@Controller
public class ChatController {
	
	private final SimpMessagingTemplate template;

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
    public void sendPrivate(ChatMessage message) {
        template.convertAndSendToUser(
//            message.getReceiver(),  // 接收者 username
            "ADMIN",
        	"/queue/messages",
            message
        );
        System.out.println(message.getSender() + "傳送給ADMIN:" + message.getContent() );
    }
    
    @MessageMapping("/adminReply")
    public void adminReply(ChatMessage message) {
    	template.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
    	System.out.println(message.getSender() + "傳送給 :" + message.getReceiver() + " " + message.getContent() );
    }
  


}
