/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.eventqueue;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.eventqueue.Event;
import com.projectgoth.fusion.eventqueue.queues.EventQueueClient;
import com.projectgoth.fusion.eventqueue.queues.MemoryQueueClient;
import com.projectgoth.fusion.eventqueue.queues.RedisQueueClient;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EventQueue {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EventQueue.class));

    protected static EventQueueClient getNewClient() {
        String queueType = SystemProperty.get(SystemPropertyEntities.EventQueueSettings.QUEUE_TYPE);
        try {
            if (StringUtil.isBlank(queueType)) {
                log.warn((Object)"System property 'fusion.eventqueue.queuetype' not set. Using RedisQueueClient as default");
                return new RedisQueueClient();
            }
            if (queueType.equals("redis")) {
                return new RedisQueueClient();
            }
            if (queueType.equals("memory")) {
                return new MemoryQueueClient();
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to create EventQueue client :" + e.getMessage()), (Throwable)e);
        }
        return null;
    }

    public static EventQueueClient getClient() {
        return EventQueue.getNewClient();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void enqueueSingleEvent(Event e) {
        if (!SystemProperty.getBool("EventQueueEnabled", false)) {
            return;
        }
        EventQueueClient client = null;
        client = EventQueue.getClient();
        client.enqueue(e);
        Object var4_2 = null;
        if (client == null) return;
        try {
            client.disconnectClient();
            return;
        }
        catch (Exception ex2) {
            return;
        }
        {
            catch (Exception ex) {
                log.error((Object)("Unable to enqueue event: " + ex.getMessage()), (Throwable)ex);
                Object var4_3 = null;
                if (client == null) return;
                try {
                    client.disconnectClient();
                    return;
                }
                catch (Exception ex2) {
                    return;
                }
            }
        }
        catch (Throwable throwable) {
            Object var4_4 = null;
            if (client == null) throw throwable;
            try {
                client.disconnectClient();
                throw throwable;
            }
            catch (Exception ex2) {
                client = null;
            }
            throw throwable;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void enqueueMultipleEvents(List<Event> eventList) {
        if (!SystemProperty.getBool("EventQueueEnabled", false)) {
            return;
        }
        EventQueueClient client = null;
        client = EventQueue.getClient();
        for (Event e : eventList) {
            client.enqueue(e);
        }
        Object var5_5 = null;
        if (client == null) return;
        try {
            client.disconnectClient();
            return;
        }
        catch (Exception ex2) {
            return;
        }
        {
            catch (Exception ex) {
                log.error((Object)("Unable to enqueue multiple events : " + ex.getMessage()), (Throwable)ex);
                Object var5_6 = null;
                if (client == null) return;
                try {
                    client.disconnectClient();
                    return;
                }
                catch (Exception ex2) {
                    return;
                }
            }
        }
        catch (Throwable throwable) {
            Object var5_7 = null;
            if (client == null) throw throwable;
            try {
                client.disconnectClient();
                throw throwable;
            }
            catch (Exception ex2) {
                client = null;
            }
            throw throwable;
        }
    }
}

