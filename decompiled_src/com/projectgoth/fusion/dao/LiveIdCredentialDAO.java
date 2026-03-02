/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.domain.LiveIdCredential;

public interface LiveIdCredentialDAO {
    public LiveIdCredential getCredential(String var1);

    public void persistCredential(LiveIdCredential var1);
}

