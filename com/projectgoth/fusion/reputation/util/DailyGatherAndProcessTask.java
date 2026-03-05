/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.util;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.reputation.ReputationServiceI;
import com.projectgoth.fusion.slice.FusionException;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class DailyGatherAndProcessTask
extends TimerTask {
    private Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DailyGatherAndProcessTask.class));
    private ReputationServiceI reputationService;

    public DailyGatherAndProcessTask(ReputationServiceI reputationService) {
        this.reputationService = reputationService;
    }

    public void run() {
        try {
            this.reputationService.gatherAndProcess();
        }
        catch (FusionException e) {
            this.log.error((Object)"failed to do daily gather and process!", (Throwable)((Object)e));
        }
    }
}

