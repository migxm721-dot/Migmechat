/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.Date;

public class ConsecutiveLoginTrigger
extends RewardProgramTrigger {
    public Date lastLoginDate;

    public ConsecutiveLoginTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.CONSECUTIVE_LOGIN, userData);
    }
}

