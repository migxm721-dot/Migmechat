/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.TTCCLayout
 */
package com.projectgoth.fusion.common;

import org.apache.log4j.TTCCLayout;

public class NoStackTracePatternTTCCLayout
extends TTCCLayout {
    public boolean ignoresThrowable() {
        return false;
    }
}

