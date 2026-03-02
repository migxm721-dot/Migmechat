/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Properties
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import Ice.Application;
import Ice.Properties;
import com.projectgoth.fusion.bothunter.KickThread;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BotKickerIce
extends Application {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(KickThread.class));
    private static final String ONE = "1";
    private static final String CONFIG_FILE = "BotKicker.cfg";

    public static void main(String[] args) {
        BotKickerIce mbi = new BotKickerIce();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = mbi.main(((Object)((Object)mbi)).getClass().getName(), args, configFile);
        System.exit(status);
    }

    public int run(String[] args) {
        try {
            log.error((Object)"Starting Bot Kicker as an ice app");
            log.error((Object)"");
            this.getSettings(args);
            log.error((Object)("BotKickerIce starting with logging level " + log.getLevel()));
            log.error((Object)("and bot hunter proxy=" + Params.BOT_HUNTER_PROXY));
            KickThread.getInstance(false).run();
            log.error((Object)"BotKicker ice app terminated");
            return 0;
        }
        catch (Exception e) {
            log.error((Object)"Exception occured. BotKicker ice app terminated. ", (Throwable)e);
            return -1;
        }
    }

    private void getSettings(String[] args) {
        Properties p = BotKickerIce.communicator().getProperties();
        Params.BOT_HUNTER_PROXY = p.getProperty("BotHunterProxy");
        if (this.booleanIceProperty(p, "AutoKick")) {
            Params.AUTOKICK = true;
            Params.REGISTRY_PROXY = p.getProperty("RegistryProxy");
        }
        if (this.booleanIceProperty(p, "Verbose") || this.booleanIceProperty(p, "VerboseKick")) {
            log.setLevel(Level.DEBUG);
        }
    }

    private boolean booleanIceProperty(Properties p, String propName) {
        return p.getProperty(propName).equals(ONE);
    }
}

