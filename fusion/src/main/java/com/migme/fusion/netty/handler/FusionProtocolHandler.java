package com.migme.fusion.netty.handler;

import com.migme.fusion.netty.session.ClientSession;
import com.migme.fusion.netty.session.ClientSessionManager;
import com.migme.fusion.protocol.FusionPacket;
import com.migme.fusion.protocol.PacketType;
import com.migme.fusion.service.AuthenticationService;
import com.migme.fusion.service.ChatService;
import com.migme.fusion.service.ContactService;
import com.migme.fusion.service.PresenceService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@ChannelHandler.Sharable
public class FusionProtocolHandler extends SimpleChannelInboundHandler<FusionPacket> {

    private static final Logger log = LoggerFactory.getLogger(FusionProtocolHandler.class);

    private final ClientSessionManager sessionManager;
    private final AuthenticationService authenticationService;
    private final ChatService chatService;
    private final PresenceService presenceService;
    private final ContactService contactService;

    public FusionProtocolHandler(ClientSessionManager sessionManager,
                                  AuthenticationService authenticationService,
                                  ChatService chatService,
                                  PresenceService presenceService,
                                  ContactService contactService) {
        this.sessionManager = sessionManager;
        this.authenticationService = authenticationService;
        this.chatService = chatService;
        this.presenceService = presenceService;
        this.contactService = contactService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ClientSession session = sessionManager.createSession(ctx.channel());
        log.info("Client connected: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        sessionManager.getSessionByChannelId(ctx.channel().id().asLongText()).ifPresent(session -> {
            if (session.isAuthenticated()) {
                presenceService.setOffline(session.getUserId());
            }
        });
        sessionManager.removeSession(ctx.channel());
        log.info("Client disconnected: {}", ctx.channel().remoteAddress());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FusionPacket packet) {
        ClientSession session = sessionManager.getSessionByChannelId(ctx.channel().id().asLongText())
                .orElseThrow(() -> new IllegalStateException("No session for channel"));

        log.debug("Received packet: type={}, seqId={}", packet.getType(), packet.getSequenceId());

        switch (packet.getType()) {
            case LOGIN -> handleLogin(ctx, session, packet);
            case LOGOUT -> handleLogout(ctx, session, packet);
            case MESSAGE -> handleMessage(ctx, session, packet);
            case PRESENCE -> handlePresence(ctx, session, packet);
            case STATUS_MESSAGE -> handleStatusMessage(ctx, session, packet);
            case GET_CONTACTS -> handleGetContacts(ctx, session, packet);
            case ADD_CONTACT -> handleAddContact(ctx, session, packet);
            case REMOVE_CONTACT -> handleRemoveContact(ctx, session, packet);
            case HEARTBEAT -> handleHeartbeat(ctx, session, packet);
            default -> log.warn("Unhandled packet type: {}", packet.getType());
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        try {
            String payload = new String(packet.getPayload(), StandardCharsets.UTF_8);
            String[] parts = payload.split(":", 2);
            if (parts.length != 2) {
                sendError(ctx, packet.getSequenceId(), "Invalid login format");
                return;
            }

            String username = parts[0];
            String password = parts[1];

            authenticationService.authenticateForFusion(username, password)
                    .ifPresentOrElse(user -> {
                        sessionManager.authenticateSession(session, user.getId(), user.getUsername());
                        presenceService.setOnline(user.getId());

                        FusionPacket response = FusionPacket.builder()
                                .type(PacketType.LOGIN_OK)
                                .sequenceId(packet.getSequenceId())
                                .payload(("OK:" + user.getUsername()).getBytes(StandardCharsets.UTF_8))
                                .build();
                        ctx.writeAndFlush(response);
                        log.info("User {} logged in via Fusion Protocol", username);
                    }, () -> {
                        FusionPacket response = FusionPacket.builder()
                                .type(PacketType.LOGIN_FAIL)
                                .sequenceId(packet.getSequenceId())
                                .payload("Invalid credentials".getBytes(StandardCharsets.UTF_8))
                                .build();
                        ctx.writeAndFlush(response);
                    });
        } catch (Exception e) {
            log.error("Error handling login", e);
            sendError(ctx, packet.getSequenceId(), "Login error");
        }
    }

    private void handleLogout(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (session.isAuthenticated()) {
            presenceService.setOffline(session.getUserId());
            log.info("User {} logged out", session.getUsername());
        }
        ctx.close();
    }

    private void handleMessage(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            chatService.processMessage(session.getUserId(), packet.getPayload());

            FusionPacket ack = FusionPacket.builder()
                    .type(PacketType.MESSAGE_ACK)
                    .sequenceId(packet.getSequenceId())
                    .payload(new byte[0])
                    .build();
            ctx.writeAndFlush(ack);
        } catch (Exception e) {
            log.error("Error handling message", e);
            sendError(ctx, packet.getSequenceId(), "Message error");
        }
    }

    private void handlePresence(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            String status = new String(packet.getPayload(), StandardCharsets.UTF_8);
            presenceService.updatePresence(session.getUserId(), status);
        } catch (Exception e) {
            log.error("Error handling presence update", e);
        }
    }

    private void handleStatusMessage(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            String statusMessage = new String(packet.getPayload(), StandardCharsets.UTF_8);
            presenceService.updateStatusMessage(session.getUserId(), statusMessage);
        } catch (Exception e) {
            log.error("Error handling status message", e);
        }
    }

    private void handleGetContacts(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            byte[] contactData = contactService.getContactListData(session.getUserId());
            FusionPacket response = FusionPacket.builder()
                    .type(PacketType.CONTACT_LIST)
                    .sequenceId(packet.getSequenceId())
                    .payload(contactData)
                    .build();
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            log.error("Error handling get contacts", e);
            sendError(ctx, packet.getSequenceId(), "Error retrieving contacts");
        }
    }

    private void handleAddContact(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            String targetUsername = new String(packet.getPayload(), StandardCharsets.UTF_8);
            contactService.addContact(session.getUserId(), targetUsername);
        } catch (Exception e) {
            log.error("Error handling add contact", e);
            sendError(ctx, packet.getSequenceId(), "Error adding contact");
        }
    }

    private void handleRemoveContact(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        if (!session.isAuthenticated()) {
            sendError(ctx, packet.getSequenceId(), "Not authenticated");
            return;
        }

        try {
            String targetUsername = new String(packet.getPayload(), StandardCharsets.UTF_8);
            contactService.removeContact(session.getUserId(), targetUsername);
        } catch (Exception e) {
            log.error("Error handling remove contact", e);
            sendError(ctx, packet.getSequenceId(), "Error removing contact");
        }
    }

    private void handleHeartbeat(ChannelHandlerContext ctx, ClientSession session, FusionPacket packet) {
        session.updateHeartbeat();
        FusionPacket ack = FusionPacket.builder()
                .type(PacketType.HEARTBEAT_ACK)
                .sequenceId(packet.getSequenceId())
                .payload(new byte[0])
                .build();
        ctx.writeAndFlush(ack);
    }

    private void sendError(ChannelHandlerContext ctx, int sequenceId, String message) {
        FusionPacket error = FusionPacket.builder()
                .type(PacketType.ERROR)
                .sequenceId(sequenceId)
                .payload(message.getBytes(StandardCharsets.UTF_8))
                .build();
        ctx.writeAndFlush(error);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in Fusion Protocol handler", cause);
        ctx.close();
    }
}
