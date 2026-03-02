/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.SystemPropertyEntryFallbackCreator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SystemPropertyEntryWithParent
implements SystemPropertyEntities.SystemPropertyEntryInterfaceWithNamespace {
    private final String currentNamespace;
    private final String fqName;
    private final SystemPropertyEntities.SystemPropertyEntryInterfaceWithNamespace parentProperty;
    private final SystemPropertyEntities.SystemPropertyEntry<?> entry;
    private final SystemPropertyEntryFallbackCreator systemPropertyEntryFallbackCreator;

    protected SystemPropertyEntryWithParent(SystemPropertyEntryFallbackCreator sysPropEntryFallbackCreator, String subNameSpace, SystemPropertyEntities.SystemPropertyEntryInterfaceWithNamespace parentProperty) {
        this.parentProperty = parentProperty;
        this.currentNamespace = SystemPropertyEntities.getNameWithNamespace(parentProperty.getNamespace(), subNameSpace);
        this.fqName = StringUtil.isBlank(subNameSpace) ? parentProperty.getNamespace() : SystemPropertyEntities.getNameWithNamespace(this.currentNamespace, parentProperty.getEntry().getName());
        this.entry = sysPropEntryFallbackCreator.create(parentProperty);
        this.systemPropertyEntryFallbackCreator = sysPropEntryFallbackCreator;
    }

    @Override
    public SystemPropertyEntities.SystemPropertyEntry<?> getEntry() {
        return this.entry;
    }

    @Override
    public String getName() {
        return this.fqName;
    }

    @Override
    public String getNamespace() {
        return this.currentNamespace;
    }

    public SystemPropertyEntities.SystemPropertyEntryInterfaceWithNamespace getParentProperty() {
        return this.parentProperty;
    }

    protected SystemPropertyEntryFallbackCreator getSystemPropertyEntryFallbackCreator() {
        return this.systemPropertyEntryFallbackCreator;
    }
}

