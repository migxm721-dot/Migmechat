package com.migme.fusion.netty.session;

import io.netty.channel.Channel;
import lombok.Data;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

@Data
public class ClientSession {

    private final String sessionId;
    private final Channel channel;
    private Long userId;
    private String username;
    private final AtomicBoolean authenticated = new AtomicBoolean(false);
    private Instant lastHeartbeat = Instant.now();
    private Instant connectedAt = Instant.now();

    public ClientSession(String sessionId, Channel channel) {
        this.sessionId = sessionId;
        this.channel = channel;
    }

    public boolean isAuthenticated() {
        return authenticated.get();
    }

    public void setAuthenticated(boolean auth) {
        authenticated.set(auth);
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = Instant.now();
    }

    public boolean isActive() {
        return channel != null && channel.isActive();
    }

    public void close() {
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }
}
