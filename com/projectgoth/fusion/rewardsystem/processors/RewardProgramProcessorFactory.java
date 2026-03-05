/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.processors;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.data.RewardProgramProcessorMappingData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.rewardsystem.processors.RewardProgramProcessor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RewardProgramProcessorFactory {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RewardProgramProcessorFactory.class));
    private static Map<RewardProgramData.TypeEnum, List<RewardProgramProcessor>> procInstanceMap = new ConcurrentHashMap<RewardProgramData.TypeEnum, List<RewardProgramProcessor>>();
    private static Semaphore semaphore = new Semaphore(1);
    private static long lastUpdated;
    private static final int LOCAL_CACHE_TIME_IN_MS = 60000;
    private static boolean refreshPeriodically;

    public static void setRewardProgramProcessorMap(Map<RewardProgramData.TypeEnum, List<RewardProgramProcessor>> map) {
        refreshPeriodically = false;
        procInstanceMap = map;
    }

    public static void resetProgramProcessorMap() {
        refreshPeriodically = true;
        lastUpdated = 0L;
        procInstanceMap.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadProcessors() {
        if (lastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            try {
                Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                List mapping = contentEJB.getRewardProgramProcessorMapping();
                if (mapping != null) {
                    log.debug((Object)("Found [" + mapping.size() + "] mappings from contentBean"));
                    for (RewardProgramProcessorMappingData m : mapping) {
                        RewardProgramData.TypeEnum type = m.getProgramType();
                        log.debug((Object)("Preparing processor list for type [" + type + "]"));
                        LinkedList<RewardProgramProcessor> processors = new LinkedList<RewardProgramProcessor>();
                        log.debug((Object)("Found processor list: " + m.getProcessorList()));
                        for (String clazzName : m.getProcessorList()) {
                            RewardProgramProcessor p = RewardProgramProcessorFactory.instantiate(clazzName);
                            log.debug((Object)("Adding :" + p));
                            if (p == null) continue;
                            processors.add(p);
                        }
                        log.debug((Object)("Completed adding [" + processors.size() + "] processors for [" + type + "]"));
                        procInstanceMap.put(type, processors);
                    }
                }
                lastUpdated = System.currentTimeMillis();
            }
            catch (Exception e) {
                log.error((Object)"Unable to load reward program processors", (Throwable)e);
                Object var10_11 = null;
                semaphore.release();
            }
            Object var10_10 = null;
            semaphore.release();
        }
        catch (Throwable throwable) {
            Object var10_12 = null;
            semaphore.release();
            throw throwable;
        }
    }

    public static synchronized List<RewardProgramProcessor> getProcessorsForProgramType(RewardProgramData.TypeEnum type) {
        List<RewardProgramProcessor> list;
        log.debug((Object)("refreshPeriodically[" + refreshPeriodically + "] lastUpdated[" + lastUpdated + "] "));
        if (refreshPeriodically && System.currentTimeMillis() - lastUpdated > 60000L) {
            log.debug((Object)"Reloading from the database");
            RewardProgramProcessorFactory.loadProcessors();
        }
        if ((list = procInstanceMap.get(type)) == null) {
            log.debug((Object)("no processors configured for [" + type + "] returning an empty list"));
            list = new LinkedList<RewardProgramProcessor>();
        }
        return list;
    }

    private static RewardProgramProcessor instantiate(String className) {
        try {
            return (RewardProgramProcessor)RewardProgramProcessor.class.cast(Class.forName(className).newInstance());
        }
        catch (Exception e) {
            log.error((Object)("Unable to load rewardprogramprocessor [" + className + "] :" + e.getMessage()), (Throwable)e);
            return null;
        }
    }

    static {
        refreshPeriodically = true;
    }
}

