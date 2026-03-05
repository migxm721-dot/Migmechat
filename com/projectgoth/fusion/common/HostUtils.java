/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.LazyLoader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtils {
    public static String getHostname() {
        return SingletonHolder.getHostnameLoader().getValue();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SingletonHolder {
        private static final long CACHETIME_MS = 60000L;
        private static LazyLoader<String> HOSTNAME_LOADER = new LazyLoader<String>("HOSTNAME_LOADER", 60000L){

            @Override
            protected String fetchValue() throws Exception {
                try {
                    return InetAddress.getLocalHost().getHostName().toUpperCase();
                }
                catch (UnknownHostException e) {
                    return "UNKNOWN";
                }
                catch (Throwable t) {
                    this.getLogger().error((Object)"Unable to resolve hostname", t);
                    return "_ERROR_";
                }
            }
        };

        private SingletonHolder() {
        }

        public static LazyLoader<String> getHostnameLoader() {
            return HOSTNAME_LOADER;
        }
    }
}

