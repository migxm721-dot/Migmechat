/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.ObjectPrx
 *  Ice.ObjectPrxHelperBase
 *  Ice.OperationMode
 *  Ice.UnknownUserException
 *  Ice.UserException
 *  IceInternal.OutgoingAsync
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice.OperationMode;
import Ice.UnknownUserException;
import Ice.UserException;
import IceInternal.OutgoingAsync;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AMI_Connection_putMessageAsync
extends OutgoingAsync {
    public abstract void ice_response();

    public abstract void ice_exception(LocalException var1);

    public abstract void ice_exception(UserException var1);

    public final boolean __invoke(ObjectPrx __prx, AMI_Connection_putMessageAsync __cb, MessageDataIce message, Map<String, String> __ctx) {
        this.__acquireCallback(__prx);
        try {
            ((ObjectPrxHelperBase)__prx).__checkTwowayOnly("putMessageAsync");
            this.__prepare(__prx, "putMessageAsync", OperationMode.Normal, __ctx);
            message.__write(this.__os);
            this.__os.endWriteEncaps();
            return this.__send();
        }
        catch (LocalException __ex) {
            this.__releaseCallback(__ex);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final void __response(boolean __ok) {
        try {
            if (!__ok) {
                try {
                    this.__throwUserException();
                }
                catch (FusionException __ex) {
                    throw __ex;
                }
                catch (UserException __ex) {
                    throw new UnknownUserException(__ex.ice_name());
                }
            }
            this.__is.skipEmptyEncaps();
        }
        catch (UserException __ex) {
            try {
                this.ice_exception(__ex);
            }
            catch (Exception ex) {
                this.__warning(ex);
            }
            finally {
                this.__releaseCallback();
            }
            return;
        }
        catch (LocalException __ex) {
            this.__finished(__ex);
            return;
        }
        this.ice_response();
        this.__releaseCallback();
    }
}

