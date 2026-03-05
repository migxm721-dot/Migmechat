/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.DispatchStatus
 *  Ice.Object
 *  Ice.OperationMode
 *  Ice.OperationNotExistException
 *  Ice.SystemException
 *  Ice._ObjectDelD
 *  IceInternal.Direct
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.DispatchStatus;
import Ice.Object;
import Ice.OperationMode;
import Ice.OperationNotExistException;
import Ice.SystemException;
import Ice._ObjectDelD;
import IceInternal.Direct;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.MessageLogger;
import com.projectgoth.fusion.slice._MessageLoggerDel;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class _MessageLoggerDelD
extends _ObjectDelD
implements _MessageLoggerDel {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void logMessage(final int type, final int sourceCountryID, final String source, final String destination, final int numRecipients, final String messageText, Map<String, String> __ctx) throws LocalExceptionWrapper {
        final Current __current = new Current();
        this.__initCurrent(__current, "logMessage", OperationMode.Normal, __ctx);
        Direct __direct = null;
        try {
            __direct = new Direct(__current){

                public DispatchStatus run(Object __obj) {
                    MessageLogger __servant = null;
                    try {
                        __servant = (MessageLogger)__obj;
                    }
                    catch (ClassCastException __ex) {
                        throw new OperationNotExistException(__current.id, __current.facet, __current.operation);
                    }
                    __servant.logMessage(type, sourceCountryID, source, destination, numRecipients, messageText, __current);
                    return DispatchStatus.DispatchOK;
                }
            };
            try {
                DispatchStatus __status = __direct.servant().__collocDispatch(__direct);
                if (__status == DispatchStatus.DispatchUserException) {
                    __direct.throwUserException();
                }
                assert (__status == DispatchStatus.DispatchOK);
                java.lang.Object var12_13 = null;
            }
            catch (Throwable throwable) {
                java.lang.Object var12_14 = null;
                __direct.destroy();
                throw throwable;
            }
            __direct.destroy();
            {
            }
        }
        catch (SystemException __ex) {
            throw __ex;
        }
        catch (Throwable __ex) {
            LocalExceptionWrapper.throwWrapper((Throwable)__ex);
        }
    }
}

