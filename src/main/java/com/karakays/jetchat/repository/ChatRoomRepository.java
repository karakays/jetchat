package com.karakays.jetchat.repository;

import org.springframework.data.repository.CrudRepository;

import com.karakays.jetchat.domain.ChatRoom;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, String> {

}
