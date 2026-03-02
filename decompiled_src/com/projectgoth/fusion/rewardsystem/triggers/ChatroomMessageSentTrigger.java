/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class ChatroomMessageSentTrigger
extends RewardProgramTrigger {
    public ChatroomMessageSentTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.FUSION_CHATROOM_MESSAGES_SENT, userData);
    }
}

