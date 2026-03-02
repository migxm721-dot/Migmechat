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
import com.projectgoth.fusion.slice.ObjectCacheAdminPrx;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice._ObjectCacheAdminDel;
import com.projectgoth.fusion.slice._ObjectCacheAdminDelD;
import com.projectgoth.fusion.slice._ObjectCacheAdminDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjectCacheAdminPrxHelper
extends ObjectPrxHelperBase
implements ObjectCacheAdminPrx {
    @Override
    public int getLoadWeightage() {
        return this.getLoadWeightage(null, false);
    }

    @Override
    public int getLoadWeightage(Map<String, String> __ctx) {
        return this.getLoadWeightage(__ctx, true);
    }

    private int getLoadWeightage(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getLoadWeightage");
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
                return __del.getLoadWeightage(__ctx);
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

    @Override
    public ObjectCacheStats getStats() throws FusionException {
        return this.getStats(null, false);
    }

    @Override
    public ObjectCacheStats getStats(Map<String, String> __ctx) throws FusionException {
        return this.getStats(__ctx, true);
    }

    private ObjectCacheStats getStats(Map<String, String> __ctx, boolean __explicitCtx) throws FusionException {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getStats");
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
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

    @Override
    public String[] getUsernames() {
        return this.getUsernames(null, false);
    }

    @Override
    public String[] getUsernames(Map<String, String> __ctx) {
        return this.getUsernames(__ctx, true);
    }

    private String[] getUsernames(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("getUsernames");
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
                return __del.getUsernames(__ctx);
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

    @Override
    public int ping() {
        return this.ping(null, false);
    }

    @Override
    public int ping(Map<String, String> __ctx) {
        return this.ping(__ctx, true);
    }

    private int ping(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                this.__checkTwowayOnly("ping");
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
                return __del.ping(__ctx);
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

    @Override
    public void reloadEmotes() {
        this.reloadEmotes(null, false);
    }

    @Override
    public void reloadEmotes(Map<String, String> __ctx) {
        this.reloadEmotes(__ctx, true);
    }

    private void reloadEmotes(Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
                __del.reloadEmotes(__ctx);
                return;
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

    @Override
    public void setLoadWeightage(int weightage) {
        this.setLoadWeightage(weightage, null, false);
    }

    @Override
    public void setLoadWeightage(int weightage, Map<String, String> __ctx) {
        this.setLoadWeightage(weightage, __ctx, true);
    }

    private void setLoadWeightage(int weightage, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _ObjectCacheAdminDel __del = (_ObjectCacheAdminDel)__delBase;
                __del.setLoadWeightage(weightage, __ctx);
                return;
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

    public static ObjectCacheAdminPrx checkedCast(ObjectPrx __obj) {
        ObjectCacheAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ObjectCacheAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ObjectCacheAdmin")) break block3;
                    ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ObjectCacheAdminPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        ObjectCacheAdminPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (ObjectCacheAdminPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::ObjectCacheAdmin", __ctx)) break block3;
                    ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static ObjectCacheAdminPrx checkedCast(ObjectPrx __obj, String __facet) {
        ObjectCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ObjectCacheAdmin")) {
                    ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
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

    public static ObjectCacheAdminPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        ObjectCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::ObjectCacheAdmin", __ctx)) {
                    ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
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

    public static ObjectCacheAdminPrx uncheckedCast(ObjectPrx __obj) {
        ObjectCacheAdminPrx __d = null;
        if (__obj != null) {
            try {
                __d = (ObjectCacheAdminPrx)__obj;
            }
            catch (ClassCastException ex) {
                ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static ObjectCacheAdminPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        ObjectCacheAdminPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            ObjectCacheAdminPrxHelper __h = new ObjectCacheAdminPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _ObjectCacheAdminDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _ObjectCacheAdminDelD();
    }

    public static void __write(BasicStream __os, ObjectCacheAdminPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static ObjectCacheAdminPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            ObjectCacheAdminPrxHelper result = new ObjectCacheAdminPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

