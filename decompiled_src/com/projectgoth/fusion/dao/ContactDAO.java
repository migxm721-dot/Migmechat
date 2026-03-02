/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.data.ContactData;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ContactDAO {
    public Set<ContactData> getContactListForUser(String var1);
}

