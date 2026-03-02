/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.configuration.ConfigurationValue
 *  com.projectgoth.configuration.IntConfigurationValue
 */
package com.projectgoth.fusion.app.dao.config;

import com.projectgoth.configuration.ConfigurationValue;
import com.projectgoth.configuration.IntConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionConfigEnum;

public class FusionIntConfigurationValue
extends IntConfigurationValue {
    public FusionIntConfigurationValue(FusionConfigEnum namespace, String name, int defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }

    public FusionIntConfigurationValue(FusionConfigEnum namespace, String name, ConfigurationValue defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }
}

