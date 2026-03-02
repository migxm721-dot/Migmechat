/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.security.SecureRandom;
import org.apache.log4j.Logger;

public class Tokenizer {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(Tokenizer.class));
    private static final SecureRandom random = new SecureRandom();

    private static String generateRandomWord(String charset, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            builder.append(charset.charAt(random.nextInt(charset.length())));
        }
        return builder.toString();
    }

    private static void storeToken(TokenIdentifier identifier, String token, String expectedVal, Long expirationInMillis) throws Exception {
        MemCachedClientWrapper.set(identifier.memcacheKeySpace(), token, expectedVal, expirationInMillis);
    }

    public static void deleteToken(TokenIdentifier identifier, String token) {
        MemCachedClientWrapper.delete(identifier.memcacheKeySpace(), token);
    }

    private static String getToken(TokenIdentifier identifier, String token) {
        return MemCachedClientWrapper.getString(identifier.memcacheKeySpace(), token);
    }

    public static String generateToken(TokenIdentifier identifier, String expectedVal, long expirationInMillis) throws Exception {
        return Tokenizer.generateToken(identifier, expectedVal, SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET), SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH), expirationInMillis);
    }

    public static String generateToken(TokenIdentifier identifier, String expectedVal) throws Exception {
        return Tokenizer.generateToken(identifier, expectedVal, SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET), SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH), SystemProperty.getLong(SystemPropertyEntities.TokenizerSettings.DEFAULT_EXPIRATION_IN_MILLIS));
    }

    public static String generateToken(TokenIdentifier identifier, String expectedVal, String charset, int length, Long expirationInMillis) throws Exception {
        int minTokenLength;
        if (StringUtil.isBlank(charset)) {
            charset = SystemProperty.get(SystemProperty.get(SystemPropertyEntities.TokenizerSettings.DEFAULT_CHARSET));
        }
        if (length < (minTokenLength = SystemProperty.getInt(SystemPropertyEntities.TokenizerSettings.MINIMUM_LENGTH))) {
            length = minTokenLength;
        }
        if (null == expirationInMillis) {
            expirationInMillis = SystemProperty.getLong(SystemPropertyEntities.TokenizerSettings.DEFAULT_EXPIRATION_IN_MILLIS);
        }
        String token = Tokenizer.generateRandomWord(charset, length);
        Tokenizer.storeToken(identifier, token, expectedVal, expirationInMillis);
        return token;
    }

    public static boolean validateToken(TokenIdentifier identifier, String token, String expectedVal) {
        String value = Tokenizer.getToken(identifier, token);
        if (null == value) {
            log.warn((Object)("Token requested may have expired already: token [" + token + "] identifier [" + (Object)((Object)identifier) + "]"));
        }
        return value != null && value.equals(expectedVal);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum TokenIdentifier {
        FORGOT_PASSWORD("FGT_PWD", MemCachedKeySpaces.TokenKeySpace.FORGOT_PASSWORD);

        private String tokenIdentifier;
        private MemCachedKeySpaces.TokenKeySpace memcacheKeySpace;

        private TokenIdentifier(String tokenIdentifier, MemCachedKeySpaces.TokenKeySpace keySpace) {
            this.tokenIdentifier = tokenIdentifier;
            this.memcacheKeySpace = keySpace;
        }

        public String identifier() {
            return this.tokenIdentifier;
        }

        public MemCachedKeySpaces.TokenKeySpace memcacheKeySpace() {
            return this.memcacheKeySpace;
        }

        public static TokenIdentifier fromValue(String tokenIdentifier) {
            for (TokenIdentifier e : TokenIdentifier.values()) {
                if (!e.identifier().equals(tokenIdentifier)) continue;
                return e;
            }
            return null;
        }
    }
}

