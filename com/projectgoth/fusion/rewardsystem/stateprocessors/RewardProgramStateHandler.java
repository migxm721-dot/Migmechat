/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public abstract class RewardProgramStateHandler {
    public abstract PerformReturn perform(RewardProgramData var1, RewardProgramTrigger var2, String var3);

    public abstract String getStateKeySuffix();

    public static class PerformReturn {
        public final boolean save;
        public final boolean cont;
        public final String newState;
        public static final PerformReturn NOTHING = new PerformReturn(false, false, null);

        private PerformReturn(boolean save, boolean cont, String newState) {
            this.save = save;
            this.cont = cont;
            this.newState = newState;
        }

        public static PerformReturn saveState(boolean cont, String state) {
            return new PerformReturn(true, cont, state);
        }
    }
}

