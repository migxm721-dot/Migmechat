/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class DataUtils {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DataUtils.class));
    public static final int MAX_MOBILE_DEVICE_LENGTH = 128;
    public static final int MAX_USERAGENT_LENGTH = 128;
    private static final Pattern USERAGENT_PATTERN = Pattern.compile("(.+)(mig33/.*)");

    public static String truncateMobileDevice(String mobileDevice) {
        return DataUtils.truncateMobileDevice(mobileDevice, false, null);
    }

    public static String truncateMobileDevice(String mobileDevice, boolean logTruncation, String logContext) {
        if (mobileDevice != null && mobileDevice.length() > 128) {
            String newValue = mobileDevice.substring(0, 128);
            if (logTruncation) {
                String logMsg = String.format("%s: mobileDevice value longer than %d, truncating it from '%s' to '%s'", logContext, 128, mobileDevice, newValue);
                log.info((Object)logMsg);
            }
            return newValue;
        }
        return mobileDevice;
    }

    public static String truncateUserAgent(String userAgent) {
        return DataUtils.truncateUserAgent(userAgent, false, null);
    }

    public static String truncateUserAgent(String userAgent, boolean logTruncation, String logContext) {
        if (userAgent != null && userAgent.length() > 128) {
            Matcher m;
            String newValue = userAgent.substring(0, 128);
            if (!userAgent.startsWith("mig33") && (m = USERAGENT_PATTERN.matcher(userAgent)).matches()) {
                newValue = m.group(2).length() > 128 ? m.group(2).substring(0, 128) : (m.group(2).length() >= 123 ? " ... ".substring(5 - (128 - m.group(2).length()), 5) + m.group(2) : m.group(1).substring(0, 123 - m.group(2).length()) + " ... " + m.group(2));
            }
            if (logTruncation) {
                String logMsg = String.format("%s: userAgent value longer than %d, truncating it from '%s' to '%s'", logContext, 128, userAgent, newValue);
                log.info((Object)logMsg);
            }
            return newValue;
        }
        return userAgent;
    }
}

