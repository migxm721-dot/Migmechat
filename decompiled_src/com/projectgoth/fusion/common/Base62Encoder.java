/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

public class Base62Encoder {
    public static String encode(int num) {
        return Base62Encoder.encode((long)num);
    }

    public static String encode(long num) {
        if (num == 0L) {
            return "0";
        }
        StringBuilder builder = new StringBuilder();
        while (num > 0L) {
            long r = num % 62L;
            if (r < 10L) {
                builder.append(r);
            } else if (r < 36L) {
                builder.append((char)(97L + r - 10L));
            } else {
                builder.append((char)(65L + r - 36L));
            }
            num /= 62L;
        }
        return builder.toString();
    }
}

