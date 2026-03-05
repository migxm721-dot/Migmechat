/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.configuration.StringConfigurationValue
 */
package com.projectgoth.fusion.app.dao.config;

import com.projectgoth.configuration.StringConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionConfigEnum;

public class FusionStringConfigurationValue
extends StringConfigurationValue {
    public FusionStringConfigurationValue(FusionConfigEnum namespace) {
        super(namespace.getIdentifier());
    }

    public FusionStringConfigurationValue(FusionConfigEnum namespace, String name, String defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }

    public FusionStringConfigurationValue(FusionConfigEnum namespace, String name) {
        super(namespace.getIdentifier(), name, "");
    }
}

