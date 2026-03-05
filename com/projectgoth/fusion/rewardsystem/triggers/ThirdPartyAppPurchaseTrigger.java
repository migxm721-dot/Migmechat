/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.purchase.ThirdPartyAppPurchaseEvent
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.purchase.ThirdPartyAppPurchaseEvent;

public class ThirdPartyAppPurchaseTrigger
extends RewardProgramTrigger
implements ThirdPartyAppPurchaseEvent {
    public String applicationName = "";

    public ThirdPartyAppPurchaseTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.THIRDPARTY_APP_PURCHASE, userData);
    }

    public String getApplicationName() {
        return this.applicationName;
    }
}

