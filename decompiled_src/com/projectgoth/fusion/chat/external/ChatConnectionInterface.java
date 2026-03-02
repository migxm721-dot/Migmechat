/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external;

import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ChatConnectionInterface {
    public String getUsername();

    public ImType getImType();

    public void signIn(String var1, String var2) throws Exception;

    public void signOut();

    public boolean isSignedIn();

    public boolean isConnected();

    public void sendMessage(String var1, String var2) throws Exception;

    public void addContact(String var1) throws Exception;

    public void removeContact(String var1) throws Exception;

    public void setAvatar(String var1) throws Exception;

    public void setStatus(PresenceType var1, String var2) throws Exception;

    public String inviteToConference(String var1, String var2) throws Exception;

    public void leaveConference(String var1) throws Exception;

    public List<String> getConferenceParticipants(String var1) throws Exception;
}

