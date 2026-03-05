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
import com.projectgoth.fusion.slice.SuspectGroupIce;
import com.projectgoth.fusion.slice._BotHunterDisp;
import com.projectgoth.fusion.slice._BotHunterOperations;

public class _BotHunterTie
extends _BotHunterDisp
implements TieBase {
    private _BotHunterOperations _ice_delegate;

    public _BotHunterTie() {
    }

    public _BotHunterTie(_BotHunterOperations delegate) {
        this._ice_delegate = delegate;
    }

    public Object ice_delegate() {
        return this._ice_delegate;
    }

    public void ice_delegate(Object delegate) {
        this._ice_delegate = (_BotHunterOperations)delegate;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        if (!(rhs instanceof _BotHunterTie)) {
            return false;
        }
        return this._ice_delegate.equals(((_BotHunterTie)rhs)._ice_delegate);
    }

    public int hashCode() {
        return this._ice_delegate.hashCode();
    }

    public SuspectGroupIce[] getLatestSuspects(Current __current) throws FusionException {
        return this._ice_delegate.getLatestSuspects(__current);
    }
}

