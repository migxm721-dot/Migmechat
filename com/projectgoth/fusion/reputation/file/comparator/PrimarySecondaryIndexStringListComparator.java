/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import java.util.Comparator;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PrimarySecondaryIndexStringListComparator
implements Comparator<List<String>> {
    private int primaryIndex;
    private int secondaryIndex;

    public PrimarySecondaryIndexStringListComparator(int primaryIndex, int secondaryIndex) {
        this.primaryIndex = primaryIndex;
        this.secondaryIndex = secondaryIndex;
    }

    @Override
    public int compare(List<String> lhs, List<String> rhs) {
        int result = lhs.get(this.primaryIndex).compareTo(rhs.get(this.primaryIndex));
        if (result == 0) {
            return lhs.get(this.secondaryIndex).compareTo(rhs.get(this.secondaryIndex));
        }
        return result;
    }
}

