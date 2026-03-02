/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.jivesoftware.smack.Chat
 *  org.jivesoftware.smack.ChatManagerListener
 *  org.jivesoftware.smack.ConnectionListener
 *  org.jivesoftware.smack.MessageListener
 *  org.jivesoftware.smack.PacketListener
 *  org.jivesoftware.smack.RosterListener
 *  org.jivesoftware.smack.packet.Message
 *  org.jivesoftware.smack.packet.Packet
 *  org.jivesoftware.smack.packet.Presence
 */
package com.projectgoth.fusion.chat.external.gtalk;

import com.projectgoth.fusion.chat.external.ChatConnectionInterface;
import com.projectgoth.fusion.chat.external.ChatConnectionListenerInterface;
import com.projectgoth.fusion.chat.external.gtalk.GTalkStatus;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.fdl.enums.ImType;
import java.util.Collection;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GTalkListener
implements RosterListener,
PacketListener,
MessageListener,
ConnectionListener,
ChatManagerListener {
    private ChatConnectionInterface source;
    private ChatConnectionListenerInterface listener;
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GTalkListener.class));

    public GTalkListener(ChatConnectionInterface source, ChatConnectionListenerInterface listener) {
        if (source == null) {
            throw new IllegalArgumentException("Please provide a source");
        }
        if (listener == null) {
            throw new IllegalArgumentException("Please provide a listener");
        }
        if (source.getImType() != ImType.GTALK) {
            throw new IllegalArgumentException("Please provide a source of IM type GTalk");
        }
        this.source = source;
        this.listener = listener;
    }

    public void entriesAdded(Collection<String> entries) {
        if (entries != null && entries.size() > 0 && this.listener != null) {
            for (String contact : entries) {
                if (contact.indexOf("/") != -1) {
                    contact = contact.substring(0, contact.indexOf("/"));
                }
                String displayName = contact;
                if (contact.indexOf("@") != -1) {
                    displayName = contact.substring(0, contact.indexOf("@"));
                }
                this.listener.onContactDetail(this.source, contact, displayName);
            }
        }
    }

    public void entriesDeleted(Collection<String> arg0) {
    }

    public void entriesUpdated(Collection<String> arg0) {
    }

    public void presenceChanged(Presence presence) {
        String sender = presence.getFrom();
        if (sender.indexOf("/") != -1) {
            sender = sender.substring(0, sender.indexOf("/"));
        }
        this.listener.onContactStatusChanged(this.source, sender, GTalkStatus.fromXMPPPresence(presence).toFusionPresence());
    }

    public void processPacket(Packet packet) {
    }

    public void processMessage(Chat chat, Message msg) {
        if (msg.getBody() == null) {
            return;
        }
        String sender = msg.getFrom();
        if (sender.indexOf("/") != -1) {
            sender = sender.substring(0, sender.indexOf("/"));
        }
        this.listener.onMessageReceived(this.source, null, sender, msg.getBody());
    }

    public void connectionClosed() {
        this.listener.onDisconnected(this.source, "Connection to gtalk server closed");
    }

    public void connectionClosedOnError(Exception excep) {
        log.warn((Object)("GTalk connection closed with error for user:" + this.source.getUsername()), (Throwable)excep);
        this.listener.onDisconnected(this.source, excep.getMessage());
    }

    public void reconnectingIn(int arg0) {
    }

    public void reconnectionFailed(Exception excep) {
    }

    public void reconnectionSuccessful() {
        this.listener.onSignInSuccess(this.source);
    }

    public void chatCreated(Chat chat, boolean local) {
        if (local) {
            return;
        }
        chat.addMessageListener((MessageListener)this);
    }
}

