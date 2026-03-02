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
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.UserPrx;

public interface ChatObjectManagerGroup {
    public IcePrxFinder getIcePrxFinder();

    public UserPrx findUserPrx(String var1) throws FusionException;

    public RegistryPrx getRegistryPrx();

    public void onGroupSessionRemoved();

    public void onGroupSessionAdded();

    public void logMessage(MessageToLog.TypeEnum var1, int var2, String var3, String var4, int var5, String var6);

    public Properties getProperties();

    public boolean isLogMessagesToFile();

    public boolean isLogMessagesToDB();
}

