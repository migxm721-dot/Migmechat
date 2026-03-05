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
import com.projectgoth.fusion.slice.AMD_User_createSession;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.SessionPrxHelper;

final class _AMD_User_createSession
extends IncomingAsync
implements AMD_User_createSession {
    public _AMD_User_createSession(Incoming in) {
        super(in);
    }

    public void ice_response(SessionPrx __ret) {
        if (this.__validateResponse(true)) {
            try {
                BasicStream __os = this.__os();
                SessionPrxHelper.__write(__os, __ret);
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

