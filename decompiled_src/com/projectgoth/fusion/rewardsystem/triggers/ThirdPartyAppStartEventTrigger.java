/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class ThirdPartyAppStartEventTrigger
extends RewardProgramTrigger {
    public String applicationName = "";

    public ThirdPartyAppStartEventTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.THIRDPARTY_APP_START, userData);
    }
}

