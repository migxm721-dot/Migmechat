package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link MessageToByteEncoder} that serialises outbound
 * {@link FusionPacket} value objects to raw bytes.
 *
 * <p>The encoding mirrors the Fusion binary wire format produced by
 * {@code FusionPacket.toByteArray()} in the core library.  When the core JAR
 * is available as a dependency, replace the call to {@link FusionPacket#toBytes()}
 * below with the real {@code com.projectgoth.fusion.packet.FusionPacket#toByteArray()}.
 */
public class FusionPacketEncoder extends MessageToByteEncoder<FusionPacket> {

    private static final Logger log = LoggerFactory.getLogger(FusionPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, FusionPacket packet, ByteBuf out) {
        byte[] bytes = packet.toBytes();
        log.debug("Encoding packet type={} txnId={} totalBytes={}",
                packet.getType(), packet.getTransactionId(), bytes.length);
        out.writeBytes(bytes);
    }
}
