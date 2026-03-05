/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncStore;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ChatSyncEntity {
    public static final int SECONDS_IN_HOUR = 3600;

    public String getKey();

    public String getValue();

    public void store(ChatSyncStore[] var1) throws FusionException;

    public void unpack(String var1, String var2) throws FusionException;

    public ChatSyncEntityType getEntityType();

    public void retrieve(ChatSyncStore[] var1) throws FusionException;

    public void retrievePipeline(ChatSyncStore var1) throws FusionException;

    public void storePipeline(ChatSyncStore var1) throws FusionException;

    public int loadPipeline(List<Object> var1, int var2) throws FusionException;

    public int processPipelineStoreResults(List<Object> var1, int var2) throws FusionException;

    public boolean canRetryReads() throws FusionException;

    public boolean canRetryWrites() throws FusionException;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ChatSyncEntityType {
        CONVERSATION,
        MESSAGE,
        USER,
        MESSAGE_STATUS_EVENT;

    }
}

