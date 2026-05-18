package com.cloudrun.microservicetemplate.huy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cloudrun.microservicetemplate.huy.entity.ChatMessage;

public interface ChatRepository extends JpaRepository<ChatMessage, Long>{
	@Query(value ="SELECT * FROM message WHERE (sender_username = :userA AND receiver_username = :userB) OR (sender_username = :userB AND receiver_username = :userA) ORDER BY timestamp DESC LIMIT :limit OFFSET :from", nativeQuery = true)
	List<ChatMessage> getChatHistory(@Param("userA") String userA, @Param("userB") String userB, @Param("from") int from, @Param("limit") int limit);
}
