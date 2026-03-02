/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import com.projectgoth.fusion.chat.external.msn.DWord;
import com.projectgoth.fusion.chat.external.msn.MSNSLPMessage;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class P2PMessage {
    private DWord sessionId = new DWord();
    private DWord identifier = new DWord();
    private long dataOffset;
    private long totalSize;
    private DWord size = new DWord();
    private DWord flag = new DWord();
    private DWord ackIdentifier = new DWord();
    private DWord ackId = new DWord();
    private long ackDataSize;
    private String destination;
    private Object content;
    private DWord appIdentifier = new DWord();

    public P2PMessage() {
    }

    public P2PMessage(String rawMessage) throws UnsupportedEncodingException {
        Pattern pattern = Pattern.compile("Content-Type: application/x-msnmsgrp2p\\r\\nP2P-Dest: (.*)", 32);
        Matcher matcher = pattern.matcher(rawMessage);
        if (matcher.find()) {
            rawMessage = matcher.group(1);
            int idx = rawMessage.indexOf("\r\n");
            this.destination = rawMessage.substring(0, idx);
            rawMessage = rawMessage.substring(idx + 4);
            int len = rawMessage.length();
            String header = rawMessage.substring(0, 48);
            String s = rawMessage.substring(48, len - 4);
            if (s != null && s.length() > 0) {
                this.content = new MSNSLPMessage(s);
            }
            String footer = rawMessage.substring(len - 4);
            ByteBuffer buffer = ByteBuffer.wrap(header.getBytes("ISO8859_1"));
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            this.sessionId.read(buffer);
            this.identifier.read(buffer);
            this.dataOffset = buffer.getLong();
            this.totalSize = buffer.getLong();
            this.size.read(buffer);
            this.flag.read(buffer);
            this.ackIdentifier.read(buffer);
            this.ackId.read(buffer);
            this.ackDataSize = buffer.getLong();
            buffer = ByteBuffer.wrap(footer.getBytes("ISO8859_1"));
            buffer.order(ByteOrder.BIG_ENDIAN);
            this.appIdentifier.read(buffer);
        }
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(byte[] data) {
        this.content = data;
        this.size = new DWord(data.length);
    }

    public void setContent(MSNSLPMessage message) {
        this.content = message;
        this.size = new DWord(message.toString().length());
    }

    public MSNSLPMessage getMSNSLP() {
        if (this.content instanceof MSNSLPMessage) {
            return (MSNSLPMessage)this.content;
        }
        return null;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public DWord getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(DWord identifier) {
        this.identifier = identifier;
    }

    public DWord getAckIdentifier() {
        return this.ackIdentifier;
    }

    public void setAckIdentifier(DWord ackIdentifier) {
        this.ackIdentifier = ackIdentifier;
    }

    private byte[] getHeader() {
        ByteBuffer header = ByteBuffer.wrap(new byte[48]);
        header.order(ByteOrder.LITTLE_ENDIAN);
        header.put(this.sessionId.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.put(this.identifier.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.putLong(this.dataOffset);
        header.putLong(this.totalSize);
        header.put(this.size.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.put(this.flag.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.put(this.ackIdentifier.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.put(this.ackId.toByteArray(ByteOrder.LITTLE_ENDIAN));
        header.putLong(this.ackDataSize);
        return header.array();
    }

    private byte[] getFooter() {
        ByteBuffer footer = ByteBuffer.wrap(new byte[4]);
        footer.order(ByteOrder.BIG_ENDIAN);
        footer.put(this.appIdentifier.toByteArray(ByteOrder.BIG_ENDIAN));
        return footer.array();
    }

    public static P2PMessage createAcknowledgement(int type, P2PMessage message) {
        P2PMessage ack = new P2PMessage();
        ack.sessionId = new DWord(message.sessionId);
        ack.totalSize = message.totalSize;
        ack.flag = new DWord(type);
        ack.ackIdentifier = new DWord(message.identifier);
        ack.ackId = new DWord(message.ackIdentifier);
        ack.ackDataSize = message.totalSize;
        return ack;
    }

    public static P2PMessage createOkStatus(DWord sessionId, P2PMessage message) {
        MSNSLPMessage invitation = (MSNSLPMessage)message.getContent();
        MSNSLPMessage sip = new MSNSLPMessage(MSNSLPMessage.Type.STATUS, 200, "OK");
        sip.setHeader("To", invitation.getHeader("From"));
        sip.setHeader("From", invitation.getHeader("To"));
        sip.setHeader("Via", invitation.getHeader("Via"));
        sip.setHeader("CSeq", "1 ");
        sip.setHeader("Call-ID", invitation.getHeader("Call-ID"));
        sip.setHeader("Max-Forwards", "0");
        sip.setHeader("Content-Type", invitation.getHeader("Content-Type"));
        MSNSLPMessage.Content sipContent = sip.new MSNSLPMessage.Content();
        sipContent.setValue("SessionID", sessionId.toString());
        sip.setContent(sipContent);
        P2PMessage status = new P2PMessage();
        status.totalSize = sip.toString().length();
        status.ackIdentifier = new DWord(Math.abs(new SecureRandom().nextInt()));
        status.setContent(sip);
        return status;
    }

    public static P2PMessage createDataPreparation(DWord sessionId) {
        P2PMessage dataPreparation = new P2PMessage();
        dataPreparation.sessionId = new DWord(sessionId);
        dataPreparation.totalSize = 4L;
        dataPreparation.ackIdentifier = new DWord(Math.abs(new SecureRandom().nextInt()));
        dataPreparation.setContent(new byte[4]);
        dataPreparation.appIdentifier = new DWord(1L);
        return dataPreparation;
    }

    public static List<P2PMessage> createDataMessages(DWord sessionId, byte[] data) {
        int totalBytes;
        ArrayList<P2PMessage> list = new ArrayList<P2PMessage>();
        SecureRandom secureRandom = new SecureRandom();
        for (int remainingBytes = totalBytes = data.length; remainingBytes > 0; remainingBytes -= 1202) {
            P2PMessage fragment = new P2PMessage();
            fragment.sessionId = new DWord(sessionId);
            fragment.dataOffset = totalBytes - remainingBytes;
            fragment.totalSize = totalBytes;
            fragment.flag = new DWord(32L);
            fragment.ackIdentifier = new DWord(Math.abs(secureRandom.nextInt()));
            int bytesToSend = remainingBytes > 1202 ? 1202 : remainingBytes;
            byte[] buffer = new byte[bytesToSend];
            System.arraycopy(data, totalBytes - remainingBytes, buffer, 0, bytesToSend);
            fragment.setContent(buffer);
            fragment.appIdentifier = new DWord(1L);
            list.add(fragment);
        }
        return list;
    }

    private String byteArrayToString(byte[] byteArray) {
        try {
            String s = new String(byteArray, "ISO8859_1");
            return s;
        }
        catch (Exception e) {
            return "";
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("MIME-Version: 1.0\r\nContent-Type: application/x-msnmsgrp2p\r\nP2P-Dest: ");
        builder.append(this.destination);
        builder.append("\r\n\r\n");
        builder.append(this.byteArrayToString(this.getHeader()));
        if (this.content != null) {
            if (this.content instanceof MSNSLPMessage) {
                builder.append(((MSNSLPMessage)this.content).toString());
            } else if (this.content instanceof String) {
                builder.append(this.content);
            } else if (this.content instanceof byte[]) {
                builder.append(this.byteArrayToString((byte[])this.content));
            }
        }
        builder.append(this.byteArrayToString(this.getFooter()));
        return builder.toString();
    }

    public String toReadableString() {
        StringBuilder builder = new StringBuilder("MIME-Version: 1.0\r\nContent-Type: application/x-msnmsgrp2p\r\nP2P-Dest: ");
        builder.append(this.destination);
        builder.append("\r\n\r\n");
        for (byte b : this.getHeader()) {
            builder.append(Integer.toHexString(b >> 4 & 0xF));
            builder.append(Integer.toHexString(b & 0xF));
            builder.append(" ");
        }
        if (this.content != null) {
            if (this.content instanceof MSNSLPMessage) {
                builder.append(((MSNSLPMessage)this.content).toString());
            } else if (this.content instanceof String) {
                builder.append(this.content);
            } else if (this.content instanceof byte[]) {
                for (byte b : (byte[])this.content) {
                    builder.append((char)b);
                }
            }
        }
        for (byte b : this.getFooter()) {
            builder.append(Integer.toHexString(b >> 4 & 0xF));
            builder.append(Integer.toHexString(b & 0xF));
            builder.append(" ");
        }
        return builder.toString();
    }
}

