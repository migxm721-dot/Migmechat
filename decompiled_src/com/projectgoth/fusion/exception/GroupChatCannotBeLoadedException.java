/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.exception;

import com.projectgoth.fusion.exception.ExceptionWithDiagnosticCode;

public class GroupChatCannotBeLoadedException
extends ExceptionWithDiagnosticCode {
    private static final String MSG = "We were unable to load the group chat from storage. Please try later";

    public GroupChatCannotBeLoadedException(Exception rootException, String contextInfo) {
        super(MSG, rootException, contextInfo);
    }
}

