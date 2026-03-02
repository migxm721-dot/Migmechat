/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.PacketDetails;
import com.projectgoth.fusion.bothunter.Params;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PacketsPerClientSocket
extends ArrayList<PacketDetails> {
    private long lastAddedTo = System.currentTimeMillis();
    private Byte winscale = null;
    private Byte claimedClientType = null;

    @Override
    public Object clone() {
        PacketsPerClientSocket clone = (PacketsPerClientSocket)super.clone();
        clone.setLastAddedTo(this.lastAddedTo);
        return clone;
    }

    @Override
    public boolean add(PacketDetails pd) {
        if (pd == null) {
            throw new RuntimeException("Trying to add null PacketDetails!");
        }
        this.lastAddedTo = System.currentTimeMillis();
        if (pd.getTcpWinscale() != null) {
            this.winscale = pd.getTcpWinscale();
            return true;
        }
        if (pd.getClaimedClientType() != null) {
            this.claimedClientType = pd.getClaimedClientType();
        }
        return super.add(pd);
    }

    public boolean isTimedOut() {
        return (System.currentTimeMillis() - this.lastAddedTo) / 1000L > (long)Params.CLIENT_PORT_TIMEOUT_SECS;
    }

    public long getLastAddedTo() {
        return this.lastAddedTo;
    }

    public void setLastAddedTo(long value) {
        this.lastAddedTo = value;
    }

    public double getMeanTcpTimestamp() {
        double sum = 0.0;
        for (PacketDetails pd : this) {
            sum += (double)pd.getTcpTimestamp();
        }
        return sum / (double)this.size();
    }

    public double getMeanTcpTimestampOverArrivalTime() {
        double sum = 0.0;
        for (PacketDetails pd : this) {
            sum += pd.getTcpTimestampOverArrivalTime();
        }
        return sum / (double)this.size();
    }

    public double getTcpTimestampOverArrivalTimeStdError(double meanRatio) {
        double sumDeltaSquared = 0.0;
        for (PacketDetails pd : this) {
            sumDeltaSquared += Math.pow(pd.getTcpTimestampOverArrivalTime() - meanRatio, 2.0);
        }
        double oneOverNMinusOne = 1.0 / ((double)this.size() - 1.0);
        return Math.sqrt(oneOverNMinusOne * sumDeltaSquared);
    }

    public Byte getLastWinscaleSeen() {
        return this.winscale;
    }

    public Byte getClaimedClientType() {
        return this.claimedClientType;
    }
}

