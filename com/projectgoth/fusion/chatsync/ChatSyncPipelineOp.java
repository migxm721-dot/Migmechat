/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.chatsync;

import com.projectgoth.fusion.chatsync.ChatSyncEntity;
import com.projectgoth.fusion.slice.FusionException;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatSyncPipelineOp {
    private ChatSyncEntity entity;
    private OpType opType;

    public ChatSyncPipelineOp(ChatSyncEntity entity, OpType opType) {
        this.entity = entity;
        this.opType = opType;
    }

    public ChatSyncEntity getEntity() {
        return this.entity;
    }

    public OpType getOpType() {
        return this.opType;
    }

    public int processResults(List<Object> pipelineResults, int startIndex) throws FusionException {
        if (this.opType.equals((Object)OpType.READ)) {
            return this.entity.loadPipeline(pipelineResults, startIndex);
        }
        return this.entity.processPipelineStoreResults(pipelineResults, startIndex);
    }

    public boolean isRead() {
        return this.opType.equals((Object)OpType.READ);
    }

    public boolean isWrite() {
        return this.opType.equals((Object)OpType.WRITE);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum OpType {
        READ,
        WRITE;

    }
}

