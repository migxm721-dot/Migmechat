/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.registry;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ConfigurableExecutor;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import org.apache.log4j.Logger;

public abstract class RegistryThreadPoolExecutor {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RegistryThreadPoolExecutor.class));
    public static long AMD_ENABLED_REFRESH_INTERVAL = 60000L;
    private static LazyLoader<Boolean> amdEnabled = new LazyLoader<Boolean>("REGISTRY_AMD_ENABLED", AMD_ENABLED_REFRESH_INTERVAL){

        @Override
        protected Boolean fetchValue() throws Exception {
            return SystemProperty.getBool(SystemPropertyEntities.IceAsyncSettings.REGISTRY_AMD_ENABLED);
        }
    };

    public static void schedule(Runnable cmd) throws Exception {
        RegistryExecutor.INSTANCE.execute(cmd);
    }

    private static class RegistryExecutor {
        public static final ConfigurableExecutor INSTANCE = new ConfigurableExecutor(SystemPropertyEntities.IceAsyncSettings.REGISTRY_TPOOL_CORE_SIZE, SystemPropertyEntities.IceAsyncSettings.REGISTRY_TPOOL_MAX_SIZE, SystemPropertyEntities.IceAsyncSettings.REGISTRY_TPOOL_KEEP_ALIVE_SECONDS, SystemPropertyEntities.IceAsyncSettings.REGISTRY_REFRESH_THREAD_POOL_PROPERTIES_ENABLED, SystemPropertyEntities.IceAsyncSettings.REGISTRY_TPOOL_PROPS_REFRESH_INTERVAL_SECS);

        private RegistryExecutor() {
        }
    }
}

