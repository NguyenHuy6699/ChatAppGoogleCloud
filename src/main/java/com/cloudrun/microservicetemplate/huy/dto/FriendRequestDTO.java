package com.cloudrun.microservicetemplate.huy.dto;

import com.cloudrun.microservicetemplate.huy.status.FriendRequestStatus;

public class FriendRequestDTO extends BaseDTO {
	private UserDTO sender;
	private UserDTO receiver;
	private String type = "FRIEND_REQUEST";
	
	private FriendRequestStatus status;
	
	public UserDTO getSender() {
		return sender;
	}

	public void setSender(UserDTO sender) {
		this.sender = sender;
	}

	public UserDTO getReceiver() {
		return receiver;
	}

	public void setReceiver(UserDTO receiver) {
		this.receiver = receiver;
	}

	public FriendRequestStatus getStatus() {
		return status;
	}

	public void setStatus(FriendRequestStatus status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
