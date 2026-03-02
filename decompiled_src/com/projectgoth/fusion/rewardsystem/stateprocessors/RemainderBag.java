/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.rewardsystem.stateprocessors;

import com.projectgoth.fusion.rewardsystem.stateprocessors.Bag;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class RemainderBag<T>
extends Bag<T> {
    private final boolean consumed;

    public RemainderBag(boolean consumed) {
        this.consumed = consumed;
    }

    public RemainderBag(int capacity, boolean consumed) {
        super(capacity);
        this.consumed = consumed;
    }

    public boolean isConsumed() {
        return this.consumed;
    }
}

