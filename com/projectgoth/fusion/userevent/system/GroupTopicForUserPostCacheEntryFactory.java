/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.constructs.blocking.CacheEntryFactory
 */
package com.projectgoth.fusion.userevent.system;

import com.projectgoth.fusion.dao.UserPostDAO;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

public class GroupTopicForUserPostCacheEntryFactory
implements CacheEntryFactory {
    private UserPostDAO userPostDAO;

    public GroupTopicForUserPostCacheEntryFactory(UserPostDAO userPostDAO) {
        this.userPostDAO = userPostDAO;
    }

    public Object createEntry(Object key) throws Exception {
        return this.userPostDAO.getTopicForUserPost((Integer)key);
    }
}

