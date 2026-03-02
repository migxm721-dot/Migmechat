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
import com.projectgoth.fusion.slice.CollectedAddressBookDataIce;

public final class CollectedAddressBookDataIceHolder {
    public CollectedAddressBookDataIce value;

    public CollectedAddressBookDataIceHolder() {
    }

    public CollectedAddressBookDataIceHolder(CollectedAddressBookDataIce value) {
        this.value = value;
    }

    public Patcher getPatcher() {
        return new Patcher();
    }

    public class Patcher
    implements IceInternal.Patcher {
        public void patch(Object v) {
            try {
                CollectedAddressBookDataIceHolder.this.value = (CollectedAddressBookDataIce)v;
            }
            catch (ClassCastException ex) {
                Ex.throwUOE((String)this.type(), (String)v.ice_id());
            }
        }

        public String type() {
            return "::com::projectgoth::fusion::slice::CollectedAddressBookDataIce";
        }
    }
}

