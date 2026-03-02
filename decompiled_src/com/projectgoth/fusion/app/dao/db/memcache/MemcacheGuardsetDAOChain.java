/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.app.dao.db.memcache;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.base.GuardsetDAOChain;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;

public class MemcacheGuardsetDAOChain
extends GuardsetDAOChain {
    private static final String MEMCACHE_KEY_SEP = ":";

    public Short getMinimumClientVersionForAccess(int clientType, int guardCapability) throws DAOException {
        Short minClientVersion = null;
        String cacheValue = (String)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.MINIMUM_CLIENT_VERSION, MemcacheGuardsetDAOChain.makeMemcacheKey(clientType, guardCapability));
        if (StringUtil.isBlank(cacheValue)) {
            minClientVersion = super.getMinimumClientVersionForAccess(clientType, guardCapability);
            Short savedValue = minClientVersion == null ? (short)Short.MAX_VALUE : (short)minClientVersion;
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.MINIMUM_CLIENT_VERSION, MemcacheGuardsetDAOChain.makeMemcacheKey(clientType, guardCapability), savedValue);
        } else {
            minClientVersion = Short.parseShort(cacheValue) == Short.MAX_VALUE ? null : Short.valueOf(Short.parseShort(cacheValue));
        }
        return minClientVersion;
    }

    public static String makeMemcacheKey(int clientType, int guardCapability) {
        return Integer.toString(clientType) + MEMCACHE_KEY_SEP + Integer.toString(guardCapability);
    }
}

