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
import com.projectgoth.fusion.slice.VirtualGiftUserEventIcePrx;
import com.projectgoth.fusion.slice._VirtualGiftUserEventIceDelD;
import com.projectgoth.fusion.slice._VirtualGiftUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class VirtualGiftUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements VirtualGiftUserEventIcePrx {
    public static VirtualGiftUserEventIcePrx checkedCast(ObjectPrx __obj) {
        VirtualGiftUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (VirtualGiftUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::VirtualGiftUserEventIce")) break block3;
                    VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VirtualGiftUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        VirtualGiftUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (VirtualGiftUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::VirtualGiftUserEventIce", __ctx)) break block3;
                    VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static VirtualGiftUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        VirtualGiftUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::VirtualGiftUserEventIce")) {
                    VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
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

    public static VirtualGiftUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        VirtualGiftUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::VirtualGiftUserEventIce", __ctx)) {
                    VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
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

    public static VirtualGiftUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        VirtualGiftUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (VirtualGiftUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static VirtualGiftUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        VirtualGiftUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            VirtualGiftUserEventIcePrxHelper __h = new VirtualGiftUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _VirtualGiftUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _VirtualGiftUserEventIceDelD();
    }

    public static void __write(BasicStream __os, VirtualGiftUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static VirtualGiftUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            VirtualGiftUserEventIcePrxHelper result = new VirtualGiftUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

