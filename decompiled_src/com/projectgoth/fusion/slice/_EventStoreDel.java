/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice._ObjectDel
 *  IceInternal.LocalExceptionWrapper
 */
package com.projectgoth.fusion.slice;

import Ice._ObjectDel;
import IceInternal.LocalExceptionWrapper;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface _EventStoreDel
extends _ObjectDel {
    public void storeUserEvent(String var1, UserEventIce var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public void storeGeneratorEvent(String var1, UserEventIce var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public UserEventIce[] getUserEventsForUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void deleteUserEvents(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1, Map<String, String> var2) throws LocalExceptionWrapper, FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws LocalExceptionWrapper, FusionException;
}

