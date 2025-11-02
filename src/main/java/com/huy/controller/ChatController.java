package com.huy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.baseResponse.BaseResponse;
import com.huy.dto.ChatMessageDTO;
import com.huy.entity.ChatMessage;
import com.huy.service.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	@Autowired
	private ChatService chatService;
	
	@GetMapping("/history")
	public BaseResponse<ChatMessageDTO> getChatHistory(
			@RequestParam String userA, 
			@RequestParam String userB
			) {
		return chatService.getChatHistory(userA, userB);
	}
}