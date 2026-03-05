/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.util;

import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class MeanUtils {
    public static int meanValue(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        long total = 0L;
        for (Integer value : values) {
            System.out.println(value);
            total += (long)value.intValue();
        }
        return (int)(total / (long)values.size());
    }
}

