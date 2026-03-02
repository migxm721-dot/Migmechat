/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.userevent.system.loadbalancing;

import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.userevent.system.loadbalancing.HashFunction;

public class FNVHashFunction
implements HashFunction {
    public long hash(String input) {
        return HashUtils.truncateToUnsigned32Bits(HashUtils.fnv(input));
    }
}

