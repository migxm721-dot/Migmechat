package com.migme.fusion.service;

import com.migme.fusion.model.entity.Conversation;
import com.migme.fusion.model.entity.Message;
import com.migme.fusion.model.entity.User;
import com.migme.fusion.netty.session.ClientSessionManager;
import com.migme.fusion.protocol.FusionPacket;
import com.migme.fusion.protocol.PacketType;
import com.migme.fusion.repository.ConversationRepository;
import com.migme.fusion.repository.MessageRepository;
import com.migme.fusion.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ClientSessionManager sessionManager;

    public ChatService(MessageRepository messageRepository,
                       ConversationRepository conversationRepository,
                       UserRepository userRepository,
                       ClientSessionManager sessionManager) {
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    /**
     * Processes an incoming message from the Fusion Protocol.
     * Payload format: "recipientUsername:messageContent"
     */
    @Transactional
    public void processMessage(Long senderId, byte[] payload) {
        String payloadStr = new String(payload, StandardCharsets.UTF_8);
        String[] parts = payloadStr.split(":", 2);
        if (parts.length != 2) {
            log.warn("Invalid message payload format");
            return;
        }

        String recipientUsername = parts[0];
        String content = parts[1];

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User recipient = userRepository.findByUsername(recipientUsername)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found: " + recipientUsername));

        Conversation conversation = conversationRepository
                .findDirectConversation(senderId, recipient.getId())
                .orElseGet(() -> createDirectConversation(sender, recipient));

        Message message = Message.builder()
                .guid(UUID.randomUUID().toString())
                .conversation(conversation)
                .sender(sender)
                .content(content)
                .build();
        messageRepository.save(message);

        conversation.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        // Broadcast to recipient if online
        sessionManager.getSessionByUserId(recipient.getId()).ifPresent(session -> {
            FusionPacket notification = FusionPacket.builder()
                    .type(PacketType.MESSAGE)
                    .sequenceId(0)
                    .payload((sender.getUsername() + ":" + content).getBytes(StandardCharsets.UTF_8))
                    .build();
            session.getChannel().writeAndFlush(notification);
        });

        log.debug("Message from {} to {} saved and delivered", sender.getUsername(), recipientUsername);
    }

    @Transactional(readOnly = true)
    public Page<Message> getMessages(Long conversationId, Pageable pageable) {
        return messageRepository.findByConversationIdAndDeletedFalseOrderByCreatedAtDesc(conversationId, pageable);
    }

    private Conversation createDirectConversation(User user1, User user2) {
        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);

        Conversation conversation = Conversation.builder()
                .type(Conversation.ConversationType.DIRECT)
                .participants(participants)
                .build();
        return conversationRepository.save(conversation);
    }
}
