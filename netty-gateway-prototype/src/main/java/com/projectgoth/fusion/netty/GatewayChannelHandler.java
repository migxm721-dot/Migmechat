package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link ChannelInboundHandlerAdapter} that receives decoded
 * {@link FusionPacket} objects and forwards them to the existing
 * {@code PacketProcessor} API.
 *
 * <p>Replace the {@code TODO} block in {@link #channelRead} with a call to the
 * real {@code PacketProcessor} once this module is wired into the main project.
 *
 * <p>This class is part of the non-invasive Netty prototype and does not
 * modify existing {@code ConnectionTCP}/{@code ConnectionHTTP} classes.
 * It can be activated in parallel to the existing networking layer for A/B
 * testing by enabling the {@code netty-prototype} Maven profile.
 */
@io.netty.channel.ChannelHandler.Sharable
public class GatewayChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayChannelHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOG.info("Client connected: {}", ctx.channel().remoteAddress());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOG.info("Client disconnected: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof FusionPacket packet)) {
            ctx.fireChannelRead(msg);
            return;
        }

        LOG.debug("Received FusionPacket type={} from {}", packet.getType(),
                ctx.channel().remoteAddress());

        // TODO: Replace with call to the real PacketProcessor, for example:
        //   packetProcessor.process(connection, packet);
        // where `connection` adapts the Netty channel to the existing Connection interface.
        processPacket(ctx, packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.error("Unhandled exception on channel {}: {}", ctx.channel().id(), cause.getMessage(), cause);
        ctx.close();
    }

    /**
     * Stub processing method – replace with delegation to the real
     * {@code PacketProcessor} once this module is integrated with the main project.
     *
     * @param ctx    the Netty channel context
     * @param packet the decoded packet
     */
    private void processPacket(ChannelHandlerContext ctx, FusionPacket packet) {
        LOG.info("Processing packet type={} (stub – wire real PacketProcessor here)", packet.getType());
        // Echo the packet back as a basic smoke test; remove in production integration
        ctx.writeAndFlush(packet);
    }
}
