package com.huy.converter;

import java.util.List;

import com.huy.dto.ChatMessageDTO;
import com.huy.entity.ChatMessage;

public class ChatMessageDTOConverter extends BaseConverter<ChatMessage, ChatMessageDTO>{
	
private static ChatMessageDTOConverter instance;
	
	public static ChatMessageDTOConverter getInstance() {
		if (instance != null) {
			return instance;
		}
		return new ChatMessageDTOConverter();
	}
	
	public ChatMessageDTOConverter() {
		
	}
	
	@Override
	public ChatMessageDTO toDTO (ChatMessage chatMessage) {
		ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
 		chatMessageDTO.setMessage(chatMessage.getMessage());
		chatMessageDTO.setReceiverUserName(chatMessage.getReceiver().getUserName());
		chatMessageDTO.setSenderUserName(chatMessage.getSender().getUserName());
		chatMessageDTO.setTimestamp(chatMessage.getTimestamp());
		
		return chatMessageDTO;
	}
	
	@Override
	public List<ChatMessageDTO> toListDTO(List<ChatMessage> listEntity) {
		return super.toListDTO(listEntity);
	}
}
