/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.gateway.packet;

import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.gateway.ConnectionI;
import com.projectgoth.fusion.gateway.packet.FusionPktError;
import com.projectgoth.fusion.gateway.packet.FusionPktOk;
import com.projectgoth.fusion.gateway.packet.FusionRequest;
import com.projectgoth.fusion.packet.FusionPacket;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.ShortTextStatusUserEventIce;
import com.projectgoth.fusion.userevent.domain.ShortTextStatusUserEvent;

public class FusionPktChangeUserEventSetting
extends FusionRequest {
    public FusionPktChangeUserEventSetting() {
        super((short)921);
    }

    public FusionPktChangeUserEventSetting(short transactionId) {
        super((short)921, transactionId);
    }

    public FusionPktChangeUserEventSetting(FusionPacket packet) {
        super(packet);
    }

    public Byte getAllEvents() {
        return this.getByteField((short)1);
    }

    public void setAllEvents(byte allEvents) {
        this.setField((short)1, allEvents);
    }

    public boolean sessionRequired() {
        return true;
    }

    protected FusionPacket[] processRequest(ConnectionI connection) {
        Byte allEvents = this.getAllEvents();
        if (allEvents != null && allEvents == 1) {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED) && connection.findEventSystem() != null) {
                    connection.findEventSystem().streamEventsToLoggingInUser(connection.getUsername(), connection.getConnectionPrx());
                } else {
                    ShortTextStatusUserEvent event = new ShortTextStatusUserEvent();
                    event.setGeneratingUsername("migme");
                    event.setStatus(SystemProperty.get(SystemPropertyEntities.Default.EVENT_SYSTEM_WELCOME_MESSAGE));
                    event.setTimestamp(System.currentTimeMillis());
                    ShortTextStatusUserEventIce eventIce = event.toIceEvent();
                    connection.putEvent(eventIce);
                }
            }
            catch (FusionException e) {
                return new FusionPacket[]{new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to change user event setting - " + e.message)};
            }
            catch (Exception e) {
                return new FusionPacket[]{new FusionPktError(this.transactionId, FusionPktError.Code.UNDEFINED, "Failed to change user event setting - " + e.getMessage())};
            }
        }
        return new FusionPacket[]{new FusionPktOk(this.transactionId)};
    }
}

