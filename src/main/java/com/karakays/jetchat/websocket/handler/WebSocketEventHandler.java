package com.karakays.jetchat.websocket.handler;

import com.karakays.jetchat.domain.ChatRoomUser;
import com.karakays.jetchat.service.chat.ChatRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
public class WebSocketEventHandler {

    @Autowired
    private ChatRoomService chatRoomService;

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getNativeHeader("chatRoomId").get(0);
        headers.getSessionAttributes().put("chatRoomId", chatRoomId);
        ChatRoomUser joiningUser = new ChatRoomUser(event.getUser().getName());
        log.info("STOMP session created id={}, attributes={}, subscriptionId={}, user={}",
                headers.getSessionId(), headers.getSessionAttributes(), headers.getSubscriptionId(), headers.getUser());

        chatRoomService.join(joiningUser, chatRoomService.findById(chatRoomId));
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String chatRoomId = headers.getSessionAttributes().get("chatRoomId").toString();
        ChatRoomUser leavingUser = new ChatRoomUser(event.getUser().getName());

        chatRoomService.leave(leavingUser, chatRoomService.findById(chatRoomId));
        log.info("STOMP session destroyed id={}", headers.getSessionId());
    }
}
