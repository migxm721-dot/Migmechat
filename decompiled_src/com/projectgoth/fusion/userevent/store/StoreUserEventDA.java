/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.je.DatabaseException
 *  com.sleepycat.persist.EntityStore
 *  com.sleepycat.persist.PrimaryIndex
 */
package com.projectgoth.fusion.userevent.store;

import com.projectgoth.fusion.userevent.store.StoreUserEvent;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StoreUserEventDA {
    PrimaryIndex<String, StoreUserEvent> primaryIndex;

    public StoreUserEventDA(EntityStore store) throws DatabaseException {
        this.primaryIndex = store.getPrimaryIndex(String.class, StoreUserEvent.class);
    }

    public PrimaryIndex<String, StoreUserEvent> getPrimaryIndex() {
        return this.primaryIndex;
    }
}

