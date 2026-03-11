package com.projectgoth.fusion.common;

import com.danga.MemCached.MemCachedClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Arrays;

public final class MemCachedUtils {

    public static enum Instance {
        common,
        authenticationService
    }

    public static final List POOL_NAMES =
        Collections.unmodifiableList(
            Arrays.asList(new String[]{
                "common",
                "authenticationService"
            })
        );

    private MemCachedUtils() {}

    public static List parsePoolNames(String pools) {
        List result = new ArrayList();

        if (pools == null) {
            return result;
        }

        StringTokenizer st = new StringTokenizer(pools, ",");

        while (st.hasMoreTokens()) {
            result.add(st.nextToken().trim());
        }

        return result;
    }

    public static MemCachedClient getMemCachedClient(Instance instance) {
        if (instance == null) {
            throw new IllegalArgumentException("Instance cannot be null");
        }

        return new MemCachedClient(instance.name());
    }
}
