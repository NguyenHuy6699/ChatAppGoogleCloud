package com.cloudrun.microservicetemplate.huy.filter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cloudrun.microservicetemplate.huy.constant.FilterOrder;
import com.cloudrun.microservicetemplate.huy.constant.ResponseType;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DuplicateRequestFilter extends BaseFilter {
	private final Set<String> processingRequests = ConcurrentHashMap.newKeySet();

	@Autowired
	public DuplicateRequestFilter(ObjectMapper objectMapper) {
		super(objectMapper);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		String url = req.getRequestURL().toString();
		String deviceId = req.getHeader("deviceId");

		if (deviceId == null) {
			writeResponse(resp, ResponseType.device_undefined, false, "Thiết bị không hỗ trợ", null);
			return;
		}

		String token = url + "_" + deviceId;

		boolean requestIsNotProcessing = processingRequests.add(token);
		if (!requestIsNotProcessing) {
			return;
		}
		try {
			chain.doFilter(request, response);
		} finally {
			processingRequests.remove(token);
		}
	}

	@Override
	public int getOrder() {
		return FilterOrder.Duplicate.ordinal();
	}
}