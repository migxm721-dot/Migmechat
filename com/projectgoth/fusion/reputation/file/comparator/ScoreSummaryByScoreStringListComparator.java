/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.reputation.file.ScoreSummary;
import com.projectgoth.fusion.reputation.file.comparator.SingleIndexIntegerStringListComparator;

public class ScoreSummaryByScoreStringListComparator
extends SingleIndexIntegerStringListComparator {
    public ScoreSummaryByScoreStringListComparator() {
        super(ScoreSummary.TOTAL_SCORE_INDEX);
    }
}

