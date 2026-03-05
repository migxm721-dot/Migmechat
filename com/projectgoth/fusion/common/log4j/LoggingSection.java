/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Level
 */
package com.projectgoth.fusion.common.log4j;

import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class LoggingSection {
    private static String getName(Enum<?> e) {
        return e.name();
    }

    private static String getNamespace(Enum<?> e) {
        return e.getDeclaringClass().getSimpleName();
    }

    public static boolean isEnabled(LoggingSectionInterface section) {
        if (!SystemProperty.getBool(SystemPropertyEntities.LoggingSections.ENABLED)) {
            return false;
        }
        String loggingFeatureNamespace = SystemPropertyEntities.getNameWithNamespace("LoggingSections", "ENABLED");
        String loggingForGroupFeaturePropertyName = SystemPropertyEntities.getNameWithNamespace(loggingFeatureNamespace, section.getLoggingSectionNamespace());
        boolean loggingForGroupEnabled = SystemProperty.getBool(loggingForGroupFeaturePropertyName, true);
        if (!loggingForGroupEnabled) {
            return false;
        }
        String loggingForSectionPropertyName = SystemPropertyEntities.getNameWithNamespace(loggingForGroupFeaturePropertyName, section.getSectionName());
        return SystemProperty.getBool(loggingForSectionPropertyName, true);
    }

    public static Level getLogLevel(LoggingSectionInterface section) {
        String logLevelPropertyName = SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getNameWithNamespace(SystemPropertyEntities.getNameWithNamespace("LoggingSections", "LEVEL"), section.getLoggingSectionNamespace()), section.getSectionName());
        String logLevelName = SystemProperty.get(logLevelPropertyName, null);
        if (logLevelName == null) {
            return Level.DEBUG;
        }
        Level level = SingletonHolder.getLevelNameMap().get(StringUtil.trimmedUpperCase(logLevelName));
        return level == null ? Level.DEBUG : level;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum AuthServiceI implements LoggingSectionInterface
    {
        CreateCredCheckPasswd(true, Level.INFO),
        UpdateCredCheckPasswd(true, Level.INFO),
        UpdateFusionCredCheckPasswd(true, Level.INFO);

        private boolean enabledByDefault;
        private Level defaultLogLevel;

        private AuthServiceI(boolean enabledByDefault, Level level) {
            this.enabledByDefault = enabledByDefault;
            this.defaultLogLevel = level;
        }

        @Override
        public boolean isEnabledByDefault() {
            return this.enabledByDefault;
        }

        @Override
        public String getSectionName() {
            return LoggingSection.getName(this);
        }

        @Override
        public Level getDefaultLogLevel() {
            return this.defaultLogLevel;
        }

        @Override
        public String getLoggingSectionNamespace() {
            return LoggingSection.getNamespace(this);
        }
    }

    public static interface LoggingSectionInterface {
        public boolean isEnabledByDefault();

        public String getSectionName();

        public Level getDefaultLogLevel();

        public String getLoggingSectionNamespace();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SingletonHolder {
        private static final Map<String, Level> levelNameMap = SingletonHolder.buildMap();

        private SingletonHolder() {
        }

        private static Map<String, Level> buildMap() {
            HashMap<String, Level> m = new HashMap<String, Level>();
            m.put("DEBUG", Level.DEBUG);
            m.put("INFO", Level.INFO);
            m.put("WARN", Level.WARN);
            m.put("ERROR", Level.ERROR);
            m.put("FATAL", Level.FATAL);
            m.put("OFF", Level.OFF);
            return Collections.unmodifiableMap(m);
        }

        public static Map<String, Level> getLevelNameMap() {
            return levelNameMap;
        }
    }
}

