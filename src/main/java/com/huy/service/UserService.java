package com.huy.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.huy.baseResponse.BaseResponse;
import com.huy.dto.UserDTO;
import com.huy.entity.UserEntity;
import com.huy.model.Status;

public interface UserService {
	boolean saveUser(UserEntity userDto);
	UserEntity findByUserName(String userName);
	BaseResponse<UserDTO> findAllContacts(String userName);
	Status addFriend(Long userId, Long friendIds);
	Status addFriend(UserEntity user1, UserEntity user2);
	Status removeFriend(String userName1, String userName2);
	BaseResponse<Void> register(String userName, String password, String fullName, String phoneNumber);
	BaseResponse<UserDTO> findUserBy(String query);
	void sendNotiMessage(String title, String message, String senderUserName, String receiverUserName) throws Exception; 
	String updateAvatar(UserEntity user, MultipartFile file) throws IOException;
	BaseResponse<UserDTO> updateProfile(String userName, UserDTO userDto);
	BaseResponse<Void> changePassWord(String sessionUserName, String newPassWord);
}
