/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.accesscontrol;

import com.projectgoth.fusion.data.UserData;
import java.io.Serializable;

public class AuthenticatedAccessControlParameter
implements Serializable {
    private static final long serialVersionUID = 4754873587828407058L;
    String username;
    boolean isMobileVerified;
    boolean isEmailVerified;

    public AuthenticatedAccessControlParameter(String username, boolean isMobileVerified, boolean isEmailVerified) {
        this.username = username;
        this.isMobileVerified = isMobileVerified;
        this.isEmailVerified = isEmailVerified;
    }

    public AuthenticatedAccessControlParameter(UserData userData) {
        this.username = userData.username;
        this.isMobileVerified = userData.mobileVerified != null && userData.mobileVerified != false;
        this.isEmailVerified = userData.emailVerified != null && userData.emailVerified != false;
    }

    public String toString() {
        return String.format("AuthenticatedAccessControlParameter [un=%s, mobileV=%s, emailV=%s]", this.username, this.isMobileVerified, this.isEmailVerified);
    }
}

