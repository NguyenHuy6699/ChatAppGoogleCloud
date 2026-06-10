package com.cloudrun.microservicetemplate.huy.dto;

import java.time.LocalDateTime;

public class ChatMessageDTO extends BaseDTO {
	private String senderUserName;
	private String receiverUserName;
	private String message;
	private LocalDateTime timestamp;
	private String type;
	private boolean read;
	
	public String getSenderUserName() {
		return senderUserName;
	}
	public void setSenderUserName(String senderUserName) {
		this.senderUserName = senderUserName;
	}
	public String getReceiverUserName() {
		return receiverUserName;
	}
	public void setReceiverUserName(String receiverUserName) {
		this.receiverUserName = receiverUserName;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public ChatMessageDTO(String senderUserName, String receiverUserName, String message, LocalDateTime timestamp) {
		this.senderUserName = senderUserName;
		this.receiverUserName = receiverUserName;
		this.message = message;
		this.timestamp = timestamp;
	}
	public ChatMessageDTO() {
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
}
