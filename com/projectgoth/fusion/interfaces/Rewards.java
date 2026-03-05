/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.ejb.EJBObject;

public interface Rewards
extends EJBObject {
    public int getRewardScoreCap(int var1, RewardProgramData.CategoryEnum var2) throws RemoteException;

    public HashMap getRewardScoreCap() throws RemoteException;

    public Boolean sendTrigger(RewardProgramTrigger var1, long var2) throws RemoteException;
}

