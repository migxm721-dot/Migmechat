/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectPrx
 */
package com.projectgoth.fusion.slice;

import Ice.ObjectPrx;
import com.projectgoth.fusion.slice.ConnectionPrx;
import com.projectgoth.fusion.slice.EventPrivacySettingIce;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.UserEventIce;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface EventSystemPrx
extends ObjectPrx {
    public UserEventIce[] getUserEventsForUser(String var1) throws FusionException;

    public UserEventIce[] getUserEventsForUser(String var1, Map<String, String> var2) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1) throws FusionException;

    public UserEventIce[] getUserEventsGeneratedByUser(String var1, Map<String, String> var2) throws FusionException;

    public void updateAllowList(String var1, String[] var2) throws FusionException;

    public void updateAllowList(String var1, String[] var2, Map<String, String> var3) throws FusionException;

    public void streamEventsToLoggingInUser(String var1, ConnectionPrx var2) throws FusionException;

    public void streamEventsToLoggingInUser(String var1, ConnectionPrx var2, Map<String, String> var3) throws FusionException;

    public void deleteUserEvents(String var1) throws FusionException;

    public void deleteUserEvents(String var1, Map<String, String> var2) throws FusionException;

    public void madePhotoPublic(String var1, int var2, String var3) throws FusionException;

    public void madePhotoPublic(String var1, int var2, String var3, Map<String, String> var4) throws FusionException;

    public void setProfileStatus(String var1, String var2) throws FusionException;

    public void setProfileStatus(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void createdPublicChatroom(String var1, String var2) throws FusionException;

    public void createdPublicChatroom(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void addedFriend(String var1, String var2) throws FusionException;

    public void addedFriend(String var1, String var2, Map<String, String> var3) throws FusionException;

    public void updatedProfile(String var1) throws FusionException;

    public void updatedProfile(String var1, Map<String, String> var2) throws FusionException;

    public void purchasedVirtualGoods(String var1, byte var2, int var3, String var4) throws FusionException;

    public void purchasedVirtualGoods(String var1, byte var2, int var3, String var4, Map<String, String> var5) throws FusionException;

    public void virtualGift(String var1, String var2, String var3, int var4);

    public void virtualGift(String var1, String var2, String var3, int var4, Map<String, String> var5);

    public void userWallPost(String var1, String var2, String var3, int var4) throws FusionException;

    public void userWallPost(String var1, String var2, String var3, int var4, Map<String, String> var5) throws FusionException;

    public void groupDonation(String var1, int var2) throws FusionException;

    public void groupDonation(String var1, int var2, Map<String, String> var3) throws FusionException;

    public void groupJoined(String var1, int var2) throws FusionException;

    public void groupJoined(String var1, int var2, Map<String, String> var3) throws FusionException;

    public void groupAnnouncement(String var1, int var2, int var3) throws FusionException;

    public void groupAnnouncement(String var1, int var2, int var3, Map<String, String> var4) throws FusionException;

    public void madeGroupUserPost(String var1, int var2, int var3) throws FusionException;

    public void madeGroupUserPost(String var1, int var2, int var3, Map<String, String> var4) throws FusionException;

    public void genericApplicationEvent(String var1, String var2, String var3, Map<String, String> var4) throws FusionException;

    public void genericApplicationEvent(String var1, String var2, String var3, Map<String, String> var4, Map<String, String> var5) throws FusionException;

    public void giftShowerEvent(String var1, String var2, String var3, int var4, int var5);

    public void giftShowerEvent(String var1, String var2, String var3, int var4, int var5, Map<String, String> var6);

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1) throws FusionException;

    public EventPrivacySettingIce getPublishingPrivacyMask(String var1, Map<String, String> var2) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;

    public void setPublishingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1) throws FusionException;

    public EventPrivacySettingIce getReceivingPrivacyMask(String var1, Map<String, String> var2) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2) throws FusionException;

    public void setReceivingPrivacyMask(String var1, EventPrivacySettingIce var2, Map<String, String> var3) throws FusionException;
}

