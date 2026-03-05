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
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponsePrx;
import com.projectgoth.fusion.slice._AuthenticationServiceCredentialResponseDelD;
import com.projectgoth.fusion.slice._AuthenticationServiceCredentialResponseDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AuthenticationServiceCredentialResponsePrxHelper
extends ObjectPrxHelperBase
implements AuthenticationServiceCredentialResponsePrx {
    public static AuthenticationServiceCredentialResponsePrx checkedCast(ObjectPrx __obj) {
        AuthenticationServiceCredentialResponsePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AuthenticationServiceCredentialResponsePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse")) break block3;
                    AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AuthenticationServiceCredentialResponsePrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        AuthenticationServiceCredentialResponsePrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (AuthenticationServiceCredentialResponsePrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse", __ctx)) break block3;
                    AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static AuthenticationServiceCredentialResponsePrx checkedCast(ObjectPrx __obj, String __facet) {
        AuthenticationServiceCredentialResponsePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse")) {
                    AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
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

    public static AuthenticationServiceCredentialResponsePrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        AuthenticationServiceCredentialResponsePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::AuthenticationServiceCredentialResponse", __ctx)) {
                    AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
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

    public static AuthenticationServiceCredentialResponsePrx uncheckedCast(ObjectPrx __obj) {
        AuthenticationServiceCredentialResponsePrx __d = null;
        if (__obj != null) {
            try {
                __d = (AuthenticationServiceCredentialResponsePrx)__obj;
            }
            catch (ClassCastException ex) {
                AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static AuthenticationServiceCredentialResponsePrx uncheckedCast(ObjectPrx __obj, String __facet) {
        AuthenticationServiceCredentialResponsePrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            AuthenticationServiceCredentialResponsePrxHelper __h = new AuthenticationServiceCredentialResponsePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _AuthenticationServiceCredentialResponseDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _AuthenticationServiceCredentialResponseDelD();
    }

    public static void __write(BasicStream __os, AuthenticationServiceCredentialResponsePrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static AuthenticationServiceCredentialResponsePrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            AuthenticationServiceCredentialResponsePrxHelper result = new AuthenticationServiceCredentialResponsePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

