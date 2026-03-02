/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem;

import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.RewardProgramsImpl;
import com.projectgoth.fusion.rewardsystem.RewardProgramsOld;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RewardPrograms {
    public void loadPrograms();

    public void add(RewardProgramData var1);

    public void resetCachedProperties();

    public Collection<RewardProgramData> getAll();

    public RewardProgramData get(int var1);

    @Deprecated
    public List<RewardProgramData> get(RewardProgramData.TypeEnum var1);

    public RewardProgramDataList getRewardPrograms(RewardProgramData.TypeEnum var1);

    public static class Instance {
        private final RewardPrograms rewardProgramsOld = new RewardProgramsOld();
        private final RewardPrograms rewardPrograms = new RewardProgramsImpl();

        private RewardPrograms _get() {
            if (SystemPropertyEntities.Temp.Cache.se218Enabled.getValue().booleanValue()) {
                return this.rewardPrograms;
            }
            return this.rewardProgramsOld;
        }

        public static final RewardPrograms get() {
            return InstanceHolder.instance._get();
        }

        public static class InstanceHolder {
            public static final Instance instance = new Instance();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class RewardProgramDataList {
        private final List<RewardProgramData> rewardPrograms;
        private final boolean needsToCheckUserReputation;

        public RewardProgramDataList(List<RewardProgramData> rewardPrograms, boolean needsToCheckUserReputation) {
            this.rewardPrograms = Collections.unmodifiableList(rewardPrograms);
            this.needsToCheckUserReputation = needsToCheckUserReputation;
        }

        public List<RewardProgramData> getRewardPrograms() {
            return this.rewardPrograms;
        }

        public boolean needsToCheckUserReputation() {
            return this.needsToCheckUserReputation;
        }
    }
}

