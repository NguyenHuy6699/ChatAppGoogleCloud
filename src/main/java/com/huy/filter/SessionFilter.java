package com.huy.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huy.constant.Paths;
import com.huy.constant.ResponseType;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class SessionFilter extends BaseFilter {

	@Autowired
	public SessionFilter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		HttpSession session = req.getSession(false);
		String path = req.getServletPath();

		if (
				path.contains(Paths.login) || 
				path.contains(Paths.register) || 
				path.contains(Paths.avatar_url) ||
				path.contains(Paths.default_avatar_url)){
			chain.doFilter(request, response);
			return;
		}
		if (session == null) {
			if (path.contains(Paths.startup_login)) {
				writeResponse(resp, ResponseType.startup_session_expired, false, "Phiên đăng nhập đã hết hạn", null);
			} else {
				writeResponse(resp, ResponseType.session_expired, false, "Phiên đăng nhập đã hết hạn", null);
			}
			return;
		} 
		chain.doFilter(request, response);
	}

}
