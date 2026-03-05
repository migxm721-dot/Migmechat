/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.slice;

import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;

public interface _EventStoreOperationsNC {
    public void storeUserEvent(String var1, UserEventIce var2) throws FusionException;

    public void storeGeneratorEvent(String var1, UserEventIce var2) throws FusionException;

    public UserEventIce[] getUserEventsForUser(String var1) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1) throws FusionException;

    public void deleteUserEvents(String var1) throws FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;
}

