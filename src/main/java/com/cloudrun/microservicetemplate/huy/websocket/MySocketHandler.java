package com.cloudrun.microservicetemplate.huy.websocket;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cloudrun.microservicetemplate.huy.constant.WebSocketObjectType;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.service.ChatService;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MySocketHandler extends TextWebSocketHandler {
	private static final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;

	private final ChatService chatService;
	private final UserService userService;

	@Autowired
	public MySocketHandler(ChatService chatService, UserService userService, ObjectMapper objectMapper) {
		this.chatService = chatService;
		this.userService = userService;
		this.objectMapper = objectMapper;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String userName = getUserName(session);
		sessions.put(userName, session);
		System.out.println("Chat session opened: " + userName);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		JsonNode jsonNode = objectMapper.readTree(payload);
		if (jsonNode.has("type")) {
			String type = jsonNode.get("type").asText();
			if (type.equals(WebSocketObjectType.chat_send.name())) {
				ChatMessageDTO chatDto = objectMapper.treeToValue(jsonNode, ChatMessageDTO.class);
				handleChatMessage(chatDto, session);
			}
		}
	}

	private void handleChatMessage(ChatMessageDTO chatMessageDto, WebSocketSession session) throws Exception {
		String receiverUserName = "";
		if (chatMessageDto != null) {
			UserEntity sender = userService.findByUserName(chatMessageDto.getSenderUserName());
			UserEntity receiver = userService.findByUserName(chatMessageDto.getReceiverUserName());
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setSender(sender);
			chatMessage.setReceiver(receiver);
			chatMessage.setMessage(chatMessageDto.getMessage());
			chatMessage.setTimestamp(chatMessageDto.getTimestamp());
			chatMessage.setRead(false);
			chatService.save(chatMessage);

			receiverUserName = chatMessageDto.getReceiverUserName();
			WebSocketSession receiverSession = sessions.get(receiverUserName);
			
			if (receiverSession != null && receiverSession.isOpen()) {
				chatMessageDto.setType(WebSocketObjectType.chat_receive.name());
				String value = objectMapper.writeValueAsString(chatMessageDto);
				receiverSession.sendMessage(new TextMessage(value));
			} else {
				userService.sendNotiMessage(chatMessageDto.getSenderUserName(), chatMessageDto.getMessage(),
						chatMessageDto.getSenderUserName(), chatMessageDto.getReceiverUserName());
			}
			if (session != null && session.isOpen()) {
				ChatMessageDTO chatMessageResend = chatMessageDto;
				chatMessageResend.setType(WebSocketObjectType.chat_send.name());
				String value = objectMapper.writeValueAsString(chatMessageResend);
				session.sendMessage(new TextMessage(value));
			}
		}
	}

	public void sendData(Object object, String receiver) throws Exception {
		WebSocketSession receiverSession = sessions.get(receiver);
		if (receiverSession != null && receiverSession.isOpen()) {
			String value = objectMapper.writeValueAsString(object);
			receiverSession.sendMessage(new TextMessage(value));
		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		sessions.remove(getUserName(session));
		System.out.println("Chat session closed: " + getUserName(session) + " - Reason: " + status.getReason());
	}

	private String getUserName(WebSocketSession session) {
		String query = session.getUri().getQuery();
		return query != null && query.startsWith("userName=") ? query.split("=")[1] : session.getId();
	}
}
