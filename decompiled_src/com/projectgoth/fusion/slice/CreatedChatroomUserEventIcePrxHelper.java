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
import com.projectgoth.fusion.slice.CreatedChatroomUserEventIcePrx;
import com.projectgoth.fusion.slice._CreatedChatroomUserEventIceDelD;
import com.projectgoth.fusion.slice._CreatedChatroomUserEventIceDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CreatedChatroomUserEventIcePrxHelper
extends ObjectPrxHelperBase
implements CreatedChatroomUserEventIcePrx {
    public static CreatedChatroomUserEventIcePrx checkedCast(ObjectPrx __obj) {
        CreatedChatroomUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CreatedChatroomUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CreatedChatroomUserEventIce")) break block3;
                    CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CreatedChatroomUserEventIcePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CreatedChatroomUserEventIcePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CreatedChatroomUserEventIcePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::CreatedChatroomUserEventIce", __ctx)) break block3;
                    CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CreatedChatroomUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet) {
        CreatedChatroomUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CreatedChatroomUserEventIce")) {
                    CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
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

    public static CreatedChatroomUserEventIcePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CreatedChatroomUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::CreatedChatroomUserEventIce", __ctx)) {
                    CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
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

    public static CreatedChatroomUserEventIcePrx uncheckedCast(ObjectPrx __obj) {
        CreatedChatroomUserEventIcePrx __d = null;
        if (__obj != null) {
            try {
                __d = (CreatedChatroomUserEventIcePrx)__obj;
            }
            catch (ClassCastException ex) {
                CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CreatedChatroomUserEventIcePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CreatedChatroomUserEventIcePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CreatedChatroomUserEventIcePrxHelper __h = new CreatedChatroomUserEventIcePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CreatedChatroomUserEventIceDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CreatedChatroomUserEventIceDelD();
    }

    public static void __write(BasicStream __os, CreatedChatroomUserEventIcePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CreatedChatroomUserEventIcePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CreatedChatroomUserEventIcePrxHelper result = new CreatedChatroomUserEventIcePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

