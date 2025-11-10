package com.cloudrun.microservicetemplate.huy.converter;

import java.util.List;

import com.cloudrun.microservicetemplate.huy.dto.UserDTO;
import com.cloudrun.microservicetemplate.huy.entity.UserEntity;

public class UserDTOConverter extends BaseConverter<UserEntity, UserDTO> {
	
	private static UserDTOConverter instance;
	
	public static UserDTOConverter getInstance() {
		if (instance != null) {
			return instance;
		}
		return new UserDTOConverter();
	}
	
	public UserDTOConverter() {
		
	}
	
	@Override
	public UserDTO toDTO (UserEntity userEntity) {
		UserDTO userDto = new UserDTO();
		userDto.setId(userEntity.getId());
		userDto.setUserName(userEntity.getUserName());
		userDto.setAvatarUrl(userEntity.getAvatarUrl());
		userDto.setPhoneNumber(userEntity.getPhoneNumber());
		userDto.setFullName(userEntity.getFullName());
		return userDto;
	}
	
	@Override
	public List<UserDTO> toListDTO(List<UserEntity> listEntity) {
		return super.toListDTO(listEntity);
	}
}