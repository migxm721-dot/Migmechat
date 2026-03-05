/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.Logger
 *  Ice.LoggerPlugin
 *  Ice.Plugin
 *  Ice.PluginFactory
 */
package com.projectgoth.fusion.common;

import Ice.Communicator;
import Ice.Logger;
import Ice.LoggerPlugin;
import Ice.Plugin;
import Ice.PluginFactory;
import com.projectgoth.fusion.common.IceLog4jLogger;

public class Log4jLoggerPluginFactory
implements PluginFactory {
    public Plugin create(Communicator communicator, String name, String[] args) {
        IceLog4jLogger logger = new IceLog4jLogger();
        return new LoggerPlugin(communicator, (Logger)logger);
    }
}

