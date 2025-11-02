package com.huy.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.huy.serviceImpl.SessionServiceImpl;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionListener implements HttpSessionListener {
	@Autowired
	SessionServiceImpl sessionService;
	
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		String sessionId = se.getSession().getId();
		boolean deleteSuccess = sessionService.deleteBySessionId(sessionId);
		if (deleteSuccess) {
			System.out.println("session deleted: " + sessionId);
		} else {
			System.out.println("session delete failed: " + sessionId);
		}
	}
}
