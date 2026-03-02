/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chat.external.msn;

import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.msn.Command;
import com.projectgoth.fusion.chat.external.msn.Connection;
import com.projectgoth.fusion.chat.external.msn.MSNException;
import com.projectgoth.fusion.chat.external.msn.MSNObject;
import com.projectgoth.fusion.chat.external.msn.MSNSLPMessage;
import com.projectgoth.fusion.chat.external.msn.P2PMessage;
import com.projectgoth.fusion.chat.external.msn.P2PSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SwitchBoard
extends Connection {
    private ChatConnectionInterface source;
    private ChatConnectionListenerInterface listener;
    private Map<String, SwitchBoard> switchBoards;
    private Set<String> users = new HashSet<String>();
    private List<P2PSession> p2pSessions = new ArrayList<P2PSession>();
    private String conferenceID;
    private Object waitingForUser = new Object();
    protected MSNObject displayPicture;

    public SwitchBoard(Map<String, SwitchBoard> switchBoards, ChatConnectionInterface source, ChatConnectionListenerInterface listener, String server, int port, MSNObject displayPicture, int timeout) throws MSNException {
        this.listener = listener;
        this.source = source;
        this.switchBoards = switchBoards;
        this.displayPicture = displayPicture;
        this.connectionTimeout = timeout;
        this.connect(server, port);
    }

    public SwitchBoard(Map<String, SwitchBoard> switchBoards, ChatConnectionInterface source, ChatConnectionListenerInterface listener, MSNObject displayPicture, int timeout) throws MSNException {
        this.listener = listener;
        this.source = source;
        this.switchBoards = switchBoards;
        this.displayPicture = displayPicture;
        this.connectionTimeout = timeout;
    }

    public void signIn(String username, String ticket) throws MSNException {
        try {
            this.sendCommand(new Command(Command.Type.USR).addParam(username).addParam(ticket));
        }
        catch (MSNException e) {
            this.disconnect("Failed to signin to switchboard - " + e.getMessage());
            throw e;
        }
    }

    public void join(String username, String sessionId, String ticket) throws MSNException {
        try {
            this.sendCommand(new Command(Command.Type.ANS).addParam(username).addParam(ticket).addParam(sessionId));
        }
        catch (MSNException e) {
            this.disconnect("Failed to join switchboard - " + e.getMessage());
            throw e;
        }
    }

    public void sendMessage(String username, String message) throws MSNException {
        if (!username.equals(this.conferenceID)) {
            this.waitForUser(username);
        }
        StringBuilder builder = new StringBuilder("MIME-Version: 1.0\r\nContent-Type: text/plain; charset=");
        builder.append("UTF-8").append("\r\n\r\n").append(message);
        try {
            byte[] payload = builder.toString().getBytes("UTF-8");
            this.sendCommand(new Command(Command.Type.MSG).addParam("A").setPayload(payload));
        }
        catch (UnsupportedEncodingException e) {
            throw new MSNException(e.getMessage());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateSwitchBoards(String username) {
        SwitchBoard oldSwitchBoard;
        Map<String, SwitchBoard> map = this.switchBoards;
        synchronized (map) {
            oldSwitchBoard = this.switchBoards.put(username, this);
        }
        if (oldSwitchBoard != null && oldSwitchBoard != this) {
            oldSwitchBoard.disconnect("");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void convertToConference(String creator) {
        Set<String> set = this.users;
        synchronized (set) {
            if (this.conferenceID == null) {
                for (String username : this.users) {
                    Map<String, SwitchBoard> map = this.switchBoards;
                    synchronized (map) {
                        if (this == this.switchBoards.get(username)) {
                            this.switchBoards.remove(username);
                        }
                    }
                }
                Map<String, SwitchBoard> map = this.switchBoards;
                synchronized (map) {
                    long maxKey = 0L;
                    for (String key : this.switchBoards.keySet()) {
                        try {
                            long thisKey = Long.parseLong(key);
                            if (thisKey <= maxKey) continue;
                            maxKey = thisKey;
                        }
                        catch (NumberFormatException e) {}
                    }
                    this.conferenceID = String.valueOf(maxKey + 1L);
                    this.updateSwitchBoards(this.conferenceID);
                }
                if (this.listener != null) {
                    this.listener.onConferenceCreated(this.source, this.conferenceID, creator);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String inviteToConference(String username) throws MSNException {
        Set<String> set = this.users;
        synchronized (set) {
            if (!this.users.contains(username)) {
                this.sendCommand(new Command(Command.Type.CAL).addParam(username));
            }
            return this.conferenceID;
        }
    }

    public String getConferenceID() {
        return this.conferenceID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> getUsers() {
        Set<String> set = this.users;
        synchronized (set) {
            return new LinkedList<String>(this.users);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void waitForUser(String username) throws MSNException {
        Object object = this.waitingForUser;
        synchronized (object) {
            Set<String> set = this.users;
            synchronized (set) {
                block12: {
                    if (!this.users.contains(username)) {
                        try {
                            this.sendCommand(new Command(Command.Type.CAL).addParam(username));
                            try {
                                this.users.wait(20000L);
                            }
                            catch (Exception e) {
                                // empty catch block
                            }
                            if (!this.users.contains(username)) {
                                throw new MSNException("Timeout while waiting for user " + username);
                            }
                        }
                        catch (MSNException e) {
                            if (e.getMSNErrorCode() == 215) break block12;
                            throw e;
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addUser(String username) {
        Set<String> set = this.users;
        synchronized (set) {
            if (this.users.size() > 0) {
                this.convertToConference(this.users.iterator().next());
            }
            if (this.isConnected() && this.conferenceID == null) {
                this.updateSwitchBoards(username);
            }
            this.users.add(username);
            if (this.conferenceID != null && this.listener != null) {
                this.listener.onUserJoinedConference(this.source, this.conferenceID, username);
            }
            this.users.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeUser(String username) {
        Set<String> set = this.users;
        synchronized (set) {
            Map<String, SwitchBoard> map = this.switchBoards;
            synchronized (map) {
                if (this == this.switchBoards.get(username)) {
                    this.switchBoards.remove(username);
                }
            }
            this.users.remove(username);
            if (this.conferenceID != null && this.listener != null) {
                this.listener.onUserLeftConference(this.source, this.conferenceID, username);
            }
            if (this.users.size() == 0) {
                this.disconnect("");
            }
        }
    }

    @Override
    protected void onDisconnect(String reason) {
        this.p2pSessions.clear();
        this.users.clear();
        this.listener = null;
        this.switchBoards = null;
        this.displayPicture = null;
        this.conferenceID = null;
    }

    @Override
    protected void onIncomingCommand(Command incomingCommand, Command originalCommand) {
        switch (incomingCommand.getType()) {
            case IRO: {
                this.addUser(incomingCommand.getParam(3));
                break;
            }
            case JOI: {
                this.addUser(incomingCommand.getParam(0));
                break;
            }
            case BYE: {
                this.removeUser(incomingCommand.getParam(0));
                break;
            }
            case MSG: {
                this.onMSG(incomingCommand);
                break;
            }
        }
    }

    private void onMSG(Command msg) {
        try {
            byte[] payload = msg.getPayload();
            if (payload != null) {
                String content = new String(payload, "UTF-8");
                Pattern plainTextPattern = Pattern.compile("Content-Type: text/plain(.*)\\r\\n\\r\\n(.+)", 32);
                Matcher matcher = plainTextPattern.matcher(content);
                if (matcher.find()) {
                    if (this.listener != null) {
                        this.listener.onMessageReceived(this.source, this.conferenceID, msg.getParam(0), matcher.group(2));
                    }
                } else if (content.contains("Content-Type: application/x-msnmsgrp2p")) {
                    this.onP2PMessage(msg.getParam(0), new P2PMessage(new String(payload, "ISO8859_1")));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onP2PMessage(String source, P2PMessage message) {
        MSNSLPMessage msnslp = message.getMSNSLP();
        if (msnslp != null && msnslp.getType() == MSNSLPMessage.Type.INVITE) {
            MSNSLPMessage.Content msnslpContent = msnslp.getContent();
            if (msnslpContent != null && "{A4268EEC-FEC5-49E5-95C3-F126696BDBF6}".equals(msnslpContent.getString("EUF-GUID")) && this.displayPicture != null) {
                P2PSession session = new P2PSession(this, source, message);
                this.p2pSessions.add(session);
            }
            return;
        }
        for (P2PSession session : this.p2pSessions) {
            if (!session.belongsToMe(message)) continue;
            session.onP2PMessage(source, message);
            break;
        }
    }
}

