/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.UserException
 *  IceInternal.Incoming
 *  IceInternal.IncomingAsync
 */
package com.projectgoth.fusion.slice;

import Ice.UserException;
import IceInternal.Incoming;
import IceInternal.IncomingAsync;
import com.projectgoth.fusion.slice.AMD_User_otherIMLogin;
import com.projectgoth.fusion.slice.FusionException;

final class _AMD_User_otherIMLogin
extends IncomingAsync
implements AMD_User_otherIMLogin {
    public _AMD_User_otherIMLogin(Incoming in) {
        super(in);
    }

    public void ice_response() {
        if (this.__validateResponse(true)) {
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

