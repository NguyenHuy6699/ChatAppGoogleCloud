package com.huy.service;

import com.huy.baseResponse.BaseResponse;
import com.huy.dto.ChatMessageDTO;
import com.huy.entity.ChatMessage;

public interface ChatService {
	void save(ChatMessage message);
	BaseResponse<ChatMessageDTO> getChatHistory(String userA, String userB);
}
