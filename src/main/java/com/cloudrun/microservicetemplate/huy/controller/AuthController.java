package com.cloudrun.microservicetemplate.huy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloudrun.microservicetemplate.huy.baseResponse.BaseResponse;
import com.cloudrun.microservicetemplate.huy.constant.Paths;
import com.cloudrun.microservicetemplate.huy.constant.SessionAtributes;
import com.cloudrun.microservicetemplate.huy.constant.SessionDestroyedCause;
import com.cloudrun.microservicetemplate.huy.converter.UserDTOConverter;
import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;
import com.cloudrun.microservicetemplate.huy.model.Status;
import com.cloudrun.microservicetemplate.huy.service.UserService;
import com.cloudrun.microservicetemplate.huy.serviceImpl.SessionServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping(Paths.auth)
public class AuthController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private SessionServiceImpl sessionService;

	@PostMapping(Paths.register)
	public BaseResponse<Void> register(
			@RequestParam String userName, 
			@RequestParam String password,
			@RequestParam String fullName,
			@RequestParam String phoneNumber) {
		return userService.register(userName, password, fullName, phoneNumber);
	}

	@PostMapping(Paths.startup_login)
	public BaseResponse<UserDTO> startupLogin(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		System.out.println("session id " + session.getId());
		String userName = (String) session.getAttribute(SessionAtributes.userName);

		if (userName != null) {
			UserEntity user = userService.findByUserName(userName);
			if (user != null) {
				UserDTO userDto = UserDTOConverter.getInstance().toDTO(user);
				return new BaseResponse<>(true, "Welcome", List.of(userDto));
			}
		}
		session.invalidate();
		return new BaseResponse<>(false, "Hết phiên đăng nhập", null);	
	}

	@PostMapping(Paths.login)
	public BaseResponse<UserDTO> login(
			@RequestParam String userName, 
			@RequestParam String password,
			@RequestParam String fcmToken,
			@RequestHeader String deviceId,
			HttpServletRequest request) {
		UserEntity existingUser = userService.findByUserName(userName);
		UserDTO userDto;
		if (existingUser != null && existingUser.getPassword().equals(password)) {
			userDto = UserDTOConverter.getInstance().toDTO(existingUser);

			HttpSession session = request.getSession(true);
			session.setAttribute(SessionAtributes.userName, userName);
			session.setAttribute(SessionAtributes.destroyedCause, null);
			sessionService.save(session, existingUser, fcmToken, deviceId);

			return new BaseResponse<>(true, "Đăng nhập thành công", List.of(userDto));
		} else {
			return new BaseResponse<>(false, "Sai tên tài khoản hoặc mật khẩu", null);
		}
	}

	@PostMapping(Paths.logout)
	public BaseResponse<Void> logout(HttpSession session) {
		session.setAttribute(SessionAtributes.destroyedCause, SessionDestroyedCause.INVALIDATED);
		session.invalidate();
		BaseResponse<Void> res = new BaseResponse<>();
		res.setOk(true);
		res.setMessage("Đăng xuất thành công");
		return res;
	}

	@GetMapping("/test")
	public Status test() {
		return new Status(true, "ok");
	}
}
