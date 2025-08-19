package com.EEITG3.Airbnb.chat.entity;

import java.util.Date;

public class ChatMessage {
	private String sender;
    private String content;
    private String receiver; //
    private String type;
    private Date timestamp;
    private Boolean isGuest;
 // 新增欄位
//    private String senderName;
//    private String senderEmail;
//    private String senderPhone;
    
    public ChatMessage() {}
    
    public ChatMessage(String content, String sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = new Date();
    }
    
    
	public ChatMessage(String sender, String content, String receiver, Date timestamp) {
		super();
		this.sender = sender;
		this.content = content;
		this.receiver = receiver;
		this.timestamp = timestamp;
	}

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
	
	public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

	public Boolean getIsGuest() {
		return isGuest;
	}

	public void setIsGuest(Boolean isGuest) {
		this.isGuest = isGuest;
	}

//	public String getSenderName() {
//		return senderName;
//	}
//
//	public void setSenderName(String senderName) {
//		this.senderName = senderName;
//	}
//
//	public String getSenderEmail() {
//		return senderEmail;
//	}
//
//	public void setSenderEmail(String senderEmail) {
//		this.senderEmail = senderEmail;
//	}
//
//	public String getSenderPhone() {
//		return senderPhone;
//	}
//
//	public void setSenderPhone(String senderPhone) {
//		this.senderPhone = senderPhone;
//	}

	@Override
	public String toString() {
		return "ChatMessage [sender=" + sender + ", content=" + content + ", receiver=" + receiver + ", type=" + type
				+ ", timestamp=" + timestamp + ", isGuest=" + isGuest +  "]";
	}

    // getter/setter

}
