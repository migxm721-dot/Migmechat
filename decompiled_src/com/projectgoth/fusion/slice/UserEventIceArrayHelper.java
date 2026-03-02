/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 *  IceInternal.BasicStream
 *  IceInternal.Patcher
 *  IceInternal.SequencePatcher
 */
package com.projectgoth.fusion.slice;

import Ice.Object;
import IceInternal.BasicStream;
import IceInternal.Patcher;
import IceInternal.SequencePatcher;
import com.projectgoth.fusion.slice.UserEventIce;

public final class UserEventIceArrayHelper {
    public static void write(BasicStream __os, UserEventIce[] __v) {
        if (__v == null) {
            __os.writeSize(0);
        } else {
            __os.writeSize(__v.length);
            for (int __i0 = 0; __i0 < __v.length; ++__i0) {
                __os.writeObject((Object)__v[__i0]);
            }
        }
    }

    public static UserEventIce[] read(BasicStream __is) {
        int __len0 = __is.readSize();
        __is.startSeq(__len0, 4);
        String __type0 = UserEventIce.ice_staticId();
        UserEventIce[] __v = new UserEventIce[__len0];
        for (int __i0 = 0; __i0 < __len0; ++__i0) {
            __is.readObject((Patcher)new SequencePatcher((java.lang.Object[])__v, UserEventIce.class, __type0, __i0));
            __is.checkSeq();
            __is.endElement();
        }
        __is.endSeq(__len0);
        return __v;
    }
}

