/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.CommunicatorDestroyedException
 *  Ice.Current
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.CommunicatorDestroyedException;
import Ice.Current;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.objectcache.ObjectCache;
import com.projectgoth.fusion.objectcache.ObjectCacheContext;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ObjectCacheStats;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice._ObjectCacheAdminDisp;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class ObjectCacheAdminI
extends _ObjectCacheAdminDisp {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCacheAdminI.class));
    private ObjectCacheContext applicationContext;
    private RegistryNotificationTask registryNotificationTask;

    public ObjectCacheAdminI(ObjectCacheContext ctx) {
        this.applicationContext = ctx;
    }

    public void startTimer() {
        long registryNotificationInterval = this.applicationContext.getProperties().getPropertyAsIntWithDefault("RegistryNotificationInterval", 1) * 1000;
        this.registryNotificationTask = new RegistryNotificationTask();
        Timer timer = new Timer(true);
        timer.schedule((TimerTask)this.registryNotificationTask, registryNotificationInterval, registryNotificationInterval);
    }

    public int ping(Current __current) {
        int currentLoad = this.applicationContext.getObjectCache().getUserCount();
        return currentLoad;
    }

    public ObjectCacheStats getStats(Current __current) throws FusionException {
        ObjectCacheStats stats = ServiceStatsFactory.getObjectCacheStats(ObjectCache.hostName, ObjectCache.startTime);
        try {
            this.applicationContext.getObjectCache().getStats(stats);
        }
        catch (Exception e) {
            log.warn((Object)("Invalid Stats: " + e));
            FusionException fe = new FusionException();
            fe.message = "Initialisation incomplete";
            throw fe;
        }
        return stats;
    }

    public String[] getUsernames(Current __current) {
        return this.applicationContext.getObjectCache().getUsernames();
    }

    public void reloadEmotes(Current __current) {
        throw new UnsupportedOperationException("This operation has been deprecated");
    }

    public void setLoadWeightage(int weightage, Current __current) {
        this.applicationContext.getObjectCache().setLoadWeightage(weightage);
    }

    public int getLoadWeightage(Current __current) {
        return this.applicationContext.getObjectCache().getLoadWeightage();
    }

    private class RegistryNotificationTask
    extends TimerTask {
        private RegistryNotificationTask() {
        }

        public void run() {
            try {
                ObjectCacheAdminI.this.applicationContext.getRegistryPrx().registerObjectCacheStats(ObjectCacheAdminI.this.applicationContext.getUniqueID(), ObjectCacheAdminI.this.getStats());
            }
            catch (ObjectNotFoundException e) {
                log.warn((Object)"ObjectCache not currently registered with Registry");
            }
            catch (FusionException e) {
                log.warn((Object)"ObjectCacheStats not initialized");
            }
            catch (CommunicatorDestroyedException e) {
                log.warn((Object)"Unable to register stats with registry due to communicator shutdown");
            }
            catch (Exception e) {
                log.error((Object)"Unable to register stats with registry", (Throwable)e);
            }
            catch (Throwable t) {
                log.error((Object)("Unhandled timer exception:" + t), t);
            }
        }
    }
}

