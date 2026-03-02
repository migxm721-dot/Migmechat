/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.exception;

import com.projectgoth.fusion.slice.FusionException;

public class GroupChatNotMemberException
extends FusionException {
    public static final String MESSAGE = "You are not a member of this group chat";

    public GroupChatNotMemberException() {
        super(MESSAGE);
    }
}

