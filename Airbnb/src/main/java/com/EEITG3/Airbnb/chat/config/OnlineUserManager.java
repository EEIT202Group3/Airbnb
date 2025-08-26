package com.EEITG3.Airbnb.chat.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class OnlineUserManager {

	 private final Set<String> onlineUsers = ConcurrentHashMap.newKeySet();

	    public void addUser(String username) {
	        onlineUsers.add(username);
	    }

	    public void removeUser(String username) {
	        onlineUsers.remove(username);
	    }

	    public Set<String> getOnlineUsers() {
	        return onlineUsers;
	    }
}
