/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.ExceptionWithErrorCause;

public class UsernameValidationException
extends ExceptionWithErrorCause {
    public UsernameValidationException(ErrorCause reasonType, Object ... errorMsgArgs) {
        super(reasonType, errorMsgArgs);
    }
}

