/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway;

import com.projectgoth.fusion.common.ByteBufferHelper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Map;

public class HTTPPacket {
    protected static final String LINE_TERMINATOR = "\r\n";
    protected static final String CHAR_ENCODING = "8859_1";
    protected byte[] content;
    protected Hashtable<String, String> properties = new Hashtable();

    public void setProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public String getProperty(String name) {
        return this.properties.get(name);
    }

    public void setContent(String contentType, byte[] content) {
        this.content = content;
        if (content == null) {
            this.properties.remove("Content-Type");
            this.setProperty("Content-Length", "0");
        } else {
            this.setProperty("Content-Type", contentType);
            this.setProperty("Content-Length", Integer.toString(content.length));
        }
    }

    public byte[] getContent() {
        return this.content;
    }

    public void read(ByteBuffer buffer) throws IOException {
        int contentLength;
        this.properties = new Hashtable();
        String line = ByteBufferHelper.readLine(buffer, CHAR_ENCODING);
        while (line != null && line.length() > 0) {
            int idx = line.indexOf(":");
            if (idx == -1) {
                throw new IOException("Cannot parse property line - " + line);
            }
            String name = line.substring(0, idx);
            String value = line.substring(idx + 1);
            this.properties.put(name, value.trim());
            line = ByteBufferHelper.readLine(buffer, CHAR_ENCODING);
        }
        this.content = null;
        String lenStr = this.properties.get("Content-Length");
        if (lenStr == null) {
            lenStr = this.properties.get("content-length");
        }
        if (lenStr != null && (contentLength = Integer.parseInt(lenStr)) > 0) {
            this.content = ByteBufferHelper.readBytes(buffer, contentLength);
        }
    }

    public void read(InputStream in) throws IOException {
        byte[] buffer = new byte[]{};
        while (true) {
            try {
                while (true) {
                    byte[] bytes;
                    if (in.read(bytes = new byte[in.available()]) > 0) {
                        buffer = ByteBufferHelper.concat(buffer, bytes);
                        this.read(ByteBuffer.wrap(buffer));
                        return;
                    }
                    Thread.sleep(100L);
                }
            }
            catch (BufferUnderflowException e) {
                continue;
            }
            catch (InterruptedException interruptedException) {
                continue;
            }
            break;
        }
    }

    public void write(OutputStream out) throws IOException {
        out.write(this.toString().getBytes(CHAR_ENCODING));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(LINE_TERMINATOR);
        }
        builder.append(LINE_TERMINATOR);
        if (this.content != null) {
            try {
                builder.append(new String(this.content, CHAR_ENCODING));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}

