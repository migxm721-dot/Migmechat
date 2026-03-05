/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.NotificationDataEntryHelper;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NotificationDataMapHelper {
    public static void write(BasicStream __os, Map<Integer, Map<String, Map<String, String>>> __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.size());
            for (Map.Entry<Integer, Map<String, Map<String, String>>> __e : __v.entrySet()) {
                __os.writeInt(__e.getKey().intValue());
                NotificationDataEntryHelper.write(__os, __e.getValue());
            }
        }
    }

    public static Map<Integer, Map<String, Map<String, String>>> read(BasicStream __is) {
        HashMap<Integer, Map<String, Map<String, String>>> __v = new HashMap<Integer, Map<String, Map<String, String>>>();
        int __sz0 = __is.readSize();
        for (int __i0 = 0; __i0 < __sz0; ++__i0) {
            int __key = __is.readInt();
            Map<String, Map<String, String>> __value = NotificationDataEntryHelper.read(__is);
            __v.put(__key, __value);
        }
        return __v;
    }
}

