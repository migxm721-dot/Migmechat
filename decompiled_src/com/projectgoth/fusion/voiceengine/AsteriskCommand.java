/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.voiceengine;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class AsteriskCommand {
    private static final String PROPERTY_DELIMITER = ": ";
    private static final String LINE_TERMINATOR = "\r\n";
    private Type type;
    private String name;
    private Map<String, String> properties = new HashMap<String, String>();

    public AsteriskCommand(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public AsteriskCommand(String rawCommand) throws ParseException {
        String[] lines = rawCommand.split(LINE_TERMINATOR);
        if (lines.length == 0) {
            throw new ParseException("Invalid command", 0);
        }
        for (int i = 0; i < lines.length; ++i) {
            String[] tokens = lines[i].split(PROPERTY_DELIMITER, 2);
            if (tokens.length == 0) {
                throw new ParseException("Incorrect command format. Line " + i + 1 + ". " + lines[i], 0);
            }
            if (i == 0) {
                try {
                    this.type = Type.valueOf(tokens[0].toUpperCase());
                }
                catch (Exception e) {
                    throw new ParseException("Unknown command type " + tokens[0], 0);
                }
                this.name = tokens.length == 1 ? null : tokens[1];
                continue;
            }
            this.properties.put(tokens[0], tokens.length == 1 ? null : tokens[1]);
        }
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty(String key) {
        if (key == null || key.length() < 1) {
            return null;
        }
        String value = this.properties.get(key);
        if (value != null) {
            return value;
        }
        for (String keyc : this.properties.keySet()) {
            if (!key.equalsIgnoreCase(keyc)) continue;
            value = this.properties.get(keyc);
            break;
        }
        return value;
    }

    public void setProperty(String key, String value) {
        if (key == null || key.length() < 1) {
            return;
        }
        this.properties.put(key, value);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append((Object)this.type).append(PROPERTY_DELIMITER).append(this.name).append(LINE_TERMINATOR);
        for (Map.Entry<String, String> property : this.properties.entrySet()) {
            builder.append(property.getKey()).append(PROPERTY_DELIMITER);
            if (property.getValue() != null) {
                builder.append(property.getValue());
            }
            builder.append(LINE_TERMINATOR);
        }
        return builder.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        ACTION,
        RESPONSE,
        EVENT;

    }
}

