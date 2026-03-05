/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class UsernameToProxyMapHelper {
    public static void write(BasicStream __os, Map<String, UserPrx> __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.size());
            for (Map.Entry<String, UserPrx> __e : __v.entrySet()) {
                __os.writeString(__e.getKey());
                UserPrxHelper.__write(__os, __e.getValue());
            }
        }
    }

    public static Map<String, UserPrx> read(BasicStream __is) {
        HashMap<String, UserPrx> __v = new HashMap<String, UserPrx>();
        int __sz0 = __is.readSize();
        for (int __i0 = 0; __i0 < __sz0; ++__i0) {
            String __key = __is.readString();
            UserPrx __value = UserPrxHelper.__read(__is);
            __v.put(__key, __value);
        }
        return __v;
    }
}

