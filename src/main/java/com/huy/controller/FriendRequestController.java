package com.huy.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.baseResponse.BaseResponse;
import com.huy.constant.Paths;
import com.huy.dto.UserDTO;
import com.huy.entity.UserEntity;
import com.huy.service.UserService;
import com.huy.serviceImpl.FriendRequestServiceImpl;

@RestController
@RequestMapping(Paths.friend_request)
public class FriendRequestController {
	@Autowired
	private FriendRequestServiceImpl frReqService;
	@Autowired
	private UserService userService;
	
	@PostMapping(Paths.send_friend_request)
	public BaseResponse<Void> sendFriendRequest(String senderUserName, String receiverUserName) {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		
		return frReqService.sendFriendRequest(sender, receiver);
	}
	
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
	
	@PostMapping(Paths.accept_friend_request)
	public BaseResponse<Void> acceptFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<Void> res = frReqService.acceptFriendRequest(sender, receiver);
		if (res.isOk()) {
			userService.addFriend(sender, receiver);
		}
		return res;
	}
	
	@PostMapping(Paths.cancel_friend_request)
	public BaseResponse<Void> cancelFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<Void> res = frReqService.cancelFriendRequest(sender, receiver);
		return res;
	}
	
	@PostMapping(Paths.reject_friend_request)
	public BaseResponse<Void> rejectFriendRequest(
			@RequestParam String senderUserName,
			@RequestParam String receiverUserName
			) {
		UserEntity sender = userService.findByUserName(senderUserName);
		UserEntity receiver = userService.findByUserName(receiverUserName);
		BaseResponse<Void> res = frReqService.rejectFriendRequest(sender, receiver);
		return res;
	}
}
