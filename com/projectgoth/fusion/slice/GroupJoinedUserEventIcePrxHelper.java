/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.GroupJoinedUserEventIcePrx;
import com.projectgoth.fusion.slice._GroupJoinedUserEventIceDelD;
import com.projectgoth.fusion.slice._GroupJoinedUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GroupJoinedUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GroupJoinedUserEventIcePrx {
    public static GroupJoinedUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GroupJoinedUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupJoinedUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupJoinedUserEventIce")) break block3;
                    GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupJoinedUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GroupJoinedUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupJoinedUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupJoinedUserEventIce", __ctx)) break block3;
                    GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupJoinedUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GroupJoinedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupJoinedUserEventIce")) {
                    GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static GroupJoinedUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GroupJoinedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupJoinedUserEventIce", __ctx)) {
                    GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch (FacetNotExistException ex) {
                // empty catch block
            }
        }
        return __d;
    }

    public static GroupJoinedUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GroupJoinedUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GroupJoinedUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GroupJoinedUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GroupJoinedUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GroupJoinedUserEventIcePrxHelper __h = new GroupJoinedUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GroupJoinedUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GroupJoinedUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GroupJoinedUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GroupJoinedUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GroupJoinedUserEventIcePrxHelper result = new GroupJoinedUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

