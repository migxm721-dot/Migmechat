/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BroadcastListDAO {
    public Set<String> getBroadcastListForUser(String var1);

    public Set<String> getBroadcastListForGroup(int var1);
}

