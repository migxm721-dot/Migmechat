package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Business-logic handler that receives fully-decoded {@link FusionPacketMessage}
 * objects from the Netty pipeline and forwards them to the existing
 * {@code PacketProcessor} API.
 *
 * <p>This handler is intentionally non-invasive: it reads from the new Netty
 * pipeline but delegates to the same processing logic used by the legacy
 * {@code ConnectionTCP} / {@code ConnectionHTTP} classes, enabling A/B testing
 * without modifying any production code.
 *
 * <p>Wiring note: inject a reference to the existing {@code PacketProcessor}
 * (or equivalent gateway service) via the constructor once the core module is
 * on the classpath.
 */
@io.netty.channel.ChannelHandler.Sharable
public class GatewayChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(GatewayChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("New connection from {}", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Connection closed: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof FusionPacketMessage packet)) {
            log.warn("Unexpected message type: {}", msg.getClass().getName());
            ctx.fireChannelRead(msg);
            return;
        }

        log.debug("Received FusionPacket: type={} payloadLen={}",
                packet.getPacketType(), packet.getPayload().length);

        // TODO: replace stub dispatch with production PacketProcessor call, e.g.:
        //   packetProcessor.process(ctx.channel(), FusionPacket.fromByteBuffer(...));
        dispatch(ctx, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Channel error on {}; closing.", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

    /**
     * Stub dispatcher – replace the body with a call to the real
     * {@code PacketProcessor.process()} method.
     */
    private void dispatch(ChannelHandlerContext ctx, FusionPacketMessage packet) {
        log.debug("Dispatching packetType={} (stub – wire to PacketProcessor)", packet.getPacketType());
        // No-op prototype: production code goes here.
    }
}
