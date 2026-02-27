package com.migmereborn.controller;

import com.migmereborn.model.Message;
import com.migmereborn.model.SendMessageRequest;
import com.migmereborn.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/rooms/{roomId}/send")
    public void handleMessage(@DestinationVariable String roomId, SendMessageRequest request) {
        Message msg = chatService.addMessage(roomId, request.getSenderId(), request.getSenderName(), request.getContent());
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, msg);
    }
}
