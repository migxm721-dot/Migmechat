/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.uns.domain;

import com.projectgoth.fusion.uns.domain.Note;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AlertNote
extends Note {
    private Set<String> users = new HashSet<String>();

    public AlertNote(String message) {
        super(message);
    }

    public Set<String> getUsers() {
        return this.users;
    }

    public String[] getUsersArray() {
        return this.users.toArray(new String[this.users.size()]);
    }

    public void addUser(String username) {
        this.users.add(username);
    }

    public boolean hasRecipients() {
        return !this.users.isEmpty();
    }
}

