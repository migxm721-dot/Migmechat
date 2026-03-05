/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  Ice.UserException
 *  IceInternal.BasicStream
 *  IceInternal.Incoming
 *  IceInternal.IncomingAsync
 */
package com.projectgoth.fusion.slice;

import Ice.LocalException;
import Ice.UserException;
import IceInternal.BasicStream;
import IceInternal.Incoming;
import IceInternal.IncomingAsync;
import com.projectgoth.fusion.slice.AMD_Registry_getMessageSwitchboard;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrxHelper;

final class _AMD_Registry_getMessageSwitchboard
extends IncomingAsync
implements AMD_Registry_getMessageSwitchboard {
    public _AMD_Registry_getMessageSwitchboard(Incoming in) {
        super(in);
    }

    public void ice_response(MessageSwitchboardPrx __ret) {
        if (this.__validateResponse(true)) {
            try {
                BasicStream __os = this.__os();
                MessageSwitchboardPrxHelper.__write(__os, __ret);
            }
            catch (LocalException __ex) {
                this.ice_exception((Exception)((Object)__ex));
            }
            this.__response(true);
        }
    }

    public void ice_exception(Exception ex) {
        block4: {
            try {
                throw ex;
            }
            catch (FusionException __ex) {
                if (this.__validateResponse(false)) {
                    this.__os().writeUserException((UserException)__ex);
                    this.__response(false);
                }
            }
            catch (Exception __ex) {
                if (!this.__validateException(__ex)) break block4;
                this.__exception(__ex);
            }
        }
    }
}

