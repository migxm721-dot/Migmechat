package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty {@link MessageToByteEncoder} skeleton that serialises {@link FusionPacket}
 * objects back into the length-prefixed binary wire format.
 *
 * <p>Replace the {@code TODO} block with a call to the real serialisation method
 * (e.g. {@code FusionPacket#toByteBuffer()}) once this module is wired into the
 * main project.
 *
 * <p>This class is part of the non-invasive Netty prototype and does not
 * modify existing {@code ConnectionTCP}/{@code ConnectionHTTP} classes.
 */
public class FusionPacketEncoder extends MessageToByteEncoder<FusionPacket> {

    private static final Logger LOG = LoggerFactory.getLogger(FusionPacketEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, FusionPacket msg, ByteBuf out) {
        byte[] payload = msg.toRawPayload();

        // TODO: Replace with FusionPacket#toByteBuffer() serialisation once the
        //       main module is on the classpath.
        out.writeInt(payload.length);
        out.writeBytes(payload);

        LOG.debug("Encoded FusionPacket type={} len={}", msg.getType(), payload.length);
    }
}
