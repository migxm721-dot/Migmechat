/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jpcap.packet.TCPPacket
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.ClientAddress;
import com.projectgoth.fusion.bothunter.MonitorThread;
import com.projectgoth.fusion.common.ConfigUtils;
import java.util.HashMap;
import jpcap.packet.TCPPacket;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClientAddressToUsername
extends HashMap<String, String> {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MonitorThread.class));
    private static final long serialVersionUID = 1L;
    private static final String COLON = ":";
    private static ClientAddressToUsername instance;
    private boolean debugEnabled = log.isDebugEnabled();

    private ClientAddressToUsername() {
    }

    public static synchronized ClientAddressToUsername getInstance() {
        if (instance == null) {
            instance = new ClientAddressToUsername();
        }
        return instance;
    }

    public void putUsername(TCPPacket loginPacket, String username) {
        String key = loginPacket.src_ip.toString() + COLON + loginPacket.src_port;
        if (this.debugEnabled) {
            log.debug((Object)("Caching username=" + username + " against key=" + key));
        }
        this.put(key, username);
    }

    public String getUsername(TCPPacket packet) {
        String key = packet.src_ip.toString() + COLON + packet.src_port;
        if (this.debugEnabled) {
            log.debug((Object)("Retrieving username for key=" + key));
        }
        return (String)this.get(key);
    }

    public String getUsername(ClientAddress ca) {
        String key = ca.getClientIP() + COLON + ca.getClientPort();
        if (this.debugEnabled) {
            log.debug((Object)("Retrieving username for key=" + key));
        }
        return (String)this.get(key);
    }
}

