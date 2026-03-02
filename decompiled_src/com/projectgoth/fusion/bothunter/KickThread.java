/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  Ice.Util
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import Ice.Communicator;
import Ice.ObjectPrx;
import Ice.Util;
import com.projectgoth.fusion.bothunter.Params;
import com.projectgoth.fusion.bothunter.Suspect;
import com.projectgoth.fusion.bothunter.SuspectGroup;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.slice.BotHunterPrx;
import com.projectgoth.fusion.slice.BotHunterPrxHelper;
import com.projectgoth.fusion.slice.ObjectCachePrx;
import com.projectgoth.fusion.slice.RegistryPrx;
import com.projectgoth.fusion.slice.RegistryPrxHelper;
import com.projectgoth.fusion.slice.SuspectGroupIce;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

public class KickThread
implements Runnable {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(KickThread.class));
    private static final String UNKNOWN_USER = "<unknown>";
    private static final String NEWLINE = "\n";
    private static final String COLON = ":";
    private static KickThread instance;
    private AtomicBoolean bContinue = new AtomicBoolean(true);
    private ConcurrentLinkedQueue<SuspectGroup> suspectQueue = new ConcurrentLinkedQueue();
    private HashMap<String, Long> lastReportedTimes = new HashMap();
    private boolean integratedKick = true;
    private boolean debugEnabled;
    private Communicator communicator;
    private Random randomKickAction = new Random(System.currentTimeMillis());
    private BotHunterPrx botHunterPrx;
    private RegistryPrx registryPrx;
    private ObjectCachePrx ocPrx;

    private KickThread(boolean integratedKick) {
        this.integratedKick = integratedKick;
        this.debugEnabled = log.isDebugEnabled();
    }

    public static synchronized KickThread getInstance(boolean integratedKick) {
        if (instance == null) {
            instance = new KickThread(integratedKick);
        }
        return instance;
    }

    public void terminate() {
        this.bContinue.set(false);
    }

    public void addSuspects(SuspectGroup grp) {
        if (this.debugEnabled) {
            log.debug((Object)("KickThread: adding " + grp.getMembers().length + " suspects"));
        }
        this.suspectQueue.add(grp);
    }

    public void run() {
        log.info((Object)("KickThread starting with AutoKick=" + Params.AUTOKICK));
        log.info((Object)("and integrated kick=" + this.integratedKick));
        if (Params.AUTOKICK) {
            try {
                log.info((Object)"Connecting to fusion registry");
                this.connectToRegistry();
            }
            catch (Exception e) {
                log.info((Object)"Can't connect to fusion registry!", (Throwable)e);
            }
        }
        if (this.integratedKick) {
            this.runIntegratedKick();
        } else {
            try {
                this.runAsIceClient();
            }
            catch (Exception e) {
                log.error((Object)"Exception running as ice client", (Throwable)e);
            }
        }
    }

    private void runAsIceClient() throws Exception {
        log.info((Object)"Connecting to bot hunter");
        this.connectToBotHunter();
        do {
            if (this.debugEnabled) {
                log.debug((Object)"Retrieving suspect groups from BotHunter");
            }
            SuspectGroupIce[] suspectGroupIces = this.botHunterPrx.getLatestSuspects();
            if (this.debugEnabled) {
                log.debug((Object)("Number of suspect groups retrieved from BotHunter=" + suspectGroupIces.length));
            }
            for (SuspectGroupIce groupIce : suspectGroupIces) {
                SuspectGroup group = new SuspectGroup(groupIce);
                this.reportSuspectGroup(group);
            }
            try {
                Thread.sleep(100L);
            }
            catch (Exception exception) {
                // empty catch block
            }
        } while (this.bContinue.get());
    }

    private void runIntegratedKick() {
        do {
            if (!this.suspectQueue.isEmpty()) {
                SuspectGroup suspects = (SuspectGroup)this.suspectQueue.remove();
                this.reportSuspectGroup(suspects);
            }
            try {
                Thread.sleep(100L);
            }
            catch (Exception exception) {
                // empty catch block
            }
        } while (this.bContinue.get());
    }

    private void reportSuspectGroup(SuspectGroup suspects) {
        StringBuffer sb = new StringBuffer();
        boolean atLeastOneNewSuspect = false;
        for (Suspect s : suspects.getMembers()) {
            String clientAddr = s.getClientIP() + COLON + s.getClientPort();
            Long lastReportedAt = this.lastReportedTimes.get(clientAddr);
            if (lastReportedAt == null || (System.currentTimeMillis() - lastReportedAt) / 1000L > (long)Params.DUPLICATE_REPORT_INTERVAL_SECS) {
                this.lastReportedTimes.put(clientAddr, System.currentTimeMillis());
                atLeastOneNewSuspect = true;
            } else if (this.debugEnabled) {
                log.debug((Object)("Client ip:port " + clientAddr + " recently reported"));
            }
            String displayUser = s.getUsername();
            if (displayUser == null || displayUser.length() == 0) {
                displayUser = UNKNOWN_USER;
            }
            sb.append(s.getClientIP() + " User " + displayUser + " clientPort " + s.getClientPort() + " meanTcpTimestamp " + s.getMeanTcpTimestamp() + " meanTcpTimestamp/arrivalTime " + s.getMeanTcpTimestampOverArrivalTime() + " lastHeardFrom/secondsAgo " + (System.currentTimeMillis() - s.getLastAddedTo()) / 1000L + " lastWinscaleSeen " + s.getLastWinscaleSeen() + " claimedClientType " + s.getClaimedClientType() + NEWLINE);
        }
        if (atLeastOneNewSuspect) {
            String ip = suspects.getClientIP();
            log.info((Object)("suspectGroupIP " + ip + " suspectClientPorts " + suspects.getMembers().length + " nonSuspectClientPorts " + suspects.getInnocentPortCount() + COLON + NEWLINE + sb.toString()));
            if (Params.AUTOKICK) {
                for (Suspect s : suspects.getMembers()) {
                    if (s.getUsername() == null) continue;
                    this.doAutokick(s.getUsername());
                }
            }
        }
    }

    private void connectToRegistry() throws Exception {
        log.info((Object)"Calling Ice.Util.initialize");
        this.communicator = Util.initialize((String[])new String[0]);
        log.info((Object)"Called Ice.Util.initialize");
        String registryAddress = Params.REGISTRY_PROXY;
        ObjectPrx basePrx = this.communicator.stringToProxy(registryAddress);
        log.info((Object)("Connecting to " + registryAddress));
        log.info((Object)("basePrx=" + basePrx));
        log.info((Object)"Casting basePrx to registryPrx");
        this.registryPrx = RegistryPrxHelper.uncheckedCast(basePrx);
        log.info((Object)("Got registryPrx = " + this.registryPrx));
        this.ocPrx = this.registryPrx.getLowestLoadedObjectCache();
        log.info((Object)("Got object cache proxy = " + this.ocPrx));
    }

    private void connectToBotHunter() throws Exception {
        log.info((Object)"Calling Ice.Util.initialize");
        this.communicator = Util.initialize((String[])new String[0]);
        log.info((Object)"Called Ice.Util.initialize");
        String botHunterAddress = Params.BOT_HUNTER_PROXY;
        ObjectPrx basePrx = this.communicator.stringToProxy(botHunterAddress);
        log.info((Object)("Connecting to " + botHunterAddress));
        log.info((Object)("basePrx=" + basePrx));
        this.botHunterPrx = BotHunterPrxHelper.uncheckedCast(basePrx);
    }

    private void doAutokick(String username) {
        try {
            log.info((Object)("Autokicking user " + username));
            UserPrx userPrx = this.ocPrx.createUserObject(username);
            if (this.debugEnabled) {
                log.debug((Object)("Got user into the ObjectCache ok: user proxy=" + userPrx));
            }
            String bs = null;
            switch (this.randomKickAction.nextInt(4)) {
                case 0: {
                    bs = "migme is currently experiencing heavy load. We value your loyalty. Please try again later.";
                    break;
                }
                case 1: {
                    bs = "Please check your network settings and try again.";
                    break;
                }
                case 2: {
                    bs = "User loyalty heuristic rewards program -- reward allocation code 7!";
                    break;
                }
                case 3: {
                    log.info((Object)("Letting off user " + username));
                    return;
                }
            }
            log.info((Object)("Disconnecting user=" + userPrx));
            userPrx.disconnect(bs);
            log.info((Object)("Disconnected user=" + userPrx));
        }
        catch (Exception e) {
            log.error((Object)("Exception kicking user " + username), (Throwable)e);
        }
    }
}

