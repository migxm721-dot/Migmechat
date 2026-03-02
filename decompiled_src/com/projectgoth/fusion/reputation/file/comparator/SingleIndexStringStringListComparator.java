/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.reputation.file.comparator;

import com.projectgoth.fusion.common.ConfigUtils;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SingleIndexStringStringListComparator
implements Comparator<List<String>> {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SingleIndexStringStringListComparator.class));
    private int index;

    public SingleIndexStringStringListComparator(int index) {
        this.index = index;
    }

    @Override
    public int compare(List<String> lhs, List<String> rhs) {
        try {
            String lhsValue = lhs.get(this.index);
            String rhsValue = rhs.get(this.index);
            if (lhsValue == null && rhsValue != null) {
                return 1;
            }
            if (lhsValue != null && rhsValue == null) {
                return -1;
            }
            return lhsValue.compareTo(rhsValue);
        }
        catch (RuntimeException e) {
            log.error((Object)("failed to compare lhs [" + lhs + "] with rhs [" + rhs + "]"), (Throwable)e);
            throw e;
        }
    }
}

