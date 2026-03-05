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
import com.projectgoth.fusion.slice.UserPrx;
import com.projectgoth.fusion.slice._EmailAlertDisp;
import com.projectgoth.fusion.slice._EmailAlertOperations;

public class _EmailAlertTie
extends _EmailAlertDisp
implements TieBase {
    private _EmailAlertOperations _ice_delegate;

    public _EmailAlertTie() {
    }

    public _EmailAlertTie(_EmailAlertOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_EmailAlertOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _EmailAlertTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_EmailAlertTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public void requestUnreadEmailCount(String username, String password, UserPrx userProxy, Current __current) {
        this._ice_delegate.requestUnreadEmailCount(username, password, userProxy, __current);
    }
}

