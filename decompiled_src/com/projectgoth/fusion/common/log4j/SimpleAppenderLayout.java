/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.SimpleLayout
 *  org.apache.log4j.spi.LoggingEvent
 */
package com.projectgoth.fusion.common.log4j;

import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.LoggingEvent;

public class SimpleAppenderLayout
extends SimpleLayout {
    public String format(LoggingEvent event) {
        return event.getRenderedMessage() + LINE_SEP;
    }
}

