/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.reputation.file.FileEntry;
import java.util.Comparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PrimarySecondaryIndexFileEntryComparator
implements Comparator<FileEntry> {
    private int primaryIndex;
    private int secondaryIndex;

    public PrimarySecondaryIndexFileEntryComparator(int primaryIndex, int secondaryIndex) {
        this.primaryIndex = primaryIndex;
        this.secondaryIndex = secondaryIndex;
    }

    @Override
    public int compare(FileEntry lhs, FileEntry rhs) {
        int result = lhs.getLine()[this.primaryIndex].compareTo(rhs.getLine()[this.primaryIndex]);
        if (result == 0) {
            return lhs.getLine()[this.secondaryIndex].compareTo(rhs.getLine()[this.secondaryIndex]);
        }
        return result;
    }
}

