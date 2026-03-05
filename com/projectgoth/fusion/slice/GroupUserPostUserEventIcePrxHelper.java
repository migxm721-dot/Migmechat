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
import com.projectgoth.fusion.slice.GroupUserPostUserEventIcePrx;
import com.projectgoth.fusion.slice._GroupUserPostUserEventIceDelD;
import com.projectgoth.fusion.slice._GroupUserPostUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GroupUserPostUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GroupUserPostUserEventIcePrx {
    public static GroupUserPostUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GroupUserPostUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupUserPostUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupUserPostUserEventIce")) break block3;
                    GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupUserPostUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GroupUserPostUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupUserPostUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupUserPostUserEventIce", __ctx)) break block3;
                    GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupUserPostUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GroupUserPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupUserPostUserEventIce")) {
                    GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
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

    public static GroupUserPostUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GroupUserPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupUserPostUserEventIce", __ctx)) {
                    GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
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

    public static GroupUserPostUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GroupUserPostUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GroupUserPostUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GroupUserPostUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GroupUserPostUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GroupUserPostUserEventIcePrxHelper __h = new GroupUserPostUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GroupUserPostUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GroupUserPostUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GroupUserPostUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GroupUserPostUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GroupUserPostUserEventIcePrxHelper result = new GroupUserPostUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

