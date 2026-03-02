/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.PacketDetails;
import com.projectgoth.fusion.bothunter.PacketsPerClientSocket;
import com.projectgoth.fusion.bothunter.Params;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PacketsPerIP {
    private boolean underAnalysis = false;
    private String ip;
    private long lastAddedTo = System.currentTimeMillis();
    private ConcurrentHashMapOfArrayLists<Integer, PacketsPerClientSocket> contents;

    public PacketsPerIP(String ip) {
        this.ip = ip;
        this.contents = new ConcurrentHashMapOfArrayLists();
    }

    public PacketsPerIP(String ip, ConcurrentHashMapOfArrayLists<Integer, PacketsPerClientSocket> pContents) {
        this.ip = ip;
        this.contents = (ConcurrentHashMapOfArrayLists)pContents.clone();
    }

    public String getIP() {
        return this.ip;
    }

    public Object clone() {
        PacketsPerIP copy = new PacketsPerIP(this.ip, this.contents);
        return copy;
    }

    public synchronized void addPacket(PacketDetails p) {
        PacketsPerClientSocket pps = (PacketsPerClientSocket)this.contents.get(p.getClientPort());
        if (pps == null) {
            pps = new PacketsPerClientSocket();
            this.contents.put(p.getClientPort(), pps);
        }
        pps.add(p);
        if (pps.size() > Params.MAX_PACKETS_PER_SOCKET) {
            pps.remove(0);
        }
        this.lastAddedTo = System.currentTimeMillis();
    }

    public PacketsPerClientSocket getPacketsPerClientSocket(int clientSocket) {
        return (PacketsPerClientSocket)this.contents.get(clientSocket);
    }

    public int[] getPortAndPacketCount() {
        int portCount = 0;
        int packetCount = 0;
        Iterator i$ = this.contents.keySet().iterator();
        while (i$.hasNext()) {
            int socket = (Integer)i$.next();
            PacketsPerClientSocket ppcs = (PacketsPerClientSocket)this.contents.get(socket);
            ++portCount;
            packetCount += ppcs.size();
        }
        int[] result = new int[]{portCount, packetCount};
        return result;
    }

    public synchronized void setUnderAnalysis() throws BeingAnalyzedException, TimedOutException {
        if ((System.currentTimeMillis() - this.lastAddedTo) / 1000L > (long)Params.CLIENT_IP_TIMEOUT_SECS) {
            throw new TimedOutException();
        }
        if (this.underAnalysis) {
            throw new BeingAnalyzedException();
        }
        this.underAnalysis = true;
    }

    public void doCleanupDuringAnalysis() {
        Set keys = this.contents.keySet();
        for (Integer clientPort : keys) {
            PacketsPerClientSocket ppcs = (PacketsPerClientSocket)this.contents.get(clientPort);
            if (!ppcs.isTimedOut()) continue;
            this.contents.remove(clientPort);
        }
    }

    public synchronized void clearUnderAnalysis() {
        this.underAnalysis = false;
    }

    public Set<Integer> getSockets() {
        return this.contents.keySet();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class ConcurrentHashMapOfArrayLists<K, V extends ArrayList>
    extends ConcurrentHashMap<K, V> {
        private static final long serialVersionUID = 1L;

        private ConcurrentHashMapOfArrayLists() {
        }

        @Override
        public Object clone() {
            ConcurrentHashMapOfArrayLists copy = new ConcurrentHashMapOfArrayLists();
            Set keys = this.keySet();
            for (Object key : keys) {
                ArrayList value = (ArrayList)this.get(key);
                if (value != null) {
                    value = (ArrayList)value.clone();
                }
                copy.put(key, value);
            }
            return copy;
        }
    }

    public class TimedOutException
    extends Exception {
    }

    public class BeingAnalyzedException
    extends Exception {
    }
}

