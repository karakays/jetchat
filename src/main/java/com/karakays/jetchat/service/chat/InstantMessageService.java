package com.karakays.jetchat.service.chat;

import java.util.List;

import com.karakays.jetchat.domain.InstantMessage;

public interface InstantMessageService {
	
	void appendInstantMessageToConversations(InstantMessage instantMessage);
	List<InstantMessage> findAllInstantMessagesFor(String username, String chatRoomId);
}
