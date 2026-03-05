/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.UserPrx;
import org.apache.log4j.Logger;

public class FusionPktEmoticonHotKeysOld
extends FusionPacket {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(FusionPktEmoticonHotKeysOld.class));

    public FusionPktEmoticonHotKeysOld() {
        super((short)916);
    }

    public FusionPktEmoticonHotKeysOld(short transactionId) {
        super((short)916, transactionId);
    }

    public FusionPktEmoticonHotKeysOld(FusionPacket packet) {
        super(packet);
    }

    public FusionPktEmoticonHotKeysOld(short transactionId, UserPrx userPrx) {
        super((short)916, transactionId);
        if (userPrx == null) {
            log.error((Object)"UserPrx is null");
            throw new IllegalArgumentException();
        }
        this.setHotKeys(StringUtil.join(userPrx.getEmoticonHotKeys(), " "));
        this.setAlternateKeys(StringUtil.join(userPrx.getEmoticonAlternateKeys(), " "));
    }

    public String getHotKeys() {
        return this.getStringField((short)1);
    }

    public void setHotKeys(String hotKeys) {
        this.setField((short)1, hotKeys);
    }

    public String getAlternateKeys() {
        return this.getStringField((short)2);
    }

    public void setAlternateKeys(String alternateKeys) {
        this.setField((short)2, alternateKeys);
    }
}

