package com.cloudrun.microservicetemplate.huy.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.converter.ChatMessageDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;
import com.cloudrun.microservicetemplate.huy.repository.ChatRepository;
import com.cloudrun.microservicetemplate.huy.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {
	@Autowired
	private ChatRepository chatRepo;
	
	@Override
	public List<ChatMessage> getChatHistory(String userA, String userB, int from, int limit) {
		List<ChatMessage> chatMessageList = chatRepo.getChatHistory(userA, userB, from, limit);
		return chatMessageList;
	}

	@Override
	public void save(ChatMessage message) {
		chatRepo.save(message);
	}

	@Override
	public ChatMessageDTO getLastMessage(String userA, String userB) {
		ChatMessage lastMessage = chatRepo.getLastMessage(userA, userB);
		return ChatMessageDTOConverter.getInstance().toDTO(lastMessage);
	}
}
