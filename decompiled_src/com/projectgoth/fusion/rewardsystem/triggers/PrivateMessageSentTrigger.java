/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class PrivateMessageSentTrigger
extends RewardProgramTrigger {
    public PrivateMessageSentTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.FUSION_PRIVATE_MESSAGE_SENT, userData);
    }
}

