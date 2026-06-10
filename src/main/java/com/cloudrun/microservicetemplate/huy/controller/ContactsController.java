package com.cloudrun.microservicetemplate.huy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.constant.Paths;
import com.cloudrun.microservicetemplate.huy.constant.WebSocketObjectType;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.ChatMessageDTO;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.model.Status;
import com.cloudrun.microservicetemplate.huy.service.ChatService;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.cloudrun.microservicetemplate.huy.websocket.MySocketHandler;

@RestController
@RequestMapping(Paths.user)
public class ContactsController {
	@Autowired
	UserService userService;
	@Autowired
	ChatService chatService;
	@Autowired
	MySocketHandler socket;

	@GetMapping(Paths.get_contact_list + "/{userName}")
	public BaseResponse<UserDTO> getFriendsList(@PathVariable String userName) {
		BaseResponse<UserDTO> resp = userService.findAllContacts(userName);
		List<UserDTO> listDTO = resp.getDataList();
		for (UserDTO user : listDTO) {
			ChatMessageDTO lastMessage = chatService.getLastMessage(userName, user.getUserName());
			List<ChatMessage> last50Messages = chatService.getChatHistory(userName, user.getUserName(), 0, 50);
			int unreadMessages = 0;
			for (ChatMessage message : last50Messages) {
				if (!message.isRead() 
						&& message.getSender().getUserName().equals(user.getUserName())
						&& message.getReceiver().getUserName().equals(userName)) {
					unreadMessages++;
				}
			}
			user.setLastMessage(lastMessage.getMessage());
			user.setUnreadMessages(unreadMessages);
		}
		resp.setDataList(listDTO);
		return resp;
	}

	@GetMapping(Paths.set_read_messages)
	public void setReadMessages(@RequestParam String userA, @RequestParam String userB) throws Exception {
		List<ChatMessage> last50Messages = chatService.getChatHistory(userA, userB, 0, 50);
		for (ChatMessage chatMessage : last50Messages) {
			if (chatMessage.getSender().getUserName().equals(userB) && chatMessage.getReceiver().getUserName().equals(userA)) {
				chatMessage.setRead(true);
				chatService.save(chatMessage);
			}
		}
		UserEntity userEntity = userService.findByUserName(userB);
		UserDTO userDTO = UserDTOConverter.getInstance().toDTO(userEntity);
		userDTO.setType(WebSocketObjectType.read_messages.name());
		socket.sendData(userDTO, userA);
	}
	
	@PostMapping("/add_friend/{userId}/{friendId}")
	public Status addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
		return userService.addFriend(userId, friendId);
	}

	@GetMapping("/search_user")
	public BaseResponse<UserDTO> searchUsers(@RequestParam String query, @RequestParam String searcherName) {
		return userService.findUsersBy(query, searcherName);
	}
}
