/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  Ice.TieBase
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import Ice.TieBase;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SMSEngineStats;
import com.projectgoth.fusion.slice._SMSEngineAdminDisp;
import com.projectgoth.fusion.slice._SMSEngineAdminOperations;

public class _SMSEngineAdminTie
extends _SMSEngineAdminDisp
implements TieBase {
    private _SMSEngineAdminOperations _ice_delegate;

    public _SMSEngineAdminTie() {
    }

    public _SMSEngineAdminTie(_SMSEngineAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_SMSEngineAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _SMSEngineAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_SMSEngineAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public SMSEngineStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

