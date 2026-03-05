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
package com.projectgoth.fusion.slice.tests;

import Ice.FacetNotExistException;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice._ObjectDel;
import Ice._ObjectDelD;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.tests.PrinterPrx;
import com.projectgoth.fusion.slice.tests._PrinterDel;
import com.projectgoth.fusion.slice.tests._PrinterDelD;
import com.projectgoth.fusion.slice.tests._PrinterDelM;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PrinterPrxHelper
extends ObjectPrxHelperBase
implements PrinterPrx {
    @Override
    public void circular(String s, int level) {
        this.circular(s, level, null, false);
    }

    @Override
    public void circular(String s, int level, Map<String, String> __ctx) {
        this.circular(s, level, __ctx, true);
    }

    private void circular(String s, int level, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _PrinterDel __del = (_PrinterDel)__delBase;
                __del.circular(s, level, __ctx);
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
    public void printString(String s) {
        this.printString(s, null, false);
    }

    @Override
    public void printString(String s, Map<String, String> __ctx) {
        this.printString(s, __ctx, true);
    }

    private void printString(String s, Map<String, String> __ctx, boolean __explicitCtx) {
        if (__explicitCtx && __ctx == null) {
            __ctx = _emptyContext;
        }
        int __cnt = 0;
        while (true) {
            _ObjectDel __delBase = null;
            try {
                __delBase = this.__getDelegate(false);
                _PrinterDel __del = (_PrinterDel)__delBase;
                __del.printString(s, __ctx);
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

    public static PrinterPrx checkedCast(ObjectPrx __obj) {
        PrinterPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PrinterPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::tests::Printer")) break block3;
                    PrinterPrxHelper __h = new PrinterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PrinterPrx checkedCast(ObjectPrx __obj, Map<String, String> __ctx) {
        PrinterPrx __d;
        block3: {
            __d = null;
            if (__obj != null) {
                try {
                    __d = (PrinterPrx)__obj;
                }
                catch (ClassCastException ex) {
                    if (!__obj.ice_isA("::com::projectgoth::fusion::slice::tests::Printer", __ctx)) break block3;
                    PrinterPrxHelper __h = new PrinterPrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static PrinterPrx checkedCast(ObjectPrx __obj, String __facet) {
        PrinterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::tests::Printer")) {
                    PrinterPrxHelper __h = new PrinterPrxHelper();
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

    public static PrinterPrx checkedCast(ObjectPrx __obj, String __facet, Map<String, String> __ctx) {
        PrinterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            try {
                if (__bb.ice_isA("::com::projectgoth::fusion::slice::tests::Printer", __ctx)) {
                    PrinterPrxHelper __h = new PrinterPrxHelper();
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

    public static PrinterPrx uncheckedCast(ObjectPrx __obj) {
        PrinterPrx __d = null;
        if (__obj != null) {
            try {
                __d = (PrinterPrx)__obj;
            }
            catch (ClassCastException ex) {
                PrinterPrxHelper __h = new PrinterPrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static PrinterPrx uncheckedCast(ObjectPrx __obj, String __facet) {
        PrinterPrxHelper __d = null;
        if (__obj != null) {
            ObjectPrx __bb = __obj.ice_facet(__facet);
            PrinterPrxHelper __h = new PrinterPrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected _ObjectDelM __createDelegateM() {
        return new _PrinterDelM();
    }

    protected _ObjectDelD __createDelegateD() {
        return new _PrinterDelD();
    }

    public static void __write(BasicStream __os, PrinterPrx v) {
        __os.writeProxy((ObjectPrx)v);
    }

    public static PrinterPrx __read(BasicStream __is) {
        ObjectPrx proxy = __is.readProxy();
        if (proxy != null) {
            PrinterPrxHelper result = new PrinterPrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}

