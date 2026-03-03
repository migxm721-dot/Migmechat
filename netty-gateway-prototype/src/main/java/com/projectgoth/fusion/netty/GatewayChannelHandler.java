package com.projectgoth.fusion.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link ChannelInboundHandlerAdapter} that receives decoded
 * {@link FusionPacket} objects from the pipeline and dispatches them to the
 * existing {@code PacketProcessor} API.
 *
 * <h2>Wiring to the existing PacketProcessor</h2>
 * <p>The existing gateway uses a {@code PacketProcessor} (found under
 * {@code com.projectgoth.fusion.gateway}) to route packets to the
 * application layer.  When the core JAR is on the classpath, inject the
 * {@code PacketProcessor} via the constructor (see TODO below) and replace
 * the stub dispatch call with the real one.
 *
 * <h2>Non-invasive design</h2>
 * <p>This handler does <em>not</em> alter or replace
 * {@code ConnectionTCP}/{@code ConnectionHTTP}.  It runs in a separate Netty
 * {@code ServerBootstrap} that binds to a different port (default {@code 9119})
 * so it can receive traffic in parallel for shadow/A-B testing.
 */
@io.netty.channel.ChannelHandler.Sharable
public class GatewayChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(GatewayChannelHandler.class);

    /**
     * TODO: Inject the real {@code PacketProcessor} instance here once the core
     * module is a dependency of this module.
     * <pre>
     *   private final PacketProcessor packetProcessor;
     *
     *   public GatewayChannelHandler(PacketProcessor packetProcessor) {
     *       this.packetProcessor = packetProcessor;
     *   }
     * </pre>
     */

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("New Netty connection from {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("Netty connection closed from {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof FusionPacket packet)) {
            log.warn("Unexpected message type: {}", msg.getClass().getName());
            return;
        }
        log.debug("Received packet: {}", packet);
        dispatch(ctx, packet);
    }

    /**
     * Dispatches the decoded packet to the application layer.
     *
     * <p>TODO: Replace stub with a real call once the core JAR is available:
     * <pre>
     *   packetProcessor.processPacket(null, packet.toBytes(), null);
     * </pre>
     *
     * @param ctx    channel context (used to write responses back to the client)
     * @param packet decoded packet to dispatch
     */
    private void dispatch(ChannelHandlerContext ctx, FusionPacket packet) {
        // Stub: echo the packet back so the pipeline can be smoke-tested end-to-end.
        log.debug("Stub dispatch: echoing packet type={}", packet.getType());
        ctx.writeAndFlush(packet);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in Netty channel handler", cause);
        ctx.close();
    }
}
