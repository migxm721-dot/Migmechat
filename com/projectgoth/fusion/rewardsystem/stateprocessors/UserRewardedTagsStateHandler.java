/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.log4j.Log4JUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.stateprocessors.Bag;
import com.projectgoth.fusion.rewardsystem.stateprocessors.RemainderBag;
import com.projectgoth.fusion.rewardsystem.stateprocessors.RewardProgramStateHandler;
import com.projectgoth.fusion.rewardsystem.stateprocessors.SpecificTriggerTypeStateHandler;
import com.projectgoth.fusion.rewardsystem.stateprocessors.StringBagJSONSerde;
import com.projectgoth.fusion.rewardsystem.triggers.UserRewardedBaseTrigger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class UserRewardedTagsStateHandler
extends SpecificTriggerTypeStateHandler<UserRewardedBaseTrigger> {
    private static final Logger log = Log4JUtils.getLogger(UserRewardedTagsStateHandler.class);

    public UserRewardedTagsStateHandler() {
        super(UserRewardedBaseTrigger.class);
    }

    @Override
    protected RewardProgramStateHandler.PerformReturn performWithSpecificTrigger(RewardProgramData program, UserRewardedBaseTrigger trigger, String currentStateData) {
        Bag<String> expectedRewardedTags = this.extractExpectedRewardedTags(program);
        if (expectedRewardedTags.uniqueItemCount() <= 0) {
            log.error((Object)("RewardProgram id:[" + program.id + "] param:[" + "qlfydRwdPgmPrms" + "] is empty."));
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        Set<String> filteredRewardedTags = this.filterRewardedTags(expectedRewardedTags, trigger.getQualifiedUserRewardProgram());
        if (filteredRewardedTags.isEmpty()) {
            return RewardProgramStateHandler.PerformReturn.NOTHING;
        }
        Bag<String> rewardedTagsState = StringUtil.isBlank(currentStateData) ? new Bag<String>() : StringBagJSONSerde.fromString(currentStateData);
        rewardedTagsState.addAll(filteredRewardedTags);
        RemainderBag<String> remainder = rewardedTagsState.matchAndFilter(expectedRewardedTags);
        return RewardProgramStateHandler.PerformReturn.saveState(remainder.isConsumed(), StringBagJSONSerde.toString(remainder));
    }

    private Set<String> filterRewardedTags(Bag<String> expectedRewardedTags, RewardProgramData qualifiedRewardProgramData) {
        HashSet<String> filteredRewardedTags = new HashSet<String>();
        for (String tag : qualifiedRewardProgramData.getParameterNames()) {
            if (expectedRewardedTags.getCount(tag) <= 0) continue;
            filteredRewardedTags.add(tag);
        }
        return filteredRewardedTags;
    }

    private Bag<String> extractExpectedRewardedTags(RewardProgramData program) {
        List<String> expectedRewardTags = program.getStringListParam("qlfydRwdPgmPrms");
        return new Bag<String>().addAll(expectedRewardTags);
    }

    @Override
    public String getStateKeySuffix() {
        return "rwdedtags";
    }
}

