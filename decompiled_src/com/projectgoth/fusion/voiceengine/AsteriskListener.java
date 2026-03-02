/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.voiceengine;

import com.projectgoth.fusion.voiceengine.AsteriskCommand;

public interface AsteriskListener {
    public void asteriskDisconnected(String var1);

    public void asteriskEventReceived(AsteriskCommand var1);
}

