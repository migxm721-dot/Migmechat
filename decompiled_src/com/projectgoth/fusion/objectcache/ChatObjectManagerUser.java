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
import com.projectgoth.fusion.objectcache.ChatSession;
import com.projectgoth.fusion.objectcache.ChatUser;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.GroupChatPrx;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.SessionCachePrx;
import com.projectgoth.fusion.slice.SessionPrx;
import com.projectgoth.fusion.slice.UserPrx;

public interface ChatObjectManagerUser {
    public ChatSession createSession(ChatUser var1, String var2, int var3, int var4, int var5, int var6, int var7, int var8, String var9, String var10, String var11, short var12, String var13, ConnectionPrx var14);

    public RequestCounter getRequestCounter();

    public Properties getProperties();

    public IcePrxFinder getIcePrxFinder();

    public void removeSession(String var1, boolean var2);

    public SessionPrx onSessionCreated(ChatSession var1);

    public void onSessionRemoved(ChatUser var1);

    public void removeUser(String var1);

    public UserPrx findUserPrx(String var1) throws FusionException;

    public UserPrx makeUserPrx(String var1);

    public GroupChatPrx findGroupChatPrx(String var1) throws FusionException;

    public SessionPrx findSessionPrx(String var1);

    public SessionPrx[] findSessionsPrx(String[] var1);

    public UserPrx findUserPrxFromRegistry(String var1) throws ObjectNotFoundException;

    public void logMessage(MessageToLog.TypeEnum var1, int var2, String var3, String var4, int var5, String var6);

    public RegistryPrx getRegistryPrx();

    public boolean isLogMessagesToFile();

    public Credential[] getUserCredentials(int var1, byte[] var2) throws FusionException;

    public ChatContentStore getFileStore();

    public SessionCachePrx getSessionCachePrx();

    public ObjectCacheContext getApplicationContext();
}

