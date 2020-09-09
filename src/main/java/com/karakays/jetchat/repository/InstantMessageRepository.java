package com.karakays.jetchat.repository;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.karakays.jetchat.domain.InstantMessage;

public interface InstantMessageRepository extends CassandraRepository<InstantMessage> {
	
	List<InstantMessage> findInstantMessagesByUsernameAndChatRoomId(String username, String chatRoomId);
}
