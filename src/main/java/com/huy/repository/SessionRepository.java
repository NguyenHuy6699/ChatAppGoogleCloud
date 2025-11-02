package com.huy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huy.entity.SessionEntity;
import com.huy.entity.UserEntity;

import jakarta.transaction.Transactional;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
	SessionEntity findByDeviceId(String deviceId);
	
	List<SessionEntity> findAllByUser(UserEntity user);
	
	@Transactional
	void deleteBySessionId(String sessionId);
	
	SessionEntity findBySessionId(String sessionId);
}
