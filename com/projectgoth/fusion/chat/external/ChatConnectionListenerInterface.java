/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external;

import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.fdl.enums.PresenceType;

public interface ChatConnectionListenerInterface {
    public void onSignInSuccess(ChatConnectionInterface var1);

    public void onSignInFailed(ChatConnectionInterface var1, String var2);

    public void onDisconnected(ChatConnectionInterface var1, String var2);

    public void onMessageReceived(ChatConnectionInterface var1, String var2, String var3, String var4);

    public void onMessageFailed(ChatConnectionInterface var1, String var2, String var3, String var4, String var5);

    public void onContactStatusChanged(ChatConnectionInterface var1, String var2, PresenceType var3);

    public void onContactDetail(ChatConnectionInterface var1, String var2, String var3);

    public void onContactRequest(ChatConnectionInterface var1, String var2, String var3);

    public void onConferenceCreated(ChatConnectionInterface var1, String var2, String var3);

    public void onUserJoinedConference(ChatConnectionInterface var1, String var2, String var3);

    public void onUserLeftConference(ChatConnectionInterface var1, String var2, String var3);

    public void onConferenceInvitationFailed(ChatConnectionInterface var1, String var2, String var3, String var4);
}

