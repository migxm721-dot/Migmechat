/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.chatterbot;

import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.Chatterbot;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.Text;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.TextCollection;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.textcollection.InspirationalQuoteIntros;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.textcollection.InspirationalQuotes;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.textcollection.PersonalProphecies;
import com.projectgoth.fusion.botservice.bot.migbot.chatterbot.textcollection.PersonalProphecyIntros;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.dao.BotDAO;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.slice.BotChannelPrx;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class GirlFriend
extends Chatterbot {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GirlFriend.class));
    DecimalFormat df = new DecimalFormat("0.00");
    Map<String, TextCollection> textMap = new HashMap<String, TextCollection>();
    Map<String, TextCollection> introMap = new HashMap<String, TextCollection>();
    private static final String COMMAND_PERSONAL_PROPHECY = "!w";
    private static final String COMMAND_INSPIRATIONAL_QUOTE = "!q";
    public static final String PREMIUM_COMMAND_COST = "premiumCommandCost";
    public static final String TIME_BETWEEN_PLAY_NOW_MESSAGES = "timeBetweenPlayNowMessages";
    private double premiumCommandCost = 0.02;
    private long timeBetweenPlayNowMessages = 150000L;
    private ScheduledFuture playNowTimer;

    public GirlFriend(ScheduledExecutorService executor, BotChannelPrx channelProxy, BotData botData, String languageCode, String botStarter, BotDAO botDAO) {
        super(executor, channelProxy, botData, languageCode, botStarter, botDAO);
        this.loadGameConfig();
        this.textMap.put(COMMAND_PERSONAL_PROPHECY, new PersonalProphecies());
        this.textMap.put(COMMAND_INSPIRATIONAL_QUOTE, new InspirationalQuotes());
        this.introMap.put(COMMAND_PERSONAL_PROPHECY, new PersonalProphecyIntros());
        this.introMap.put(COMMAND_INSPIRATIONAL_QUOTE, new InspirationalQuoteIntros());
        this.sendChannelMessage(this.createMessage("GREETING_MESSAGE", null));
        this.playNowTimer = executor.scheduleWithFixedDelay(new Runnable(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            public void run() {
                GirlFriend girlFriend = GirlFriend.this;
                synchronized (girlFriend) {
                    long now = System.currentTimeMillis();
                    if (now - GirlFriend.this.timeLastUserJoined < GirlFriend.this.timeBetweenPlayNowMessages || now - GirlFriend.this.timeLastMessageReceived < GirlFriend.this.timeBetweenPlayNowMessages) {
                        return;
                    }
                    GirlFriend.this.sendChannelMessage(GirlFriend.this.createMessage("PLAY_NOW", null));
                }
            }
        }, 0L, this.timeBetweenPlayNowMessages, TimeUnit.MILLISECONDS);
    }

    private void loadGameConfig() {
        this.premiumCommandCost = this.getDoubleParameter(PREMIUM_COMMAND_COST, this.premiumCommandCost);
        this.timeBetweenPlayNowMessages = this.getLongParameter(TIME_BETWEEN_PLAY_NOW_MESSAGES, this.timeBetweenPlayNowMessages);
    }

    public void onUserJoinChannel(String username) {
        super.onUserJoinChannel(username);
        this.sendMessage(this.createMessage("WELCOME_MESSAGE", username), username);
    }

    public synchronized void stopBot() {
        super.stopBot();
        if (this.playNowTimer != null) {
            this.playNowTimer.cancel(true);
            this.playNowTimer = null;
        }
    }

    protected String createMessage(String messageKey, String player) {
        try {
            String messageToSend = (String)this.messages.get(messageKey);
            if (messageToSend == null) {
                messageToSend = messageKey;
            }
            if (player != null) {
                messageToSend = messageToSend.replace("PLAYER", player);
            }
            messageToSend = messageToSend.replace("BOT_NAME", this.botData.getDisplayName());
            messageToSend = messageToSend.replace("CURRENCY", "USD");
            messageToSend = messageToSend.replace("COMMAND_1", COMMAND_PERSONAL_PROPHECY);
            messageToSend = messageToSend.replace("COMMAND_2", COMMAND_INSPIRATIONAL_QUOTE);
            messageToSend = messageToSend.replace("PREMIUM_AMOUNT", this.df.format(this.premiumCommandCost));
            return messageToSend;
        }
        catch (NullPointerException e) {
            log.error((Object)("Outgoing message could not be created, key = " + messageKey), (Throwable)e);
            return "";
        }
    }

    public void onMessage(String username, String messageText, long receivedTimestamp) {
        TextCollection tc = this.textMap.get(messageText);
        TextCollection intro = this.introMap.get(messageText);
        if (tc == null || intro == null) {
            super.onMessage(username, messageText, receivedTimestamp);
            return;
        }
        try {
            if (!this.userCanAffordItem(username, this.premiumCommandCost)) {
                return;
            }
            Text text = tc.getNextText();
            Text response = intro.getNextText();
            String message = response.getContent();
            message = message.replace("USERNAME", username);
            message = message.replace("TEXT", text.getContent());
            this.sendChannelMessage(message);
            this.chargeUserForItem(username, "girlfriend." + tc.getCode(), this.premiumCommandCost, "Purchase of " + tc.getDisplayName() + " from " + this.botData.getDisplayName());
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception username [" + username + "] cost[" + this.premiumCommandCost + "USD" + "] messageText[" + messageText + "] :" + e.getMessage()), (Throwable)e);
            this.sendMessage("I'm sorry I'm not feeling well right now. Please visit me another time.", username);
        }
        this.timeLastMessageReceived = System.currentTimeMillis();
    }
}

