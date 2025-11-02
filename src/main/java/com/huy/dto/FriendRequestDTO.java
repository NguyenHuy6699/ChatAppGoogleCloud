package com.huy.dto;

import com.huy.status.FriendRequestStatus;

public class FriendRequestDTO extends BaseDTO {
	private UserDTO sender;
	private UserDTO receiver;
	
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
}
