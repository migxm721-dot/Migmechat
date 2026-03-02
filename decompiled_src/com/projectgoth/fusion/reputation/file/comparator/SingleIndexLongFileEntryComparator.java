/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.reputation.file.FileEntry;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleIndexLongFileEntryComparator
implements Comparator<FileEntry> {
    private int index;

    public SingleIndexLongFileEntryComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(FileEntry lhs, FileEntry rhs) {
        long rhsValue;
        long lhsValue = Long.parseLong(lhs.getLine()[this.index]);
        if (lhsValue < (rhsValue = Long.parseLong(rhs.getLine()[this.index]))) {
            return -1;
        }
        if (lhsValue > rhsValue) {
            return 1;
        }
        return 0;
    }
}

