/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.smsengine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MessageHistory {
    private static final int HISTORY_SIZE = 100000;
    private static long[] idArray = new long[100000];
    private static Set<Long> idSet = new HashSet<Long>(100000);
    private static int top = 0;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean add(long id) {
        Set<Long> set = idSet;
        synchronized (set) {
            if (idSet.contains(id)) {
                return false;
            }
            idSet.remove(idArray[top]);
            MessageHistory.idArray[MessageHistory.top] = id;
            ++top;
            top %= 100000;
            idSet.add(id);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void remove(long id) {
        Set<Long> set = idSet;
        synchronized (set) {
            idSet.remove(id);
        }
    }

    static {
        Arrays.fill(idArray, Long.MIN_VALUE);
    }
}

