/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.emote;

import com.projectgoth.fusion.common.IcePrxFinder;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.emote.ChatSource;
import com.projectgoth.fusion.emote.EmoteCommand;
import com.projectgoth.fusion.emote.EmoteCommandFactory;
import com.projectgoth.fusion.emote.EmoteCommandState;
import com.projectgoth.fusion.objectcache.ChatSourceGroup;
import com.projectgoth.fusion.objectcache.ChatSourceRoom;
import com.projectgoth.fusion.objectcache.ChatSourceUser;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.SessionPrx;
import java.util.HashMap;
import java.util.Map;

public class EmoteCommandStateStorage {
    private Map<String, EmoteCommandState> emoteCommandStats = new HashMap<String, EmoteCommandState>();
    private static final long DEFAULT_STATE_CLEANUP_PERIOD = 60000L;
    private long lastCleanUpTime = 0L;
    private IcePrxFinder icePrxFinder;

    public EmoteCommandStateStorage(IcePrxFinder icePrxFinder) {
        this.icePrxFinder = icePrxFinder;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public EmoteCommandState getEmoteCommandState(String emoteCommand, ChatSource.ChatType chatType) {
        Map<String, EmoteCommandState> map = this.emoteCommandStats;
        synchronized (map) {
            String stateKey;
            EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(emoteCommand, chatType, this.icePrxFinder);
            EmoteCommandState s = null;
            if (ec != null && (stateKey = ec.getEmoteCommandData().getCommandStateName()) != null && (s = this.emoteCommandStats.get(stateKey)) == null && (s = ec.createDefaultState(chatType)) != null) {
                this.emoteCommandStats.put(emoteCommand, s);
            }
            if (System.currentTimeMillis() - this.lastCleanUpTime > 60000L) {
                for (EmoteCommandState si : this.emoteCommandStats.values()) {
                    si.cleanUp();
                }
                this.lastCleanUpTime = System.currentTimeMillis();
            }
            return s;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeEmoteCommandState(String emoteCommand) {
        Map<String, EmoteCommandState> map = this.emoteCommandStats;
        synchronized (map) {
            this.emoteCommandStats.remove(emoteCommand);
        }
    }

    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, ChatSourceRoom chatRoom) throws FusionException {
        EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(emoteCommand, ChatSource.ChatType.CHATROOM_CHAT, this.icePrxFinder);
        if (ec == null) {
            return EmoteCommand.ResultType.NOTHANDLED.value();
        }
        EmoteCommandState ecState = this.getEmoteCommandState(emoteCommand, ChatSource.ChatType.CHATROOM_CHAT);
        if (ecState != null) {
            return ecState.execute(ec.getEmoteCommandData(), new MessageData(message), ChatSource.createChatSourceForChatRoom(sessionProxy, chatRoom)).value();
        }
        return EmoteCommand.ResultType.NOTHANDLED.value();
    }

    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, ChatSourceGroup chatGroup) throws FusionException {
        EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(emoteCommand, ChatSource.ChatType.GROUP_CHAT, this.icePrxFinder);
        if (ec == null) {
            return EmoteCommand.ResultType.NOTHANDLED.value();
        }
        EmoteCommandState ecState = this.getEmoteCommandState(emoteCommand, ChatSource.ChatType.GROUP_CHAT);
        if (ecState != null) {
            return ecState.execute(ec.getEmoteCommandData(), new MessageData(message), ChatSource.createChatSourceForGroupChat(sessionProxy, chatGroup)).value();
        }
        return EmoteCommand.ResultType.NOTHANDLED.value();
    }

    public int executeEmoteCommandWithState(String emoteCommand, MessageDataIce message, SessionPrx sessionProxy, ChatSourceUser userI) throws FusionException {
        EmoteCommand ec = EmoteCommandFactory.getEmoteCommand(emoteCommand, ChatSource.ChatType.PRIVATE_CHAT, this.icePrxFinder);
        if (ec == null) {
            return EmoteCommand.ResultType.NOTHANDLED.value();
        }
        EmoteCommandState ecState = this.getEmoteCommandState(emoteCommand, ChatSource.ChatType.PRIVATE_CHAT);
        if (ecState != null) {
            return ecState.execute(ec.getEmoteCommandData(), new MessageData(message), ChatSource.createChatSourceForPrivateChat(sessionProxy, userI, message.source, message.messageDestinations[0].destination)).value();
        }
        return EmoteCommand.ResultType.NOTHANDLED.value();
    }
}

