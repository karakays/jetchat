package com.karakays.jetchat.service.chat.impl;

import com.karakays.jetchat.domain.ChatRoom;
import com.karakays.jetchat.domain.ChatRoomUser;
import com.karakays.jetchat.domain.InstantMessage;
import com.karakays.jetchat.repository.ChatRoomRepository;
import com.karakays.jetchat.service.chat.ChatRoomService;
import com.karakays.jetchat.service.chat.InstantMessageService;
import com.karakays.jetchat.utils.Destinations;
import com.karakays.jetchat.utils.SystemMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisChatRoomService implements ChatRoomService {

	@Autowired
	private SimpMessagingTemplate webSocketMessagingTemplate;

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private InstantMessageService instantMessageService;

	@Override
	public ChatRoom save(ChatRoom chatRoom) {
		return chatRoomRepository.save(chatRoom);
	}

	@Override
	public ChatRoom findById(String chatRoomId) {
		return chatRoomRepository.findOne(chatRoomId);
	}

	@Override
	public ChatRoom join(ChatRoomUser joiningUser, ChatRoom chatRoom) {
		chatRoom.addUser(joiningUser);
		chatRoomRepository.save(chatRoom);

		sendPublicMessage(SystemMessages.welcome(chatRoom.getId(), joiningUser.getUsername()));
		updateConnectedUsersViaWebSocket(List.of(chatRoom));
		return chatRoom;
	}

	@Override
	public ChatRoom leave(ChatRoomUser leavingUser, ChatRoom chatRoom) {
		sendPublicMessage(SystemMessages.goodbye(chatRoom.getId(), leavingUser.getUsername()));
		
		chatRoom.removeUser(leavingUser);
		chatRoomRepository.save(chatRoom);
		
		updateConnectedUsersViaWebSocket(List.of(chatRoom));
		return chatRoom;
	}

	@Override
	public List<ChatRoom> leave(ChatRoomUser leavingUser) {
	    List<ChatRoom> rooms = findAll();
	    rooms.forEach(r -> r.removeUser(leavingUser));
	    rooms = (List<ChatRoom>) chatRoomRepository.save(rooms);
		rooms.forEach(r -> sendPublicMessage(SystemMessages.goodbye(r.getId(), leavingUser.getUsername())));
		updateConnectedUsersViaWebSocket(rooms);
		return rooms;
	}

	@Override
	public void sendPublicMessage(InstantMessage instantMessage) {
		webSocketMessagingTemplate.convertAndSend(
				Destinations.ChatRoom.publicMessages(instantMessage.getChatRoomId()), instantMessage);
		instantMessageService.appendInstantMessageToConversations(instantMessage);
	}

	@Override
	public void sendPrivateMessage(InstantMessage instantMessage) {
		webSocketMessagingTemplate.convertAndSendToUser(
				instantMessage.getToUser(),
				Destinations.ChatRoom.privateMessages(instantMessage.getChatRoomId()), 
				instantMessage);
		
		webSocketMessagingTemplate.convertAndSendToUser(
				instantMessage.getFromUser(),
				Destinations.ChatRoom.privateMessages(instantMessage.getChatRoomId()), 
				instantMessage);

		instantMessageService.appendInstantMessageToConversations(instantMessage);
	}

	@Override
	public List<ChatRoom> findAll() {
		return (List<ChatRoom>) chatRoomRepository.findAll();
	}
	
	private void updateConnectedUsersViaWebSocket(List<ChatRoom> chatRoom) {
		chatRoom.forEach(r ->
		webSocketMessagingTemplate.convertAndSend(
				Destinations.ChatRoom.connectedUsers(r.getId()),
				r.getConnectedUsers())
		);
	}
}
