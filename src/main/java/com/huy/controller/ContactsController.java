package com.huy.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.huy.baseResponse.BaseResponse;
import com.huy.constant.Paths;
import com.huy.dto.UserDTO;
import com.huy.dto.output.Contact;
import com.huy.entity.UserEntity;
import com.huy.model.Status;
import com.huy.service.UserService;

@RestController
@RequestMapping(Paths.user)
public class ContactsController {
	@Autowired
	UserService userService;

	@GetMapping(Paths.get_contact_list + "/{userName}")
	public BaseResponse<UserDTO> getFriendsList(@PathVariable String userName) {
		return userService.findAllContacts(userName);
	}

	@PostMapping("/add_friend/{userId}/{friendId}")
	public Status addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
		return userService.addFriend(userId, friendId);
	}
	
	@GetMapping("/search_user")
	public BaseResponse<UserDTO> searchUsers(@RequestParam String query) {
		return userService.findUserBy(query);
	}
}
