/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import java.util.Comparator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleIndexIntegerStringListComparator
implements Comparator<List<String>> {
    private int index;

    public SingleIndexIntegerStringListComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(List<String> lhs, List<String> rhs) {
        int rhsValue;
        int lhsValue = Integer.parseInt(lhs.get(this.index));
        if (lhsValue < (rhsValue = Integer.parseInt(rhs.get(this.index)))) {
            return -1;
        }
        if (lhsValue > rhsValue) {
            return 1;
        }
        return 0;
    }
}

