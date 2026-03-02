/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 */
package com.projectgoth.fusion.smsengine;

import Ice.Current;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.SMSEngineStats;
import com.projectgoth.fusion.slice._SMSEngineAdminDisp;
import com.projectgoth.fusion.smsengine.SMSEngine;

public class SMSEngineAdminI
extends _SMSEngineAdminDisp {
    private SMSEngine smsEngine;

    public SMSEngineAdminI(SMSEngine smsEngine) {
        this.smsEngine = smsEngine;
    }

    public SMSEngineStats getStats(Current __current) throws FusionException {
        try {
            return this.smsEngine.getStats();
        }
        catch (Exception e) {
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
    }
}

