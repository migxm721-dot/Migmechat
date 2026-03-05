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
import com.projectgoth.fusion.slice.GroupEventPrx;
import com.projectgoth.fusion.slice._GroupEventDelD;
import com.projectgoth.fusion.slice._GroupEventDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GroupEventPrxHelper
extends ObjectPrxHelperBase
implements GroupEventPrx {
    public static GroupEventPrx checkedCast(ObjectPrx __obj) {
        GroupEventPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupEventPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupEvent")) break block3;
                    GroupEventPrxHelper __h = new GroupEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupEventPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GroupEventPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupEventPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupEvent", __ctx)) break block3;
                    GroupEventPrxHelper __h = new GroupEventPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupEventPrx checkedCast(ObjectPrx __obj, String __facet) {
        GroupEventPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupEvent")) {
                    GroupEventPrxHelper __h = new GroupEventPrxHelper();
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

    public static GroupEventPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GroupEventPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupEvent", __ctx)) {
                    GroupEventPrxHelper __h = new GroupEventPrxHelper();
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

    public static GroupEventPrx uncheckedCast(ObjectPrx __obj) {
        GroupEventPrx __d = null;
        if (__obj != null) {
            try {
                __d = (GroupEventPrx)__obj;
            }
            catch (ClassCastException ex) {
                GroupEventPrxHelper __h = new GroupEventPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GroupEventPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GroupEventPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GroupEventPrxHelper __h = new GroupEventPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GroupEventDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GroupEventDelD();
    }

    public static void __write(BasicStream __os, GroupEventPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GroupEventPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GroupEventPrxHelper result = new GroupEventPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

