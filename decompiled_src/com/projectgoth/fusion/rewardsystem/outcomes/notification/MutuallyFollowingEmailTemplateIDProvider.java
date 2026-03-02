/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.outcomes.notification;

import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeData;
import com.projectgoth.fusion.rewardsystem.outcomes.RewardProgramOutcomeProcessor;
import com.projectgoth.fusion.rewardsystem.outcomes.notification.EmailTemplateIDOutcomeData;
import com.projectgoth.fusion.rewardsystem.triggers.MutuallyFollowingEventTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import org.apache.log4j.Logger;

public class MutuallyFollowingEmailTemplateIDProvider
extends RewardProgramOutcomeProcessor {
    private static Logger log = Log4JUtils.getLogger(MutuallyFollowingEmailTemplateIDProvider.class);
    public static final String PARAM_INITIATOR_EMAIL_TEMPLATE_ID = "initiatorEmailTmpltID";
    public static final String PARAM_FOLLOW_BACKER_EMAIL_TEMPLATE_ID = "followBackerEmailTmpltID";

    public RewardProgramOutcomeData getOutcome(RewardProgramData data, RewardProgramTrigger trigger) throws Exception {
        if (!(trigger instanceof MutuallyFollowingEventTrigger)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("does not support selecting outcome from trigger:[" + trigger + "]"));
            }
            return null;
        }
        MutuallyFollowingEventTrigger mfeTrigger = (MutuallyFollowingEventTrigger)trigger;
        int emailTemplateID = mfeTrigger.isThisUserFollowedBacked() ? data.getIntParam(PARAM_FOLLOW_BACKER_EMAIL_TEMPLATE_ID, -1) : data.getIntParam(PARAM_INITIATOR_EMAIL_TEMPLATE_ID, -1);
        if (emailTemplateID >= 1) {
            return new EmailTemplateIDOutcomeData(emailTemplateID);
        }
        return null;
    }
}

