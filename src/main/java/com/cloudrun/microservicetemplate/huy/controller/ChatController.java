package com.cloudrun.microservicetemplate.huy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.converter.ChatMessageDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;
import com.cloudrun.microservicetemplate.huy.service.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	@GetMapping("/history")
	public BaseResponse<ChatMessageDTO> getChatHistory(
			@RequestParam String userA, 
			@RequestParam String userB,
			@RequestParam int from,
			@RequestParam int limit
		) {
		List<ChatMessage> historyMessages = chatService.getChatHistory(userA, userB, from, limit);
		return new BaseResponse<ChatMessageDTO> (true, null, ChatMessageDTOConverter.getInstance().toListDTO(historyMessages));
	}
}