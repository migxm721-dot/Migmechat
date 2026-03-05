/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Object
 *  IceInternal.Ex
 *  IceInternal.Patcher
 */
package com.projectgoth.fusion.slice;

import Ice.Object;
import IceInternal.Ex;
import com.projectgoth.fusion.slice.EventQueueWorkerServiceStats;

public final class EventQueueWorkerServiceStatsHolder {
    public EventQueueWorkerServiceStats value;

    public EventQueueWorkerServiceStatsHolder() {
    }

    public EventQueueWorkerServiceStatsHolder(EventQueueWorkerServiceStats value) {
        this.value = value;
    }

    public Patcher getPatcher() {
        return new Patcher();
    }

    public class Patcher
    implements IceInternal.Patcher {
        public void patch(Object v) {
            try {
                EventQueueWorkerServiceStatsHolder.this.value = (EventQueueWorkerServiceStats)v;
            }
            catch (ClassCastException ex) {
                Ex.throwUOE((String)this.type(), (String)v.ice_id());
            }
        }

        public String type() {
            return "::com::projectgoth::fusion::slice::EventQueueWorkerServiceStats";
        }
    }
}

