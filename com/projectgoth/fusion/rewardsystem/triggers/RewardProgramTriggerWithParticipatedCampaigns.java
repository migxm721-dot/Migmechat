/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.CampaignParticipation
 *  com.projectgoth.leto.common.event.campaigns.SubjectParticipatedCampaigns
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.CampaignParticipation;
import com.projectgoth.leto.common.event.campaigns.SubjectParticipatedCampaigns;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class RewardProgramTriggerWithParticipatedCampaigns
extends RewardProgramTrigger
implements SubjectParticipatedCampaigns {
    private Map<Integer, CampaignParticipation> participatedCampaigns = new HashMap<Integer, CampaignParticipation>();

    protected RewardProgramTriggerWithParticipatedCampaigns(RewardProgramData.TypeEnum programType, UserData userData) {
        super(programType, userData);
    }

    public Map<Integer, CampaignParticipation> getParticipatedCampaigns() {
        return this.participatedCampaigns;
    }

    public void setParticipatedCampaigns(Map<Integer, CampaignParticipation> participatedCampaigns) {
        this.participatedCampaigns = participatedCampaigns;
    }
}

