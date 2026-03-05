/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.interfaces.MIS;
import com.projectgoth.fusion.interfaces.MISHome;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;

public class EmoteCommandFactory {
    private static final int LOCAL_CACHE_RANDOMNESS_TIME = 13;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EmoteCommandFactory.class));
    private static Map<String, EmoteCommandData> commands = new ConcurrentHashMap<String, EmoteCommandData>();
    private static Semaphore semaphore = new Semaphore(1);
    private static long lastUpdated;
    private static Random random;
    private static IcePrxFinder icePrxFinder;

    public static EmoteCommand getEmoteCommand(String command, ChatSource.ChatType chatType, IcePrxFinder icePrxFinder) {
        EmoteCommandData ecd;
        long reloadInterval;
        if (chatType == null) {
            return null;
        }
        long curTime = System.currentTimeMillis();
        if (curTime - lastUpdated > (reloadInterval = (long)(SystemProperty.getInt(SystemPropertyEntities.Emote.EMOTE_COMMAND_RELOAD_INTERVAL_IN_SECONDS) * 1000)) && curTime - lastUpdated - reloadInterval > (long)(random.nextInt(13) * 1000)) {
            EmoteCommandFactory.loadCommands();
        }
        if ((ecd = commands.get(command)) != null && ecd.supportChatType(chatType)) {
            return ecd.getEmoteCommandHandler().setIcePrxFinder(icePrxFinder);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadCommands() {
        if (lastUpdated == 0L) {
            semaphore.acquireUninterruptibly();
        } else if (!semaphore.tryAcquire()) {
            return;
        }
        try {
            try {
                MIS misEJB = (MIS)EJBHomeCache.getObject("ejb/MIS", MISHome.class);
                List newCommands = misEJB.getEmoteCommands();
                HashSet<String> newKeySet = new HashSet<String>();
                for (EmoteCommandData emoteCommandData : newCommands) {
                    try {
                        String key = emoteCommandData.getCommandName().toLowerCase();
                        if (commands.containsKey(key)) {
                            log.debug((Object)String.format("updating emote command %s", key));
                            EmoteCommandData oldecd = commands.get(key);
                            if (oldecd.getHandlerClassName().equals(emoteCommandData.getHandlerClassName())) {
                                emoteCommandData.setEmoteCommandHandler(oldecd.getEmoteCommandHandler());
                            } else {
                                emoteCommandData.instantiateEmoteCommandHandler();
                            }
                            log.debug((Object)String.format("updated emote command %s", key));
                        } else {
                            log.info((Object)String.format("adding emote command %s", key));
                            emoteCommandData.instantiateEmoteCommandHandler();
                            log.info((Object)String.format("added emote command %s", key));
                        }
                        newKeySet.add(key);
                        commands.put(key, emoteCommandData);
                    }
                    catch (Exception e) {
                        log.error((Object)("Unable to load emote[" + emoteCommandData.getCommandName() + "]"), (Throwable)e);
                    }
                }
                for (String key : commands.keySet()) {
                    if (newKeySet.contains(key)) continue;
                    log.info((Object)String.format("removing emote command %s", key));
                    commands.remove(key);
                    log.info((Object)String.format("removed emote command %s", key));
                }
                lastUpdated = System.currentTimeMillis();
            }
            catch (Exception e) {
                log.error((Object)"Unable to load emote command", (Throwable)e);
                Object var8_10 = null;
                semaphore.release();
            }
            Object var8_9 = null;
            semaphore.release();
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            semaphore.release();
            throw throwable;
        }
    }

    static {
        random = new Random();
        EmoteCommandFactory.loadCommands();
    }
}

