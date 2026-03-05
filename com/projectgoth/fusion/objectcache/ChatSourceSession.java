/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.data.ChatRoomEmoteLogData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.fdl.enums.ClientType;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserDataIce;
import com.projectgoth.fusion.slice.UserPrx;

public interface ChatSourceSession {
    public void sendMessageBackToUserAsEmote(MessageData var1, String var2) throws FusionException;

    public void putMessage(MessageDataIce var1) throws FusionException;

    public void logEmoteData(ChatRoomEmoteLogData var1);

    public String getSessionID();

    public ClientType getDeviceType();

    public short getClientVersion();

    public int getUserID();

    public String getUsername();

    public String getRemoteAddress();

    public String getMobileDevice();

    public String getUserAgent();

    public ConnectionPrx getConnectionProxy();

    public UserPrx findUserPrx(String var1) throws FusionException;

    public SessionPrx findSessionPrx(String var1);

    public ChatRoomPrx findChatRoomPrx(String var1) throws FusionException;

    public GroupChatPrx findGroupChatPrx(String var1) throws FusionException;

    public IcePrxFinder getIcePrxFinder();

    public UserDataIce getUserDataIce();

    public double getBalance();

    public UserData.TypeEnum getUserType();
}

