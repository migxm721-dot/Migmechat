/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.voiceengine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FastAGICommand {
    private String request;
    private String callerID;
    private String channel;
    private Map<String, String> map = new HashMap<String, String>();
    private List<String> lines = new ArrayList<String>();

    public FastAGICommand() {
    }

    public FastAGICommand(InputStream in) throws IOException {
        this.read(in);
    }

    public String getRequest() {
        return this.request;
    }

    public String getCallerID() {
        return this.callerID;
    }

    public void setCallerID(String callerID) {
        this.callerID = callerID;
    }

    public String getChannel() {
        return this.channel;
    }

    public String getParameter(String name) {
        return this.map.get(name);
    }

    public String setParameter(String name, String value) {
        return this.map.put(name, value);
    }

    public Integer getParameterAsInt(String name) {
        try {
            String param = this.map.get(name);
            if (param == null) {
                return null;
            }
            return Integer.valueOf(param);
        }
        catch (Exception e) {
            return null;
        }
    }

    public String getRawCommand() {
        StringBuilder builder = new StringBuilder();
        for (String line : this.lines) {
            builder.append(line).append('\n');
        }
        return builder.toString();
    }

    public void read(InputStream in) throws IOException {
        this.request = null;
        this.callerID = null;
        this.map.clear();
        this.lines.clear();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = reader.readLine();
        while (line != null && line.length() > 0 && line != "\r" && line != "\r\n") {
            String[] tokens = line.split(":", 2);
            if (tokens.length == 2) {
                if ("agi_callerid".equalsIgnoreCase(tokens[0])) {
                    this.callerID = tokens[1].trim();
                } else if ("agi_uniqueid".equalsIgnoreCase(tokens[0])) {
                    this.channel = tokens[1].trim();
                } else if ("agi_request".equalsIgnoreCase(tokens[0])) {
                    for (String param : tokens[1].split("[/&]")) {
                        tokens = param.split("=", 2);
                        if (tokens.length != 2) continue;
                        if ("request".equalsIgnoreCase(tokens[0])) {
                            this.request = tokens[1];
                            continue;
                        }
                        this.map.put(tokens[0], tokens[1]);
                    }
                }
            }
            this.lines.add(line);
            line = reader.readLine();
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Reqeust: ").append(this.request).append('\n');
        builder.append("Caller ID: ").append(this.callerID).append('\n');
        for (String key : this.map.keySet()) {
            builder.append("Param: ").append(key).append(" = ").append(this.map.get(key)).append('\n');
        }
        return builder.toString();
    }
}

