/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.ConfigUtils;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;

public class MessageBundle {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MessageBundle.class));
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final String MESSAGE_BUNDLE_DIR_KEY = "messageResourceBundle.dir";
    public static final String DEFAULT_BUNDLE = "resource.fusion";
    public static final ClassLoader LOADER = MessageBundle.getDefaultLoader();
    public static final Locale INDONESIAN_LOCALE = new Locale("id", "ID");

    private static ClassLoader getDefaultLoader() {
        String loaderDir = null;
        if (null == System.getProperty(MESSAGE_BUNDLE_DIR_KEY)) {
            System.setProperty(MESSAGE_BUNDLE_DIR_KEY, ConfigUtils.getConfigDirectory());
            loaderDir = ConfigUtils.getConfigDirectory();
        } else {
            loaderDir = System.getProperty(MESSAGE_BUNDLE_DIR_KEY).endsWith(File.separator) ? System.getProperty(MESSAGE_BUNDLE_DIR_KEY) : System.getProperty(MESSAGE_BUNDLE_DIR_KEY) + File.separator;
        }
        File file = new File(loaderDir);
        try {
            URL[] urls = new URL[]{file.toURI().toURL()};
            return new URLClassLoader(urls);
        }
        catch (MalformedURLException e) {
            log.error((Object)("Unable to load loader from default directory [" + loaderDir + "]"));
            return null;
        }
    }

    public static ResourceBundle getBundle() throws MissingResourceException {
        return MessageBundle.getBundle(DEFAULT_BUNDLE, DEFAULT_LOCALE);
    }

    public static ResourceBundle getBundle(String bundleName) throws MissingResourceException {
        return MessageBundle.getBundle(bundleName, DEFAULT_LOCALE);
    }

    public static ResourceBundle getBundle(String bundleName, Locale locale) throws MissingResourceException {
        try {
            return MessageBundle.getBundle(bundleName, locale, LOADER);
        }
        catch (Exception ee) {
            throw new MissingResourceException("Unable to load resource bundle for " + bundleName + " " + locale.toString(), "MessageBundle", bundleName);
        }
    }

    public static ResourceBundle getBundle(String bundleName, Locale locale, ClassLoader classLoader) throws MissingResourceException {
        return ResourceBundle.getBundle(bundleName, locale, classLoader);
    }

    public static String getMessage(String key) {
        return MessageBundle.getMessage(key, new Object[0]);
    }

    public static String getMessage(String key, Object ... parameters) {
        try {
            return MessageFormat.format(MessageBundle.getBundle().getString(key), parameters);
        }
        catch (MissingResourceException mre) {
            return key;
        }
    }

    public static String getMessage(String bundleName, String key) {
        return MessageBundle.getMessage(bundleName, key, new Object[0]);
    }

    public static String getMessage(String bundleName, String key, Object ... parameters) {
        try {
            return MessageFormat.format(MessageBundle.getBundle(bundleName).getString(key), parameters);
        }
        catch (MissingResourceException mre) {
            log.error((Object)("Unable to get resourceBundle [" + bundleName + "] " + mre.getMessage()));
            return key;
        }
    }

    public static String getMessage(String bundleName, Locale locale, String key) {
        try {
            return MessageBundle.getBundle(bundleName, locale).getString(key);
        }
        catch (MissingResourceException mre) {
            log.error((Object)("Unable to get resourceBundle [" + bundleName + "] " + mre.getMessage()));
            return key;
        }
    }

    public static String getMessage(String bundleName, Locale locale, String key, Object ... parameters) {
        try {
            return MessageFormat.format(MessageBundle.getBundle(bundleName, locale).getString(key), parameters);
        }
        catch (MissingResourceException mre) {
            log.error((Object)("Unable to get resourceBundle [" + bundleName + "] " + mre.getMessage()));
            return key;
        }
    }

    public static String getMessage(String bundleName, Locale locale, ClassLoader loader, String key, Object ... parameters) {
        try {
            return MessageFormat.format(MessageBundle.getBundle(bundleName, locale, loader).getString(key), parameters);
        }
        catch (MissingResourceException mre) {
            log.error((Object)mre.getMessage());
            return key;
        }
    }
}

