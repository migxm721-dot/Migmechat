/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.reputation.util;

public class CSVUtils {
    public static String getColumnFromLine(String line, int column, char delimeter) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int index = 0;
        int currentColumn = 0;
        if (column > 0) {
            while (currentColumn++ < column && index < line.length() - 1) {
                index = line.indexOf(delimeter, index + 1);
            }
            if (index >= line.length() - 1 || index < 0) {
                return null;
            }
        }
        int finalIndex = line.indexOf(delimeter, index + 1);
        if (column > 0) {
            ++index;
        }
        if (finalIndex < 0) {
            return line.substring(index);
        }
        return line.substring(index, finalIndex);
    }

    public static String getColumnFromLineAfter(String line, int column, char delimeter, String after) {
        if (line == null || line.length() == 0) {
            return null;
        }
        int index = line.indexOf(after) + after.length();
        int currentColumn = 0;
        if (column > 0) {
            while (currentColumn++ < column && index < line.length() - 1) {
                index = line.indexOf(delimeter, index + 1);
            }
            if (index >= line.length() - 1 || index < 0) {
                return null;
            }
        }
        int finalIndex = line.indexOf(delimeter, index + 1);
        if (column > 0) {
            ++index;
        }
        if (finalIndex < 0) {
            return line.substring(index);
        }
        return line.substring(index, finalIndex);
    }

    public static void main(String[] args) {
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 0, ','));
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 1, ','));
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 2, ','));
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 3, ','));
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 4, ','));
        System.out.println(CSVUtils.getColumnFromLine("100,1,102,3,104", 5, ','));
        System.out.println(CSVUtils.getColumnFromLineAfter("bla bla - 100,1,102,3,104", 0, ',', "- "));
        System.out.println(CSVUtils.getColumnFromLine("bla bla - 100,1,102,3,104", 2, ','));
    }
}

