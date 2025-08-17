package com.EEITG3.Airbnb.chat.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChatWebSocketHandler extends TextWebSocketHandler {
	/*
	 // 儲存 userId → session
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 連線建立時可透過 Query 參數或 Header 帶 userId
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            System.out.println("User connected: " + userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = mapper.readTree(message.getPayload());
        String toUser = json.get("toUser").asText();
        String content = json.get("content").asText();
        String fromUser = getUserId(session);

        // 封裝訊息
        ObjectNode msgNode = mapper.createObjectNode();
        msgNode.put("fromUser", fromUser);
        msgNode.put("content", content);
        msgNode.put("ts", System.currentTimeMillis());

        // 找到對方 session
        WebSocketSession targetSession = userSessions.get(toUser);
        if (targetSession != null && targetSession.isOpen()) {
            targetSession.sendMessage(new TextMessage(msgNode.toString()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            System.out.println("User disconnected: " + userId);
        }
    }

    private String getUserId(WebSocketSession session) {
        // 從 URL query 取得，例如 ws://localhost:8080/ws/chat?userId=123
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("userId=")) {
            return query.substring(7);
        }
        return null;
    }
    */
}

