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
import com.projectgoth.fusion.slice.GroupAnnouncementUserEventIcePrx;
import com.projectgoth.fusion.slice._GroupAnnouncementUserEventIceDelD;
import com.projectgoth.fusion.slice._GroupAnnouncementUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GroupAnnouncementUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GroupAnnouncementUserEventIcePrx {
    public static GroupAnnouncementUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GroupAnnouncementUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupAnnouncementUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupAnnouncementUserEventIce")) break block3;
                    GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupAnnouncementUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GroupAnnouncementUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GroupAnnouncementUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GroupAnnouncementUserEventIce", __ctx)) break block3;
                    GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GroupAnnouncementUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GroupAnnouncementUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupAnnouncementUserEventIce")) {
                    GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
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

    public static GroupAnnouncementUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GroupAnnouncementUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GroupAnnouncementUserEventIce", __ctx)) {
                    GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
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

    public static GroupAnnouncementUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GroupAnnouncementUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GroupAnnouncementUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GroupAnnouncementUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GroupAnnouncementUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GroupAnnouncementUserEventIcePrxHelper __h = new GroupAnnouncementUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GroupAnnouncementUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GroupAnnouncementUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GroupAnnouncementUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GroupAnnouncementUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GroupAnnouncementUserEventIcePrxHelper result = new GroupAnnouncementUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

