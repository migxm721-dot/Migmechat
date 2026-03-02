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
public final class NotificationMapHelper {
    public static void write(BasicStream __os, Map<Integer, Integer> __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.size());
            for (Map.Entry<Integer, Integer> __e : __v.entrySet()) {
                __os.writeInt(__e.getKey().intValue());
                __os.writeInt(__e.getValue().intValue());
            }
        }
    }

    public static Map<Integer, Integer> read(BasicStream __is) {
        HashMap<Integer, Integer> __v = new HashMap<Integer, Integer>();
        int __sz0 = __is.readSize();
        for (int __i0 = 0; __i0 < __sz0; ++__i0) {
            int __key = __is.readInt();
            int __value = __is.readInt();
            __v.put(__key, __value);
        }
        return __v;
    }
}

