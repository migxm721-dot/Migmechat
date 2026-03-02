/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.fdl.packets;

import com.projectgoth.fusion.fdl.enums.PacketType;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class FusionPktDataUploadFile
extends FusionRequest {
    public FusionPktDataUploadFile() {
        super(PacketType.UPLOAD_FILE);
    }

    public FusionPktDataUploadFile(short transactionId) {
        super(PacketType.UPLOAD_FILE, transactionId);
    }

    public FusionPktDataUploadFile(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktDataUploadFile(FusionPacket packet) {
        super(packet);
    }

    public final boolean sessionRequired() {
        return true;
    }

    public final FileType getFileType() {
        return FileType.fromValue(this.getShortField((short)1));
    }

    public final void setFileType(FileType fileType) {
        this.setField((short)1, fileType.value());
    }

    public final byte[] getFileContent() {
        return this.getByteArrayField((short)2);
    }

    public final void setFileContent(byte[] fileContent) {
        this.setField((short)2, fileContent);
    }

    public final String getDescription() {
        return this.getStringField((short)3);
    }

    public final void setDescription(String description) {
        this.setField((short)3, description);
    }

    public final String getDestination() {
        return this.getStringField((short)4);
    }

    public final void setDestination(String destination) {
        this.setField((short)4, destination);
    }

    public final Boolean getUseAsDisplayPicture() {
        return this.getBooleanField((short)5);
    }

    public final void setUseAsDisplayPicture(boolean useAsDisplayPicture) {
        this.setField((short)5, useAsDisplayPicture);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum FileType {
        IMAGE(1),
        AUDIO(2),
        VIDEO(3);

        private short value;
        private static final HashMap<Short, FileType> LOOKUP;

        private FileType(short value) {
            this.value = value;
        }

        public short value() {
            return this.value;
        }

        public static FileType fromValue(int value) {
            return LOOKUP.get((short)value);
        }

        public static FileType fromValue(Short value) {
            return LOOKUP.get(value);
        }

        static {
            LOOKUP = new HashMap();
            for (FileType fileType : FileType.values()) {
                LOOKUP.put(fileType.value, fileType);
            }
        }
    }
}

