/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface RewardsLocal
extends EJBLocalObject {
    public int getRewardScoreCap(int var1, RewardProgramData.CategoryEnum var2) throws EJBException;

    public HashMap getRewardScoreCap() throws EJBException;

    public Boolean sendTrigger(RewardProgramTrigger var1, long var2);
}

