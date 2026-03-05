/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mig33.rabbitmqclient.RabbitMQ
 *  com.mig33.rabbitmqclient.settings.Settings
 */
package com.projectgoth.fusion.rewardsystem.mmv2;

import com.mig33.rabbitmqclient.RabbitMQ;
import com.mig33.rabbitmqclient.settings.Settings;
import com.projectgoth.fusion.rewardsystem.mmv2.MMv2RMQClientSettings;

public class MMv2RabbitMQ
extends RabbitMQ {
    private MMv2RabbitMQ(Settings settings) {
        super(settings);
    }

    public static RabbitMQ getInstance() {
        return MMv2RabbitMQHolder.INSTANCE;
    }

    public static class MMv2RabbitMQHolder {
        private static final MMv2RabbitMQ INSTANCE = new MMv2RabbitMQ(new MMv2RMQClientSettings());
    }
}

