/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.BotData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Message;
import com.projectgoth.fusion.interfaces.MessageHome;
import com.projectgoth.fusion.slice.BotInstance;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BotChannelHelper {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(BotChannelHelper.class));

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateBots(String username, BotData.BotCommandEnum command, Map<String, BotInstance> channelBots, String channelID) {
        Map<String, BotInstance> map = channelBots;
        synchronized (map) {
            for (BotInstance botInstance : channelBots.values()) {
                try {
                    botInstance.botServiceProxy.sendNotificationToBotsInChannel(channelID, username, command.value());
                }
                catch (Exception e) {
                    log.error((Object)("Could not update bots for user '" + username + "' " + command.name()), (Throwable)e);
                }
            }
        }
    }

    public static List<BotData> getGames() {
        try {
            Message messageEJB = (Message)EJBHomeCache.getObject("ejb/Message", MessageHome.class);
            return messageEJB.getBots();
        }
        catch (Exception e) {
            log.error((Object)"Exception in getGames(). ", (Throwable)e);
            return Collections.EMPTY_LIST;
        }
    }
}

