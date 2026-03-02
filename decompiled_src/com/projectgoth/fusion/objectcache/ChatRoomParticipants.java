/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.objectcache.ChatParticipants;
import com.projectgoth.fusion.objectcache.ChatRoomParticipant;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.FusionExceptionWithErrorCauseCode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatRoomParticipants
extends ChatParticipants {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatRoomParticipants.class));
    private final Map<String, ChatRoomParticipant> participants = new ConcurrentHashMap<String, ChatRoomParticipant>();
    private final String chatRoomName;

    public ChatRoomParticipants(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    private String getRoomName() {
        return this.chatRoomName;
    }

    public ChatRoomParticipant get(String username) {
        return this.participants.get(username);
    }

    public Set<String> getAllNames() {
        return this.participants.keySet();
    }

    public Collection<ChatRoomParticipant> getAll() {
        return this.participants.values();
    }

    public ChatRoomParticipant remove(String username) {
        return this.participants.remove(username);
    }

    public boolean isParticipant(String username) {
        return this.participants.containsKey(username);
    }

    public ChatRoomParticipant verifyYouAreParticipant(String username) throws FusionException {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant == null) {
            throw new FusionException("You are not in the " + this.getRoomName() + " chat room");
        }
        return participant;
    }

    public ChatRoomParticipant verifyIsParticipant(String username) throws FusionException {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant == null) {
            throw new FusionException(username + " is no longer in the chat room " + this.getRoomName());
        }
        return participant;
    }

    public ChatRoomParticipant verifyYouAreParticipant(String username, ErrorCause errorCause) throws FusionExceptionWithErrorCauseCode {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant == null) {
            throw new FusionExceptionWithErrorCauseCode("You are not in the " + this.getRoomName() + " chat room", errorCause.getCode());
        }
        return participant;
    }

    public ChatRoomParticipant verifyIsParticipant(String username, ErrorCause error) throws FusionExceptionWithErrorCauseCode {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant == null) {
            throw new FusionExceptionWithErrorCauseCode(username + " is no longer in the chat room " + this.getRoomName() + " chat room", error.getCode());
        }
        return participant;
    }

    public ChatRoomParticipant verifyHasNotLeftParticipant(String username) throws FusionException {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant == null) {
            throw new FusionException(username + " has left the chat");
        }
        return participant;
    }

    public String[] getAdministrators(String requestingUsername) {
        ArrayList<String> list = new ArrayList<String>();
        for (ChatRoomParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(requestingUsername) || !participant.hasAdminOrModeratorRights() || participant.isHiddenAdmin()) continue;
            list.add(participant.getUsername());
        }
        return list.toArray(new String[list.size()]);
    }

    public int size() {
        return this.participants.size();
    }

    public boolean isEmpty() {
        return this.participants.isEmpty();
    }

    public boolean isAdministrator(String requestingUsername) {
        ChatRoomParticipant participant = this.participants.get(requestingUsername);
        if (participant == null) {
            return false;
        }
        return participant.hasAdminOrModeratorRights() && !participant.isHiddenAdmin();
    }

    public String[] getAllParticipants(String requestingUsername) {
        ArrayList<String> list = new ArrayList<String>();
        for (ChatRoomParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(requestingUsername)) continue;
            list.add(participant.getUsername());
        }
        return list.toArray(new String[list.size()]);
    }

    public String[] getAllParticipantsExceptHiddenAdmins(String requestingUsername) {
        ArrayList<String> list = new ArrayList<String>();
        for (ChatRoomParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(requestingUsername) || participant.isHiddenAdmin()) continue;
            list.add(participant.getUsername());
        }
        return list.toArray(new String[list.size()]);
    }

    public HashMap<String, List<String>> getIpToUserMap() {
        HashMap<String, List<String>> ipToUsernames = new HashMap<String, List<String>>();
        for (Map.Entry<String, ChatRoomParticipant> entry : this.participants.entrySet()) {
            String username = entry.getKey();
            ChatRoomParticipant participant = entry.getValue();
            String ip = participant.getIPAddress();
            if (!ipToUsernames.containsKey(ip)) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(username);
                ipToUsernames.put(ip, list);
                continue;
            }
            ipToUsernames.get(ip).add(username);
        }
        return ipToUsernames;
    }

    public ChatRoomParticipant add(ChatRoomParticipant participant) {
        return this.participants.put(participant.getUsername(), participant);
    }

    public void updateLastTimeMessageSent(String username) {
        ChatRoomParticipant participant = this.participants.get(username);
        if (participant != null) {
            participant.updateLastTimeMessageSent();
        }
    }

    public void notifyUserLeftChatRoom(String username) {
        for (ChatRoomParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(username)) continue;
            participant.notifyUserLeftChatRoom_async(username);
        }
    }

    public void notifyUserJoinedChatRoom(boolean isBanned, String username) {
        boolean isAdministrator = this.isAdministrator(username);
        for (ChatRoomParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(username)) continue;
            participant.notifyUserJoinedChatRoom_async(username, isAdministrator, isBanned);
        }
    }
}

