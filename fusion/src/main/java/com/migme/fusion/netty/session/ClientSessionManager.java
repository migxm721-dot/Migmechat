package com.migme.fusion.netty.session;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientSessionManager {

    private static final Logger log = LoggerFactory.getLogger(ClientSessionManager.class);
    private static final long HEARTBEAT_TIMEOUT_SECONDS = 120;

    private final ConcurrentHashMap<String, ClientSession> sessionsBySessionId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, ClientSession> sessionsByUserId = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ClientSession> sessionsByChannelId = new ConcurrentHashMap<>();

    public ClientSession createSession(Channel channel) {
        String sessionId = UUID.randomUUID().toString();
        ClientSession session = new ClientSession(sessionId, channel);
        sessionsBySessionId.put(sessionId, session);
        sessionsByChannelId.put(channel.id().asLongText(), session);
        log.info("Created session {} for channel {}", sessionId, channel.id());
        return session;
    }

    public void authenticateSession(ClientSession session, Long userId, String username) {
        session.setUserId(userId);
        session.setUsername(username);
        session.setAuthenticated(true);
        sessionsByUserId.put(userId, session);
        log.info("Session {} authenticated for user {} ({})", session.getSessionId(), username, userId);
    }

    public Optional<ClientSession> getSessionByChannelId(String channelId) {
        return Optional.ofNullable(sessionsByChannelId.get(channelId));
    }

    public Optional<ClientSession> getSessionByUserId(Long userId) {
        return Optional.ofNullable(sessionsByUserId.get(userId));
    }

    public Optional<ClientSession> getSessionBySessionId(String sessionId) {
        return Optional.ofNullable(sessionsBySessionId.get(sessionId));
    }

    public void removeSession(Channel channel) {
        ClientSession session = sessionsByChannelId.remove(channel.id().asLongText());
        if (session != null) {
            sessionsBySessionId.remove(session.getSessionId());
            if (session.getUserId() != null) {
                sessionsByUserId.remove(session.getUserId());
            }
            log.info("Removed session {} for user {}", session.getSessionId(), session.getUsername());
        }
    }

    public Collection<ClientSession> getAllSessions() {
        return sessionsBySessionId.values();
    }

    public int getActiveSessionCount() {
        return sessionsBySessionId.size();
    }

    @Scheduled(fixedDelay = 30000)
    public void cleanupInactiveSessions() {
        Instant cutoff = Instant.now().minus(HEARTBEAT_TIMEOUT_SECONDS, ChronoUnit.SECONDS);
        sessionsBySessionId.values().removeIf(session -> {
            if (!session.isActive() || session.getLastHeartbeat().isBefore(cutoff)) {
                log.info("Removing inactive session {} (user: {})", session.getSessionId(), session.getUsername());
                if (session.getUserId() != null) {
                    sessionsByUserId.remove(session.getUserId());
                }
                sessionsByChannelId.remove(session.getChannel().id().asLongText());
                session.close();
                return true;
            }
            return false;
        });
    }
}
