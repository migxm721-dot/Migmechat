package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * Netty {@link ChannelInitializer} for the Fusion gateway prototype.
 *
 * <p>Wires the inbound decoding pipeline and outbound encoding pipeline together
 * with the business-logic handler ({@link GatewayChannelHandler}).  Activate
 * this code path via the Maven profile {@code netty-prototype} or the system
 * property {@code netty.prototype.enabled=true}.
 *
 * <p>Example bootstrap (for reference – see README.md for full wiring):
 * <pre>{@code
 *   ServerBootstrap b = new ServerBootstrap();
 *   b.group(bossGroup, workerGroup)
 *    .channel(NioServerSocketChannel.class)
 *    .childHandler(new NettyGatewayInitializer());
 *   b.bind(9119).sync();
 * }</pre>
 */
public class NettyGatewayInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("decoder", new FusionPacketDecoder());
        pipeline.addLast("encoder", new FusionPacketEncoder());
        pipeline.addLast("handler", new GatewayChannelHandler());
    }
}
