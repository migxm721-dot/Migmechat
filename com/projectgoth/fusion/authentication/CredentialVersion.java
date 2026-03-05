/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.authentication;

import com.projectgoth.fusion.common.ValueEnum;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum CredentialVersion implements ValueEnum<Byte>,
Serializable
{
    UNENCRYPTED_UNMIGRATED(0),
    UNENCRYPTED_MIGRATED(1),
    KEYCZAR(10);

    private final byte value;

    private CredentialVersion(byte value) {
        this.value = value;
    }

    @Override
    public Byte value() {
        return this.value;
    }

    public static CredentialVersion fromValue(byte value) {
        for (CredentialVersion e : CredentialVersion.values()) {
            if (e.value() != value) continue;
            return e;
        }
        return null;
    }
}

