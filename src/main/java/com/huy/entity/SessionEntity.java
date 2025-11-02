package com.huy.entity;

import com.huy.constant.TableName;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = TableName.session)
public class SessionEntity extends BaseEntity {
	
	private String sessionId;
	private String fcmToken;
	private String deviceId;
	
	@ManyToOne
	@JoinColumn(name = "user", referencedColumnName = "userName")
	private UserEntity user;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getFcmToken() {
		return fcmToken;
	}

	public void setFcmToken(String fcmToken) {
		this.fcmToken = fcmToken;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
	public SessionEntity(String sessionId, String fcmToken, UserEntity user, String deviceId) {
		super();
		this.sessionId = sessionId;
		this.fcmToken = fcmToken;
		this.user = user;
		this.deviceId = deviceId;
	}
	
	public SessionEntity() {
		
	}
}
