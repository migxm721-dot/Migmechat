/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

public class ByteBufferHelper {
    private static final String READ_MODE = "r";

    public static String readLine(ByteBuffer buffer, String charset) throws UnsupportedEncodingException {
        int position = buffer.position();
        int limit = buffer.limit();
        for (int i = position; i < limit; ++i) {
            if (buffer.get(i) == 13) {
                if (i < limit - 1 && buffer.get(i + 1) == 10) {
                    return new String(ByteBufferHelper.readBytes(buffer, i - position + 2), 0, i - position, charset);
                }
                return new String(ByteBufferHelper.readBytes(buffer, i - position + 1), 0, i - position, charset);
            }
            if (buffer.get(i) != 10) continue;
            return new String(ByteBufferHelper.readBytes(buffer, i - position + 1), 0, i - position, charset);
        }
        throw new BufferUnderflowException();
    }

    public static String readCString(ByteBuffer buffer, String charset) throws UnsupportedEncodingException {
        int position = buffer.position();
        int limit = buffer.limit();
        for (int i = position; i < limit; ++i) {
            if (buffer.get(i) != 0) continue;
            return new String(ByteBufferHelper.readBytes(buffer, i - position + 1), 0, i - position, charset);
        }
        throw new BufferUnderflowException();
    }

    public static ByteBuffer adjustSize(ByteBuffer buffer, int minSize, int maxSize, double growFactor) throws BufferOverflowException {
        int position;
        int capacity = buffer.capacity();
        if (capacity == (position = buffer.position())) {
            if (capacity > maxSize) {
                throw new BufferOverflowException();
            }
            buffer = ByteBuffer.allocate((int)((double)capacity * growFactor)).put(buffer.array());
        } else if (capacity > minSize && position < minSize) {
            buffer = ByteBuffer.allocate(minSize).put(buffer.array(), 0, position);
        }
        return buffer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ByteBuffer readFile(File file) throws IOException {
        ByteBuffer byteBuffer;
        RandomAccessFile raf = new RandomAccessFile(file, READ_MODE);
        try {
            byte[] ba = new byte[(int)file.length()];
            raf.readFully(ba);
            byteBuffer = ByteBuffer.wrap(ba);
            Object var5_4 = null;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            raf.close();
            throw throwable;
        }
        raf.close();
        return byteBuffer;
    }

    public static byte[] readBytes(ByteBuffer buffer, int len) {
        byte[] ba = new byte[len];
        buffer.get(ba);
        return ba;
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static int compare(byte[] a, byte[] b) {
        if (a.length < b.length) {
            return -1;
        }
        if (a.length > b.length) {
            return 1;
        }
        for (int i = 0; i < a.length; ++i) {
            if (a[i] < b[i]) {
                return -1;
            }
            if (a[i] <= b[i]) continue;
            return 1;
        }
        return 0;
    }

    public static String toHexString(byte[] ba) {
        return ByteBufferHelper.toHexString(ba, "");
    }

    public static String toHexString(byte[] ba, String separator) {
        StringBuilder builder = new StringBuilder();
        for (byte b : ba) {
            builder.append(Integer.toHexString(b >> 4 & 0xF)).append(Integer.toHexString(b & 0xF)).append(separator);
        }
        return builder.toString();
    }

    public static String toPrintableString(ByteBuffer buffer) {
        ByteBuffer b = buffer.duplicate();
        String s = new String(ByteBufferHelper.readBytes(b, b.limit()));
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\n", "\\\\n\n");
        return s;
    }
}

