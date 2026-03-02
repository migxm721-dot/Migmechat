/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MSNSLPMessage {
    private static final String VERSION = "MSNSLP/1.0";
    private Type type;
    private String destination;
    private int statusCode;
    private String statusReason;
    private Content header;
    private Content content;

    public MSNSLPMessage(Type type, String destination) {
        this.type = type;
        this.destination = destination;
        this.header = new Content();
    }

    public MSNSLPMessage(Type type, int statusCode, String statusReason) {
        this.type = type;
        this.statusCode = statusCode;
        this.statusReason = statusReason;
        this.header = new Content();
    }

    public MSNSLPMessage(String rawMessage) {
        String[] lines = rawMessage.split("\r\n", 2);
        if (lines.length == 2) {
            String[] tokens = lines[0].split(" ");
            if (tokens.length > 2) {
                String token = tokens[0];
                if (VERSION.equals(token)) {
                    this.type = Type.STATUS;
                    this.statusCode = Integer.parseInt(tokens[1]);
                    this.statusReason = tokens[2];
                } else {
                    try {
                        this.type = Type.valueOf(token);
                        this.destination = tokens[1];
                    }
                    catch (Exception e) {
                        this.type = Type.UNKNOWN;
                    }
                }
            }
            if (this.type != Type.UNKNOWN && (lines = lines[1].split("\r\n\r\n", 2)).length > 0) {
                this.header = new Content(lines[0]);
                if (lines.length > 1) {
                    this.content = new Content(lines[1]);
                }
            }
        }
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDestination() {
        return this.destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getHeader(String key) {
        return this.header == null ? null : this.header.getString(key);
    }

    public void setHeader(String key, String value) {
        this.header.setValue(key, value);
    }

    public Content getContent() {
        return this.content;
    }

    public void setContent(Content content) {
        this.content = content;
        this.header.setValue("Content-Length", String.valueOf(content.toString().length()));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.type == Type.STATUS) {
            builder.append(VERSION).append(" ").append(this.statusCode).append(" ").append(this.statusReason).append("\r\n");
        } else {
            builder.append(this.type.toString()).append(" ").append(this.destination).append(" ").append(VERSION).append("\r\n");
        }
        if (this.header != null) {
            builder.append(this.header.toString());
        }
        builder.append("\r\n");
        if (this.content != null) {
            builder.append(this.content.toString());
        }
        return builder.append('\u0000').toString();
    }

    public class Content {
        private Map<String, String> map = new ConcurrentHashMap<String, String>();

        public Content() {
        }

        public Content(String rawContent) {
            String[] lines;
            for (String line : lines = rawContent.split("\r\n")) {
                String[] tokens = line.split(": ", 2);
                if (tokens.length != 2) continue;
                this.map.put(tokens[0], tokens[1]);
            }
        }

        public String getString(String key) {
            return this.map.get(key);
        }

        public Integer getInteger(String key) {
            try {
                String s = this.map.get(key);
                if (s == null) {
                    return null;
                }
                return Integer.parseInt(s);
            }
            catch (Exception e) {
                return null;
            }
        }

        public void setValue(String key, String value) {
            this.map.put(key, value);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (String key : this.map.keySet()) {
                builder.append(key).append(": ").append(this.map.get(key)).append("\r\n");
            }
            return builder.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        UNKNOWN,
        INVITE,
        BYE,
        STATUS;

    }
}

