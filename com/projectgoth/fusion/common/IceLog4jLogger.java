/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Logger
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import org.apache.log4j.Logger;

public class IceLog4jLogger
implements Ice.Logger {
    private static final Logger log = Logger.getLogger(IceLog4jLogger.class);

    public void error(String message) {
        log.error((Object)message);
    }

    public void print(String message) {
        log.info((Object)message);
    }

    public void trace(String category, String message) {
        log.debug((Object)(category + ": " + message));
    }

    public void warning(String message) {
        log.warn((Object)message);
    }
}

