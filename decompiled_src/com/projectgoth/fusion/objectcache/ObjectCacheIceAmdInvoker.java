/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ConfigurableExecutor;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ice.IceAmdInvoker;
import org.apache.log4j.Logger;

public abstract class ObjectCacheIceAmdInvoker
extends IceAmdInvoker {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ObjectCacheIceAmdInvoker.class));
    public static long AMD_ENABLED_REFRESH_INTERVAL = 60000L;
    private static LazyLoader<Boolean> amdEnabled = new LazyLoader<Boolean>("OBJECT_CACHE_AMD_ENABLED", AMD_ENABLED_REFRESH_INTERVAL){

        @Override
        protected Boolean fetchValue() throws Exception {
            return SystemProperty.getBool(SystemPropertyEntities.IceAsyncSettings.OBJECT_CACHE_AMD_ENABLED);
        }
    };

    public boolean isAMDEnabled() {
        return amdEnabled.getValue();
    }

    public void schedule(Runnable cmd) throws Exception {
        ObjcExecutor.INSTANCE.execute(cmd);
    }

    public static ConfigurableExecutor getExecutor() {
        return ObjcExecutor.INSTANCE;
    }

    private static class ObjcExecutor {
        public static final ConfigurableExecutor INSTANCE = new ConfigurableExecutor(SystemPropertyEntities.IceAsyncSettings.OBJC_TPOOL_CORE_SIZE, SystemPropertyEntities.IceAsyncSettings.OBJC_TPOOL_MAX_SIZE, SystemPropertyEntities.IceAsyncSettings.OBJC_TPOOL_KEEP_ALIVE_SECONDS, SystemPropertyEntities.IceAsyncSettings.OBJC_REFRESH_THREAD_POOL_PROPERTIES_ENABLED, SystemPropertyEntities.IceAsyncSettings.OBJC_TPOOL_PROPS_REFRESH_INTERVAL_SECS);

        private ObjcExecutor() {
        }
    }
}

