/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

public abstract class ChatParticipant {
    protected final String username;

    ChatParticipant(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}

