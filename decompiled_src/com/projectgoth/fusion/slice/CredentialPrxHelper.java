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
import com.projectgoth.fusion.slice.CredentialPrx;
import com.projectgoth.fusion.slice._CredentialDelD;
import com.projectgoth.fusion.slice._CredentialDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CredentialPrxHelper
extends ObjectPrxHelperBase
implements CredentialPrx {
    public static CredentialPrx checkedCast(ObjectPrx __obj) {
        CredentialPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CredentialPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Credential")) break block3;
                    CredentialPrxHelper __h = new CredentialPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CredentialPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        CredentialPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (CredentialPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::Credential", __ctx)) break block3;
                    CredentialPrxHelper __h = new CredentialPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static CredentialPrx checkedCast(ObjectPrx __obj, String __facet) {
        CredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Credential")) {
                    CredentialPrxHelper __h = new CredentialPrxHelper();
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

    public static CredentialPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        CredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::Credential", __ctx)) {
                    CredentialPrxHelper __h = new CredentialPrxHelper();
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

    public static CredentialPrx uncheckedCast(ObjectPrx __obj) {
        CredentialPrx __d = null;
        if (__obj != null) {
            try {
                __d = (CredentialPrx)__obj;
            }
            catch (ClassCastException ex) {
                CredentialPrxHelper __h = new CredentialPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static CredentialPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        CredentialPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            CredentialPrxHelper __h = new CredentialPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _CredentialDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _CredentialDelD();
    }

    public static void __write(BasicStream __os, CredentialPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static CredentialPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            CredentialPrxHelper result = new CredentialPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

