/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Log4JUtils {
    public static void log(Logger logger, Level logLevel, Object logObject) {
        Log4JUtils.log(logger, logLevel, logObject, null);
    }

    public static void log(Logger logger, Level logLevel, Object logObject, Throwable t) {
        int logLevelInt = logLevel == null ? 20000 : logLevel.toInt();
        switch (logLevelInt) {
            case 10000: {
                if (t != null) {
                    logger.debug(logObject, t);
                    break;
                }
                logger.debug(logObject);
                break;
            }
            case 30000: {
                if (t != null) {
                    logger.warn(logObject, t);
                    break;
                }
                logger.warn(logObject);
                break;
            }
            case 40000: {
                if (t != null) {
                    logger.error(logObject, t);
                    break;
                }
                logger.error(logObject);
                break;
            }
            case 50000: {
                if (t != null) {
                    logger.fatal(logObject, t);
                    break;
                }
                logger.fatal(logObject);
                break;
            }
            default: {
                if (t != null) {
                    logger.info(logObject, t);
                    break;
                }
                logger.info(logObject);
            }
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger((String)ConfigUtils.getLoggerName(clazz));
    }
}

