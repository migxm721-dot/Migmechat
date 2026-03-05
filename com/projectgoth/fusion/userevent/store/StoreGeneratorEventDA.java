/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sleepycat.je.DatabaseException
 *  com.sleepycat.persist.EntityStore
 *  com.sleepycat.persist.PrimaryIndex
 */
package com.projectgoth.fusion.userevent.store;

import com.projectgoth.fusion.userevent.store.StoreGeneratorEvent;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StoreGeneratorEventDA {
    PrimaryIndex<String, StoreGeneratorEvent> primaryIndex;

    public StoreGeneratorEventDA(EntityStore store) throws DatabaseException {
        this.primaryIndex = store.getPrimaryIndex(String.class, StoreGeneratorEvent.class);
    }

    public PrimaryIndex<String, StoreGeneratorEvent> getPrimaryIndex() {
        return this.primaryIndex;
    }
}

