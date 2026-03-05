/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.configuration.StringListConfigurationValue
 */
package com.projectgoth.fusion.app.dao.config;

import com.projectgoth.configuration.StringListConfigurationValue;
import com.projectgoth.fusion.app.dao.config.FusionConfigEnum;

public class FusionStringListConfigurationValue
extends StringListConfigurationValue {
    public FusionStringListConfigurationValue(FusionConfigEnum namespace, String name, String defaultValue) {
        super(namespace.getIdentifier(), name, defaultValue);
    }

    public FusionStringListConfigurationValue(FusionConfigEnum namespace, String name) {
        super(namespace.getIdentifier(), name, "");
    }
}

