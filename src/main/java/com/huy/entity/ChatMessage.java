package com.huy.entity;

import java.time.LocalDateTime;

import com.huy.constant.TableName;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = TableName.message)
public class ChatMessage extends BaseEntity {
	private String message;
	private LocalDateTime timestamp; 
	
	@ManyToOne
	@JoinColumn(name = "sender_username", referencedColumnName = "userName")
	private UserEntity sender;
	
	@ManyToOne
	@JoinColumn(name = "receiver_username", referencedColumnName = "userName")
	private UserEntity receiver;
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public UserEntity getSender() {
		return sender;
	}
	public void setSender(UserEntity sender) {
		this.sender = sender;
	}
	public UserEntity getReceiver() {
		return receiver;
	}
	public void setReceiver(UserEntity receiver) {
		this.receiver = receiver;
	}
	public ChatMessage(String message, LocalDateTime timestamp) {
		super();
		this.message = message;
		this.timestamp = timestamp;
	}
	public ChatMessage() {
		super();
	}
}
