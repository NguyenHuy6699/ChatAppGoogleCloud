package com.huy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.huy.entity.FriendRequestEntity;
import com.huy.entity.UserEntity;

public interface FriendRequestRepository extends JpaRepository<FriendRequestEntity, Long> {
	FriendRequestEntity findBySenderAndReceiver(UserEntity sender, UserEntity receiver);
	List<FriendRequestEntity> findAllBySenderAndReceiver(UserEntity sender, UserEntity receiver);
}
