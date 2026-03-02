/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class ThirdPartyAppInternalInvitationTrigger
extends RewardProgramTrigger {
    public String applicationName = "";

    public ThirdPartyAppInternalInvitationTrigger(StateEnum state, UserData userData) {
        super(RewardProgramData.TypeEnum.MANUAL, userData);
        switch (state) {
            case SENT: {
                this.programType = RewardProgramData.TypeEnum.THIRDPARTY_APP_INTERNAL_INVITATION_SENT;
                break;
            }
            case ACCEPTED: {
                this.programType = RewardProgramData.TypeEnum.THIRDPARTY_APP_INTERNAL_INVITATION_ACCEPTED;
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum StateEnum {
        SENT(1),
        ACCEPTED(2);

        private int type;

        private StateEnum(int type) {
            this.type = type;
        }

        public int getType() {
            return this.type;
        }

        public static boolean isValid(int type) {
            return StateEnum.fromType(type) != null;
        }

        public static StateEnum fromType(int type) {
            for (StateEnum e : StateEnum.values()) {
                if (e.type != type) continue;
                return e;
            }
            return null;
        }
    }
}

