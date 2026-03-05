/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  IceInternal.BasicStream
 */
package com.projectgoth.fusion.slice;

import IceInternal.BasicStream;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactDataIceArrayHelper;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIceArrayHelper;
import java.io.Serializable;
import java.util.Arrays;

public final class ContactList
implements Cloneable,
Serializable {
    public ContactGroupDataIce[] contactGroups;
    public ContactDataIce[] contacts;
    public int version;

    public ContactList() {
    }

    public ContactList(ContactGroupDataIce[] contactGroups, ContactDataIce[] contacts, int version) {
        this.contactGroups = contactGroups;
        this.contacts = contacts;
        this.version = version;
    }

    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }
        ContactList _r = null;
        try {
            _r = (ContactList)rhs;
        }
        catch (ClassCastException classCastException) {
            // empty catch block
        }
        if (_r != null) {
            if (!Arrays.equals(this.contactGroups, _r.contactGroups)) {
                return false;
            }
            if (!Arrays.equals(this.contacts, _r.contacts)) {
                return false;
            }
            return this.version == _r.version;
        }
        return false;
    }

    public int hashCode() {
        int __h = 0;
        if (this.contactGroups != null) {
            for (int __i0 = 0; __i0 < this.contactGroups.length; ++__i0) {
                if (this.contactGroups[__i0] == null) continue;
                __h = 5 * __h + this.contactGroups[__i0].hashCode();
            }
        }
        if (this.contacts != null) {
            for (int __i1 = 0; __i1 < this.contacts.length; ++__i1) {
                if (this.contacts[__i1] == null) continue;
                __h = 5 * __h + this.contacts[__i1].hashCode();
            }
        }
        __h = 5 * __h + this.version;
        return __h;
    }

    public Object clone() {
        Object o;
        block2: {
            o = null;
            try {
                o = super.clone();
            }
            catch (CloneNotSupportedException ex) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
        return o;
    }

    public void __write(BasicStream __os) {
        ContactGroupDataIceArrayHelper.write(__os, this.contactGroups);
        ContactDataIceArrayHelper.write(__os, this.contacts);
        __os.writeInt(this.version);
    }

    public void __read(BasicStream __is) {
        this.contactGroups = ContactGroupDataIceArrayHelper.read(__is);
        this.contacts = ContactDataIceArrayHelper.read(__is);
        this.version = __is.readInt();
    }
}

