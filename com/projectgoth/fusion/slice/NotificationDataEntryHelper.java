/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.NotificationDataParamHelper;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class NotificationDataEntryHelper {
    public static void write(BasicStream __os, Map<String, Map<String, String>> __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.size());
            for (Map.Entry<String, Map<String, String>> __e : __v.entrySet()) {
                __os.writeString(__e.getKey());
                NotificationDataParamHelper.write(__os, __e.getValue());
            }
        }
    }

    public static Map<String, Map<String, String>> read(BasicStream __is) {
        HashMap<String, Map<String, String>> __v = new HashMap<String, Map<String, String>>();
        int __sz0 = __is.readSize();
        for (int __i0 = 0; __i0 < __sz0; ++__i0) {
            String __key = __is.readString();
            Map<String, String> __value = NotificationDataParamHelper.read(__is);
            __v.put(__key, __value);
        }
        return __v;
    }
}

