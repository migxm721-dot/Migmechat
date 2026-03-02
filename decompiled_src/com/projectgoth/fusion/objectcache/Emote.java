/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.data.EmoteData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.Content;
import com.projectgoth.fusion.interfaces.ContentHome;
import com.projectgoth.fusion.slice.ChatRoomPrx;
import com.projectgoth.fusion.slice.GroupChatPrx;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class Emote {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Emote.class));
    public static final String MAGIC_CHAR = "/";
    public static final String[] EIGHT_BALL_TEXTS = new String[]{"Yep", "OK", "Maybe", "No", "Don't Bother"};
    private static Map<String, List<EmoteData>> emoteMap = new ConcurrentHashMap<String, List<EmoteData>>();
    private static SecureRandom secureRandom = new SecureRandom();
    private final long LOCAL_CACHE_TIME = 300L;
    private long emoteMapNextUpdate = -1L;
    private String target = "";
    private String finalString = "";

    public Emote(String sender, String msg) throws Exception {
        List<EmoteData> emoteDataList;
        if (msg == null || msg.length() == 0 || !msg.startsWith(MAGIC_CHAR)) {
            throw new Exception("Emotes must start with '/'");
        }
        String command = "";
        String[] tokens = msg.split(" ");
        if (tokens.length > 0) {
            command = tokens[0];
        }
        if ("/me".equalsIgnoreCase(command)) {
            this.finalString = msg.replaceFirst("/[Mm][Ee]", sender);
            return;
        }
        if (emoteMap.isEmpty() || this.emoteMapNextUpdate <= System.currentTimeMillis()) {
            this.loadEmotes();
        }
        if ((emoteDataList = emoteMap.get(command.toLowerCase())) == null) {
            throw new Exception("'" + command + "' is not a valid command");
        }
        EmoteData emoteData = emoteDataList.size() == 1 ? emoteDataList.get(0) : emoteDataList.get(secureRandom.nextInt(emoteDataList.size()));
        for (int i = 1; i < tokens.length; ++i) {
            if (tokens[i].length() <= 0) continue;
            this.target = tokens[i];
            break;
        }
        this.finalString = this.target.length() > 0 ? emoteData.actionWithTarget.replaceAll("%s", sender).replaceAll("%t", this.target) : emoteData.action.replaceAll("%s", sender);
        if ("/roll".equalsIgnoreCase(command)) {
            this.finalString = this.finalString.replaceAll("%r", String.valueOf(secureRandom.nextInt(100) + 1));
        } else if ("/8ball".equalsIgnoreCase(command)) {
            this.finalString = this.finalString.replaceAll("%r", EIGHT_BALL_TEXTS[secureRandom.nextInt(EIGHT_BALL_TEXTS.length)]);
        }
    }

    public Emote(String sender, String msg, String validTarget) throws Exception {
        this(sender, msg);
        if (this.requiresTarget() && !this.target.equals(validTarget)) {
            throw new Exception(this.target + " is not a valid target");
        }
    }

    public Emote(String sender, String msg, ChatRoomPrx chatRoomPrx) throws Exception {
        this(sender, msg);
        if (this.requiresTarget()) {
            Object[] validTargets = chatRoomPrx.getParticipants(null);
            Arrays.sort(validTargets);
            if (Arrays.binarySearch(validTargets, this.target) < 0) {
                throw new Exception(this.target + " is not a valid target");
            }
        }
    }

    public Emote(String sender, String msg, GroupChatPrx groupChatPrx) throws Exception {
        this(sender, msg);
        if (this.requiresTarget()) {
            Object[] validTargets = groupChatPrx.getParticipants(null);
            Arrays.sort(validTargets);
            if (Arrays.binarySearch(validTargets, this.target) < 0) {
                throw new Exception(this.target + " is not a valid target");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private synchronized void loadEmotes() {
        try {
            try {
                Content contentEJB = (Content)EJBHomeCache.getObject("ejb/Content", ContentHome.class);
                List emoteDataList = contentEJB.getEmotes();
                emoteMap.clear();
                for (EmoteData emoteData : emoteDataList) {
                    String key = MAGIC_CHAR + emoteData.command.toLowerCase();
                    if (!emoteMap.containsKey(key)) {
                        emoteMap.put(key, new ArrayList());
                    }
                    emoteMap.get(key).add(emoteData);
                }
                Object var7_7 = null;
            }
            catch (Exception e) {
                log.warn((Object)("Unable to load emotes - " + e.getClass().getName() + ":" + e.getMessage()));
                Object var7_8 = null;
                this.emoteMapNextUpdate = System.currentTimeMillis() + 1000L * SystemProperty.getLong("EmotesReloadIntervalInSeconds", 300L);
                return;
            }
        }
        catch (Throwable throwable) {
            Object var7_9 = null;
            this.emoteMapNextUpdate = System.currentTimeMillis() + 1000L * SystemProperty.getLong("EmotesReloadIntervalInSeconds", 300L);
            throw throwable;
        }
        this.emoteMapNextUpdate = System.currentTimeMillis() + 1000L * SystemProperty.getLong("EmotesReloadIntervalInSeconds", 300L);
    }

    public boolean requiresTarget() {
        return this.target.length() > 0;
    }

    public String toString() {
        return this.finalString;
    }

    public static boolean isEmote(String msg) {
        return !StringUtil.isBlank(msg) && msg.startsWith(MAGIC_CHAR);
    }
}

