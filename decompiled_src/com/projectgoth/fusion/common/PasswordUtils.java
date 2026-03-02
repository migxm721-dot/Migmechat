/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.keyczar.Crypter
 *  org.keyczar.exceptions.KeyczarException
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.authentication.CredentialVersion;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.authentication.domain.PersistedCredential;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ValidateCredentialResult;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionCredential;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PasswordUtils {
    private static final Logger logger = Logger.getLogger((String)ConfigUtils.getLoggerName(PasswordUtils.class));

    public static Pattern getPasswordRegex() {
        PasswordRegexLoader loader = SingletonHolder.getPasswordRegexLoader();
        return (Pattern)loader.getValue();
    }

    public static Set<String> getUppercasedBannedPasswords() {
        UppercasedBannedPasswordsLoader loader = SingletonHolder.getUppercasedBannedPasswordsLoader();
        return (Set)loader.getValue();
    }

    public static Set<Pattern> getBannedPasswordPatterns() {
        return (Set)SingletonHolder.getBannedPasswordPatternLoader().getValue();
    }

    public static void invalidateCachedConfig() {
        SingletonHolder.invalidateCachedConfig();
    }

    public static boolean isValidPassword(String password) {
        ValidateCredentialResult result = PasswordUtils.validatePassword(null, password, false);
        return null != result && result.valid;
    }

    public static ValidateCredentialResult validatePassword(String username, String password) {
        return PasswordUtils.validatePassword(username, password, true);
    }

    private static ValidateCredentialResult validatePassword(String username, String password, boolean checkAgainstUsername) {
        if (password == null || password.length() == 0) {
            return new ValidateCredentialResult(false, "Password must not be blank");
        }
        if (checkAgainstUsername && password.trim().equalsIgnoreCase(username)) {
            return new ValidateCredentialResult(false, "Password must not be the same as the username");
        }
        int minPasswordLength = SystemProperty.getInt(SystemPropertyEntities.Default.MIN_PASSWORD_LENGTH);
        if (password.length() < minPasswordLength) {
            return new ValidateCredentialResult(false, "The password must contain at least " + minPasswordLength + " characters");
        }
        int maxPasswordLength = SystemProperty.getInt(SystemPropertyEntities.Default.MAX_PASSWORD_LENGTH);
        if (password.length() > maxPasswordLength) {
            return new ValidateCredentialResult(false, "The password must not contain more than " + maxPasswordLength + " characters");
        }
        if (!PasswordUtils.getPasswordRegex().matcher(password).matches()) {
            return new ValidateCredentialResult(false, SystemProperty.get(SystemPropertyEntities.Credentials.INVALID_PASSWORD_MESSAGE));
        }
        if (PasswordUtils.passwordContainsBannedWords(password)) {
            return new ValidateCredentialResult(false, "You cannot use " + password + " as your password");
        }
        return new ValidateCredentialResult(true, "valid");
    }

    public static boolean passwordContainsBannedWords(String password) {
        String pwLowerCase = password.toLowerCase();
        for (Pattern pattern : PasswordUtils.getBannedPasswordPatterns()) {
            Matcher m = pattern.matcher(pwLowerCase);
            if (!m.matches()) continue;
            return true;
        }
        return false;
    }

    public static String decryptPassword(PersistedCredential credential, Crypter crypter) throws KeyczarException {
        if (!StringUtils.hasLength((String)credential.getPassword())) {
            return "";
        }
        if (credential.getVersion() == CredentialVersion.UNENCRYPTED_MIGRATED || credential.getVersion() == CredentialVersion.UNENCRYPTED_UNMIGRATED) {
            return credential.getPassword();
        }
        return PasswordUtils.decrypt(credential.getPassword(), crypter);
    }

    public static String decrypt(String toBeDecrypted, Crypter crypter) throws KeyczarException {
        if (!StringUtils.hasLength((String)toBeDecrypted)) {
            return "";
        }
        return crypter.decrypt(toBeDecrypted);
    }

    public static String encryptPassword(String password, Crypter crypter) throws KeyczarException {
        if (!StringUtils.hasLength((String)password)) {
            return "";
        }
        return PasswordUtils.encrypt(password, crypter);
    }

    public static String encrypt(String toBeEncrypted, Crypter crypter) throws KeyczarException {
        if (!StringUtils.hasLength((String)toBeEncrypted)) {
            return "";
        }
        return crypter.encrypt(toBeEncrypted);
    }

    public static ValidateCredentialResult validateCredential(Credential credential) {
        if (credential == null || !StringUtils.hasLength((String)credential.username)) {
            return new ValidateCredentialResult(false, "Username is blank");
        }
        if (!(credential instanceof FusionCredential)) {
            if (!StringUtils.hasLength((String)credential.password) || credential.passwordType < 1) {
                return new ValidateCredentialResult(false, "Password must not be blank");
            }
            if (credential.passwordType == PasswordType.FUSION.value()) {
                ValidateCredentialResult validationResult = PasswordUtils.validatePassword(credential.username, credential.password);
                if (validationResult == null) {
                    return new ValidateCredentialResult(false, "Unable to validate credential");
                }
                if (!validationResult.valid) {
                    return validationResult;
                }
            }
        }
        return new ValidateCredentialResult(true, "valid");
    }

    public static PersistedCredential filterPersistedCredentialByPasswordType(List<PersistedCredential> credentials, PasswordType passwordType) {
        if (credentials == null || credentials.isEmpty()) {
            return null;
        }
        for (PersistedCredential cred : credentials) {
            if (cred.getPasswordType() != passwordType.value().byteValue()) continue;
            return cred;
        }
        return null;
    }

    public static List<PersistedCredential> filterPersistedCredentialsByPasswordTypes(List<PersistedCredential> credentials, PasswordType[] passwordTypes) {
        ArrayList<PersistedCredential> results = new ArrayList<PersistedCredential>(passwordTypes.length);
        if (credentials == null || credentials.isEmpty()) {
            return results;
        }
        Arrays.sort(passwordTypes);
        for (PersistedCredential cred : credentials) {
            if (Arrays.binarySearch(passwordTypes, PasswordType.fromValue(cred.getPasswordType())) < 0) continue;
            results.add(cred);
        }
        return results;
    }

    public static void main(String[] args) throws KeyczarException {
        Crypter crypter = new Crypter("/usr/fusion/etc/aeskeys/");
        String enc = PasswordUtils.encryptPassword("01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890", crypter);
        System.out.println(enc);
        System.out.println(enc.length());
    }

    private static class SingletonHolder {
        private static final PasswordRegexLoader passwordRegexLoader = new PasswordRegexLoader();
        private static final UppercasedBannedPasswordsLoader uppercaseBannedPasswordsLoader = new UppercasedBannedPasswordsLoader();
        private static final BannedPasswordPatternsLoader bannedPasswordPatterns = new BannedPasswordPatternsLoader();

        private SingletonHolder() {
        }

        public static PasswordRegexLoader getPasswordRegexLoader() {
            return passwordRegexLoader;
        }

        public static UppercasedBannedPasswordsLoader getUppercasedBannedPasswordsLoader() {
            return uppercaseBannedPasswordsLoader;
        }

        public static BannedPasswordPatternsLoader getBannedPasswordPatternLoader() {
            return bannedPasswordPatterns;
        }

        public static void invalidateCachedConfig() {
            passwordRegexLoader.invalidateCache();
            uppercaseBannedPasswordsLoader.invalidateCache();
            bannedPasswordPatterns.invalidateCache();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class BannedPasswordPatternsLoader
    extends LazyLoader<Set<Pattern>> {
        private static final int CACHE_TTL_MILLIES = 120000;

        public BannedPasswordPatternsLoader() {
            super("BannedPasswordPatternsLoader", 120000L);
        }

        @Override
        protected Set<Pattern> fetchValue() throws Exception {
            String[] entries;
            HashSet<Pattern> patterns = new HashSet<Pattern>();
            for (String entry : entries = SystemProperty.getArray(SystemPropertyEntities.Default.BANNEDPASSWORDS)) {
                try {
                    Pattern ptn = Pattern.compile(entry);
                    patterns.add(ptn);
                }
                catch (Exception e) {
                    logger.error((Object)("Exception compiling " + SystemPropertyEntities.Default.BANNEDPASSWORDS.getName() + " pattern entry=" + entry + ": e=" + e), (Throwable)e);
                }
            }
            return Collections.unmodifiableSet(patterns);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class UppercasedBannedPasswordsLoader
    extends LazyLoader<Set<String>> {
        private static final int CACHE_TTL_MILLIES = 120000;

        public UppercasedBannedPasswordsLoader() {
            super("UppercasedBannedPasswordsLoader", 120000L);
        }

        @Override
        protected Set<String> fetchValue() throws Exception {
            String[] bannedPasswords;
            HashSet<String> passwordSet = new HashSet<String>();
            for (String bannedPassword : bannedPasswords = SystemProperty.getArray(SystemPropertyEntities.Default.BANNEDPASSWORDS)) {
                passwordSet.add(bannedPassword.toUpperCase());
            }
            return Collections.unmodifiableSet(passwordSet);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class PasswordRegexLoader
    extends LazyLoader<Pattern> {
        private static final int CACHE_TTL_MILLIES = 120000;

        public PasswordRegexLoader() {
            super("PasswordRegexLoader", 120000L);
        }

        @Override
        protected Pattern fetchValue() throws Exception {
            String passwordRegex = SystemProperty.get(SystemPropertyEntities.Credentials.PASSWORD_REG_EX);
            try {
                return Pattern.compile(passwordRegex);
            }
            catch (Exception ex) {
                this.getLogger().error((Object)("System property:[" + SystemPropertyEntities.Credentials.PASSWORD_REG_EX.getName() + "] has an invalid regular expression[" + passwordRegex + "].Exception:" + ex), (Throwable)ex);
                throw ex;
            }
        }
    }
}

