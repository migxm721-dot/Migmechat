/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.rewardsystem.RewardProgramParamName;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.List;
import org.apache.log4j.Logger;

public class UserWhitelistRewardProgramProcessor
extends RewardProgramProcessor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UserWhitelistRewardProgramProcessor.class));
    @RewardProgramParamName
    public static final String WHITELISTED_USERNAMES_PARAM_KEY = "whitelistedUsernames";

    protected boolean processInternal(RewardProgramData programData, RewardProgramTrigger trigger) {
        List<String> whitelistedUsernames = programData.getStringListParam(WHITELISTED_USERNAMES_PARAM_KEY);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Whitelisted users: " + whitelistedUsernames));
        }
        boolean passedWhiteListCheck = true;
        if (whitelistedUsernames.size() != 0) {
            try {
                User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                String triggerUsername = userBean.getUsernameByUserid(trigger.userData.userID, null);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Trigger username [" + triggerUsername + "]"));
                }
                if (!whitelistedUsernames.contains(triggerUsername)) {
                    passedWhiteListCheck = false;
                }
            }
            catch (Exception e) {
                log.error((Object)("Unable to retrieve username for trigger [" + trigger.programType + "] for user [" + trigger.userData.userID + "]"));
                passedWhiteListCheck = false;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("passedWhiteListCheck [" + whitelistedUsernames + "][" + passedWhiteListCheck + "]"));
        }
        return passedWhiteListCheck;
    }
}

