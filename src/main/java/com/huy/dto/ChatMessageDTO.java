package com.huy.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ChatMessageDTO {
	private String senderUserName;
	private String receiverUserName;
	private String message;
	private LocalDateTime timestamp;
	
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
}
