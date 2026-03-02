/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.slice;

import Ice.Current;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;

public interface _EventStoreOperations {
    public void storeUserEvent(String var1, UserEventIce var2, Current var3) throws FusionException;

    public void storeGeneratorEvent(String var1, UserEventIce var2, Current var3) throws FusionException;

    public UserEventIce[] getUserEventsForUser(String var1, Current var2) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1, Current var2) throws FusionException;

    public void deleteUserEvents(String var1, Current var2) throws FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1, Current var2) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2, Current var3) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1, Current var2) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2, Current var3) throws FusionException;
}

