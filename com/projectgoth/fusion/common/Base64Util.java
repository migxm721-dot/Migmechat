/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

public class Base64Util {
    static byte[] encodeData;
    static String charSet;

    private Base64Util() {
    }

    public static String encode(String s) {
        return Base64Util.encode(s.getBytes());
    }

    public static String encode(byte[] src) {
        return Base64Util.encode(src, 0, src.length);
    }

    public static String encode(byte[] src, int start, int length) {
        byte[] dst = new byte[(length + 2) / 3 * 4];
        byte x = 0;
        int dstIndex = 0;
        int state = 0;
        byte old = 0;
        int max = length + start;
        for (int srcIndex = start; srcIndex < max; ++srcIndex) {
            x = src[srcIndex];
            switch (++state) {
                case 1: {
                    dst[dstIndex++] = encodeData[x >> 2 & 0x3F];
                    break;
                }
                case 2: {
                    dst[dstIndex++] = encodeData[old << 4 & 0x30 | x >> 4 & 0xF];
                    break;
                }
                case 3: {
                    dst[dstIndex++] = encodeData[old << 2 & 0x3C | x >> 6 & 3];
                    dst[dstIndex++] = encodeData[x & 0x3F];
                    state = 0;
                }
            }
            old = x;
        }
        switch (state) {
            case 1: {
                dst[dstIndex++] = encodeData[old << 4 & 0x30];
                dst[dstIndex++] = 61;
                dst[dstIndex++] = 61;
                break;
            }
            case 2: {
                dst[dstIndex++] = encodeData[old << 2 & 0x3C];
                dst[dstIndex++] = 61;
            }
        }
        return new String(dst);
    }

    public static byte[] decode(String s) {
        int end = 0;
        if (s.endsWith("=")) {
            ++end;
        }
        if (s.endsWith("==")) {
            ++end;
        }
        int len = (s.length() + 3) / 4 * 3 - end;
        byte[] result = new byte[len];
        int dst = 0;
        try {
            int code;
            block8: for (int src = 0; src < s.length() && (code = charSet.indexOf(s.charAt(src))) != -1; ++src) {
                switch (src % 4) {
                    case 0: {
                        result[dst] = (byte)(code << 2);
                        continue block8;
                    }
                    case 1: {
                        int n = dst++;
                        result[n] = (byte)(result[n] | (byte)(code >> 4 & 3));
                        result[dst] = (byte)(code << 4);
                        continue block8;
                    }
                    case 2: {
                        int n = dst++;
                        result[n] = (byte)(result[n] | (byte)(code >> 2 & 0xF));
                        result[dst] = (byte)(code << 6);
                        continue block8;
                    }
                    case 3: {
                        int n = dst++;
                        result[n] = (byte)(result[n] | (byte)(code & 0x3F));
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // empty catch block
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("encode: " + args[0] + " -> (" + Base64Util.encode(args[0]) + ")");
        System.out.println("decode: " + args[0] + " -> (" + new String(Base64Util.decode(args[0])) + ")");
    }

    static {
        charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        encodeData = new byte[64];
        for (int i = 0; i < 64; ++i) {
            byte c;
            Base64Util.encodeData[i] = c = (byte)charSet.charAt(i);
        }
    }
}

