/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class WebCommon {
    public static int calculateAge(Date dateOfBirth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateOfBirth);
        int yearBorn = calendar.get(1);
        calendar.setTime(new Date(System.currentTimeMillis()));
        int currentYear = calendar.get(1);
        int age = currentYear - yearBorn;
        return age;
    }

    public static String toNiceDuration(long milliseconds) {
        long days = milliseconds / 86400000L;
        long hours = milliseconds % 86400000L / 3600000L;
        long minutes = milliseconds % 3600000L / 60000L;
        long seconds = milliseconds % 60000L / 1000L;
        StringBuffer sb = new StringBuffer();
        sb.append(days);
        sb.append(" day");
        if (days != 1L) {
            sb.append("s");
        }
        sb.append(" ");
        sb.append(hours);
        sb.append(" hour");
        if (hours != 1L) {
            sb.append("s");
        }
        sb.append(" ");
        sb.append(minutes);
        sb.append(" minute");
        if (minutes != 1L) {
            sb.append("s");
        }
        sb.append(" ");
        sb.append(seconds);
        sb.append(" second");
        if (seconds != 1L) {
            sb.append("s");
        }
        return sb.toString();
    }

    public static String httpPost(String server, String content) throws IOException {
        URL url = new URL(server);
        HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
        httpConn.setDoOutput(true);
        httpConn.setUseCaches(false);
        httpConn.getOutputStream().write(content.getBytes("UTF-8"));
        if (httpConn.getResponseCode() == 200) {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
        return "HTTP " + httpConn.getResponseCode() + " " + httpConn.getResponseMessage();
    }

    public static double toIPNumber(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        if (tokens.length == 4) {
            return Long.parseLong(tokens[0]) * 0x1000000L + Long.parseLong(tokens[1]) * 65536L + Long.parseLong(tokens[2]) * 256L + Long.parseLong(tokens[3]);
        }
        return 0.0;
    }
}

