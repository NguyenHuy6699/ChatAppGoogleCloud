package com.cloudrun.microservicetemplate.huy.dto;

public class UserDTO extends BaseDTO {
	private String userName;
	private String avatarUrl;
	private String defaultAvatarUrl;
	private String phoneNumber;
	private String fullName;
	private String lastMessage;
	private int unreadMessages;
	private String type;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getDefaultAvatarUrl() {
		return defaultAvatarUrl;
	}
	public void setDefaultAvatarUrl(String defaultAvatarUrl) {
		this.defaultAvatarUrl = defaultAvatarUrl;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public int getUnreadMessages() {
		return unreadMessages;
	}
	public void setUnreadMessages(int unreadMessages) {
		this.unreadMessages = unreadMessages;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
