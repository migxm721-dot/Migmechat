/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import java.util.Comparator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleIndexLongStringListComparator
implements Comparator<List<String>> {
    private int index;

    public SingleIndexLongStringListComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(List<String> lhs, List<String> rhs) {
        long rhsValue;
        long lhsValue = Long.parseLong(lhs.get(this.index));
        if (lhsValue < (rhsValue = Long.parseLong(rhs.get(this.index)))) {
            return -1;
        }
        if (lhsValue > rhsValue) {
            return 1;
        }
        return 0;
    }
}

