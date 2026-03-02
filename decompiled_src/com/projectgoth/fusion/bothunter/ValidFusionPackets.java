/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.bothunter;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.packet.FusionPacket;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class ValidFusionPackets {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ValidFusionPackets.class));
    private final ConcurrentHashMap<Short, String> validTypes = new ConcurrentHashMap();

    private ValidFusionPackets() {
        try {
            this.populate();
        }
        catch (Exception e) {
            log.error((Object)("Unable to populate ValidFusionPackets: e=" + e));
        }
    }

    public static ValidFusionPackets getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private void populate() throws Exception {
        Field[] fields;
        int modMask = 25;
        for (Field f : fields = FusionPacket.class.getDeclaredFields()) {
            try {
                boolean equality;
                if ((f.getModifiers() & 0x19) != 25) continue;
                boolean bl = equality = f.getType() != null && f.getType().equals(Short.TYPE);
                if (!equality) continue;
                f.setAccessible(true);
                Short value = (Short)f.get(null);
                this.validTypes.put(value, f.getName());
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("Adding fusion packet=" + value + " " + f.getName()));
            }
            catch (Exception e) {
                log.error((Object)("Exception parsing field=" + f.getName() + " e=" + e));
            }
        }
        log.info((Object)("Found " + this.validTypes.size() + " fusion packet types"));
    }

    public boolean contains(short packetType) {
        return this.validTypes.get(packetType) != null;
    }

    private static class SingletonHolder {
        public static final ValidFusionPackets INSTANCE = new ValidFusionPackets();

        private SingletonHolder() {
        }
    }
}

