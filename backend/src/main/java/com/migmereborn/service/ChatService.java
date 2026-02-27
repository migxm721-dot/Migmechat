package com.migmereborn.service;

import com.migmereborn.model.Message;
import com.migmereborn.model.Room;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final Map<String, Room> rooms = new LinkedHashMap<>();
    private final Map<String, List<Message>> messages = new ConcurrentHashMap<>();

    public ChatService() {
        addRoom(new Room("general", "General"));
        addRoom(new Room("random", "Random"));
        addRoom(new Room("tech", "Tech Talk"));

        addDemoMessage("general", "system", "migmereborn", "Welcome to the General room!");
        addDemoMessage("random", "system", "migmereborn", "Random thoughts welcome here.");
        addDemoMessage("tech", "system", "migmereborn", "Discuss all things tech!");
    }

    private void addRoom(Room room) {
        rooms.put(room.getId(), room);
        messages.put(room.getId(), new CopyOnWriteArrayList<>());
    }

    private void addDemoMessage(String roomId, String senderId, String senderName, String content) {
        List<Message> list = messages.get(roomId);
        if (list != null) {
            list.add(new Message(UUID.randomUUID().toString(), roomId, senderId, senderName, content, Instant.now()));
        }
    }

    public List<Room> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Optional<Room> getRoom(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public List<Message> getMessages(String roomId, int limit) {
        List<Message> list = messages.getOrDefault(roomId, Collections.emptyList());
        int from = Math.max(0, list.size() - limit);
        return new ArrayList<>(list.subList(from, list.size()));
    }

    public Message addMessage(String roomId, String senderId, String senderName, String content) {
        Message msg = new Message(UUID.randomUUID().toString(), roomId, senderId, senderName, content, Instant.now());
        messages.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(msg);
        return msg;
    }
}
