package com.karakays.jetchat.api;

import com.karakays.jetchat.domain.ChatRoom;
import com.karakays.jetchat.domain.ChatRoomUser;
import com.karakays.jetchat.domain.InstantMessage;
import com.karakays.jetchat.service.chat.ChatRoomService;
import com.karakays.jetchat.service.chat.InstantMessageService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
public class ChatRoomController {

	private final ChatRoomService chatRoomService;

	private final InstantMessageService instantMessageService;

	private final HttpSession session;

	public ChatRoomController(ChatRoomService chatRoomService, InstantMessageService instantMessageService,
							   HttpSession httpSession) {
		this.chatRoomService = chatRoomService;
		this.instantMessageService = instantMessageService;
		this.session = httpSession;
	}

	@Secured("ROLE_ADMIN")
	@RequestMapping(path = "/chatroom", method = RequestMethod.POST)
	@ResponseBody
	@ResponseStatus(code = HttpStatus.CREATED)
	public ChatRoom createChatRoom(@RequestBody ChatRoom chatRoom) {
		return chatRoomService.save(chatRoom);
	}

	@RequestMapping("/chatroom/{chatRoomId}")
	public ModelAndView join(@PathVariable String chatRoomId, Principal principal) {
		ModelAndView modelAndView = new ModelAndView("chatroom");
		modelAndView.addObject("chatRoom", chatRoomService.findById(chatRoomId));
		return modelAndView;
	}

	@ResponseBody
	@GetMapping(value = "/chatrooms", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ChatRoom> listChatRooms() {
		return chatRoomService.findAll();
	}

	@ResponseBody
	@GetMapping(value = "/session-details", produces = MediaType.APPLICATION_JSON_VALUE)
	public SessionDetails getSessionDetails(Principal principal) {
		log.info("session.class={}, SESSION.HASH={}", session.getClass(), session.hashCode());
		Integer count = (Integer) session.getAttribute("count");
		if(count == null) {
			count = 0;
		}

		session.setAttribute("count", ++count);
		return new SessionDetails(count, session.getCreationTime(), session.getLastAccessedTime(), session.getMaxInactiveInterval());
	}

	@NoArgsConstructor
	@Data
	static class SessionDetails {
		private int hitCount;
		private String creationTime;
		private String lastAccessedTime;
		private int maxInactiveInterval;

		public SessionDetails(int hitCount, long creationTime, long lastAccessedTime, int maxInactiveInterval) {
			this.hitCount = hitCount;
			this.creationTime = DateTimeFormatter.ISO_DATE_TIME
					.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTime), ZoneId.systemDefault()));
			this.lastAccessedTime = DateTimeFormatter.ISO_DATE_TIME
					.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(lastAccessedTime), ZoneId.systemDefault()));
			this.maxInactiveInterval = maxInactiveInterval;
		}
	}

	@SubscribeMapping("/{chatroomId}/connected.users")
	public List<ChatRoomUser> listChatRoomConnectedUsersOnSubscribe(SimpMessageHeaderAccessor headerAccessor,
																	@DestinationVariable("chatroomId") String roomId) {
		log.debug("Subscribed to {} with session id={}, attributes={}, subscriptionId={}, user={}",
				headerAccessor.getDestination(), headerAccessor.getSessionId(), headerAccessor.getSessionAttributes(),
				headerAccessor.getSubscriptionId(), headerAccessor.getUser());
		return chatRoomService.findById(roomId).getConnectedUsers();
	}

	@SubscribeMapping("/{chatroomId}/old.messages")
	public List<InstantMessage> listOldMessagesFromUserOnSubscribe(Principal principal,
                                                                   SimpMessageHeaderAccessor headerAccessor,
																   @DestinationVariable("chatroomId") String roomId) {
		return instantMessageService.findAllInstantMessagesFor(principal.getName(), roomId);
	}

	@MessageMapping("/send.message")
	public void sendMessage(@Payload InstantMessage instantMessage, Principal principal,
			SimpMessageHeaderAccessor headerAccessor) {
		log.info("{} sending message with session id={}, attributes={}, subscriptionId={}",
				headerAccessor.getDestination(), headerAccessor.getSessionId(), headerAccessor.getSessionAttributes(),
				headerAccessor.getSubscriptionId());
		String chatRoomId = headerAccessor.getSessionAttributes().get("chatRoomId").toString();
		instantMessage.setFromUser(principal.getName());
		instantMessage.setChatRoomId(chatRoomId);

		if (instantMessage.isPublic()) {
			chatRoomService.sendPublicMessage(instantMessage);
		} else {
			chatRoomService.sendPrivateMessage(instantMessage);
		}
	}
}
