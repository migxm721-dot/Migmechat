/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.bothunter.PacketDetails;

public interface PacketSource {
    public PacketDetails nextPacket() throws Exception;

    public boolean finished() throws Exception;

    public void close();

    public String getSourceIP();

    public String getDestinationIP();

    public long getTcpTimestamp();

    public long getTimeReceived();
}

