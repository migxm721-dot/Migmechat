/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Properties
 */
package com.projectgoth.fusion.objectcache;

import Ice.Properties;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.objectcache.ChatContentStore;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.MessageSwitchboardPrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;

public interface ChatObjectManagerSession {
    public RequestCounter getRequestCounter();

    public MessageSwitchboardPrx getMessageSwitchboardPrx() throws FusionException;

    public UserPrx findUserPrx(String var1) throws FusionException;

    public GroupChatPrx findGroupChatPrx(String var1) throws FusionException;

    public ChatRoomPrx findChatRoomPrx(String var1) throws FusionException;

    public SessionPrx findSessionPrx(String var1);

    public SessionPrx makeSessionPrx(String var1);

    public Properties getProperties();

    public IcePrxFinder getIcePrxFinder();

    public void logMessage(MessageToLog.TypeEnum var1, int var2, String var3, String var4, int var5, String var6);

    public ObjectCacheContext getApplicationContext();

    public ChatContentStore getFileStore();
}

