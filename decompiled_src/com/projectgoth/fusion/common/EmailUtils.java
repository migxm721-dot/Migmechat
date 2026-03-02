/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.app.dao.DAOException;
import com.projectgoth.fusion.app.dao.DAOFactory;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

public class EmailUtils {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(EmailUtils.class));
    private static final Pattern emailAddressRegex = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    private static final LazyLoader<Pattern> emailAddressRegexLoader = new LazyLoader<Pattern>("EMAIL_ADDRESS_REGEX", 60000L){

        @Override
        protected Pattern fetchValue() {
            return Pattern.compile(SystemProperty.get(SystemPropertyEntities.Email.EMAIL_BASIC_CHECKS_REGEX));
        }
    };
    private static final String EMAIL_ADDRESS_REGEXP_FILTERS_SEPARATOR = "##";
    public static final String INVALID_EMAIL_ADDRESS_EXCEPTION_MSG = "Your email address contains invalid characters. Please provide a valid email address.";
    private static volatile String currentEmailAddressRegexpFilters = "";
    private static volatile ConcurrentHashMap<String, Pattern> regexpToPatternMap = new ConcurrentHashMap();
    private static final LazyLoader<Set<String>> whitelistEmailAddress = new LazyLoader<Set<String>>("WHITELIST_EMAIL_ADDRESS", 300000L){

        @Override
        protected Set<String> fetchValue() throws Exception {
            return new HashSet<String>(Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.UserNotificationServiceSettings.INTERNAL_EMAIL_WHITELIST)));
        }
    };

    public static EmailValidatationResult externalEmailIsValid(String emailAddress) {
        if (SystemPropertyEntities.Temp.Cache.se605EmailHyphensEnabled.getValue().booleanValue()) {
            if (StringUtil.isBlank(emailAddress) || !StringUtil.isValidNonMig33Email(emailAddress) || !emailAddressRegexLoader.getValue().matcher(emailAddress).matches()) {
                return new EmailValidatationResult(EmailValidatationEnum.INVALID, "Please provide a valid email address for registration");
            }
        } else if (StringUtil.isBlank(emailAddress) || !StringUtil.isValidNonMig33Email(emailAddress) || !emailAddressRegex.matcher(emailAddress).matches()) {
            return new EmailValidatationResult(EmailValidatationEnum.INVALID, "Please provide a vaild email address for registration");
        }
        String emailDomain = StringUtil.getEmailDomain(emailAddress);
        boolean isBlackListedEmailDomain = Arrays.asList(SystemProperty.getArray(SystemPropertyEntities.Default.BLACKLISTED_EXTERNAL_MAIL_DOMAINS)).contains(emailDomain);
        if (isBlackListedEmailDomain) {
            return new EmailValidatationResult(EmailValidatationEnum.INVALID, "Please provide a valid email address for registration");
        }
        try {
            EmailUtils.doRegexpBasedValidation(emailAddress);
        }
        catch (Exception exception) {
            return new EmailValidatationResult(EmailValidatationEnum.INVALID, exception.getMessage());
        }
        if (SystemProperty.getBool(SystemPropertyEntities.Registration.SPECIAL_VALIDATION_FOR_GMAIL_ENABLED) && EmailUtils.isGmailAddress(emailAddress)) {
            String local = emailAddress.substring(0, emailAddress.indexOf(64)).toLowerCase();
            if (emailAddress.toLowerCase().endsWith("googlemail.com")) {
                log.info((Object)String.format("Invalid email address:%s, googlemail.com addresses are not allowed", emailAddress));
                return new EmailValidatationResult(EmailValidatationEnum.INVALID, String.format("googlemail.com addresses are not allowed, please use %s@gmail.com instead.", local));
            }
            if (local.contains("+")) {
                return new EmailValidatationResult(EmailValidatationEnum.INVALID, "Invalid character '+' provided. Please provide a valid email address.");
            }
            int numberOfPeriods = StringUtils.countOccurrencesOf((String)local, (String)".");
            if (numberOfPeriods > SystemProperty.getInt(SystemPropertyEntities.Registration.NUMBER_OF_PERIODS_ALLOWED_IN_GMAIL_ADDRESSES)) {
                String stripped = local.replace(".", "");
                log.info((Object)String.format("Invalid email address:%s, more than %s periods", emailAddress, SystemPropertyEntities.Registration.NUMBER_OF_PERIODS_ALLOWED_IN_GMAIL_ADDRESSES));
                return new EmailValidatationResult(EmailValidatationEnum.PERIODS_EXCEED_IN_GMAIL, String.format("Invalid email address provided. Do you mean to use %s@%s?", stripped, emailAddress.substring(emailAddress.indexOf(64) + 1)));
            }
        }
        if (EmailUtils.isBounceEmailAddress(emailAddress)) {
            return new EmailValidatationResult(EmailValidatationEnum.BOUNCED_EMAIL_ADDRESS, "Please provide a valid email address for registration");
        }
        return new EmailValidatationResult(EmailValidatationEnum.VALID, null);
    }

    public static boolean isBounceEmailAddress(String email) {
        boolean result = false;
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_EMAIL_DAO)) {
            try {
                result = DAOFactory.getInstance().getEmailDAO().isBounceEmailAddress(email);
            }
            catch (DAOException e) {
                log.warn((Object)String.format("Failed to check isBounceEmailAddress for email:%s, will treat it as non-bounce email address", email), (Throwable)e);
            }
        } else {
            try {
                User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
                result = userBean.isBounceEmailAddress(email);
            }
            catch (Exception e) {
                log.warn((Object)String.format("Failed to check isBounceEmailAddress for email:%s, will treat it as non-bounce email address", email), (Throwable)e);
            }
        }
        if (result) {
            log.info((Object)("Found email address in bouncedb:" + email));
        }
        return result;
    }

    public static boolean isGmailAddress(String emailAddress) {
        if (StringUtil.isBlank(emailAddress)) {
            return false;
        }
        String emailDomain = StringUtil.getEmailDomain(emailAddress);
        if (StringUtil.isBlank(emailDomain)) {
            return false;
        }
        return SystemProperty.isValueInArray(emailDomain, SystemPropertyEntities.Default.GMAIL_DOMAINS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static void doRegexpBasedValidation(String emailAddress) throws Exception {
        Pattern p;
        Matcher m;
        String regexpsSysprop = SystemProperty.get(SystemPropertyEntities.Registration.EMAIL_EXCLUSION_REGULAR_EXPRESSIONS);
        if (StringUtil.isBlank(regexpsSysprop)) {
            return;
        }
        if (!regexpsSysprop.equals(currentEmailAddressRegexpFilters)) {
            Class<EmailUtils> clazz = EmailUtils.class;
            // MONITORENTER : com.projectgoth.fusion.common.EmailUtils.class
            if (!regexpsSysprop.equals(currentEmailAddressRegexpFilters)) {
                String[] regexps;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Reloading regular expressions from " + SystemPropertyEntities.Registration.EMAIL_EXCLUSION_REGULAR_EXPRESSIONS));
                }
                currentEmailAddressRegexpFilters = regexpsSysprop;
                ConcurrentHashMap<String, Pattern> newMap = new ConcurrentHashMap<String, Pattern>();
                for (String re : regexps = regexpsSysprop.split(EMAIL_ADDRESS_REGEXP_FILTERS_SEPARATOR)) {
                    Pattern p2 = Pattern.compile(re);
                    newMap.put(re, p2);
                }
                regexpToPatternMap = newMap;
            }
            // MONITOREXIT : clazz
        }
        Collection<Pattern> patterns = regexpToPatternMap.values();
        Iterator<Pattern> i$ = patterns.iterator();
        do {
            if (!i$.hasNext()) return;
        } while (!(m = (p = i$.next()).matcher(emailAddress)).matches());
        log.info((Object)("Rejecting email address: " + emailAddress + " as it matches regexp=" + p.pattern()));
        throw new Exception(INVALID_EMAIL_ADDRESS_EXCEPTION_MSG);
    }

    public static String stripPeriodsFromGmailAddress(String emailAddress) {
        String local = emailAddress.substring(0, emailAddress.indexOf(64)).toLowerCase();
        local = local.replace(".", "");
        return local + "@gmail.com";
    }

    public static String getSanitizedEmailAddressLocalPart(String emailAddress) {
        String localEmail = emailAddress.substring(0, emailAddress.indexOf(64)).toLowerCase();
        if (localEmail.contains("+")) {
            return localEmail.substring(0, localEmail.indexOf("+")).replaceAll("\\W", "");
        }
        return localEmail.replaceAll("\\W", "");
    }

    public static String getDomain(String emailAddress) throws Exception {
        if (StringUtil.isBlank(emailAddress)) {
            throw new Exception("Please provide an email address.");
        }
        if (!StringUtil.isValidEmail(emailAddress)) {
            throw new Exception("Invalid email address provided.");
        }
        return emailAddress.substring(emailAddress.indexOf(64) + 1, emailAddress.length()).toLowerCase();
    }

    public static String getTopLevelDomain(String emailAddress) throws Exception {
        String domain = EmailUtils.getDomain(emailAddress);
        String[] parts = domain.split("\\.");
        if (parts.length < 2) {
            throw new Exception("Invalid email address provided.");
        }
        return String.format("%s.%s", parts[parts.length - 2], parts[parts.length - 1]);
    }

    public static boolean isInternalEmailAddress(String email, String defaultMigEmailDomain) {
        if (StringUtil.isBlank(email) || StringUtil.isBlank(defaultMigEmailDomain)) {
            return false;
        }
        try {
            email = email.toLowerCase();
            return email.endsWith(defaultMigEmailDomain) && !whitelistEmailAddress.getValue().contains(email);
        }
        catch (Exception e) {
            return false;
        }
    }

    public static class EmailValidatationResult {
        public String reason;
        public EmailValidatationEnum result;

        public EmailValidatationResult(EmailValidatationEnum result, String reason) {
            this.result = result;
            this.reason = reason;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum EmailValidatationEnum {
        VALID,
        INVALID,
        BOUNCED_EMAIL_ADDRESS,
        PERIODS_EXCEED_IN_GMAIL;

    }
}

