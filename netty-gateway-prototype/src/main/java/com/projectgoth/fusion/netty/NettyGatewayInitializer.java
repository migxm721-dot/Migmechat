package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link ChannelInitializer} for the Fusion gateway prototype.
 *
 * <p>Wires together the pipeline stages:
 * <ol>
 *   <li>{@link FusionPacketDecoder} – accumulates bytes and emits complete
 *       {@link FusionPacket} value objects.</li>
 *   <li>{@link FusionPacketEncoder} – serialises outbound {@link FusionPacket}
 *       objects back to raw bytes.</li>
 *   <li>{@link GatewayChannelHandler} – business-logic handler that delegates
 *       to the existing {@code PacketProcessor} API.</li>
 * </ol>
 *
 * <p>This class is safe to use in parallel with the existing
 * {@code ConnectionTCP}/{@code ConnectionHTTP} implementation; it does not
 * replace them.  Enable via the {@code netty-prototype} Maven profile and the
 * {@code netty.gateway.enabled=true} system/configuration property.
 */
public class NettyGatewayInitializer extends ChannelInitializer<SocketChannel> {

    private static final Logger log = LoggerFactory.getLogger(NettyGatewayInitializer.class);

    @Override
    protected void initChannel(SocketChannel ch) {
        log.debug("Initialising Netty pipeline for {}", ch.remoteAddress());
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new FusionPacketDecoder());
        pipeline.addLast("encoder", new FusionPacketEncoder());
        pipeline.addLast("handler", new GatewayChannelHandler());
    }
}
