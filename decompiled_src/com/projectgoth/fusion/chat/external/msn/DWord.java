/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class DWord {
    long value;

    public DWord() {
    }

    public DWord(long value) {
        this.value = value;
    }

    public DWord(ByteBuffer byteBuffer) {
        this.read(byteBuffer);
    }

    public DWord(byte[] byteArray, ByteOrder order) {
        this.read(byteArray, order);
    }

    public DWord(DWord dWord) {
        this.value = dWord.value;
    }

    public int intValue() {
        return (int)this.value;
    }

    public long longValue() {
        return this.value;
    }

    public void read(ByteBuffer byteBuffer) {
        byte[] byteArray = new byte[4];
        byteBuffer.get(byteArray);
        this.read(byteArray, byteBuffer.order());
    }

    public void read(byte[] byteArray, ByteOrder order) {
        this.value = 0L;
        if (order == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < 4; ++i) {
                this.value <<= 8;
                this.value |= (long)(byteArray[i] & 0xFF);
            }
        } else {
            for (int i = 3; i >= 0; --i) {
                this.value <<= 8;
                this.value |= (long)(byteArray[i] & 0xFF);
            }
        }
    }

    public byte[] toByteArray(ByteOrder order) {
        byte[] buffer = new byte[4];
        long val = this.value;
        if (order == ByteOrder.BIG_ENDIAN) {
            for (int i = 3; i >= 0; --i) {
                buffer[i] = (byte)(val & 0xFFL);
                val >>= 8;
            }
        } else {
            for (int i = 0; i < 4; ++i) {
                buffer[i] = (byte)(val & 0xFFL);
                val >>= 8;
            }
        }
        return buffer;
    }

    public String toString() {
        return String.valueOf(this.longValue());
    }
}

