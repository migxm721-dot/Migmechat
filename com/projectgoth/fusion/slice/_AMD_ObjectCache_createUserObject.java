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
import com.projectgoth.fusion.slice.AMD_ObjectCache_createUserObject;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectExistsException;
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice.UserPrxHelper;

final class _AMD_ObjectCache_createUserObject
extends IncomingAsync
implements AMD_ObjectCache_createUserObject {
    public _AMD_ObjectCache_createUserObject(Incoming in) {
        super(in);
    }

    public void ice_response(UserPrx __ret) {
        if (this.__validateResponse(true)) {
            try {
                BasicStream __os = this.__os();
                UserPrxHelper.__write(__os, __ret);
            }
            catch (LocalException __ex) {
                this.ice_exception((Exception)((Object)__ex));
            }
            this.__response(true);
        }
    }

    public void ice_exception(Exception ex) {
        block6: {
            try {
                throw ex;
            }
            catch (ObjectExistsException __ex) {
                if (this.__validateResponse(false)) {
                    this.__os().writeUserException((UserException)__ex);
                    this.__response(false);
                }
            }
            catch (FusionException __ex) {
                if (this.__validateResponse(false)) {
                    this.__os().writeUserException((UserException)__ex);
                    this.__response(false);
                }
            }
            catch (Exception __ex) {
                if (!this.__validateException(__ex)) break block6;
                this.__exception(__ex);
            }
        }
    }
}

