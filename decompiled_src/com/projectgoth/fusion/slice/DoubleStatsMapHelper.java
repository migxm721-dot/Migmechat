/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class DoubleStatsMapHelper {
    public static void write(BasicStream __os, Map<String, Double> __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.size());
            for (Map.Entry<String, Double> __e : __v.entrySet()) {
                __os.writeString(__e.getKey());
                __os.writeDouble(__e.getValue().doubleValue());
            }
        }
    }

    public static Map<String, Double> read(BasicStream __is) {
        HashMap<String, Double> __v = new HashMap<String, Double>();
        int __sz0 = __is.readSize();
        for (int __i0 = 0; __i0 < __sz0; ++__i0) {
            String __key = __is.readString();
            double __value = __is.readDouble();
            __v.put(__key, __value);
        }
        return __v;
    }
}

