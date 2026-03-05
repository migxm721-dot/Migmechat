/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandData;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.emote.EmoteCommandUtils;
import com.projectgoth.fusion.slice.FusionException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

public class Flames
extends EmoteCommand {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Flames.class));
    private static final String DEFAULT_NO_MATCH_MESSAGE = "**Too bad, %s and %s are not a match**";
    public static final String[][] FLAMES_VALUES = new String[][]{{"S", "Sis/Bro"}, {"F", "Friendship"}, {"L", "Love"}, {"A", "Admiration"}, {"M", "Marriage"}, {"E", "Enemy"}};

    public Flames(EmoteCommandData emoteCommandData) {
        super(emoteCommandData);
    }

    public EmoteCommandState createDefaultState(ChatSource.ChatType chatType) {
        return null;
    }

    public static int getFlamesScore(String username1, String username2) {
        HashMap<Character, Integer> occurrenceUser1 = new HashMap<Character, Integer>();
        for (char c : username1.toCharArray()) {
            if (occurrenceUser1.containsKey(Character.valueOf(c))) {
                occurrenceUser1.put(Character.valueOf(c), (Integer)occurrenceUser1.get(Character.valueOf(c)) + 1);
                continue;
            }
            occurrenceUser1.put(Character.valueOf(c), 1);
        }
        HashMap<Character, Integer> occurrenceCommon = new HashMap<Character, Integer>();
        for (char c : username2.toCharArray()) {
            if (!occurrenceUser1.containsKey(Character.valueOf(c))) continue;
            if (occurrenceCommon.containsKey(Character.valueOf(c))) {
                occurrenceCommon.put(Character.valueOf(c), (Integer)occurrenceCommon.get(Character.valueOf(c)) + 1);
                continue;
            }
            occurrenceCommon.put(Character.valueOf(c), (Integer)occurrenceUser1.get(Character.valueOf(c)) + 1);
        }
        int total = 0;
        Iterator i$ = occurrenceCommon.values().iterator();
        while (i$.hasNext()) {
            int i = (Integer)i$.next();
            total += i;
        }
        return total;
    }

    protected EmoteCommand.ResultType execute(MessageData messageData, ChatSource chatSource) throws FusionException {
        EmoteCommandUtils.TwoUsernameCommand tuc = EmoteCommandUtils.parseTwoUsernameCommand(this.emoteCommandData.getCommandName(), messageData, chatSource, false);
        int flameScore = Flames.getFlamesScore(tuc.username1, tuc.username2);
        if (flameScore == 0) {
            messageData.messageText = String.format(DEFAULT_NO_MATCH_MESSAGE, tuc.username1, tuc.username2);
        } else {
            String[] values = FLAMES_VALUES[flameScore % FLAMES_VALUES.length];
            messageData.messageText = String.format(this.emoteCommandData.getMessageText(), tuc.username1, tuc.username2, values[0], values[1]);
        }
        this.emoteCommandData.updateMessageData(messageData);
        chatSource.sendMessageToAllUsersInChat(messageData);
        return EmoteCommand.ResultType.HANDLED_AND_STOP;
    }
}

