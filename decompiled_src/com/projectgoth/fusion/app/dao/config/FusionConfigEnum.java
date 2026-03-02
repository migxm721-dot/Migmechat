/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.config;

import com.projectgoth.fusion.app.dao.config.FusionPropertiesFileConfigurationNamespace;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum FusionConfigEnum {
    DAO(new FusionPropertiesFileConfigurationNamespace("dao.properties")),
    FUSION_DB_READ(new FusionPropertiesFileConfigurationNamespace("fusion_db_read.properties")),
    OLAP_DB_READ(new FusionPropertiesFileConfigurationNamespace("olap_db_read.properties")),
    FUSION_DB_WRITE(new FusionPropertiesFileConfigurationNamespace("fusion_db_write.properties"));

    private FusionPropertiesFileConfigurationNamespace configurationNamespace;

    private FusionConfigEnum(FusionPropertiesFileConfigurationNamespace configurationNamespace) {
        this.configurationNamespace = configurationNamespace;
    }

    public String getIdentifier() {
        return this.configurationNamespace.getIdentifier();
    }

    private static void setupPeriodicCheck() {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1);
        scheduler.scheduleAtFixedRate(new PeriodicCheck(), 60L, 60L, TimeUnit.SECONDS);
    }

    static {
        FusionConfigEnum.setupPeriodicCheck();
    }

    private static class PeriodicCheck
    implements Runnable {
        private PeriodicCheck() {
        }

        public void run() {
            for (FusionConfigEnum e : FusionConfigEnum.values()) {
                e.configurationNamespace.reloadProperties();
            }
        }
    }
}

