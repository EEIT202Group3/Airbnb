package com.EEITG3.Airbnb.chat.config;


import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	
	@Bean
	public DefaultHandshakeHandler handshakeHandler() {
	    return new DefaultHandshakeHandler() {
	        @Override
	        protected Principal determineUser(ServerHttpRequest request,
	                                          WebSocketHandler wsHandler,
	                                          Map<String, Object> attributes) {
	        	System.out.println(">> handleGuestConnection() 被呼叫");
	            // 把 username 提前處理為 final
	            final String userId = (String) attributes.get("username");
	            final String username = (userId != null) ? userId : "guest_" + System.currentTimeMillis();
	            System.out.println("✅ 成功設定 Principal username: " + username);

	            // 包裝成 Principal
	            return () -> username;
	        }
	    };
	}
	
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 設定前端連線端點，例如 ws://localhost:8080/ws-chat
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new HttpHandshakeInterceptor())
                .setHandshakeHandler(handshakeHandler());// 允許跨域
//                .withSockJS(); // 支援 SockJS fallback
        System.out.println("測試連接~");	
    }	
	
	@Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客戶端訂閱的目標前綴（伺服器推送用）
        registry.enableSimpleBroker("/topic", "/queue"); //不能使用 /user 會失敗

        // 客戶端發送訊息時的目標前綴（伺服器接收用）
        registry.setApplicationDestinationPrefixes("/app");

        // /user 開頭的訊息會送到指定使用者
        registry.setUserDestinationPrefix("/user");
    }
	
	@Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // ✅ 將 attributes 中的 Principal 設為 STOMP user
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (accessor != null && accessor.getUser() == null) {
                    Object raw = accessor.getHeader("simpSessionAttributes");

                    if (raw instanceof Map sessionAttributes) {
                        Object user = sessionAttributes.get("user");
                        if (user instanceof Principal principal) {
                            accessor.setUser(principal); // ✅ 設定給 STOMP session
                        }
                    }
                }
                return message;
            }
        });
    }
   
}

