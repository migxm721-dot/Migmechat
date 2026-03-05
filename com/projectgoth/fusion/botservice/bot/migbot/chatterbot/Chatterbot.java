/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.chatterbot;

import com.projectgoth.fusion.botservice.bot.Bot;
import com.projectgoth.fusion.botservice.bot.migbot.eliza.Eliza;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.log4j.Logger;

public abstract class Chatterbot
extends Bot {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Chatterbot.class));
    private String scriptLocation = "/usr/fusion/eliza/GirlFriend.txt";
    private long idleInterval = 1800000L;
    protected Map<String, Eliza> chatters = new ConcurrentHashMap<String, Eliza>();
    protected long timeLastMessageReceived = System.currentTimeMillis();
    protected long timeLastUserJoined = System.currentTimeMillis();

    public Chatterbot(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDAO) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDAO);
        this.scriptLocation = this.getStringParameter("ScriptLocation", this.scriptLocation);
        this.idleInterval = this.getLongParameter("IdleInterval", this.idleInterval);
    }

    public boolean isIdle() {
        return System.currentTimeMillis() - this.timeLastMessageReceived > this.idleInterval;
    }

    public boolean canBeStoppedNow() {
        return true;
    }

    public synchronized void stopBot() {
        for (String chatter : this.chatters.keySet()) {
            this.sendMessage("Bye for now. Talk to you later...", chatter);
        }
        this.chatters.clear();
    }

    public void onUserLeaveChannel(String username) {
        this.chatters.remove(username);
    }

    public void onUserJoinChannel(String username) {
        this.timeLastUserJoined = System.currentTimeMillis();
    }

    public void onMessage(String username, String messageText, long receivedTimestamp) {
        if (messageText.startsWith("!")) {
            Eliza eliza = this.chatters.get(username);
            if (eliza == null) {
                eliza = new Eliza();
                eliza.readScript(true, this.scriptLocation);
                this.chatters.put(username, eliza);
                log.info((Object)(username + " started chatting to bot"));
            }
            this.sendMessage(eliza.processInput(messageText), username);
            this.timeLastMessageReceived = System.currentTimeMillis();
        }
    }
}

