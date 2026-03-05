/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.fusion.rewardsystem.triggers.VGSentTrigger;
import org.apache.log4j.Logger;

public class VGSentRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(VGSentRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String UNIQUE_GIFT_PARAM_KEY = "virtualGiftID";
    @RewardProgramParamName
    public static final String IS_FROM_INVENTORY = "isFromInventory";
    @RewardProgramParamName
    public static final String UNIQUE_RECEIVER_PARAM_KEY = "receiverUserID";
    @RewardProgramParamName
    public static final String RECEIVER_VERIFIED_ACCOUNT_STATUS = "rcvrVfydAccStats";
    @RewardProgramParamName
    public static final String RECEIVER_VERIFIED_ACCOUNT_ENTITY_TYPE = "rcvrVfydAccEntityType";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        int giftid;
        if (!(trigger instanceof VGSentTrigger)) {
            return false;
        }
        VGSentTrigger vgTrigger = (VGSentTrigger)trigger;
        boolean valid = true;
        UserData receiverUserData = vgTrigger.getReceiverUserData();
        int userid = programData.getIntParam(UNIQUE_RECEIVER_PARAM_KEY, -1);
        if (userid > 0) {
            boolean bl = valid = userid == receiverUserData.userID;
        }
        if ((giftid = programData.getIntParam(UNIQUE_GIFT_PARAM_KEY, -1)) > 0) {
            boolean bl = valid = valid && giftid == vgTrigger.virtualGiftID;
        }
        if (SystemProperty.getBool(SystemPropertyEntities.MechanicsEngineSettings.REWARD_PROCESSOR_MATCH_FROM_INVENTORY_ENABLED)) {
            valid = valid && this.matchesFromSenderInventory(programData, vgTrigger);
        }
        valid &= programData.matchesVerifiedAccountStatusConstraint(RECEIVER_VERIFIED_ACCOUNT_STATUS, receiverUserData.accountVerified) && programData.matchesVerifiedAccountTypeConstraint(RECEIVER_VERIFIED_ACCOUNT_ENTITY_TYPE, receiverUserData.accountType);
        if (log.isDebugEnabled()) {
            log.debug((Object)(userid + " " + receiverUserData.userID + " " + giftid + " " + vgTrigger.virtualGiftID + " " + valid));
        }
        return valid;
    }

    private boolean matchesFromSenderInventory(RewardProgramData programData, VGSentTrigger vgTrigger) {
        if (programData.hasParameter(IS_FROM_INVENTORY)) {
            return vgTrigger.fromSenderInventory == programData.getBoolParam(IS_FROM_INVENTORY, false);
        }
        return true;
    }
}

