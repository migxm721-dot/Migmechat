/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.triggers;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;

public class PhotoUploadTrigger
extends RewardProgramTrigger {
    public PhotoUploadTrigger(UserData userData) {
        super(RewardProgramData.TypeEnum.PHOTOS_UPLOADED, userData);
    }
}

