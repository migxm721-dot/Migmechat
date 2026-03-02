/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.yahoo;

import com.projectgoth.fusion.chat.external.yahoo.YahooService;
import com.projectgoth.fusion.chat.external.yahoo.YahooStatus;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YMSGPacket {
    private static byte[] SEPARATOR = new byte[]{-64, -128};
    private String protocol = "YMSG";
    private short version = (short)16;
    private short service;
    private int status;
    private int sessionId;
    private Map<String, List<String>> data = new HashMap<String, List<String>>();

    public YMSGPacket(YahooService service, YahooStatus status, int sessionId) {
        this.service = service.getValue();
        this.status = status.getValue();
        this.sessionId = sessionId;
    }

    public YMSGPacket(String protocol, short version, YahooService service, YahooStatus status, int sessionId) {
        this(service, status, sessionId);
        this.protocol = protocol;
        this.version = version;
    }

    public YMSGPacket(ByteBuffer buffer) throws IOException, BufferUnderflowException {
        byte[] b = new byte[4];
        buffer.get(b);
        this.protocol = new String(b);
        this.version = buffer.getShort();
        buffer.getShort();
        short length = buffer.getShort();
        this.service = buffer.getShort();
        this.status = buffer.getInt();
        this.sessionId = buffer.getInt();
        if (length > 0) {
            b = new byte[length];
            buffer.get(b);
            String[] tokens = new String(b, "UTF-8").split(new String(SEPARATOR, "UTF-8"));
            for (int i = 0; i < tokens.length; i += 2) {
                List<String> l = this.data.get(tokens[i]);
                if (l == null) {
                    l = new ArrayList<String>();
                    this.data.put(tokens[i], l);
                }
                if (i + 1 < tokens.length) {
                    l.add(tokens[i + 1]);
                    continue;
                }
                l.add(null);
            }
        }
    }

    public short getService() {
        return this.service;
    }

    public int getStatus() {
        return this.status;
    }

    public int getSessionId() {
        return this.sessionId;
    }

    public String getField(int key) {
        return this.getField(key, 0);
    }

    public String getField(int key, int index) {
        List<String> l = this.data.get(String.valueOf(key));
        if (l == null || index >= l.size()) {
            return null;
        }
        return l.get(index);
    }

    public void setField(int key, String value) {
        List<String> l = this.data.get(String.valueOf(key));
        if (l == null) {
            l = new ArrayList<String>();
            l.add(value);
            this.data.put(String.valueOf(key), l);
        } else if (l.size() == 0) {
            l.add(value);
        } else {
            l.add(value);
        }
    }

    public int length() {
        byte[] bytes = this.toByteArrayBa();
        if (bytes != null) {
            return bytes.length;
        }
        return 0;
    }

    private String byteArrayToHexString(byte[] ba) {
        StringBuilder builder = new StringBuilder();
        for (byte b : ba) {
            builder.append(Integer.toHexString(b >> 4 & 0xF));
            builder.append(Integer.toHexString(b & 0xF));
        }
        return builder.toString();
    }

    private byte[] toByteArrayBa() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            DataOutputStream os = new DataOutputStream(baos);
            for (Map.Entry<String, List<String>> entry : this.data.entrySet()) {
                byte[] keyBytes = entry.getKey().toString().getBytes();
                for (String value : entry.getValue()) {
                    os.write(keyBytes);
                    os.write(SEPARATOR);
                    if (value != null) {
                        os.write(value.getBytes("UTF-8"));
                    }
                    os.write(SEPARATOR);
                }
            }
            return baos.toByteArray();
        }
        catch (Exception e) {
            return null;
        }
    }

    public byte[] toByteArray() {
        try {
            byte[] ba = this.toByteArrayBa();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            DataOutputStream os = new DataOutputStream(baos);
            os.write(this.protocol.getBytes());
            os.writeShort(this.version);
            os.writeShort(0);
            os.writeShort(ba.length);
            os.writeShort(this.service);
            os.writeInt(this.status);
            os.writeInt(this.sessionId);
            os.write(ba);
            return baos.toByteArray();
        }
        catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Protocol: ").append(this.protocol).append("\n");
        builder.append("Version: ").append(this.version).append("\n");
        builder.append("Length: ").append(this.length()).append("\n");
        builder.append("Service: ").append(this.service).append("\n");
        builder.append("Status: ").append(this.status).append("\n");
        builder.append("Session ID: ").append(this.sessionId).append("\n");
        for (Map.Entry<String, List<String>> entry : this.data.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue()) {
                builder.append(key).append(": ").append(value).append("\n");
            }
        }
        builder.append("Byte array: ").append(this.byteArrayToHexString(this.toByteArray()));
        return builder.toString();
    }
}

