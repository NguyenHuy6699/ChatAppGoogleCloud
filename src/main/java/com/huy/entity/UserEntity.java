package com.huy.entity;
import java.util.ArrayList;
import java.util.List;

import com.huy.constant.TableName;
import com.huy.model.Status;

import jakarta.persistence.*;

@Entity
@Table(name = TableName.user)
public class UserEntity extends BaseEntity {
	@Column(unique = true, nullable = false)
	private String userName;
	private String password;
	private String avatarUrl;
	private String defaultAvatarUrl;
	private String phoneNumber;
	private String fullName;
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	private List<ChatMessage> sentMessages = new ArrayList();
	
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
	private List<ChatMessage> receivedMessages = new ArrayList();
	
	@OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
	private List<FriendRequestEntity> sentFriendRequest= new ArrayList();
	
	@OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
	private List<FriendRequestEntity> receivedFriendRequest = new ArrayList();
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	private List<SessionEntity> sessions = new ArrayList();
	
	private List<Long> friendIds = new ArrayList();
	
	public List<Long> getFriendIds() {
		return friendIds;
	}
	public void setFriends(List<Long> friends) {
		this.friendIds = friends;
	}
	public List<SessionEntity> getSessions() {
		return sessions;
	}
	public void setSessions(List<SessionEntity> sessions) {
		this.sessions = sessions;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public List<FriendRequestEntity> getSentFriendRequest() {
		return sentFriendRequest;
	}
	public void setSentFriendRequest(List<FriendRequestEntity> sentFriendRequest) {
		this.sentFriendRequest = sentFriendRequest;
	}
	public List<FriendRequestEntity> getReceivedFriendRequest() {
		return receivedFriendRequest;
	}
	public void setReceivedFriendRequest(List<FriendRequestEntity> receivedFriendRequest) {
		this.receivedFriendRequest = receivedFriendRequest;
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
	public UserEntity(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}
	public UserEntity() {
		super();
	}
	public Status addFriend(UserEntity friend) {
		Status status = new Status();
		if (!this.friendIds.contains(friend.getId())) {
			this.friendIds.add(friend.getId());
			status.setOk(true);
			status.setMessage("Thêm bạn thành công");
		} else {
			status.setOk(false);
			status.setMessage("Đã là bạn bè");
		}
		return status;
	}
	
	public Status removeFriend(UserEntity friend) {
		Status status = new Status();
		if (this.friendIds.contains(friend.getId())) {
			this.friendIds.remove(friend.getId());
			status.setOk(true);
			status.setMessage("Xóa bạn thành công");
		} else {
			status.setOk(false);
			status.setMessage("Chưa là bạn bè");
		}
		return status;
	}
}
