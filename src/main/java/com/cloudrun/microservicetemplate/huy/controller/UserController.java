package com.cloudrun.microservicetemplate.huy.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.constant.Paths;
import com.cloudrun.microservicetemplate.huy.constant.ResponseType;
import com.cloudrun.microservicetemplate.huy.constant.SessionAtributes;
import com.cloudrun.microservicetemplate.huy.constant.WebSocketObjectType;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.FriendRequestDTO;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.SessionEntity;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.model.Status;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.cloudrun.microservicetemplate.huy.serviceImpl.FriendRequestServiceImpl;
import com.cloudrun.microservicetemplate.huy.websocket.MySocketHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping(Paths.user)
public class UserController {
	@Autowired
	private UserService userService;

	@Autowired
	private FriendRequestServiceImpl frReqService;
	@Autowired
	private MySocketHandler wsHandler;

	@PostMapping(Paths.upload_avatar_path)
	public ResponseEntity<String> uploadAvatar(@RequestParam String userName, @RequestParam("file") MultipartFile file)
			throws IOException {
		UserEntity user = userService.findByUserName(userName);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userService.updateAvatar(user, file));
	}

	@PostMapping(Paths.update_fcm_token)
	public BaseResponse<Void> updateFcmToken(@RequestParam String userName, @RequestParam String token,
			HttpSession session) {

		if (session == null)
			return new BaseResponse<>(false, "Hết phiên đăng nhập");

		UserEntity user = userService.findByUserName(userName);
		if (user == null)
			new BaseResponse<>(false, "Người dùng không tồn tại");

		if (user.getSessions() == null) {
			user.setSessions(new ArrayList<>());
			userService.saveUser(user);
			return new BaseResponse<>(false, "Hết phiên đăng nhập");
		}
		if (!user.getSessions().isEmpty()) {
			List<SessionEntity> sessions = user.getSessions();
			for (SessionEntity sessionEntity : sessions) {
				if (sessionEntity.getSessionId().equals(session.getId())) {
					sessionEntity.setFcmToken(token);
					break;
				}
			}
			userService.saveUser(user);
			return new BaseResponse<>(true, "Thành công");
		} else {
			return new BaseResponse<>(false, "Hết phiên đăng nhập");
		}

	}

	@GetMapping(Paths.get_friend_relationship)
	public BaseResponse<FriendRequestDTO> getFriendRelationShip(@RequestParam String loggedUserName,
			@RequestParam String userName) {
		UserEntity loggedUser = userService.findByUserName(loggedUserName);
		UserEntity user = userService.findByUserName(userName);
		if (loggedUser == null || user == null) {
			return new BaseResponse<>(false, "Người dùng không tồn tại", null);
		}
		return frReqService.getRelationship(loggedUser, user);

	}

	@PostMapping(Paths.remove_friend)
	public BaseResponse<Void> removeFriend(@RequestParam String userName1, @RequestParam String userName2) throws Exception {
		Status removeFriendStatus = userService.removeFriend(userName1, userName2);

		UserEntity user1 = userService.findByUserName(userName1);
		UserEntity user2 = userService.findByUserName(userName2);
		boolean removeFriendRequestSuccess = frReqService.removeFriendRequest(user1, user2);

		if (removeFriendStatus.isOk() && removeFriendRequestSuccess) {
			UserDTO user1DTO = UserDTOConverter.getInstance().toDTO(user1);
			UserDTO user2DTO = UserDTOConverter.getInstance().toDTO(user2);
			
			user1DTO.setType(WebSocketObjectType.remove_contact.name());
			user2DTO.setType(WebSocketObjectType.remove_contact.name());
			
			wsHandler.sendData(user2DTO, userName1);
			wsHandler.sendData(user1DTO, userName2);
			return new BaseResponse<>(true, "Xóa bạn thành công");
		} else {
			return new BaseResponse<>(true, removeFriendStatus.getMessage());
		}
	}

	@PostMapping(Paths.update_profile)
	public BaseResponse<UserDTO> updateProfile(
			@RequestBody UserDTO userDto,
			@RequestParam String loggedUserName,
			HttpServletRequest req
			) {
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute(SessionAtributes.userName) == null) {
			return new BaseResponse<>(false, "Hết phiên đăng nhập", null, ResponseType.session_expired);
		}
		String sessionUserName = (String) session.getAttribute(SessionAtributes.userName);
		if (!sessionUserName.equals(loggedUserName)) {
			return new BaseResponse<>(false, "Người dùng không hợp lệ", null);
		}
		BaseResponse<UserDTO> resp = userService.updateProfile(loggedUserName, userDto);
		if (resp.isOk()) {
			session.setAttribute(SessionAtributes.userName, userDto.getUserName());
		}
		return resp;
	}
	
	@PostMapping(Paths.change_password)
	public BaseResponse<Void> changePassWord(@RequestParam String loggedUserName, @RequestParam String oldPassWord, @RequestParam String newPassWord, HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session == null || session.getAttribute(SessionAtributes.userName) == null) {
			return new BaseResponse<>(false, "Hết phiên đăng nhập", null);
		}
		String sessionUserName = (String) session.getAttribute(SessionAtributes.userName);
		if (!sessionUserName.equals(loggedUserName)) {
			return new BaseResponse<>(false, "Người dùng không hợp lệ", null);
		}
		return userService.changePassWord(sessionUserName, oldPassWord, newPassWord);
	}
}
