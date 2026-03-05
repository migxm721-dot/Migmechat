/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.ice;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.FusionException;
import org.apache.log4j.Logger;

public abstract class IceAmdInvoker
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(IceAmdInvoker.class));

    public abstract boolean isAMDEnabled();

    public abstract void schedule(Runnable var1) throws Exception;

    public abstract void payload() throws Exception;

    public abstract void ice_response();

    public abstract void ice_exception(Exception var1);

    public abstract String getLogContext();

    public void invoke() throws FusionException {
        if (this.isAMDEnabled()) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Scheduling (AMD)..." + this.getLogContext()));
            }
            try {
                this.schedule(this);
            }
            catch (Exception e) {
                log.error((Object)("Exception scheduling AMD job: " + e), (Throwable)e);
                throw new FusionException("Exception scheduling AMD job: " + e);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Executing (non-AMD)..." + this.getLogContext()));
            }
            this.run();
        }
    }

    public void run() {
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Executing..." + this.getLogContext()));
            }
            this.payload();
            this.ice_response();
        }
        catch (Exception e) {
            this.ice_exception(e);
        }
    }
}

