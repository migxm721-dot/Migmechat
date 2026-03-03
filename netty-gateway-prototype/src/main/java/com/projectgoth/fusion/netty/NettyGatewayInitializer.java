package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link ChannelInitializer} that wires the Fusion packet pipeline.
 *
 * <p>Pipeline layout (inbound left → right, outbound right → left):
 * <pre>
 *   [FusionPacketDecoder] → [GatewayChannelHandler]
 *                         ← [FusionPacketEncoder]
 * </pre>
 *
 * <p>Usage – bind on a port and supply this initializer to
 * {@code ServerBootstrap.childHandler(...)}:
 * <pre>{@code
 *   ServerBootstrap b = new ServerBootstrap();
 *   b.group(bossGroup, workerGroup)
 *    .channel(NioServerSocketChannel.class)
 *    .childHandler(new NettyGatewayInitializer());
 *   b.bind(9119).sync();
 * }</pre>
 *
 * <p>This class is part of the non-invasive Netty prototype and does not
 * modify existing {@code ConnectionTCP}/{@code ConnectionHTTP} classes.
 */
public class NettyGatewayInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(NettyGatewayInitializer.class);

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        LOG.debug("Initialising Netty pipeline for channel {}", ch.id());

        // Inbound: decode raw bytes into FusionPacket objects
        pipeline.addLast("decoder", new FusionPacketDecoder());

        // Outbound: encode FusionPacket objects back to raw bytes
        pipeline.addLast("encoder", new FusionPacketEncoder());

        // Inbound: business-logic handler – forwards decoded packets to PacketProcessor
        pipeline.addLast("handler", new GatewayChannelHandler());
    }
}
