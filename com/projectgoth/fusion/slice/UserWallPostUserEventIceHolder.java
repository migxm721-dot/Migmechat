/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 *  IceInternal.Ex
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.Object;
import IceInternal.Ex;
import com.projectgoth.fusion.slice.UserWallPostUserEventIce;

public final class UserWallPostUserEventIceHolder {
    public UserWallPostUserEventIce value;

    public UserWallPostUserEventIceHolder() {
    }

    public UserWallPostUserEventIceHolder(UserWallPostUserEventIce value) {
        this.value = value;
    }

    public Patcher getPatcher() {
        return new Patcher();
    }

    public class Patcher
    implements IceInternal.Patcher {
        public void patch(Object v) {
            try {
                UserWallPostUserEventIceHolder.this.value = (UserWallPostUserEventIce)v;
            }
            catch (ClassCastException ex) {
                Ex.throwUOE((String)this.type(), (String)v.ice_id());
            }
        }

        public String type() {
            return "::com::projectgoth::fusion::slice::UserWallPostUserEventIce";
        }
    }
}

