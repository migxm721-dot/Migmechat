/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.common;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

public class HashObjectUtils {
    public static Hashtable dataObjectToHashtable(Object dataObject) {
        Hashtable<String, Object> hashData = new Hashtable<String, Object>();
        if (dataObject == null) {
            return hashData;
        }
        try {
            Class<?> c = dataObject.getClass();
            Field[] fields = c.getFields();
            for (int i = 0; i < fields.length; ++i) {
                Object value = fields[i].get(dataObject);
                if (value == null) continue;
                if (value instanceof String) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Integer) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Long) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Double) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Enum) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Date) {
                    Date date = (Date)value;
                    hashData.put(fields[i].getName(), String.valueOf(date.getTime() / 1000L));
                    continue;
                }
                if (value instanceof Boolean) {
                    Boolean bool = (Boolean)value;
                    String rval = "0";
                    if (bool.booleanValue()) {
                        rval = "1";
                    }
                    hashData.put(fields[i].getName(), rval);
                    continue;
                }
                if (value instanceof String[]) {
                    Vector<String> v = new Vector<String>();
                    for (String s : (String[])value) {
                        v.add(s);
                    }
                    hashData.put(fields[i].getName(), v);
                    continue;
                }
                if (!(value instanceof Object)) continue;
                Class<?> internalClass = value.getClass();
                Field[] internalFields = internalClass.getFields();
                for (int z = 0; z < internalFields.length; ++z) {
                    Object internalValue = internalFields[z].get(value);
                    if (internalValue == null) continue;
                    if (internalValue instanceof String) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Integer) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Long) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Double) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Enum) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Date) {
                        Date date = (Date)internalValue;
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(date.getTime() / 1000L));
                        continue;
                    }
                    if (internalValue instanceof Boolean) {
                        Boolean bool = (Boolean)internalValue;
                        String rval = "0";
                        if (bool.booleanValue()) {
                            rval = "1";
                        }
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), rval);
                        continue;
                    }
                    if (value instanceof String[]) {
                        Vector<String> v = new Vector<String>();
                        for (String s : (String[])value) {
                            v.add(s);
                        }
                        hashData.put(fields[i].getName(), v);
                        continue;
                    }
                    if (!(internalValue instanceof Object)) continue;
                }
            }
            return hashData;
        }
        catch (SecurityException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        return null;
    }

    public static Hashtable dataObjectToHashtableWithNulls(Object dataObject) {
        try {
            Hashtable<String, String> hashData = new Hashtable<String, String>();
            Class<?> c = dataObject.getClass();
            Field[] fields = c.getFields();
            for (int i = 0; i < fields.length; ++i) {
                Object value = fields[i].get(dataObject);
                if (value instanceof String) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Integer) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Long) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Double) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Enum) {
                    hashData.put(fields[i].getName(), String.valueOf(value));
                    continue;
                }
                if (value instanceof Date) {
                    Date date = (Date)value;
                    hashData.put(fields[i].getName(), String.valueOf(date.getTime() / 1000L));
                    continue;
                }
                if (value instanceof Boolean) {
                    Boolean bool = (Boolean)value;
                    String rval = "0";
                    if (bool.booleanValue()) {
                        rval = "1";
                    }
                    hashData.put(fields[i].getName(), rval);
                    continue;
                }
                if (value == null) {
                    hashData.put(fields[i].getName(), "null");
                    continue;
                }
                if (!(value instanceof Object)) continue;
                Class<?> internalClass = value.getClass();
                Field[] internalFields = internalClass.getFields();
                for (int z = 0; z < internalFields.length; ++z) {
                    Object internalValue = internalFields[z].get(value);
                    if (internalValue instanceof String) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Integer) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Long) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Double) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Enum) {
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(internalValue));
                        continue;
                    }
                    if (internalValue instanceof Date) {
                        Date date = (Date)internalValue;
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), String.valueOf(date.getTime() / 1000L));
                        continue;
                    }
                    if (internalValue instanceof Boolean) {
                        Boolean bool = (Boolean)internalValue;
                        String rval = "0";
                        if (bool.booleanValue()) {
                            rval = "1";
                        }
                        hashData.put(fields[i].getName() + '.' + internalFields[z].getName(), rval);
                        continue;
                    }
                    if (value == null) {
                        hashData.put(fields[i].getName(), "null");
                        continue;
                    }
                    if (!(internalValue instanceof Object)) continue;
                }
            }
            return hashData;
        }
        catch (SecurityException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        return null;
    }

    public static Object stringArrayToDataObject(String[] keys, String[] values, Object obj) {
        try {
            for (int i = 0; i < keys.length; ++i) {
                try {
                    Field field = obj.getClass().getField(keys[i]);
                    if (field.getType().toString().equals("class java.lang.String")) {
                        if (values[i] == null) continue;
                        field.set(obj, values[i]);
                        continue;
                    }
                    if (field.getType().toString().equals("class java.util.Date")) {
                        if (values[i] == null) continue;
                        field.set(obj, new Date(Long.valueOf(values[i]) * 1000L));
                        continue;
                    }
                    if (field.getType().toString().equals("class java.lang.Integer")) {
                        if (values[i] == null) continue;
                        field.set(obj, Integer.valueOf(values[i]));
                        continue;
                    }
                    if (field.getType().toString().equals("class java.lang.Double")) {
                        if (values[i] == null) continue;
                        field.set(obj, Double.valueOf(values[i]));
                        continue;
                    }
                    if (field.getType().toString().equals("class java.lang.Boolean")) {
                        if (values[i] == null) continue;
                        if (values[i].equals("1")) {
                            field.set(obj, true);
                            continue;
                        }
                        field.set(obj, false);
                        continue;
                    }
                    if (field.getType().toString().indexOf("Enum") <= 0 || values[i] == null) continue;
                    field.set(obj, Enum.valueOf(field.getType(), values[i]));
                    continue;
                }
                catch (NoSuchFieldException ignored) {
                    // empty catch block
                }
            }
            return obj;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Object stringArrayToDataObject(String[] stringArrayData, Object obj) {
        try {
            Field[] fields = obj.getClass().getFields();
            for (int i = 0; i < fields.length; ++i) {
                Field field = fields[i];
                if (field.getType().toString().equals("class java.lang.String")) {
                    if (stringArrayData[i] == null) continue;
                    field.set(obj, stringArrayData[i]);
                    continue;
                }
                if (field.getType().toString().equals("class java.util.Date")) {
                    if (stringArrayData[i] == null) continue;
                    field.set(obj, new Date(Long.valueOf(stringArrayData[i])));
                    continue;
                }
                if (field.getType().toString().equals("class java.lang.Integer")) {
                    if (stringArrayData[i] == null) continue;
                    field.set(obj, Integer.valueOf(stringArrayData[i]));
                    continue;
                }
                if (field.getType().toString().equals("class java.lang.Double")) {
                    if (stringArrayData[i] == null) continue;
                    field.set(obj, Double.valueOf(stringArrayData[i]));
                    continue;
                }
                if (field.getType().toString().equals("class java.lang.Boolean")) {
                    if (stringArrayData[i] == null) continue;
                    if (stringArrayData[i].equals("1")) {
                        field.set(obj, true);
                        continue;
                    }
                    field.set(obj, false);
                    continue;
                }
                if (field.getType().toString().indexOf("Enum") <= 0 || stringArrayData[i] == null) continue;
                field.set(obj, Enum.valueOf(field.getType(), stringArrayData[i]));
            }
            return obj;
        }
        catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void appendPagingMarker(Vector vectorToAdd, int currentPage, int numRows, int numEntries) {
        Hashtable<String, Integer> markerHash = new Hashtable<String, Integer>();
        markerHash.put("page", currentPage + 1);
        markerHash.put("numEntries", numRows);
        if (numRows == 0) {
            markerHash.put("numPages", 0);
        }
        if (numRows > 0 && numRows / numEntries == 0) {
            markerHash.put("numPages", 1);
        } else {
            double numRowsD = numRows;
            double numEntriesD = numEntries;
            markerHash.put("numPages", (int)Math.ceil(numRowsD / numEntriesD));
        }
        vectorToAdd.add(markerHash);
    }
}

