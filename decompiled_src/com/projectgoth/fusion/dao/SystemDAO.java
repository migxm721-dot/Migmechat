/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SystemDAO {
    public Map<String, String> getSystemProperties();

    public String getSystemProperty(String var1);
}

