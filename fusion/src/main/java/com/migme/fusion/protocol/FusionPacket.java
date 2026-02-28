package com.migme.fusion.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FusionPacket {

    private PacketType type;
    private int sequenceId;
    private byte[] payload;

    public int getPayloadLength() {
        return payload != null ? payload.length : 0;
    }
}
