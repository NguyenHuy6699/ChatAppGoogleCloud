package com.huy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.huy.constant.TableName;
import com.huy.entity.ChatMessage;

public interface ChatRepository extends JpaRepository<ChatMessage, Long>{
	@Query("SELECT m FROM ChatMessage m WHERE " +
			"(m.sender.userName = :userA AND m.receiver.userName = :userB) OR " +
			"(m.sender.userName = :userB AND m.receiver.userName = :userA) " +
			"ORDER BY m.timestamp ASC")
	List<ChatMessage> getChatHistory(@Param("userA") String userA, @Param("userB") String userB);
}
