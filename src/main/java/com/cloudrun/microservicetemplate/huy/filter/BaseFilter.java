package com.cloudrun.microservicetemplate.huy.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.core.Ordered;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;

public abstract class BaseFilter implements Filter, Ordered {
	
	protected final ObjectMapper objectMapper;
	
	public BaseFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	protected <T> void writeResponse (HttpServletResponse resp, int respType, boolean status, String message, List<T> data) throws IOException, ServletException {
		resp.setContentType("application/json;charset=UTF-8");
		BaseResponse<T> baseResp = new BaseResponse<>();
		baseResp.setCode(respType);
		baseResp.setOk(status);
		baseResp.setMessage(message);
		baseResp.setDataList(data);
		String baseRespJson = objectMapper.writeValueAsString(baseResp);
		resp.getWriter().write(baseRespJson);
	}
}
