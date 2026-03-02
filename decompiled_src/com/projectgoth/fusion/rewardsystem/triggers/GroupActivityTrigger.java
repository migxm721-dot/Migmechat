/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class GroupActivityTrigger
extends RewardProgramTrigger {
    public String groupID = "";

    public GroupActivityTrigger(ActivityTypeEnum activityType, UserData userData) {
        super(RewardProgramData.TypeEnum.MANUAL, userData);
        switch (activityType) {
            case TOPIC_CREATED: {
                this.programType = RewardProgramData.TypeEnum.GROUP_TOPIC_CREATED;
                break;
            }
            case TOPIC_COMMENTED: {
                this.programType = RewardProgramData.TypeEnum.GROUP_TOPIC_COMMENT_CREATED;
                break;
            }
            case WALLPOST_CREATED: {
                this.programType = RewardProgramData.TypeEnum.GROUP_WALLPOST_CREATED;
                break;
            }
            case WALLPOST_COMMENTED: {
                this.programType = RewardProgramData.TypeEnum.GROUP_WALLPOST_COMMENT_CREATED;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ActivityTypeEnum {
        TOPIC_CREATED(1),
        TOPIC_COMMENTED(2),
        WALLPOST_CREATED(3),
        WALLPOST_COMMENTED(4);

        private int type;

        private ActivityTypeEnum(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public static boolean isValid(int type) {
            return ActivityTypeEnum.fromType(type) != null;
        }

        public static ActivityTypeEnum fromType(int type) {
            for (ActivityTypeEnum e : ActivityTypeEnum.values()) {
                if (e.type != type) continue;
                return e;
            }
            return null;
        }
    }
}

