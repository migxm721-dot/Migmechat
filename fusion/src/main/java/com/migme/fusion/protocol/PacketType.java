package com.migme.fusion.protocol;

import java.util.HashMap;
import java.util.Map;

public enum PacketType {
    // Authentication
    LOGIN(0x01),
    LOGIN_OK(0x02),
    LOGIN_FAIL(0x03),
    LOGOUT(0x04),

    // Messaging
    MESSAGE(0x10),
    MESSAGE_ACK(0x11),
    MESSAGE_STATUS_EVENT(0x12),

    // Presence
    PRESENCE(0x20),
    STATUS_MESSAGE(0x21),
    DISPLAY_PICTURE(0x22),

    // Contacts
    GET_CONTACTS(0x30),
    CONTACT_LIST(0x31),
    ADD_CONTACT(0x32),
    REMOVE_CONTACT(0x33),
    CONTACT_NOTIFICATION(0x34),

    // Chat
    CHAT(0x40),
    CHAT_LIST_VERSION(0x41),

    // Heartbeat
    HEARTBEAT(0x50),
    HEARTBEAT_ACK(0x51),

    // Error
    ERROR(0xFF);

    private final int code;
    private static final Map<Integer, PacketType> CODE_MAP = new HashMap<>();

    static {
        for (PacketType type : values()) {
            CODE_MAP.put(type.code, type);
        }
    }

    PacketType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PacketType fromCode(int code) {
        PacketType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Unknown packet type code: " + code);
        }
        return type;
    }
}
