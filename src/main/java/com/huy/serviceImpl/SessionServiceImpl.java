package com.huy.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.huy.entity.SessionEntity;
import com.huy.entity.UserEntity;
import com.huy.repository.SessionRepository;

import jakarta.servlet.http.HttpSession;

@Service
public class SessionServiceImpl {
	@Autowired
	SessionRepository sessionRepo;
	
	public boolean save(SessionEntity session) {
		return sessionRepo.save(session) != null;
	}
	
	public boolean save(HttpSession session, UserEntity user, String fcmToken, String deviceId) {
		SessionEntity existingSession = sessionRepo.findByDeviceId(deviceId);
		if (existingSession != null) {
			existingSession.setUser(user);
			existingSession.setSessionId(session.getId());
			existingSession.setFcmToken(fcmToken);
			return sessionRepo.save(existingSession) != null;
		}
		SessionEntity sessionEntity = new SessionEntity(session.getId(), fcmToken, user, deviceId);
		return sessionRepo.save(sessionEntity) != null;
	}
	
	public boolean deleteBySessionId(String sessionId) {
		sessionRepo.deleteBySessionId(sessionId);
		boolean success = sessionRepo.findBySessionId(sessionId) == null;
		return success;
	}
}
