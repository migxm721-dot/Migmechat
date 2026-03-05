/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  Ice._ObjectDelM
 *  IceInternal.BasicStream
 *  IceInternal.LocalExceptionWrapper
 *  IceInternal.Outgoing
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import Ice._ObjectDelM;
import IceInternal.BasicStream;
import IceInternal.LocalExceptionWrapper;
import IceInternal.Outgoing;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;
import com.projectgoth.fusion.slice._EmailAlertDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _EmailAlertDelM
extends _ObjectDelM
implements _EmailAlertDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void requestUnreadEmailCount(String username, String password, UserPrx userProxy, Map<String, String> __ctx) throws LocalExceptionWrapper {
        Outgoing __og = this.__handler.getOutgoing("requestUnreadEmailCount", OperationMode.Normal, __ctx);
        try {
            try {
                BasicStream __os = __og.os();
                __os.writeString(username);
                __os.writeString(password);
                UserPrxHelper.__write(__os, userProxy);
            }
            catch (LocalException __ex) {
                __og.abort(__ex);
            }
            boolean __ok = __og.invoke();
            if (!__og.is().isEmpty()) {
                try {
                    if (!__ok) {
                        try {
                            __og.throwUserException();
                        }
                        catch (UserException __ex) {
                            throw new UnknownUserException(__ex.ice_name());
                        }
                    }
                    __og.is().skipEmptyEncaps();
                }
                catch (LocalException __ex) {
                    throw new LocalExceptionWrapper(__ex, false);
                }
            }
            Object var9_11 = null;
        }
        catch (Throwable throwable) {
            Object var9_12 = null;
            this.__handler.reclaimOutgoing(__og);
            throw throwable;
        }
        this.__handler.reclaimOutgoing(__og);
    }
}

