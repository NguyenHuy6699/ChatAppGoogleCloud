package com.cloudrun.microservicetemplate.huy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.constant.Paths;
import com.cloudrun.microservicetemplate.huy.constant.WebSocketObjectType;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.FriendRequestDTO;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.cloudrun.microservicetemplate.huy.serviceImpl.FriendRequestServiceImpl;
import com.cloudrun.microservicetemplate.huy.websocket.MySocketHandler;

@RestController
@RequestMapping(Paths.friend_request)
public class FriendRequestController {
	@Autowired
	private FriendRequestServiceImpl frReqService;
	@Autowired
	private UserService userService;
	@Autowired
	private MySocketHandler wsHandler;
	
	@GetMapping(Paths.get_sent_friend_request)
	public BaseResponse<UserDTO> getSentFriendRequest(
			@RequestParam String userName
			) {
		UserEntity user = userService.findByUserName(userName);
		return frReqService.getSentFriendRequest(user);
	}
	
	@GetMapping(Paths.get_received_friend_request)
	public BaseResponse<UserDTO> getReceivedFriendRequest(
			@RequestParam String userName
			) {
		UserEntity user = userService.findByUserName(userName);
		return frReqService.getReceivedFriendRequest(user);
	}
	
	@PostMapping(Paths.send_friend_request)
	public BaseResponse<FriendRequestDTO> sendFriendRequest(String senderUserName, String receiverUserName) throws Exception {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<FriendRequestDTO> resp = frReqService.sendFriendRequest(sender, receiver);
		if (resp.isOk() && resp.getDataList().get(0) != null) {
			FriendRequestDTO frRqDTO = resp.getDataList().get(0);
			frRqDTO.setType(WebSocketObjectType.new_frend_request.name());
			wsHandler.sendData(frRqDTO, senderUserName);
			wsHandler.sendData(frRqDTO, receiverUserName);
		}
		return resp;
	}
	
	@PostMapping(Paths.accept_friend_request)
	public BaseResponse<FriendRequestDTO> acceptFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) throws Exception {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<FriendRequestDTO> res = frReqService.acceptFriendRequest(sender, receiver);
		if (res.isOk()) {
			userService.addFriend(sender, receiver);
			UserDTO senderDTO = UserDTOConverter.getInstance().toDTO(sender);
			UserDTO receiverDTO = UserDTOConverter.getInstance().toDTO(receiver);
			
			senderDTO.setType(WebSocketObjectType.accept_contact.name());
			receiverDTO.setType(WebSocketObjectType.accept_contact.name());
			
			wsHandler.sendData(receiverDTO, senderUserName);
			wsHandler.sendData(senderDTO, receiverUserName);
			
			FriendRequestDTO frRqDTO = res.getDataList().get(0);
			frRqDTO.setType(WebSocketObjectType.accept_friend_request.name());
			wsHandler.sendData(frRqDTO, senderUserName);
			wsHandler.sendData(frRqDTO, receiverUserName);
		}
		return res;
	}
	
	@PostMapping(Paths.cancel_friend_request)
	public BaseResponse<FriendRequestDTO> cancelFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) throws Exception {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<FriendRequestDTO> res = frReqService.cancelFriendRequest(sender, receiver);
		if (res.isOk() && res.getDataList().get(0) != null) {
			FriendRequestDTO frRqDTO = res.getDataList().get(0);
			frRqDTO.setType(WebSocketObjectType.cancel_friend_request.name());
			wsHandler.sendData(frRqDTO, senderUserName);
			wsHandler.sendData(frRqDTO, receiverUserName);
		}
		return res;
	}
	
	@PostMapping(Paths.reject_friend_request)
	public BaseResponse<FriendRequestDTO> rejectFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) throws Exception {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<FriendRequestDTO> res = frReqService.rejectFriendRequest(sender, receiver);
		if (res.isOk() && res.getDataList().get(0) != null) {
			FriendRequestDTO frRqDTO = res.getDataList().get(0);
			frRqDTO.setType(WebSocketObjectType.reject_friend_request.name());
			wsHandler.sendData(frRqDTO, senderUserName);
			wsHandler.sendData(frRqDTO, receiverUserName);
		}
		return res;
	}
	
	@GetMapping(Paths.get_sent_friend_request_count)
	public BaseResponse<Integer> getSentFrReqCount(@RequestParam String sender) {
		UserEntity user = userService.findByUserName(sender);
		Integer count = frReqService.getSentFrReqCount(user);
		return new BaseResponse<Integer>(true, null, List.of(count));
	}
	
	@GetMapping(Paths.get_received_friend_request_count)
	public BaseResponse<Integer> getReceivedFrReqCount(@RequestParam String receiver) {
		UserEntity user = userService.findByUserName(receiver);
		Integer count = frReqService.getReceivedFrReqCount(user);
		return new BaseResponse<Integer>(true, null, List.of(count));
	}
}
