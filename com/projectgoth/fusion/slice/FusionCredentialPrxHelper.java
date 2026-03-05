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
import com.projectgoth.fusion.slice.FusionCredentialPrx;
import com.projectgoth.fusion.slice._FusionCredentialDelD;
import com.projectgoth.fusion.slice._FusionCredentialDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FusionCredentialPrxHelper
extends ObjectPrxHelperBase
implements FusionCredentialPrx {
    public static FusionCredentialPrx checkedCast(ObjectPrx __obj) {
        FusionCredentialPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (FusionCredentialPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::FusionCredential")) break block3;
                    FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static FusionCredentialPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        FusionCredentialPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (FusionCredentialPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::FusionCredential", __ctx)) break block3;
                    FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static FusionCredentialPrx checkedCast(ObjectPrx __obj, String __facet) {
        FusionCredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::FusionCredential")) {
                    FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
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

    public static FusionCredentialPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        FusionCredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::FusionCredential", __ctx)) {
                    FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
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

    public static FusionCredentialPrx uncheckedCast(ObjectPrx __obj) {
        FusionCredentialPrx __d = null;
        if (__obj != null) {
            try {
                __d = (FusionCredentialPrx)__obj;
            }
            catch (ClassCastException ex) {
                FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static FusionCredentialPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        FusionCredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            FusionCredentialPrxHelper __h = new FusionCredentialPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _FusionCredentialDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _FusionCredentialDelD();
    }

    public static void __write(BasicStream __os, FusionCredentialPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static FusionCredentialPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            FusionCredentialPrxHelper result = new FusionCredentialPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

