/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.smsengine;

import com.projectgoth.fusion.common.ConfigUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenericSourceNumberPool {
    private static Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GenericSourceNumberPool.class));
    private static final int cacheTime = 300000;
    private static Map<String, List<String>> sourceNumberMap = new ConcurrentHashMap<String, List<String>>();
    private static SecureRandom random = new SecureRandom();
    private static Map<String, Long> lastUpdatedMap = new ConcurrentHashMap<String, Long>();

    private static boolean validateMDNNumbers(List<String> numbers) {
        for (String number : numbers) {
            if (number.matches("[0-9]+$")) continue;
            return false;
        }
        return true;
    }

    private static void loadSourceNumbers(String gatewayName) throws IOException, Exception {
        Properties properties = new Properties();
        String propertiesLocation = ConfigUtils.getConfigDirectory() + "resource/" + "smsengine/" + gatewayName.toLowerCase() + ".properties";
        LinkedList<String> numbers = new LinkedList();
        try {
            FileInputStream inputStream = new FileInputStream(new File(propertiesLocation));
            properties.load(inputStream);
            numbers = new LinkedList<Object>(properties.values());
            if (!GenericSourceNumberPool.validateMDNNumbers(numbers)) {
                throw new Exception("property file " + gatewayName.toLowerCase() + ".properties, contains an incorrectly formatted MDN number");
            }
            log.info((Object)("mdn number pool for " + gatewayName + " successfully loaded with " + numbers.size() + " entries"));
        }
        catch (IOException e) {
            log.error((Object)("unable to load " + gatewayName + ".properties file"), (Throwable)e);
            throw e;
        }
        lastUpdatedMap.put(gatewayName, System.currentTimeMillis());
        sourceNumberMap.put(gatewayName, numbers);
    }

    private static List<String> get(String gatewayName) throws IOException, Exception {
        if (!lastUpdatedMap.containsKey(gatewayName) || System.currentTimeMillis() - lastUpdatedMap.get(gatewayName) > 300000L) {
            GenericSourceNumberPool.loadSourceNumbers(gatewayName);
        }
        return sourceNumberMap.get(gatewayName);
    }

    public static String getSourceNumber(String gatewayName) throws Exception {
        List<String> pool = GenericSourceNumberPool.get(gatewayName);
        if (pool.isEmpty()) {
            throw new Exception("gateway source pool for " + gatewayName + " is empty");
        }
        return pool.get(random.nextInt(pool.size()));
    }

    public static void main(String[] args) {
        try {
            String mdn = GenericSourceNumberPool.getSourceNumber("iris");
            assert (!mdn.equals(GenericSourceNumberPool.getSourceNumber("iris")));
            mdn = GenericSourceNumberPool.getSourceNumber("smarttelcom");
            assert (!mdn.equals(GenericSourceNumberPool.getSourceNumber("smarttelcom")));
            int i = 0;
            for (i = 0; i < 10; ++i) {
                System.out.println(GenericSourceNumberPool.getSourceNumber("smarttelcom"));
                Thread.sleep(2000L);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

