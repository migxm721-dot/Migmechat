/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.botgame.BotGameUserWonEvent
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.botgame.BotGameUserWonEvent;

public class BotGameWonTrigger
extends RewardProgramTrigger
implements BotGameUserWonEvent {
    public int botID;

    public BotGameWonTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.BOT_GAME_WON, userData);
    }

    public int getBotID() {
        return this.botID;
    }
}

