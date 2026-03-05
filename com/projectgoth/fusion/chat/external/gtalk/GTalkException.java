/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jivesoftware.smack.XMPPException
 */
package com.projectgoth.fusion.chat.external.gtalk;

import org.jivesoftware.smack.XMPPException;

public class GTalkException
extends XMPPException {
    private static final long serialVersionUID = -3872107634754334566L;

    public GTalkException(String message) {
        super(message);
    }

    public GTalkException(Throwable t) {
        super(t);
    }
}

