/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.bothunter;

public class Params {
    public static final String BOT_DETECTION_MODE_PARAM_NAME = "BotDetectionMode";
    public static final String INVALID_FUSION_PACKET_TYPE_DETECTION_MODE_PARAM_NAME = "InvalidFusionPacketTypeDetectionMode";
    public static final String INVALID_FUSION_PACKET_DETECTION_MODE_PARAM_NAME = "InvalidFusionPacketDetectionMode";
    public static final String INVALID_FUSION_PACKET_THRESHOLD_PARAM_NAME = "InvalidFusionPacketThreshold";
    public static final String INVALID_FUSION_PACKET_LIMIT_USERS_PARAM_NAME = "InvalidFusionPacketLimitUsers";
    public static final String INVALID_FUSION_PACKET_SHELL_COMMAND_PARAM_NAME = "InvalidFusionPacketShellCommand";
    public static final String ANALYSE_RATIOS_PARAM_NAME = "AnalyseRatios";
    public static final String ANALYSE_RATIOS_SENSITIVITY_PARAM_NAME = "AnalyseRatiosSensitivity";
    public static final String ANALYSE_SEQUENCE_PARAM_NAME = "AnalyseSequence";
    public static final String ANALYSIS_THREADS_PARAM_NAME = "AnalysisThreadCount";
    public static final String AUTOKICK_PARAM_NAME = "AutoKick";
    public static final String BOT_HUNTER_PROXY_PARAM_NAME = "BotHunterProxy";
    public static final String CLIENT_IP_TIMEOUT_PARAM_NAME = "ClientIpTimeout";
    public static final String CLIENT_PORT_TIMEOUT_PARAM_NAME = "ClientPortTimeout";
    public static final String DUPLICATE_REPORT_INTERVAL_PARAM_NAME = "DuplicateReportInterval";
    public static final String MAX_PACKETS_PER_SOCKET_PARAM_NAME = "MaxPacketsPerSocket";
    public static final String MIN_ANALYSIS_PACKETS_PARAM_NAME = "MinAnalysisPackets";
    public static final String MITC_PARAM_NAME = "Mitc";
    public static final String PACKET_SOURCE_PARAM_NAME = "PacketSource";
    public static final String PCAP_FILTER_PARAM_NAME = "PcapFilter";
    public static final String REGISTRY_IP_PARAM_NAME = "RegistryIp";
    public static final String REGISTRY_PORT_PARAM_NAME = "RegistryPort";
    public static final String REGISTRY_PROXY_PARAM_NAME = "RegistryProxy";
    public static final String STATS_INTERVAL_SECONDS_PARAM_NAME = "StatsInterval";
    public static final String VERBOSE_ANALYSIS_PARAM_NAME = "VerboseAnalysis";
    public static final String VERBOSE_KICK_PARAM_NAME = "VerboseKick";
    public static final String VERBOSE_PACKET_CAPTURE_PARAM_NAME = "VerbosePacketCapture";
    public static final String VERBOSE_PARAM_NAME = "Verbose";
    public static volatile Mode MODE = Mode.INVALID_FUSION_PACKET_TYPE_DETECTION;
    public static volatile int INVALID_FUSION_PACKET_THRESHOLD = 10;
    public static volatile int INVALID_FUSION_PACKET_LIMIT_USERS = 0;
    public static volatile String INVALID_FUSION_PACKET_SHELL_COMMAND = "echo 'your command goes here...' ";
    public static volatile int MAX_PACKETS_PER_SOCKET = 40;
    public static volatile int CLIENT_IP_TIMEOUT_SECS = 180;
    public static volatile int CLIENT_PORT_TIMEOUT_SECS = 180;
    public static volatile int DUPLICATE_REPORT_INTERVAL_SECS = 60;
    public static volatile String PACKET_SOURCE = null;
    public static volatile boolean AUTOKICK = false;
    public static volatile String BOT_HUNTER_PROXY = null;
    public static volatile String REGISTRY_PROXY = null;
    public static boolean SEQUENCE_ANALYSIS = true;
    public static boolean RATIO_ANALYSIS = true;
    public static double ANALYSE_RATIOS_SENSITIVITY = 0.1;
    public static volatile int MIN_INTERLEAVE_TRANSITIONS = 2;
    public static volatile int MIN_ANALYSIS_PACKETS_PER_SOCKET = 10;
    public static volatile int ANALYSIS_THREADS = 1;
    public static volatile String PCAP_FILTER = "";
    public static volatile int STATS_INTERVAL_SECONDS = 30;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Mode {
        BOT_DETECTION,
        INVALID_FUSION_PACKET_TYPE_DETECTION,
        INVALID_FUSION_PACKET_DETECTION;

    }
}

