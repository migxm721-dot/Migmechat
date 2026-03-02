/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common.metrics;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.metrics.MetricsEnums;
import org.apache.log4j.Logger;

public class MetricsLogger {
    static final Logger metricsLog = Logger.getLogger((String)"MetricsLog");
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MetricsLogger.class));
    static final String SEPARATOR_CHARACTER = ",";

    public static void log(MetricsEnums.MetricsEntryInterface metricsEntry, String eventID, Object value) {
        try {
            boolean isMetricEntryEnabled = SystemProperty.getBool(new SystemPropertyEntities.MetricsLoggingEnabled(metricsEntry));
            if (isMetricEntryEnabled) {
                metricsLog.info((Object)((Object)((Object)metricsEntry.getScope()) + SEPARATOR_CHARACTER + metricsEntry.getEventName() + SEPARATOR_CHARACTER + eventID + SEPARATOR_CHARACTER + metricsEntry.getMetricName() + SEPARATOR_CHARACTER + (Object)((Object)metricsEntry.getMetricType()) + SEPARATOR_CHARACTER + value));
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to log metrics :" + e.getMessage()), (Throwable)e);
        }
    }
}

