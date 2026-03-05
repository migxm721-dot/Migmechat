/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface EventStorePrx
extends ObjectPrx {
    public void storeUserEvent(String var1, UserEventIce var2) throws FusionException;

    public void storeUserEvent(String var1, UserEventIce var2, Map<String, String> var3) throws FusionException;

    public void storeGeneratorEvent(String var1, UserEventIce var2) throws FusionException;

    public void storeGeneratorEvent(String var1, UserEventIce var2, Map<String, String> var3) throws FusionException;

    public UserEventIce[] getUserEventsForUser(String var1) throws FusionException;

    public UserEventIce[] getUserEventsForUser(String var1, Map<String, String> var2) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1, Map<String, String> var2) throws FusionException;

    public void deleteUserEvents(String var1) throws FusionException;

    public void deleteUserEvents(String var1, Map<String, String> var2) throws FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1) throws FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1, Map<String, String> var2) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1, Map<String, String> var2) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws FusionException;
}

