/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.utils.enums.IEnumValueGetter
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.common;

import com.projectgoth.leto.common.utils.enums.IEnumValueGetter;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum RedisShardAssignmentMode implements IEnumValueGetter<Integer>
{
    SET_WITH_DIST_LOCK(0, false, true),
    SETNX_AND_GET_PIPELINE_WITH_DIST_LOCK(1, true, true),
    SETNX_AND_GET_PIPELINE(2, true, false);

    private final boolean usesDistLock;
    private final boolean usesSetNXAndGetPipeline;
    private final Integer mode;

    private RedisShardAssignmentMode(Integer mode, boolean usesSetNXAndGetPipeline, boolean usesDistLock) {
        this.mode = mode;
        this.usesDistLock = usesDistLock;
        this.usesSetNXAndGetPipeline = usesSetNXAndGetPipeline;
    }

    public boolean isUsesDistLock() {
        return this.usesDistLock;
    }

    public boolean isUsesSetNXAndGetPipeline() {
        return this.usesSetNXAndGetPipeline;
    }

    public static RedisShardAssignmentMode fromCode(int mode) {
        return (RedisShardAssignmentMode)ValueToEnumMapInstance.INSTANCE.toEnum((Object)mode);
    }

    public Integer getEnumValue() {
        return this.mode;
    }

    private static final class ValueToEnumMapInstance {
        private static final ValueToEnumMap<Integer, RedisShardAssignmentMode> INSTANCE = new ValueToEnumMap(RedisShardAssignmentMode.class);

        private ValueToEnumMapInstance() {
        }
    }
}

