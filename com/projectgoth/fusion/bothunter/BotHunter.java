/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.CommandLine
 *  org.apache.commons.cli.GnuParser
 *  org.apache.commons.cli.HelpFormatter
 *  org.apache.commons.cli.Option
 *  org.apache.commons.cli.OptionBuilder
 *  org.apache.commons.cli.Options
 *  org.apache.log4j.Appender
 *  org.apache.log4j.ConsoleAppender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.PatternLayout
 *  org.apache.log4j.Priority
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.AnalysisThread;
import com.projectgoth.fusion.bothunter.KickThread;
import com.projectgoth.fusion.bothunter.MonitorThread;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.bothunter.StatsCollector;
import com.projectgoth.fusion.bothunter.ValidFusionPackets;
import com.projectgoth.fusion.common.ConfigUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

public class BotHunter {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotHunter.class));
    private static Options options;
    private static CommandLine line;

    public static void main(String[] args) {
        BotHunter.initializeLog4jForConsoleApp();
        BotHunter.mainInner(args, true);
    }

    private static void initializeLog4jForConsoleApp() {
        ConsoleAppender console = new ConsoleAppender();
        String PATTERN = "%d [%p|%C{1}] %m%n";
        console.setLayout((Layout)new PatternLayout(PATTERN));
        console.setThreshold((Priority)Level.DEBUG);
        console.activateOptions();
        Logger.getRootLogger().addAppender((Appender)console);
        Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void mainInner(String[] args, boolean isConsoleApp) {
        try {
            ValidFusionPackets.getInstance();
            BotHunter.createOptions();
            if (isConsoleApp) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Bot Hunter", options);
                System.out.println("");
                System.out.println("If no analysis options specified, defaults to all");
            }
            BotHunter.extractArgs(args);
            if (Params.PACKET_SOURCE == null) {
                log.error((Object)"Please specify a NIC or log file");
                return;
            }
            StatsCollector.getInstance().setInstanceStats(Params.STATS_INTERVAL_SECONDS);
            BotHunter.startThreads(isConsoleApp);
        }
        catch (Exception e) {
            log.error((Object)"Exception:", (Throwable)e);
        }
    }

    private static void createOptions() {
        Option invalidPacketTypeDetectMode = new Option("InvalidFusionPacketTypeDetectionMode", "run in invalid-fusion-packet-type detection mode [default]");
        Option invalidPacketDetectMode = new Option("InvalidFusionPacketDetectionMode", "run in invalid-fusion-packet detection mode");
        Option botDetectMode = new Option("BotDetectionMode", "run in bot detection mode");
        OptionBuilder.withArgName((String)"invalid packet threshold");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"number of invalid fusion packets from a given IP after which to trigger ban script");
        Option invalidPacketThreshold = OptionBuilder.create((String)"InvalidFusionPacketThreshold");
        Option invalidPacketCommand = new Option("InvalidFusionPacketShellCommand", "the shell command to run when the invalid-fusion-packet thresholds are exceeded");
        Option invalidPacketLimitUsers = new Option("InvalidFusionPacketLimitUsers", "the maximum number of users on an ip for which to run the invalid-fusion-packet shell command");
        Option autokick = new Option("AutoKick", "(by default off, bot hunter just reports suspects)");
        Option verbose = new Option("Verbose", "all verbose logging");
        Option verboseLogins = new Option("VerbosePacketCapture", "verbose display packet capture only");
        Option verboseAnalysis = new Option("VerboseAnalysis", "verbose analysis only");
        Option verboseKick = new Option("VerboseKick", "verbose kick processing only");
        Option ar = new Option("AnalyseRatios", "timestamp ratio analysis");
        Option as = new Option("AnalyseSequence", "sequence analysis");
        OptionBuilder.withArgName((String)"nic or logfile");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"nic or logfile from which to read the packets");
        Option packetSource = OptionBuilder.create((String)"PacketSource");
        OptionBuilder.withArgName((String)"ip");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"fusion registry IP address (only applicable if autokick on)");
        Option regIP = OptionBuilder.create((String)"RegistryIp");
        OptionBuilder.withArgName((String)"port");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"fusion registry port (only applicable if autokick on)");
        Option regPort = OptionBuilder.create((String)"RegistryPort");
        OptionBuilder.withArgName((String)"transition count");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("minimum interleave transition count (default=" + Params.MIN_INTERLEAVE_TRANSITIONS + ")"));
        Option mitc = OptionBuilder.create((String)"Mitc");
        OptionBuilder.withArgName((String)"packets");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("minimum analysis packets per socket (default=" + Params.MIN_ANALYSIS_PACKETS_PER_SOCKET + ")"));
        Option map = OptionBuilder.create((String)"MinAnalysisPackets");
        OptionBuilder.withArgName((String)"threads");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"number of analysis threads to create");
        Option analysisThreads = OptionBuilder.create((String)"AnalysisThreadCount");
        OptionBuilder.withArgName((String)"filter");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"pcap filter (if in live capture mode)");
        Option pcapFilter = OptionBuilder.create((String)"PcapFilter");
        OptionBuilder.withArgName((String)"interval in seconds");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)"interval at which to make stats available to monitor");
        Option statsInterval = OptionBuilder.create((String)"StatsInterval");
        OptionBuilder.withArgName((String)"percentage agreement");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("The percentage to within which two mean(tcpTimestamp/arrivalTime) values from two client sockets should agree for them to be labelled as suspect (default " + Params.ANALYSE_RATIOS_SENSITIVITY + ")"));
        Option analyseRatiosSensitivity = OptionBuilder.create((String)"AnalyseRatiosSensitivity");
        OptionBuilder.withArgName((String)"packets");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("the maximum number of packets from a client socket to cache before deleting the oldest (default=" + Params.MAX_PACKETS_PER_SOCKET + ")"));
        Option maxPacketsPerSocket = OptionBuilder.create((String)"MaxPacketsPerSocket");
        OptionBuilder.withArgName((String)"seconds");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("length of time after which to timeout all packets from a given client IP if no further traffic has been heard from that IP (default=" + Params.CLIENT_IP_TIMEOUT_SECS + ")"));
        Option clientIpTimeoutSecs = OptionBuilder.create((String)"ClientIpTimeout");
        OptionBuilder.withArgName((String)"seconds");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("length of time after which to timeout all packets from a given client port if no further traffic has been heard from that port (default=" + Params.CLIENT_PORT_TIMEOUT_SECS + ")"));
        Option clientPortTimeoutSecs = OptionBuilder.create((String)"ClientPortTimeout");
        OptionBuilder.withArgName((String)"seconds");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription((String)("length of time between reporting (and optionally autokicking) suspects on a given client IP if no change has no occurred in the suspect set (default=" + Params.DUPLICATE_REPORT_INTERVAL_SECS + ")"));
        Option duplicateReportIntervalSecs = OptionBuilder.create((String)"DuplicateReportInterval");
        options = new Options();
        options.addOption(invalidPacketTypeDetectMode);
        options.addOption(invalidPacketDetectMode);
        options.addOption(botDetectMode);
        options.addOption(invalidPacketThreshold);
        options.addOption(autokick);
        options.addOption(verbose);
        options.addOption(verboseLogins);
        options.addOption(verboseAnalysis);
        options.addOption(verboseKick);
        options.addOption(ar);
        options.addOption(as);
        options.addOption(packetSource);
        options.addOption(regIP);
        options.addOption(regPort);
        options.addOption(mitc);
        options.addOption(map);
        options.addOption(analysisThreads);
        options.addOption(pcapFilter);
        options.addOption(analyseRatiosSensitivity);
        options.addOption(maxPacketsPerSocket);
        options.addOption(clientIpTimeoutSecs);
        options.addOption(clientPortTimeoutSecs);
        options.addOption(duplicateReportIntervalSecs);
    }

    private static void extractArgs(String[] args) throws Exception {
        GnuParser parser = new GnuParser();
        line = parser.parse(options, args);
        boolean analysisOptionsGiven = false;
        Params.SEQUENCE_ANALYSIS = false;
        Params.RATIO_ANALYSIS = false;
        if (line.hasOption("InvalidFusionPacketTypeDetectionMode")) {
            Params.MODE = Params.Mode.INVALID_FUSION_PACKET_TYPE_DETECTION;
        }
        if (line.hasOption("InvalidFusionPacketDetectionMode")) {
            Params.MODE = Params.Mode.INVALID_FUSION_PACKET_DETECTION;
        }
        if (line.hasOption("BotDetectionMode")) {
            Params.MODE = Params.Mode.BOT_DETECTION;
        }
        if (line.hasOption("InvalidFusionPacketThreshold")) {
            Params.INVALID_FUSION_PACKET_THRESHOLD = Integer.parseInt(line.getOptionValue("InvalidFusionPacketThreshold"));
        }
        if (line.hasOption("InvalidFusionPacketLimitUsers")) {
            Params.INVALID_FUSION_PACKET_LIMIT_USERS = Integer.parseInt(line.getOptionValue("InvalidFusionPacketLimitUsers"));
        }
        if (line.hasOption("InvalidFusionPacketShellCommand")) {
            Params.INVALID_FUSION_PACKET_SHELL_COMMAND = line.getOptionValue("InvalidFusionPacketShellCommand");
        }
        if (line.hasOption("PacketSource")) {
            Params.PACKET_SOURCE = line.getOptionValue("PacketSource", null);
        }
        if (line.hasOption("AutoKick")) {
            Params.AUTOKICK = true;
            String ip = line.getOptionValue("RegistryIp");
            String port = line.getOptionValue("RegistryPort");
            Params.REGISTRY_PROXY = "RegistryAdmin: tcp -h " + ip + " -p " + port;
        }
        if (line.hasOption("Verbose")) {
            Logger.getRootLogger().setLevel(Level.DEBUG);
        }
        if (line.hasOption("VerbosePacketCapture")) {
            Logger monitorLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(MonitorThread.class));
            monitorLogger.setLevel(Level.DEBUG);
        }
        if (line.hasOption("VerboseAnalysis")) {
            Logger analysisLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(AnalysisThread.class));
            analysisLogger.setLevel(Level.DEBUG);
        }
        if (line.hasOption("VerboseKick")) {
            Logger kickLogger = Logger.getLogger((String)ConfigUtils.getLoggerName(KickThread.class));
            kickLogger.setLevel(Level.DEBUG);
        }
        if (line.hasOption("AnalyseSequence")) {
            Params.SEQUENCE_ANALYSIS = true;
            analysisOptionsGiven = true;
        }
        if (line.hasOption("AnalyseRatios")) {
            Params.RATIO_ANALYSIS = true;
            analysisOptionsGiven = true;
        }
        if (line.hasOption("Mitc")) {
            String sTransitions = line.getOptionValue("Mitc");
            Params.MIN_INTERLEAVE_TRANSITIONS = Integer.parseInt(sTransitions);
        }
        if (line.hasOption("Mitc")) {
            String sMinPkts = line.getOptionValue("Mitc");
            Params.MIN_ANALYSIS_PACKETS_PER_SOCKET = Integer.parseInt(sMinPkts);
        }
        if (line.hasOption("AnalysisThreadCount")) {
            String threads = line.getOptionValue("AnalysisThreadCount");
            Params.ANALYSIS_THREADS = Integer.parseInt(threads);
        }
        if (line.hasOption("PcapFilter")) {
            Params.PCAP_FILTER = line.getOptionValue("PcapFilter");
        }
        if (line.hasOption("AnalyseRatiosSensitivity")) {
            Params.ANALYSE_RATIOS_SENSITIVITY = Double.parseDouble(line.getOptionValue("AnalyseRatiosSensitivity"));
        }
        if (!analysisOptionsGiven) {
            Params.SEQUENCE_ANALYSIS = true;
            Params.RATIO_ANALYSIS = true;
        }
    }

    private static void startThreads(boolean isConsoleApp) {
        if (isConsoleApp) {
            Thread kick = new Thread(KickThread.getInstance(true));
            kick.setPriority(1);
            kick.start();
        }
        new MonitorThread(Params.PACKET_SOURCE, Params.PCAP_FILTER, Params.ANALYSIS_THREADS, isConsoleApp).monitor();
    }
}

