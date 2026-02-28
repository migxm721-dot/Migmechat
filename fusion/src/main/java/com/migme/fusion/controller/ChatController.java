package com.migme.fusion.controller;

import com.migme.fusion.model.entity.Message;
import com.migme.fusion.service.ChatService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<Message>> getMessages(
            @PathVariable Long conversationId,
            Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessages(conversationId, pageable));
    }
}
