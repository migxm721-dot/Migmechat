/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.reputation.file.ScoreSummary;
import com.projectgoth.fusion.reputation.file.comparator.SingleIndexIntegerFileEntryComparator;

public class ScoreSummaryByScoreFileEntryComparator
extends SingleIndexIntegerFileEntryComparator {
    public ScoreSummaryByScoreFileEntryComparator() {
        super(ScoreSummary.TOTAL_SCORE_INDEX);
    }
}

