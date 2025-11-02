package com.huy.converter;

import java.util.List;

import com.huy.dto.FriendRequestDTO;
import com.huy.entity.FriendRequestEntity;

public class FriendRequestDTOConverter extends BaseConverter<FriendRequestEntity, FriendRequestDTO> {
	
private static FriendRequestDTOConverter instance;
	
	public static FriendRequestDTOConverter getInstance() {
		if (instance != null) {
			return instance;
		}
		return new FriendRequestDTOConverter();
	}
	
	public FriendRequestDTOConverter() {
		
	}
	
	@Override
	public FriendRequestDTO toDTO(FriendRequestEntity entity) {
		FriendRequestDTO frReqDto = new FriendRequestDTO();
		frReqDto.setId(entity.getId());
		frReqDto.setReceiver(UserDTOConverter.getInstance().toDTO(entity.getReceiver()));
		frReqDto.setSender(UserDTOConverter.getInstance().toDTO(entity.getSender()));
		frReqDto.setStatus(entity.getStatus());
		return frReqDto;
	}
	
	@Override
	public List<FriendRequestDTO> toListDTO(List<FriendRequestEntity> listEntity) {
		return super.toListDTO(listEntity);
	}
}
