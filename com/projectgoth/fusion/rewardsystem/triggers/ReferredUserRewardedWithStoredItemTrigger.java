/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.userreward.UserRewardedWithStoreItemEvent
 *  com.projectgoth.leto.common.storeitem.StoreItem
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.RewardedStoreItemData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.ReferredUserRewardedBaseTrigger;
import com.projectgoth.leto.common.event.userreward.UserRewardedWithStoreItemEvent;
import com.projectgoth.leto.common.storeitem.StoreItem;

public class ReferredUserRewardedWithStoredItemTrigger
extends ReferredUserRewardedBaseTrigger
implements UserRewardedWithStoreItemEvent {
    private final RewardedStoreItemData rewardedStoreItemData;

    public ReferredUserRewardedWithStoredItemTrigger(UserData referrerUserData, UserData referredUserData, RewardProgramData referredUserRewardProgram, RewardedStoreItemData rewardedStoreItemData) {
        super(RewardProgramData.TypeEnum.REFERRED_USER_REWARDED_WITH_STOREITEM, referrerUserData, referredUserData, referredUserRewardProgram);
        this.rewardedStoreItemData = rewardedStoreItemData;
        this.quantityDelta = 1;
        this.amountDelta = rewardedStoreItemData.getPrice();
        this.currency = rewardedStoreItemData.getCurrency();
    }

    public RewardedStoreItemData getRewardedStoreItemData() {
        return this.rewardedStoreItemData;
    }

    public StoreItem getStoreItem() {
        return this.getRewardedStoreItemData();
    }
}

