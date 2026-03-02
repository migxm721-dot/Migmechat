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

public abstract class FusionPktDataUploadAddressBookContacts
extends FusionRequest {
    public FusionPktDataUploadAddressBookContacts() {
        super(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS);
    }

    public FusionPktDataUploadAddressBookContacts(short transactionId) {
        super(PacketType.UPLOAD_ADDRESS_BOOK_CONTACTS, transactionId);
    }

    public FusionPktDataUploadAddressBookContacts(ByteBuffer byteBuffer) throws IOException {
        super(byteBuffer);
    }

    public FusionPktDataUploadAddressBookContacts(FusionPacket packet) {
        super(packet);
    }

    public final boolean sessionRequired() {
        return true;
    }

    public final DataType getDataType() {
        return DataType.fromValue(this.getByteField((short)1));
    }

    public final void setDataType(DataType dataType) {
        this.setField((short)1, dataType.value());
    }

    public final String[] getDataList() {
        return this.getStringArrayField((short)2);
    }

    public final void setDataList(String[] dataList) {
        this.setField((short)2, dataList);
    }

    public final String getTicketId() {
        return this.getStringField((short)3);
    }

    public final void setTicketId(String ticketId) {
        this.setField((short)3, ticketId);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum DataType {
        PHONE_NUMBER(1),
        EMAIL_ADDRESS(2);

        private byte value;
        private static final HashMap<Byte, DataType> LOOKUP;

        private DataType(byte value) {
            this.value = value;
        }

        public byte value() {
            return this.value;
        }

        public static DataType fromValue(int value) {
            return LOOKUP.get((byte)value);
        }

        public static DataType fromValue(Byte value) {
            return LOOKUP.get(value);
        }

        static {
            LOOKUP = new HashMap();
            for (DataType dataType : DataType.values()) {
                LOOKUP.put(dataType.value, dataType);
            }
        }
    }
}

