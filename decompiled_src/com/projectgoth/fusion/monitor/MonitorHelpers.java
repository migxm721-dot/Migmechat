/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.monitor;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MonitorHelpers {
    public static Map sortDesc(Map unsorted) {
        return MonitorHelpers.sortDesc(unsorted, null);
    }

    public static Map sortDesc(Map unsorted, Integer limit) {
        LinkedList list = new LinkedList(unsorted.entrySet());
        Collections.sort(list, new Comparator(){

            public int compare(Object o1, Object o2) {
                return ((Comparable)((Map.Entry)o2).getValue()).compareTo(((Map.Entry)o1).getValue());
            }
        });
        LinkedHashMap sorted = new LinkedHashMap();
        for (Map.Entry entry : list) {
            sorted.put(entry.getKey(), entry.getValue());
            if (limit == null || sorted.size() < limit) continue;
            return sorted;
        }
        return sorted;
    }

    public static String countsMapToString(Map<String, ? extends Number> counts, Long total, DecimalFormat decFormat) {
        StringBuilder sb = new StringBuilder();
        Set<String> keys = counts.keySet();
        for (String key : keys) {
            Number count = counts.get(key);
            sb.append(key + "=" + decFormat.format(count));
            if (total != null) {
                double percent = 100.0 * (count.doubleValue() / (double)total.longValue());
                sb.append(" (" + decFormat.format(percent) + "%)");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

