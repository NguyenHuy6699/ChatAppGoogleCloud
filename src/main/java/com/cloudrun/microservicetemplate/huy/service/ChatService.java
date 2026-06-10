package com.cloudrun.microservicetemplate.huy.service;

import java.util.List;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;

public interface ChatService {
	void save(ChatMessage message);
	List<ChatMessage> getChatHistory(String userA, String userB, int from, int limit);
	ChatMessageDTO getLastMessage(String userA, String userB);
}
