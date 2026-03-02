/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.fdl.packets.FusionPktDataEmoticonHotkeys;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public class FusionPktEmoticonHotkeys
extends FusionPktDataEmoticonHotkeys {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktEmoticonHotkeys.class));

    public FusionPktEmoticonHotkeys() {
    }

    public FusionPktEmoticonHotkeys(FusionPacket packet) {
        super(packet);
    }

    public FusionPktEmoticonHotkeys(short transactionId, UserPrx userPrx) {
        super(transactionId);
        if (userPrx == null) {
            log.error((Object)"UserPrx is null");
            throw new IllegalArgumentException();
        }
        this.setHotkeyList(userPrx.getEmoticonHotKeys());
        this.setAlternateHotkeyList(userPrx.getEmoticonAlternateKeys());
    }
}

