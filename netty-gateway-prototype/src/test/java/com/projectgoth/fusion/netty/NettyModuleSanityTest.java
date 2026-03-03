package com.projectgoth.fusion.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sanity tests that verify the Netty pipeline initialises correctly and that
 * the decoder/encoder round-trip works for a minimal Fusion packet.
 */
class NettyModuleSanityTest {

    /**
     * Verifies that the expected pipeline handlers can be instantiated and
     * added to an EmbeddedChannel without errors.
     */
    @Test
    void pipelineContainsExpectedHandlers() {
        EmbeddedChannel channel = buildChannel();
        assertNotNull(channel.pipeline().get("decoder"), "decoder stage must be present");
        assertNotNull(channel.pipeline().get("encoder"), "encoder stage must be present");
        assertNotNull(channel.pipeline().get("handler"), "handler stage must be present");
        channel.finish();
    }

    /**
     * Verifies that a valid Fusion binary packet can be decoded by
     * {@link FusionPacketDecoder} and that the resulting {@link FusionPacket}
     * has the correct type, transaction ID and payload.
     */
    @Test
    void decoderParsesValidPacket() {
        short type = 42;
        short txnId = 7;
        byte[] payload = {0x01, 0x02, 0x03};

        ByteBuf raw = encodedPacketBuf(type, txnId, payload);

        // Test the decoder in isolation so the decoded packet is not consumed by the handler.
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast("decoder", new FusionPacketDecoder());
        channel.writeInbound(raw);

        FusionPacket decoded = channel.readInbound();
        assertNotNull(decoded, "Decoder must emit a FusionPacket");
        assertEquals(type, decoded.getType());
        assertEquals(txnId, decoded.getTransactionId());
        assertArrayEquals(payload, decoded.getPayload());
        channel.finish();
    }

    /**
     * Verifies that a partial packet (missing the last byte) does not produce
     * a decoded message – the decoder must wait for more data.
     */
    @Test
    void decoderWaitsForCompletePacket() {
        short type = 1;
        short txnId = 0;
        byte[] payload = {0x0A, 0x0B};

        ByteBuf raw = encodedPacketBuf(type, txnId, payload);
        // Trim the last byte to simulate a partial delivery.
        ByteBuf partial = raw.slice(0, raw.readableBytes() - 1).retain();

        // Test the decoder in isolation.
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast("decoder", new FusionPacketDecoder());
        channel.writeInbound(partial);

        assertNull(channel.readInbound(), "Partial packet must not be emitted");
        channel.finish();
    }

    /**
     * Verifies that the encoder serialises a {@link FusionPacket} to the
     * expected binary wire format.
     */
    @Test
    void encoderWritesCorrectWireFormat() {
        short type = 10;
        short txnId = 3;
        byte[] payload = {0x05, 0x06};

        FusionPacket packet = new FusionPacket(type, txnId, payload);
        EmbeddedChannel channel = buildChannel();
        channel.writeOutbound(packet);

        ByteBuf written = channel.readOutbound();
        assertNotNull(written, "Encoder must write a ByteBuf");
        assertEquals(FusionPacket.HEADER_SIZE + payload.length, written.readableBytes());

        // Verify STX marker
        assertEquals(0x02, written.getByte(0));
        // Verify type
        assertEquals(type, written.getShort(1));
        // Verify transaction ID
        assertEquals(txnId, written.getShort(3));
        // Verify content length
        assertEquals(payload.length, written.getInt(5));

        channel.finish();
    }

    /**
     * Verifies that {@link FusionPacket#fromBytes(byte[])} round-trips through
     * {@link FusionPacket#toBytes()} correctly.
     */
    @Test
    void fusionPacketRoundTrip() {
        short type = 99;
        short txnId = 1234;
        byte[] payload = {0x11, 0x22, 0x33, 0x44};

        FusionPacket original = new FusionPacket(type, txnId, payload);
        byte[] bytes = original.toBytes();
        FusionPacket parsed = FusionPacket.fromBytes(bytes);

        assertEquals(original.getType(), parsed.getType());
        assertEquals(original.getTransactionId(), parsed.getTransactionId());
        assertArrayEquals(original.getPayload(), parsed.getPayload());
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Builds an EmbeddedChannel with the same pipeline stages that
     * {@link NettyGatewayInitializer} would add to a real {@link io.netty.channel.socket.SocketChannel}.
     */
    private static EmbeddedChannel buildChannel() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast("decoder", new FusionPacketDecoder());
        channel.pipeline().addLast("encoder", new FusionPacketEncoder());
        channel.pipeline().addLast("handler", new GatewayChannelHandler());
        return channel;
    }

    /** Builds a raw Fusion binary packet ByteBuf from parts. */
    private static ByteBuf encodedPacketBuf(short type, short txnId, byte[] payload) {
        FusionPacket packet = new FusionPacket(type, txnId, payload);
        byte[] bytes = packet.toBytes();
        return Unpooled.wrappedBuffer(bytes);
    }
}

