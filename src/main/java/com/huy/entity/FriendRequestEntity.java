package com.huy.entity;

import com.huy.constant.TableName;
import com.huy.status.FriendRequestStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = TableName.friendRequest)
public class FriendRequestEntity extends BaseEntity {
	@ManyToOne
	@JoinColumn(name = "sender_username", referencedColumnName = "userName")
	private UserEntity sender;
	
	@ManyToOne
	@JoinColumn(name = "receiver_username", referencedColumnName = "userName")
	private UserEntity receiver;
	
	@Enumerated(EnumType.STRING)
	private FriendRequestStatus status;
	
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

	public FriendRequestStatus getStatus() {
		return status;
	}

	public void setStatus(FriendRequestStatus status) {
		this.status = status;
	}
}