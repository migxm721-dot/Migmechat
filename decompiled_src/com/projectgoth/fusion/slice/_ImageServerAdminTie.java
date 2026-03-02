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
import com.projectgoth.fusion.slice.ImageServerStats;
import com.projectgoth.fusion.slice._ImageServerAdminDisp;
import com.projectgoth.fusion.slice._ImageServerAdminOperations;

public class _ImageServerAdminTie
extends _ImageServerAdminDisp
implements TieBase {
    private _ImageServerAdminOperations _ice_delegate;

    public _ImageServerAdminTie() {
    }

    public _ImageServerAdminTie(_ImageServerAdminOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_ImageServerAdminOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _ImageServerAdminTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_ImageServerAdminTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public ImageServerStats getStats(Current __current) throws FusionException {
        return this._ice_delegate.getStats(__current);
    }
}

