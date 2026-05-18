package com.cloudrun.microservicetemplate.huy.service;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;

public interface ChatService {
	void save(ChatMessage message);
	BaseResponse<ChatMessageDTO> getChatHistory(String userA, String userB, int from, int limit);
}
