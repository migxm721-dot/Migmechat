/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  org.apache.log4j.Logger
 *  org.springframework.beans.factory.InitializingBean
 */
package com.projectgoth.fusion.botservice;

import Ice.Current;
import com.projectgoth.fusion.botservice.BotLoader;
import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.RequestCounter;
import com.projectgoth.fusion.common.ServiceStatsFactory;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.slice.BotChannelPrx;
import com.projectgoth.fusion.slice.BotInstance;
import com.projectgoth.fusion.slice.BotServicePrx;
import com.projectgoth.fusion.slice.BotServiceStats;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice._BotServiceDisp;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class BotServiceI
extends _BotServiceDisp
implements InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotServiceI.class));
    private BotServicePrx proxy;
    private ScheduledExecutorService executor;
    private RequestCounter requestCounter = new RequestCounter();
    private BotDAO botDAO;
    private Map<String, Bot> botMappings = new ConcurrentHashMap<String, Bot>();
    private Map<String, Set<Bot>> channelBotMappings = new ConcurrentHashMap<String, Set<Bot>>();
    private long startTime = System.currentTimeMillis();
    private int maxBotIntances;
    private int maxChannels;

    public void afterPropertiesSet() throws Exception {
    }

    public void shutdown() {
    }

    public BotServiceStats getStats() {
        BotServiceStats stats = ServiceStatsFactory.getBotServiceStats(this.startTime);
        stats.numBotObjects = this.botMappings.size();
        stats.maxBotObjects = this.maxBotIntances;
        stats.numBotChannelObjects = this.channelBotMappings.size();
        stats.maxBotChannelObjects = this.maxChannels;
        stats.requestsPerSecond = this.requestCounter.getRequestsPerSecond();
        stats.maxRequestsPerSecond = this.requestCounter.getMaxRequestsPerSecond();
        if (this.executor instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor e = (ScheduledThreadPoolExecutor)this.executor;
            stats.threadPoolSize = e.getActiveCount();
            stats.maxThreadPoolSize = e.getCorePoolSize();
            stats.threadPoolQueueSize = e.getQueue().size();
        }
        return stats;
    }

    public void setBotDAO(BotDAO botDAO) {
        this.botDAO = botDAO;
    }

    public void setProxy(BotServicePrx proxy) {
        this.proxy = proxy;
    }

    public void setExecutor(ScheduledExecutorService executor) {
        this.executor = executor;
    }

    public BotInstance addBotToChannel(BotChannelPrx channelProxy, String botCommandName, String starterUsername, boolean purgeIfIdle, Current __current) throws FusionException {
        String channelID = channelProxy.ice_getIdentity().name;
        Set<Bot> botsInChannel = this.channelBotMappings.get(channelID);
        if (botsInChannel == null) {
            botsInChannel = new CopyOnWriteArraySet<Bot>();
            this.channelBotMappings.put(channelID, botsInChannel);
            int mapSize = this.channelBotMappings.size();
            if (mapSize > this.maxChannels) {
                this.maxChannels = mapSize;
            }
        }
        for (Bot bot : botsInChannel) {
            if (!bot.getBotData().getCommandName().equals(botCommandName) || !bot.getChannelProxy().equals(channelProxy)) continue;
            BotInstance botInstance = bot.getInstance();
            botInstance.botServiceProxy = this.proxy;
            return botInstance;
        }
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            BotData botData = messageEJB.getBotFromCommandName(botCommandName);
            if (botData == null) {
                throw new FusionException(botCommandName + " is not a valid bot command");
            }
            Bot bot = BotLoader.addBotToChannel(this.executor, botData, channelProxy, this.botDAO, starterUsername, "ENG");
            bot.setPurgeIfIdle(purgeIfIdle);
            botsInChannel.add(bot);
            this.botMappings.put(bot.getInstanceID(), bot);
            int mapSize = this.botMappings.size();
            if (mapSize > this.maxBotIntances) {
                this.maxBotIntances = mapSize;
            }
            log.info((Object)(starterUsername + " started " + bot.getBotData().getDisplayName() + " [" + bot.getInstanceID() + "] in channel [" + channelID + "]"));
            BotInstance botInstance = bot.getInstance();
            botInstance.botServiceProxy = this.proxy;
            this.requestCounter.add();
            return botInstance;
        }
        catch (FusionException e) {
            throw e;
        }
        catch (Exception e) {
            log.error((Object)("Error adding bot to channel :" + e.getMessage()), (Throwable)e);
            throw new FusionException("Error adding bot to channel. " + e.getMessage());
        }
    }

    public void removeBot(String botInstanceID, boolean stopEvenIfGameInProgress, Current __current) throws FusionException {
        Bot bot = this.botMappings.get(botInstanceID);
        if (bot == null) {
            return;
        }
        if (!stopEvenIfGameInProgress && !bot.canBeStoppedNow()) {
            throw new FusionException("Unable to remove '" + bot.getBotData().getDisplayName() + "'. There is a game currently in progress");
        }
        this.botMappings.remove(botInstanceID);
        Set<Bot> botsInChannel = this.channelBotMappings.get(bot.getChannelProxy().ice_getIdentity().name);
        if (botsInChannel != null) {
            botsInChannel.remove(bot);
            if (botsInChannel.size() == 0) {
                this.channelBotMappings.remove(bot.getChannelProxy().ice_getIdentity().name);
            }
        }
        bot.stopBot();
        log.info((Object)(bot.getBotData().getDisplayName() + " [" + bot.getInstanceID() + "] stopped channel [" + bot.getChannelProxy().ice_getIdentity().name + "]"));
        this.requestCounter.add();
    }

    public void sendMessageToBot(String botInstanceID, String username, String message, long receivedTimestamp, Current __current) throws FusionException {
        Bot bot = this.botMappings.get(botInstanceID);
        if (bot != null) {
            bot.queueIncomingMessage(message, username, receivedTimestamp);
        }
        this.requestCounter.add();
    }

    public void sendMessageToBotsInChannel(String channelID, String username, String message, long receivedTimestamp, Current __current) throws FusionException {
        Set<Bot> botsInChannel = this.channelBotMappings.get(channelID);
        if (botsInChannel != null) {
            for (Bot bot : botsInChannel) {
                bot.queueIncomingMessage(message, username, receivedTimestamp);
            }
        }
        this.requestCounter.add();
    }

    public void sendNotificationToBotsInChannel(String channelID, String username, int notification, Current __current) throws FusionException {
        Set<Bot> botsInChannel = this.channelBotMappings.get(channelID);
        if (botsInChannel != null) {
            BotData.BotCommandEnum botCommand = BotData.BotCommandEnum.fromValue(notification);
            for (Bot bot : botsInChannel) {
                bot.queueIncomingNotification(botCommand, username);
            }
        }
        this.requestCounter.add();
    }

    public void purgeIdleBots() {
        for (Bot bot : this.botMappings.values()) {
            if (!bot.purgeIfIdle() || !bot.isIdle()) continue;
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Removing idle bot instanceID [" + bot.getInstanceID() + "]"));
                }
                this.removeBot(bot.getInstanceID(), false);
                bot.getChannelProxy().botKilled(bot.getInstanceID());
            }
            catch (Exception e) {
                log.warn((Object)("Error removing idle bot: " + bot.getInstanceID()), (Throwable)e);
            }
        }
    }
}

