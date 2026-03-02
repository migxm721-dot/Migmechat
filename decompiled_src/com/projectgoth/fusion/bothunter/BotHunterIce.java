/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Application
 *  Ice.Object
 *  Ice.ObjectAdapter
 *  Ice.Properties
 *  Ice.Util
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import Ice.Application;
import Ice.Object;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.Util;
import com.projectgoth.fusion.bothunter.AnalysisThread;
import com.projectgoth.fusion.bothunter.BotHunter;
import com.projectgoth.fusion.bothunter.BotHunterAdminI;
import com.projectgoth.fusion.bothunter.BotHunterI;
import com.projectgoth.fusion.bothunter.KickThread;
import com.projectgoth.fusion.bothunter.MonitorThread;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BotHunterIce
extends Application {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotHunterIce.class));
    private static final String ONE = "1";
    private static final String CONFIG_FILE = "BotHunter.cfg";
    private ObjectAdapter bhAdaptor;
    private ObjectAdapter adminAdaptor;

    public static void main(String[] args) {
        BotHunterIce mbi = new BotHunterIce();
        String configFile = args.length > 0 ? args[0] : CONFIG_FILE;
        int status = mbi.main(((java.lang.Object)((java.lang.Object)mbi)).getClass().getName(), args, configFile);
        System.exit(status);
    }

    public int run(String[] args) {
        try {
            log.error((java.lang.Object)"Starting Bot Hunter as an ice app");
            log.error((java.lang.Object)"");
            this.bhAdaptor = BotHunterIce.communicator().createObjectAdapter("BotHunterAdapter");
            this.bhAdaptor.add((Object)new BotHunterI(), Util.stringToIdentity((String)"BotHunter"));
            this.bhAdaptor.activate();
            this.adminAdaptor = BotHunterIce.communicator().createObjectAdapter("AdminAdapter");
            this.adminAdaptor.add((Object)new BotHunterAdminI(), Util.stringToIdentity((String)"BotHunterAdmin"));
            this.adminAdaptor.activate();
            this.getSettings(args);
            String[] argsMinusFirst = new String[args.length - 1];
            System.arraycopy(args, 1, argsMinusFirst, 0, args.length - 1);
            BotHunter.mainInner(argsMinusFirst, false);
            log.error((java.lang.Object)"Server terminated");
            return 0;
        }
        catch (Exception e) {
            log.error((java.lang.Object)"Exception occured. Server terminated. ", (Throwable)e);
            return -1;
        }
    }

    private void getSettings(String[] args) {
        Properties p = BotHunterIce.communicator().getProperties();
        if (this.booleanIceProperty(p, "AnalyseRatios")) {
            Params.RATIO_ANALYSIS = true;
        }
        if (this.booleanIceProperty(p, "AnalyseSequence")) {
            Params.SEQUENCE_ANALYSIS = true;
        }
        Params.ANALYSIS_THREADS = p.getPropertyAsIntWithDefault("AnalysisThreadCount", Params.ANALYSIS_THREADS);
        if (this.booleanIceProperty(p, "AutoKick")) {
            Params.AUTOKICK = true;
            Params.REGISTRY_PROXY = p.getProperty("RegistryProxy");
        }
        Params.MIN_ANALYSIS_PACKETS_PER_SOCKET = p.getPropertyAsIntWithDefault("MinAnalysisPackets", Params.MIN_ANALYSIS_PACKETS_PER_SOCKET);
        Params.MIN_INTERLEAVE_TRANSITIONS = p.getPropertyAsIntWithDefault("Mitc", Params.MIN_INTERLEAVE_TRANSITIONS);
        Params.PACKET_SOURCE = p.getPropertyWithDefault("PacketSource", null);
        Params.PCAP_FILTER = p.getPropertyWithDefault("PcapFilter", Params.PCAP_FILTER);
        Params.STATS_INTERVAL_SECONDS = p.getPropertyAsIntWithDefault("StatsInterval", Params.STATS_INTERVAL_SECONDS);
        if (this.booleanIceProperty(p, "VerboseAnalysis")) {
            Logger analysisLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(AnalysisThread.class));
            analysisLogger.setLevel(Level.DEBUG);
        }
        if (this.booleanIceProperty(p, "VerboseKick")) {
            Logger kickLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(KickThread.class));
            kickLogger.setLevel(Level.DEBUG);
        }
        if (this.booleanIceProperty(p, "VerbosePacketCapture")) {
            Logger monitorLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(MonitorThread.class));
            monitorLogger.setLevel(Level.DEBUG);
        }
    }

    private boolean booleanIceProperty(Properties p, String propName) {
        return p.getProperty(propName).equals(ONE);
    }
}

