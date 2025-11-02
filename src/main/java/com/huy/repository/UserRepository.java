package com.huy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.huy.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	@Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.sessions WHERE u.userName = :userName")
	UserEntity findOneByUserName(String userName); 
	List<UserEntity> findByUserNameContainingIgnoreCase(String query);	
}
