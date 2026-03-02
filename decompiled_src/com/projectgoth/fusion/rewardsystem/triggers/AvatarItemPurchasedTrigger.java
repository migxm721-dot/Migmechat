/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.purchase.AvatarItemPurchasedEvent
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.purchase.AvatarItemPurchasedEvent;

public class AvatarItemPurchasedTrigger
extends RewardProgramTrigger
implements AvatarItemPurchasedEvent {
    public Integer storeItemID;

    public AvatarItemPurchasedTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.AVATAR_PURCHASED, userData);
    }

    public Integer getStoreItemID() {
        return this.storeItemID;
    }
}

