/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.configuration.BooleanConfigurationValue
 */
package com.projectgoth.fusion.app.dao.config;

import com.projectgoth.configuration.BooleanConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionConfigEnum;

public class FusionBooleanConfigurationValue
extends BooleanConfigurationValue {
    public FusionBooleanConfigurationValue(FusionConfigEnum namespace, String name, boolean defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }

    public FusionBooleanConfigurationValue(FusionConfigEnum namespace, String name, BooleanConfigurationValue defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }
}

