/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.botservice.bot.migbot.warriors;

import com.projectgoth.fusion.botservice.bot.migbot.warriors.WarriorsBot;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import org.apache.log4j.Logger;

class GameData {
    String username = null;
    int consecutive_wins = 0;
    int consecutive_losses = 0;
    int played = 0;
    static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(WarriorsBot.class));
    private static String data_separator = ";";

    GameData() {
    }

    public String toString() {
        return this.username + data_separator + Integer.toString(this.consecutive_wins) + data_separator + Integer.toString(this.consecutive_losses) + data_separator + Integer.toString(this.played);
    }

    public static boolean setGameDataInMemcache(String username, GameData d) {
        boolean result;
        try {
            result = MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.BOT_CHALLENGE_GAME_DATA, username, d.toString());
        }
        catch (Exception e) {
            result = false;
        }
        log.debug((Object)("Setting gamedata [" + username + "] [" + d + "] success?[" + result + "]"));
        return result;
    }

    public static GameData getGameDataFromMemcache(String username) {
        GameData result = new GameData();
        result.username = username;
        try {
            String data = MemCachedClientWrapper.getString(MemCachedKeySpaces.CommonKeySpace.BOT_CHALLENGE_GAME_DATA, username);
            log.debug((Object)("getGameDataFromMemcache [" + username + "] [" + data + "]"));
            if (!StringUtil.isBlank(data)) {
                String[] tokens = data.split(data_separator);
                log.debug((Object)(" tokens length [" + tokens.length + "] [" + tokens[0] + "] [" + tokens[1] + "]"));
                if (tokens.length == 4) {
                    result.username = tokens[0];
                    result.consecutive_wins = Integer.parseInt(tokens[1]);
                    result.consecutive_losses = Integer.parseInt(tokens[2]);
                    result.played = Integer.parseInt(tokens[3]);
                }
            }
        }
        catch (Exception e) {
            log.error((Object)("Unexpected exception while retrieving game data for [" + username + "] from memcache"));
        }
        log.debug((Object)("User [" + username + "] GameData [" + result + "]"));
        return result;
    }
}

