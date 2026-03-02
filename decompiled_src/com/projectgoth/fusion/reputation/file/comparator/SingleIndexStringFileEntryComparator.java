/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.reputation.file.FileEntry;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleIndexStringFileEntryComparator
implements Comparator<FileEntry> {
    private int index;

    public SingleIndexStringFileEntryComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(FileEntry lhs, FileEntry rhs) {
        String lhsValue = lhs.getLine()[this.index];
        String rhsValue = rhs.getLine()[this.index];
        if (lhsValue == null && rhsValue != null) {
            return 1;
        }
        if (lhsValue != null && rhsValue == null) {
            return -1;
        }
        return lhsValue.compareTo(rhsValue);
    }
}

