/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.data;

import com.projectgoth.fusion.data.RegistrationContextData;
import com.projectgoth.fusion.data.UserData;
import java.io.Serializable;

public class UserDataAndRegistrationContextData
implements Serializable {
    public UserData userData;
    public RegistrationContextData regContextData;

    public UserDataAndRegistrationContextData(UserData userData, RegistrationContextData regContextData) {
        this.userData = userData;
        this.regContextData = regContextData;
    }
}

