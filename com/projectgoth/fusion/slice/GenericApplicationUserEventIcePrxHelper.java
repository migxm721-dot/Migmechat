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
import com.projectgoth.fusion.slice.GenericApplicationUserEventIcePrx;
import com.projectgoth.fusion.slice._GenericApplicationUserEventIceDelD;
import com.projectgoth.fusion.slice._GenericApplicationUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class GenericApplicationUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements GenericApplicationUserEventIcePrx {
    public static GenericApplicationUserEventIcePrx checkedCast(ObjectPrx __obj) {
        GenericApplicationUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GenericApplicationUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GenericApplicationUserEventIce")) break block3;
                    GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GenericApplicationUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        GenericApplicationUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (GenericApplicationUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::GenericApplicationUserEventIce", __ctx)) break block3;
                    GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static GenericApplicationUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        GenericApplicationUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GenericApplicationUserEventIce")) {
                    GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
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

    public static GenericApplicationUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        GenericApplicationUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::GenericApplicationUserEventIce", __ctx)) {
                    GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
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

    public static GenericApplicationUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        GenericApplicationUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (GenericApplicationUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static GenericApplicationUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        GenericApplicationUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            GenericApplicationUserEventIcePrxHelper __h = new GenericApplicationUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _GenericApplicationUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _GenericApplicationUserEventIceDelD();
    }

    public static void __write(BasicStream __os, GenericApplicationUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static GenericApplicationUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            GenericApplicationUserEventIcePrxHelper result = new GenericApplicationUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

