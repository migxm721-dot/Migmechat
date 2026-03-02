/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Properties
 */
package com.projectgoth.fusion.objectcache;

import Ice.Properties;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.messagelogger.MessageToLog;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;

public interface ChatObjectManagerRoom {
    public RegistryPrx getRegistryPrx();

    public ChatRoomPrx findChatRoomPrx(String var1) throws FusionException;

    public Properties getProperties();

    public IcePrxFinder getIcePrxFinder();

    public void onRoomSessionRemoved();

    public void onRoomSessionAdded();

    public void logMessage(MessageToLog.TypeEnum var1, int var2, String var3, String var4, int var5, String var6);

    public ScheduledExecutorService getDistributionService();

    public long getChatRoomIdleTimeout();

    public Semaphore getGiftAllSemaphore();
}

