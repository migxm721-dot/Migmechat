package com.migmereborn.controller;

import com.migmereborn.model.Message;
import com.migmereborn.model.Room;
import com.migmereborn.model.SendMessageRequest;
import com.migmereborn.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public RoomController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping
    public List<Room> listRooms() {
        return chatService.getRooms();
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit) {
        if (chatService.getRoom(roomId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(chatService.getMessages(roomId, limit));
    }

    @PostMapping("/{roomId}/messages")
    public ResponseEntity<Message> postMessage(
            @PathVariable String roomId,
            @RequestBody SendMessageRequest request) {
        if (chatService.getRoom(roomId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Message msg = chatService.addMessage(roomId, request.getSenderId(), request.getSenderName(), request.getContent());
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, msg);
        return ResponseEntity.ok(msg);
    }
}
