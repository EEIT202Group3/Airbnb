package com.EEITG3.Airbnb.chat.entity;

public class ChatMessage {
	private String sender;
    private String content;
    private String receiver; //
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    // getter/setter

}
