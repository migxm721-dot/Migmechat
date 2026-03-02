/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.ObjectNotExistException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import Ice.ObjectNotExistException;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.objectcache.ChatGroup;
import com.projectgoth.fusion.objectcache.ChatGroupParticipant;
import com.projectgoth.fusion.objectcache.ChatParticipants;
import com.projectgoth.fusion.slice.MessageDataIce;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatGroupParticipants
extends ChatParticipants {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatGroup.class));
    private ConcurrentHashMap<String, ChatGroupParticipant> participants = new ConcurrentHashMap();
    private String id;

    ChatGroupParticipants(String id) {
        this.id = id;
    }

    public int getParticipantLimit() {
        return SystemProperty.getInt(SystemPropertyEntities.GroupChat.GROUP_CHAT_PARTICIPANT_LIMIT);
    }

    public void add(String username, ChatGroupParticipant participant) {
        this.participants.put(username, participant);
    }

    public ChatGroupParticipant get(String username) {
        return this.participants.get(username);
    }

    public boolean isParticipant(String username) {
        return this.participants.containsKey(username);
    }

    public String[] getUserNames() {
        ArrayList<String> usernames = new ArrayList<String>();
        for (ChatGroupParticipant p : this.participants.values()) {
            usernames.add(p.getUsername());
        }
        return usernames.toArray(new String[usernames.size()]);
    }

    public String getList(String separator) {
        StringBuffer sb = new StringBuffer();
        boolean firstParticipant = true;
        for (ChatGroupParticipant p : this.participants.values()) {
            if (!firstParticipant) {
                sb.append(separator);
            } else {
                firstParticipant = false;
            }
            sb.append(p.getUsername());
        }
        return sb.toString();
    }

    public List<ChatGroupParticipant> getParticipants() {
        ArrayList<ChatGroupParticipant> currentParticipants = new ArrayList<ChatGroupParticipant>(this.participants.size());
        currentParticipants.addAll(this.participants.values());
        return currentParticipants;
    }

    public String[] getParticipants(String excludeUsername) {
        ArrayList<String> participantUsernames = new ArrayList<String>();
        for (String username : this.participants.keySet()) {
            if (username.equalsIgnoreCase(excludeUsername)) continue;
            participantUsernames.add(username);
        }
        return participantUsernames.toArray(new String[participantUsernames.size()]);
    }

    public int[] getUserIDs() {
        ArrayList<Integer> participantUserIDs = new ArrayList<Integer>();
        for (ChatGroupParticipant p : this.participants.values()) {
            participantUserIDs.add(p.getUserID());
        }
        int[] results = new int[participantUserIDs.size()];
        for (int i = 0; i < participantUserIDs.size(); ++i) {
            results[i] = (Integer)participantUserIDs.get(i);
        }
        return results;
    }

    public int size() {
        return this.participants.size();
    }

    public boolean supportsBinaryMessage(String usernameToExclude) {
        for (ChatGroupParticipant p : this.participants.values()) {
            try {
                if (p.getUsername().equals(usernameToExclude) || !p.supportsBinaryMessage()) continue;
                return true;
            }
            catch (Exception e) {
            }
        }
        return false;
    }

    public void putFileReceived(MessageDataIce messageIce) {
        ArrayList<ChatGroupParticipant> removeParticipants = new ArrayList<ChatGroupParticipant>();
        for (ChatGroupParticipant participant : this.participants.values()) {
            if (messageIce.source.equals(participant.getUsername())) continue;
            try {
                participant.putFileReceived(messageIce);
            }
            catch (Exception e) {
                removeParticipants.add(participant);
            }
        }
        this.remove(removeParticipants);
    }

    public ChatGroupParticipant remove(String username) {
        ChatGroupParticipant participant = this.participants.remove(username);
        if (participant == null) {
            return null;
        }
        try {
            participant.leavingGroupChat();
        }
        catch (Exception exception) {
            // empty catch block
        }
        return participant;
    }

    public void removeAll() {
        for (ChatGroupParticipant participant : this.participants.values()) {
            try {
                participant.leavingGroupChat();
            }
            catch (Exception exception) {}
        }
        this.participants.clear();
    }

    public void removeAllParticipants() {
        LinkedList userNames = new LinkedList(this.participants.keySet());
        for (String userName : userNames) {
            this.remove(userName);
        }
    }

    private void remove(List<ChatGroupParticipant> removeParticipants) {
        if (removeParticipants == null) {
            return;
        }
        for (ChatGroupParticipant p : removeParticipants) {
            this.participants.remove(p.getUsername());
        }
    }

    public void notifyUserJoined(ChatGroupParticipant user) {
        for (ChatGroupParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(user.getUsername())) continue;
            if (!participant.hasUserProxy()) {
                this.debugLogNonNotification(user, participant, "joined group chat", "participant is offline (and has been since group chat was created");
                continue;
            }
            try {
                participant.notifyUserJoinedGroupChat(this.id, user.getUsername());
            }
            catch (ObjectNotExistException e) {
                this.debugLogNonNotification(user, participant, "joined group chat", "participant is offline");
            }
        }
    }

    public void notifyUserLeft(ChatGroupParticipant user) {
        for (ChatGroupParticipant participant : this.participants.values()) {
            if (participant.getUsername().equals(user.getUsername())) continue;
            if (!participant.hasUserProxy()) {
                this.debugLogNonNotification(user, participant, "left group chat", "participant is offline (and has been since group chat was created");
                continue;
            }
            try {
                participant.notifyUserLeftGroupChat(this.id, user.getUsername());
            }
            catch (ObjectNotExistException e) {
                this.debugLogNonNotification(user, participant, "left group chat", "participant is offline");
            }
        }
    }

    private void debugLogNonNotification(ChatGroupParticipant user, ChatGroupParticipant participant, String event, String reason) {
        if (!log.isDebugEnabled()) {
            return;
        }
        log.debug((Object)("Not notifying participant " + participant.getUsername() + "that user " + user.getUsername() + " has " + event + " as " + reason));
    }
}

