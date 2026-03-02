/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.FacetNotExistException
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice._ObjectDel
 *  Ice._ObjectDelD
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageSwitchboardAdminPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardStats;
import com.projectgoth.fusion.slice._MessageSwitchboardAdminDel;
import com.projectgoth.fusion.slice._MessageSwitchboardAdminDelD;
import com.projectgoth.fusion.slice._MessageSwitchboardAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MessageSwitchboardAdminPrxHelper
extends ObjectPrxHelperBase
implements MessageSwitchboardAdminPrx {
    @Override
    public MessageSwitchboardStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public MessageSwitchboardStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private MessageSwitchboardStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _MessageSwitchboardAdminDel __del = (_MessageSwitchboardAdminDel)__delBase;
                return __del.getStats(__ctx);
            }
            catch (LocalExceptionWrapper __ex) {
                this.__handleExceptionWrapper(__delBase, __ex, null);
                continue;
            }
            catch (LocalException __ex) {
                __cnt = this.__handleException(__delBase, __ex, null, __cnt);
                continue;
            }
            break;
        }
    }

    public static MessageSwitchboardAdminPrx checkedCast(ObjectPrx __obj) {
        MessageSwitchboardAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageSwitchboardAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboardAdmin")) break block3;
                    MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageSwitchboardAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        MessageSwitchboardAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageSwitchboardAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboardAdmin", __ctx)) break block3;
                    MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageSwitchboardAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        MessageSwitchboardAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboardAdmin")) {
                    MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
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

    public static MessageSwitchboardAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        MessageSwitchboardAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageSwitchboardAdmin", __ctx)) {
                    MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
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

    public static MessageSwitchboardAdminPrx uncheckedCast(ObjectPrx __obj) {
        MessageSwitchboardAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (MessageSwitchboardAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MessageSwitchboardAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        MessageSwitchboardAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            MessageSwitchboardAdminPrxHelper __h = new MessageSwitchboardAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _MessageSwitchboardAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _MessageSwitchboardAdminDelD();
    }

    public static void __write(BasicStream __os, MessageSwitchboardAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static MessageSwitchboardAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            MessageSwitchboardAdminPrxHelper result = new MessageSwitchboardAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

