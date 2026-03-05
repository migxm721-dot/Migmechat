/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.common.StringUtil;
import java.io.File;

public abstract class ConfigUtils {
    public static final String CONFIG_ENV_NAME = "config.dir";
    public static final String ROOT_LOG_FOLDER_ENV_NAME = "log.dir";
    public static final String LOG_FILENAME = "log.filename";
    public static final String DEFAULT_INSTANCE_NAME = "java";

    public static final String getConfigDirectory() {
        if (System.getProperty(CONFIG_ENV_NAME) != null) {
            if (System.getProperty(CONFIG_ENV_NAME).endsWith(File.separator)) {
                return System.getProperty(CONFIG_ENV_NAME);
            }
            return System.getProperty(CONFIG_ENV_NAME) + File.separator;
        }
        return "";
    }

    public static final String getDefaultLog4jConfigFilename() {
        return ConfigUtils.getConfigDirectory() + "log4j.xml";
    }

    public static final String getApplicationLog4jFilename(String applicationName) {
        return ConfigUtils.getConfigDirectory() + "log4j." + applicationName + ".xml";
    }

    public static String getLoggerName(Class clazz) {
        if (clazz.getName().startsWith("com.projectgoth.fusion")) {
            return clazz.getName().substring(23);
        }
        return clazz.getName();
    }

    public static String getRootLogFolder() {
        if (!StringUtil.isBlank(System.getProperty(ROOT_LOG_FOLDER_ENV_NAME))) {
            if (System.getProperty(ROOT_LOG_FOLDER_ENV_NAME).endsWith(File.separator)) {
                return System.getProperty(ROOT_LOG_FOLDER_ENV_NAME);
            }
            return System.getProperty(ROOT_LOG_FOLDER_ENV_NAME) + File.separator;
        }
        return "";
    }

    public static boolean mkdirsRootLogFolder() {
        String rootLogFolder = ConfigUtils.getRootLogFolder();
        if (StringUtil.isBlank(rootLogFolder)) {
            return false;
        }
        File logFolder = new File(rootLogFolder);
        if (!logFolder.exists()) {
            return logFolder.mkdirs();
        }
        return false;
    }

    public static final String getInstanceName() {
        String logFilename = System.getProperty(LOG_FILENAME);
        return StringUtil.isBlank(logFilename) ? DEFAULT_INSTANCE_NAME : logFilename;
    }
}

