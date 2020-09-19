package com.karakays.jetchat.service.chat;

import java.util.List;

import com.karakays.jetchat.domain.ChatRoom;
import com.karakays.jetchat.domain.ChatRoomUser;
import com.karakays.jetchat.domain.InstantMessage;

public interface ChatRoomService {
	
	ChatRoom save(ChatRoom chatRoom);
	ChatRoom findById(String chatRoomId);
	ChatRoom join(ChatRoomUser joiningUser, ChatRoom chatRoom);
	ChatRoom leave(ChatRoomUser leavingUser, ChatRoom chatRoom);
	List<ChatRoom> leave(ChatRoomUser leavingUser);
	void sendPublicMessage(InstantMessage instantMessage);
	void sendPrivateMessage(InstantMessage instantMessage);
	List<ChatRoom> findAll();
}
