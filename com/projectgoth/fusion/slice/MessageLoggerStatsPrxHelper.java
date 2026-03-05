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
import com.projectgoth.fusion.slice.MessageLoggerStatsPrx;
import com.projectgoth.fusion.slice._MessageLoggerStatsDelD;
import com.projectgoth.fusion.slice._MessageLoggerStatsDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MessageLoggerStatsPrxHelper
extends ObjectPrxHelperBase
implements MessageLoggerStatsPrx {
    public static MessageLoggerStatsPrx checkedCast(ObjectPrx __obj) {
        MessageLoggerStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageLoggerStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageLoggerStats")) break block3;
                    MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageLoggerStatsPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        MessageLoggerStatsPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (MessageLoggerStatsPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::MessageLoggerStats", __ctx)) break block3;
                    MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static MessageLoggerStatsPrx checkedCast(ObjectPrx __obj, String __facet) {
        MessageLoggerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageLoggerStats")) {
                    MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
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

    public static MessageLoggerStatsPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        MessageLoggerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::MessageLoggerStats", __ctx)) {
                    MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
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

    public static MessageLoggerStatsPrx uncheckedCast(ObjectPrx __obj) {
        MessageLoggerStatsPrx __d = null;
        if (__obj != null) {
            try {
                __d = (MessageLoggerStatsPrx)__obj;
            }
            catch (ClassCastException ex) {
                MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static MessageLoggerStatsPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        MessageLoggerStatsPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            MessageLoggerStatsPrxHelper __h = new MessageLoggerStatsPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _MessageLoggerStatsDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _MessageLoggerStatsDelD();
    }

    public static void __write(BasicStream __os, MessageLoggerStatsPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static MessageLoggerStatsPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            MessageLoggerStatsPrxHelper result = new MessageLoggerStatsPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

