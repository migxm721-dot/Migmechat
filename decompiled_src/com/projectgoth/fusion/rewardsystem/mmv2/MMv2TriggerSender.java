/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mig33.rabbitmqclient.RabbitMQ
 *  com.projectgoth.leto.common.event.Event
 *  com.projectgoth.leto.common.event.EventEnvelope
 *  com.projectgoth.leto.common.event.EventMarshaller
 *  com.rabbitmq.client.AMQP$BasicProperties
 *  com.rabbitmq.client.MessageProperties
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.rewardsystem.mmv2;

import com.mig33.rabbitmqclient.RabbitMQ;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.RewardProgramData;
import com.projectgoth.fusion.rewardsystem.mmv2.MMv2RabbitMQ;
import com.projectgoth.fusion.rewardsystem.triggers.RewardProgramTrigger;
import com.projectgoth.leto.common.event.Event;
import com.projectgoth.leto.common.event.EventEnvelope;
import com.projectgoth.leto.common.event.EventMarshaller;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.MessageProperties;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class MMv2TriggerSender {
    public static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MMv2TriggerSender.class));
    private static final String CURRENT_EVENT_ENVELOP_VERSION = "1.0";
    private ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 5, 300000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    private final LazyLoader<Set<RewardProgramData.TypeEnum>> whitelistedTriggerTypesLoader = new LazyLoader<Set<RewardProgramData.TypeEnum>>("MMv2TriggerSender.WhitelistedTypes", 60000L){

        @Override
        protected Set<RewardProgramData.TypeEnum> fetchValue() throws Exception {
            int[] triggerTypeWhitelist = SystemProperty.getIntArray(SystemPropertyEntities.MarketingMechanicsV2.TRIGGER_TYPE_WHITELIST);
            if (triggerTypeWhitelist != null && triggerTypeWhitelist.length > 0) {
                HashSet<RewardProgramData.TypeEnum> list = new HashSet<RewardProgramData.TypeEnum>();
                for (int triggerType : triggerTypeWhitelist) {
                    RewardProgramData.TypeEnum type = RewardProgramData.TypeEnum.fromValue(triggerType);
                    if (type == null) continue;
                    list.add(type);
                }
                return list;
            }
            return Collections.EMPTY_SET;
        }
    };
    private static volatile MMv2TriggerSender overrideInstance;

    protected MMv2TriggerSender() {
    }

    public static MMv2TriggerSender getInstance() {
        return overrideInstance == null ? MMv2TriggerSenderSingletonHolder.INSTANCE : overrideInstance;
    }

    protected static void overrideInstance(MMv2TriggerSender instance) {
        overrideInstance = instance;
    }

    public static void resetInstanceToDefault() {
        MMv2TriggerSender.overrideInstance(null);
    }

    public final void send(RewardProgramTrigger trigger) throws Exception {
        if (this.mustSendToRMQ(trigger)) {
            this.sendTriggerToRMQ(trigger);
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("Not sending trigger [" + trigger + "] to RMQ"));
        }
    }

    private boolean mustSendToRMQ(RewardProgramTrigger trigger) {
        if (trigger == null) {
            return false;
        }
        boolean enableOutgoingTrigger = SystemProperty.getBool(SystemPropertyEntities.MarketingMechanicsV2.ENABLE_OUTBOUND_TRIGGERS);
        if (enableOutgoingTrigger) {
            boolean sendWhiteListedTypesOnly = SystemProperty.getBool(SystemPropertyEntities.MarketingMechanicsV2.SEND_WHITELIST_TRIGGER_TYPES_ONLY);
            if (sendWhiteListedTypesOnly) {
                if (trigger.programType != null) {
                    Set<RewardProgramData.TypeEnum> whitelist = this.whitelistedTriggerTypesLoader.getValue();
                    return whitelist != null && whitelist.contains(trigger.programType);
                }
                return false;
            }
            return true;
        }
        return false;
    }

    private void sendTriggerToRMQ(RewardProgramTrigger trigger) throws Exception {
        RoutingSpec routingSpec = MMv2TriggerSender.getRoutingSpec(trigger);
        if (log.isDebugEnabled()) {
            log.debug((Object)("sending trigger [" + trigger + "] with routing spec [" + routingSpec + "]"));
        }
        if (routingSpec.isUsesSynchronousDispatch()) {
            this.asyncDispatch(routingSpec, trigger);
        } else {
            this.dispatch(routingSpec, trigger);
        }
    }

    private void asyncDispatch(RoutingSpec routingSpec, RewardProgramTrigger trigger) {
        int maxQueueSize;
        int queueSize;
        if (log.isDebugEnabled()) {
            log.debug((Object)("asyncDispatch [" + trigger + "] with routing spec [" + routingSpec + "]"));
        }
        if ((queueSize = this.executor.getQueue().size()) <= (maxQueueSize = SystemProperty.getInt(SystemPropertyEntities.MarketingMechanicsV2_RMQ.MAX_DISPATCH_TASK_SIZE))) {
            this.executor.submit(new DispatchEventEnvelop(routingSpec, trigger));
        } else {
            log.warn((Object)("DispatchEventEnvelop queue too busy (" + queueSize + "/" + maxQueueSize + "). Dropping Trigger: " + trigger.toString()));
        }
    }

    private void dispatch(RoutingSpec routingSpec, RewardProgramTrigger trigger) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("dispatching [" + trigger + "] with routing spec [" + routingSpec + "]"));
        }
        EventEnvelope eventEnvelop = MMv2TriggerSender.toEventEnvelope(trigger);
        byte[] eventEnvelopBytes = EventMarshaller.getInstance().marshalToBytes(eventEnvelop);
        this.send(routingSpec, eventEnvelopBytes);
        if (log.isDebugEnabled()) {
            log.debug((Object)("sent [" + trigger + "] with routing spec [" + routingSpec + "]"));
        }
    }

    protected void send(RoutingSpec routingSpec, byte[] eventEnvelopBytes) throws Exception {
        String exchange = SystemProperty.get(SystemPropertyEntities.MarketingMechanicsV2_RMQ.EXCHANGE_NAME);
        AMQP.BasicProperties messageProperties = routingSpec.isUsesPersistentDelivery() ? MessageProperties.PERSISTENT_TEXT_PLAIN : MessageProperties.TEXT_PLAIN;
        RabbitMQ rmq = MMv2RabbitMQ.getInstance();
        if (log.isDebugEnabled()) {
            log.debug((Object)("Sending " + new String(eventEnvelopBytes) + "to exchange [" + exchange + "] with spec [" + routingSpec + "]"));
        }
        rmq.send(exchange, routingSpec.getRoutingKey(), messageProperties, (Serializable)eventEnvelopBytes);
    }

    private static EventEnvelope toEventEnvelope(RewardProgramTrigger trigger) {
        EventEnvelope eventEnvelop = new EventEnvelope();
        eventEnvelop.setId(UUID.randomUUID().toString());
        eventEnvelop.setMetadataVersion(CURRENT_EVENT_ENVELOP_VERSION);
        eventEnvelop.setPayload((Event)trigger);
        if (trigger != null && trigger.userData != null && trigger.userData.userID != null) {
            eventEnvelop.setSenderid(trigger.userData.userID.toString());
        }
        eventEnvelop.setSenttime(new Timestamp(System.currentTimeMillis()));
        eventEnvelop.setType(trigger.getEventType());
        return eventEnvelop;
    }

    public static RoutingSpec getRoutingSpec(RewardProgramTrigger trigger) {
        int eventTypeID = trigger.programType.getId();
        String routingKey = SystemProperty.get(SystemPropertyEntities.MarketingMechanicsV2_RMQ_TriggerRouting.ROUTING_KEY.forEventTypeID(eventTypeID));
        boolean usesPersistentDelivery = SystemProperty.getBool(SystemPropertyEntities.MarketingMechanicsV2_RMQ_TriggerRouting.PERSISTENT_MODE.forEventTypeID(eventTypeID));
        boolean useSynchDispatch = SystemProperty.getBool(SystemPropertyEntities.MarketingMechanicsV2_RMQ_TriggerRouting.SYNC_DISPATCH.forEventTypeID(eventTypeID));
        return new RoutingSpec(usesPersistentDelivery, routingKey, useSynchDispatch);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class DispatchEventEnvelop
    implements Callable<Object> {
        private final RoutingSpec routingSpec;
        private final RewardProgramTrigger trigger;

        public DispatchEventEnvelop(RoutingSpec routingSpec, RewardProgramTrigger trigger) {
            this.routingSpec = routingSpec;
            this.trigger = trigger;
        }

        @Override
        public Object call() throws Exception {
            MMv2TriggerSender.this.dispatch(this.routingSpec, this.trigger);
            return null;
        }
    }

    public static final class RoutingSpec {
        private final boolean usesPersistentDelivery;
        private final String routingKey;
        private final boolean usesSynchronousDispatch;

        public RoutingSpec(boolean persistentDelivery, String routingKey, boolean useSynchronousDispatch) {
            this.usesPersistentDelivery = persistentDelivery;
            this.routingKey = routingKey;
            this.usesSynchronousDispatch = useSynchronousDispatch;
        }

        public boolean isUsesPersistentDelivery() {
            return this.usesPersistentDelivery;
        }

        public String getRoutingKey() {
            return this.routingKey;
        }

        public boolean isUsesSynchronousDispatch() {
            return this.usesSynchronousDispatch;
        }

        public String toString() {
            return "RoutingSpec [usesPersistentDelivery=" + this.usesPersistentDelivery + ", routingKey=" + this.routingKey + ", usesSynchronousDispatch=" + this.usesSynchronousDispatch + "]";
        }
    }

    private static class MMv2TriggerSenderSingletonHolder {
        private static final MMv2TriggerSender INSTANCE = new MMv2TriggerSender();

        private MMv2TriggerSenderSingletonHolder() {
        }
    }
}

