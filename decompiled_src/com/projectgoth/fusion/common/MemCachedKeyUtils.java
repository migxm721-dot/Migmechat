/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.InvalidMemCachedKeyException;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import org.apache.log4j.Logger;

public class MemCachedKeyUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MemCachedKeyUtils.class));
    public static final String KEY_SEPERATOR = "/";
    public static final String URL_ENCODED_SPACE = "%20";
    public static final String URL_ENCODED_LF = "%0A";
    public static final String URL_ENCODED_CR = "%0D";

    public static String getFullKeyFromStrings(String key1, String ... keys) {
        String rawKey;
        String rawKeyWithEncodedSpace;
        String encodedKey;
        StringBuilder builder = new StringBuilder();
        builder.append(key1);
        if (keys.length > 0) {
            for (String key : keys) {
                builder.append(KEY_SEPERATOR);
                builder.append(key);
            }
        }
        if ((encodedKey = (rawKeyWithEncodedSpace = (rawKey = builder.toString()).replaceAll(" ", URL_ENCODED_SPACE)).replaceAll("\n", URL_ENCODED_LF).replaceAll("\r", URL_ENCODED_CR)).length() != rawKeyWithEncodedSpace.length()) {
            String msg = "Invalid characters found in memcache key. Encoding it to : " + encodedKey;
            log.warn((Object)msg, (Throwable)new InvalidMemCachedKeyException(msg));
        }
        return encodedKey;
    }

    public static String getFullKeyForKeySpace(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String ... keys) {
        return MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), keys);
    }

    public static String[] getFullKeysForKeySpace(MemCachedKeySpaces.MemCachedKeySpaceInterface keySpace, String[] keys) {
        String[] fullKeys = new String[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            fullKeys[i] = MemCachedKeyUtils.getFullKeyFromStrings(keySpace.getName(), keys[i]);
        }
        return fullKeys;
    }
}

