/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.UsernameValidationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;

public class UsernameUtils {
    private static final Pattern CREATE_USERNAME_PATTERN = Pattern.compile("^[a-zA-Z]{1}(\\.{0,1}[\\w-]){5,}$");
    public static final int MIN_LENGTH = 6;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(UsernameUtils.class));

    public static String normalizeUsername(String username) {
        return StringUtil.trimmedLowerCase(username);
    }

    public static int validateUsernameLength(String username) throws UsernameValidationException {
        if (username == null) {
            throw new UsernameValidationException(ErrorCause.InvalidUserName.VALUE_NOT_SPECIFIED, new Object[0]);
        }
        int usernameLength = username.length();
        if (usernameLength == 0) {
            throw new UsernameValidationException(ErrorCause.InvalidUserName.VALUE_IS_BLANK, new Object[0]);
        }
        if (usernameLength > MemCachedClientWrapper.MAX_KEY_LENGTH) {
            throw new UsernameValidationException(ErrorCause.InvalidUserName.UNSUPPORTED_STRING_LENGTH, new Object[0]);
        }
        return usernameLength;
    }

    public static String validateUsernameCharacters(String username, boolean normalizeBeforeValidate) throws UsernameValidationException {
        if (normalizeBeforeValidate) {
            username = UsernameUtils.normalizeUsername(username);
        }
        int usernameLength = UsernameUtils.validateUsernameLength(username);
        Matcher matcher = CREATE_USERNAME_PATTERN.matcher(username);
        if (!matcher.matches()) {
            throw new UsernameValidationException(ErrorCause.InvalidUserName.CONTAINS_ILLEGAL_PATTERNS, 6);
        }
        int maxUsernameLength = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_USERNAME_LENGTH);
        if (usernameLength > maxUsernameLength) {
            throw new UsernameValidationException(ErrorCause.InvalidUserName.STRING_LENGTH_BEYOND_CONFIGURED_LIMIT, maxUsernameLength);
        }
        return !normalizeBeforeValidate ? UsernameUtils.normalizeUsername(username) : username;
    }

    public static void validateAgainstBannedWords(String normalizedUsername, String[] bannedWords) throws UsernameValidationException {
        if (bannedWords == null || bannedWords.length == 0) {
            return;
        }
        for (String bannedWord : bannedWords) {
            String normalizedBannedWord = UsernameUtils.normalizeUsername(bannedWord);
            if (StringUtil.isBlank(normalizedBannedWord) || normalizedUsername.indexOf(normalizedBannedWord) == -1) continue;
            throw new UsernameValidationException(ErrorCause.InvalidUserName.DETECTED_BANNED_WORDS, normalizedBannedWord);
        }
    }

    public static void validateUsernameAsSearchKeyword(String usernameKeyword) throws UsernameValidationException {
        UsernameUtils.validateUsernameLength(usernameKeyword);
        Pattern pattern = SingletonHolder.getUsernameAsSearchKeywordRegexLoader().getValue();
        if (pattern != null) {
            Matcher matcher = pattern.matcher(usernameKeyword);
            if (!matcher.matches()) {
                throw new UsernameValidationException(ErrorCause.InvalidUserName.INVALID_FORMAT, usernameKeyword);
            }
        } else {
            log.warn((Object)("Character validation is not performed on username [" + usernameKeyword + "] since the compiled regex pattern is null."));
        }
    }

    public static void invalidateUsernameSearchKeywordRegex() {
        SingletonHolder.USERNAME_AS_SEARCH_KEYWORD_REGEX_LOADER.invalidateCache();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SingletonHolder {
        private static final long CACHED_TTL_MILLIES = 60000L;
        private static final LazyLoader<Pattern> USERNAME_AS_SEARCH_KEYWORD_REGEX_LOADER = new LazyLoader<Pattern>("USERNAME_AS_SEARCH_KEYWORD_REGEX_LOADER", 60000L){

            @Override
            protected Pattern fetchValue() throws Exception {
                String regex = SystemProperty.get(SystemPropertyEntities.Default.USERNAME_AS_SEARCH_KEYWORD_REGEX);
                Pattern pattern = null;
                try {
                    pattern = Pattern.compile(regex);
                }
                catch (Exception e) {
                    log.error((Object)("Unable to compile regex [" + regex + "] defined under System property [" + SystemPropertyEntities.Default.USERNAME_AS_SEARCH_KEYWORD_REGEX.getName() + "]"), (Throwable)e);
                    throw e;
                }
                return pattern;
            }
        };

        private SingletonHolder() {
        }

        public static LazyLoader<Pattern> getUsernameAsSearchKeywordRegexLoader() {
            return USERNAME_AS_SEARCH_KEYWORD_REGEX_LOADER;
        }
    }
}

