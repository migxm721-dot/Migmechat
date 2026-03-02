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
import com.projectgoth.fusion.slice.MessageLoggerStats;
import com.projectgoth.fusion.slice._MessageLoggerAdminDisp;
import com.projectgoth.fusion.slice._MessageLoggerAdminOperations;

public class _MessageLoggerAdminTie
extends _MessageLoggerAdminDisp
implements TieBase {
    private _MessageLoggerAdminOperations _ice_delegate;

    public _MessageLoggerAdminTie() {
    }

    public _MessageLoggerAdminTie(_MessageLoggerAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_MessageLoggerAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _MessageLoggerAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_MessageLoggerAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public MessageLoggerStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

