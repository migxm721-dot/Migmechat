/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.common;

import com.projectgoth.fusion.app.dao.db.fusiondb.DBUtils;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.LazyLoader;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SystemProperty {
    private static final Pattern LIST_SEPARATOR_PATTERN = Pattern.compile("(\\s*;\\s*)+");
    private static final String[] EMPTY_ARRAY = new String[0];
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(SystemProperty.class));
    private static final DatabaseSystemPropertyLoader DB_LOADER = new DatabaseSystemPropertyLoader();
    private static SystemPropertyLoader loader = DB_LOADER;

    public static SystemPropertyLoader setSystemPropertyLoader(SystemPropertyLoader newLoader) {
        SystemPropertyLoader oldLoader = newLoader;
        loader = newLoader;
        return oldLoader;
    }

    public static void resetSystemPropertyLoaderToDefault() {
        loader = DB_LOADER;
    }

    public static Map<String, String> getAllSystemProperties() {
        return loader.getSystemProperties();
    }

    public static synchronized void ejbInit(DataSource dataSource) throws SQLException {
        DB_LOADER.ejbInit(dataSource);
    }

    public static void resetCachedProperties() {
        loader.resetSystemProperties();
    }

    public static String get(String propertyName) throws NoSuchFieldException {
        Map<String, String> properties = loader.getSystemProperties();
        String value = properties.get(propertyName.toLowerCase());
        if (value == null) {
            throw new NoSuchFieldException("Undefined system property: " + propertyName);
        }
        return value;
    }

    public static int getInt(String propertyName) throws NoSuchFieldException {
        return Integer.parseInt(SystemProperty.get(propertyName));
    }

    public static long getLong(String propertyName) throws NoSuchFieldException {
        return Long.parseLong(SystemProperty.get(propertyName));
    }

    public static double getDouble(String propertyName) throws NoSuchFieldException {
        return Double.parseDouble(SystemProperty.get(propertyName));
    }

    public static boolean getBool(String propertyName) throws NoSuchFieldException {
        return !SystemProperty.get(propertyName).equals("0");
    }

    public static String[] getArray(String propertyName) throws NoSuchFieldException {
        String value = SystemProperty.get(propertyName);
        return StringUtil.isBlank(value) ? EMPTY_ARRAY : LIST_SEPARATOR_PATTERN.split(value);
    }

    public static int[] getIntArray(String propertyName) throws NoSuchFieldException {
        String[] tokens = SystemProperty.getArray(propertyName);
        int[] array = new int[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            array[i] = Integer.parseInt(tokens[i]);
        }
        return array;
    }

    public static short[] getShortArray(String propertyName) throws NoSuchFieldException {
        String[] tokens = SystemProperty.getArray(propertyName);
        short[] array = new short[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            array[i] = Short.parseShort(tokens[i]);
        }
        return array;
    }

    public static double[] getDoubleArray(String propertyName) throws NoSuchFieldException {
        String[] tokens = SystemProperty.getArray(propertyName);
        double[] array = new double[tokens.length];
        for (int i = 0; i < tokens.length; ++i) {
            array[i] = Double.parseDouble(tokens[i]);
        }
        return array;
    }

    public static String optGet(String propertyName) {
        Map<String, String> properties = loader.getSystemProperties();
        return properties.get(propertyName.toLowerCase());
    }

    public static String get(String propertyName, String defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        return result == null ? defaultValue : result;
    }

    private static byte getByte(String propertyName, byte defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        if (result == null) {
            return defaultValue;
        }
        try {
            return Byte.parseByte(result);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static int getInt(String propertyName, int defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        if (result == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(result);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static long getLong(String propertyName, long defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        if (result == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(result);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static double getDouble(String propertyName, double defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        if (result == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(result);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean getBool(String propertyName, boolean defaultValue) {
        String result = SystemProperty.optGet(propertyName);
        if (result == null) {
            return defaultValue;
        }
        return !result.equals("0");
    }

    private static String[] optGetStringArray(String propertyName) {
        String value = SystemProperty.optGet(propertyName);
        if (value == null || StringUtil.isBlank(value)) {
            return null;
        }
        return LIST_SEPARATOR_PATTERN.split(value);
    }

    public static String[] getArray(String propertyName, String[] defaultValue) {
        String[] value = SystemProperty.optGetStringArray(propertyName);
        return value != null ? value : defaultValue;
    }

    public static short[] getShortArray(String propertyName, short[] defaultValue) {
        try {
            String[] tokens = SystemProperty.optGetStringArray(propertyName);
            if (tokens == null) {
                return defaultValue;
            }
            short[] result = new short[tokens.length];
            for (int i = 0; i < tokens.length; ++i) {
                result[i] = Short.parseShort(tokens[i]);
            }
            return result;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static int[] getIntArray(String propertyName, int[] defaultValue) {
        try {
            String[] tokens = SystemProperty.optGetStringArray(propertyName);
            if (tokens == null) {
                return defaultValue;
            }
            int[] result = new int[tokens.length];
            for (int i = 0; i < tokens.length; ++i) {
                result[i] = Integer.parseInt(tokens[i]);
            }
            return result;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    private static double[] getDoubleArray(String propertyName, double[] defaultValue) {
        try {
            String[] tokens = SystemProperty.optGetStringArray(propertyName);
            if (tokens == null) {
                return defaultValue;
            }
            double[] result = new double[tokens.length];
            for (int i = 0; i < tokens.length; ++i) {
                result[i] = Double.parseDouble(tokens[i]);
            }
            return result;
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static String get(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        return SystemProperty.get(propertyEntry.getName(), propertyEntry.getEntry().getDefaultValue().toString());
    }

    public static String get(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, String defaultValue) {
        return SystemProperty.get(propertyEntry.getName(), defaultValue);
    }

    public static byte getByte(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryByte) {
            return SystemProperty.getByte(propertyEntry.getName(), (byte)((Byte)((SystemPropertyEntities.SystemPropertyEntryByte)propertyEntry.getEntry()).getDefaultValue()));
        }
        log.error((Object)String.format("incorrect property type for getByte [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getByte [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static int getInt(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryInteger) {
            return SystemProperty.getInt(propertyEntry.getName(), (int)((Integer)((SystemPropertyEntities.SystemPropertyEntryInteger)propertyEntry.getEntry()).getDefaultValue()));
        }
        log.error((Object)String.format("incorrect property type for getInt [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getInt [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static int getByte(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, byte defaultValue) {
        return SystemProperty.getByte(propertyEntry.getName(), defaultValue);
    }

    public static int getInt(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, int defaultValue) {
        return SystemProperty.getInt(propertyEntry.getName(), defaultValue);
    }

    public static long getLong(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryLong) {
            return SystemProperty.getLong(propertyEntry.getName(), (long)((Long)((SystemPropertyEntities.SystemPropertyEntryLong)propertyEntry.getEntry()).getDefaultValue()));
        }
        log.error((Object)String.format("incorrect property type for getLong [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getLong [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static long getLong(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, long defaultValue) {
        return SystemProperty.getLong(propertyEntry.getName(), defaultValue);
    }

    public static boolean getBool(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryBoolean) {
            return SystemProperty.getBool(propertyEntry.getName(), (boolean)((Boolean)((SystemPropertyEntities.SystemPropertyEntryBoolean)propertyEntry.getEntry()).getDefaultValue()));
        }
        log.error((Object)String.format("incorrect property type for getBool [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getBool [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static boolean getBool(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, boolean defaultValue) {
        return SystemProperty.getBool(propertyEntry.getName(), defaultValue);
    }

    public static double getDouble(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryDouble) {
            return SystemProperty.getDouble(propertyEntry.getName(), (double)((Double)((SystemPropertyEntities.SystemPropertyEntryDouble)propertyEntry.getEntry()).getDefaultValue()));
        }
        log.error((Object)String.format("incorrect property type for getDouble [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getDouble [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static double getDouble(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, double defaultValue) {
        return SystemProperty.getDouble(propertyEntry.getName(), defaultValue);
    }

    public static String[] getArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryStringArray) {
            return SystemProperty.getArray(propertyEntry.getName(), (String[])((SystemPropertyEntities.SystemPropertyEntryStringArray)propertyEntry.getEntry()).getDefaultValue());
        }
        log.error((Object)String.format("incorrect property type for getStringArray [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getStringArray string [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static String[] getArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, String[] defaultValue) {
        return SystemProperty.getArray(propertyEntry.getName(), defaultValue);
    }

    public static int[] getIntArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryIntegerArray) {
            return SystemProperty.getIntArray(propertyEntry.getName(), (int[])((SystemPropertyEntities.SystemPropertyEntryIntegerArray)propertyEntry.getEntry()).getDefaultValue());
        }
        log.error((Object)String.format("incorrect property type for getIntArray [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getIntArray string [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static int[] getIntArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, int[] defaultValue) {
        return SystemProperty.getIntArray(propertyEntry.getName(), defaultValue);
    }

    public static short[] getShortArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryShortArray) {
            return SystemProperty.getShortArray(propertyEntry.getName(), (short[])((SystemPropertyEntities.SystemPropertyEntryShortArray)propertyEntry.getEntry()).getDefaultValue());
        }
        log.error((Object)String.format("incorrect property type for getShortArray [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getShortArray string [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static double[] getDoubleArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        if (propertyEntry.getEntry() instanceof SystemPropertyEntities.SystemPropertyEntryDoubleArray) {
            return SystemProperty.getDoubleArray(propertyEntry.getName(), (double[])((SystemPropertyEntities.SystemPropertyEntryDoubleArray)propertyEntry.getEntry()).getDefaultValue());
        }
        log.error((Object)String.format("incorrect property type for getDoubleArray [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
        throw new IllegalArgumentException(String.format("incorrect property type for getDoubleArray string [%s] [%s]", propertyEntry.getClass().getName(), propertyEntry.getName()));
    }

    public static double[] getDoubleArray(SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry, double[] defaultValue) {
        return SystemProperty.getDoubleArray(propertyEntry.getName(), defaultValue);
    }

    public static boolean isValueInArray(String value, SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        String[] values;
        boolean found = false;
        if (!StringUtil.isBlank(value) && (values = SystemProperty.getArray(propertyEntry)) != null) {
            for (String v : values) {
                if (!value.equalsIgnoreCase(v)) continue;
                found = true;
                break;
            }
        }
        return found;
    }

    public static boolean isValueInIntegerArray(int value, SystemPropertyEntities.SystemPropertyEntryInterface propertyEntry) {
        boolean found = false;
        int[] values = SystemProperty.getIntArray(propertyEntry);
        if (values != null) {
            int[] arr$ = values;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                Integer v = arr$[i$];
                if (!v.equals(value)) continue;
                found = true;
                break;
            }
        }
        return found;
    }

    public static <T extends Number> T getNumber(SystemPropertyEntities.SystemPropertyEntryInterface sysProp, Class<T> valueType) {
        String propValue = SystemProperty.get(sysProp);
        if (Double.class.equals(valueType)) {
            return (T)Double.valueOf(propValue);
        }
        if (Integer.class.equals(valueType)) {
            return (T)Integer.valueOf(propValue);
        }
        if (Long.class.equals(valueType)) {
            return (T)Long.valueOf(propValue);
        }
        throw new IllegalArgumentException("value type " + valueType + " not supported");
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DatabaseSystemPropertyLoader
    implements SystemPropertyLoader {
        private static final int LOCAL_CACHE_TIME = 60000;
        private final LazyLoader<Map<String, String>> local_system_properties = new LazyLoader<Map<String, String>>("LOCAL_SYSTEM_PROPERTIES", 60000L){

            @Override
            protected Map<String, String> fetchValue() throws SQLException {
                return DatabaseSystemPropertyLoader.this.retrievePropertiesFromDBDirectly();
            }
        };

        public Map<String, String> retrievePropertiesFromDBDirectly() throws SQLException {
            ConcurrentHashMap<String, String> concurrentHashMap;
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                if (log.isDebugEnabled()) {
                    log.debug((Object)"Loading system properties directly from fusion database");
                }
                conn = DBUtils.getFusionReadConnection();
                ps = conn.prepareStatement("select * from system");
                rs = ps.executeQuery();
                ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<String, String>();
                while (rs.next()) {
                    properties.put(rs.getString("propertyname").toLowerCase(), rs.getString("propertyvalue"));
                }
                concurrentHashMap = properties;
                Object var7_7 = null;
            }
            catch (SQLException e) {
                try {
                    log.error((Object)"Failed to get System properties from database directly", (Throwable)e);
                    throw e;
                }
                catch (Throwable throwable) {
                    Object var7_8 = null;
                    DBUtils.closeResource(rs, ps, conn, log);
                    throw throwable;
                }
            }
            DBUtils.closeResource(rs, ps, conn, log);
            return concurrentHashMap;
        }

        public synchronized void ejbInit(DataSource dataSource) throws SQLException {
            this.local_system_properties.getValue();
        }

        @Override
        public synchronized void resetSystemProperties() {
        }

        @Override
        public Map<String, String> getSystemProperties() {
            return this.local_system_properties.getValue();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface SystemPropertyLoader {
        public Map<String, String> getSystemProperties();

        public void resetSystemProperties();
    }
}

