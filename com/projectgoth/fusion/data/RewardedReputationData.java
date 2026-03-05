/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.userreward.ReputationScoreSourceTypeEnum
 */
package com.projectgoth.fusion.data;

import com.projectgoth.leto.common.event.userreward.ReputationScoreSourceTypeEnum;
import java.io.Serializable;

public class RewardedReputationData
implements Serializable {
    private final int oldScore;
    private final int newScore;
    private final int oldLevel;
    private final int newLevel;
    private final ReputationRewardSourceEnum source;

    public RewardedReputationData(ReputationRewardSourceEnum source, int oldScore, int oldLevel, int newScore, int newLevel) {
        this.source = source;
        this.oldScore = oldScore;
        this.oldLevel = oldLevel;
        this.newScore = newScore;
        this.newLevel = newLevel;
    }

    public int getOldScore() {
        return this.oldScore;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }

    public int getNewScore() {
        return this.newScore;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public ReputationRewardSourceEnum getSource() {
        return this.source;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RewardedReputationData [source=");
        builder.append((Object)this.source);
        builder.append(", oldScore=");
        builder.append(this.oldScore);
        builder.append(", oldLevel=");
        builder.append(this.oldLevel);
        builder.append(", newScore=");
        builder.append(this.newScore);
        builder.append(", newLevel=");
        builder.append(this.newLevel);
        builder.append("]");
        return builder.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ReputationRewardSourceEnum {
        GIVE_SCORE_REWARD(ReputationScoreSourceTypeEnum.GIVE_SCORE_REWARD),
        GIVE_LEVEL_REWARD(ReputationScoreSourceTypeEnum.GIVE_LEVEL_REWARD);

        private final ReputationScoreSourceTypeEnum sourceType;

        private ReputationRewardSourceEnum(ReputationScoreSourceTypeEnum sourceType) {
            this.sourceType = sourceType;
        }

        public int getTypeID() {
            return this.sourceType.getTypeId();
        }

        public ReputationScoreSourceTypeEnum toReputationScoreSourceTypeEnum() {
            return this.sourceType;
        }
    }
}

