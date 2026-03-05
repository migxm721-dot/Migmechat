/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;

public interface ChatSourceUser {
    public String getUsername();

    public void putMessage(MessageDataIce var1) throws FusionException;

    public UserDataIce getUserData();

    public SessionPrx getSessionPrx(String var1);
}

