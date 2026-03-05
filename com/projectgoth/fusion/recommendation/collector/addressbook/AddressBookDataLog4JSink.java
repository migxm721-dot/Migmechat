/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.collector.addressbook;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DateTimeUtils;
import com.projectgoth.fusion.recommendation.collector.addressbook.AddressBookRecordData;
import com.projectgoth.fusion.recommendation.collector.sinks.log4j.Log4JSink;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AddressBookDataLog4JSink
extends Log4JSink<AddressBookRecordData> {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AddressBookDataLog4JSink.class));
    private static final char FIELD_SEPARATOR = '\u0001';

    public AddressBookDataLog4JSink(String name, String logCategoryName) {
        super(name, logCategoryName);
    }

    @Override
    protected String toString(AddressBookRecordData record) {
        StringBuilder builder = new StringBuilder();
        builder.append(DateTimeUtils.getStringForTimestamp(record.createDate)).append('\u0001').append(record.createDate.getTime()).append('\u0001').append(record.submitterUserID).append('\u0001').append(record.contactType).append('\u0001').append(AddressBookDataLog4JSink.sanitizeContactValue(record.contactValue));
        return builder.toString();
    }

    public static String sanitizeContactValue(String contactValue) {
        if (contactValue == null) {
            log.warn((Object)"Found null contact value");
            return null;
        }
        boolean hasIllegalCharsReplaced = false;
        contactValue = contactValue.trim();
        StringBuilder contactValueStringBuilder = new StringBuilder(contactValue.length());
        for (int i = 0; i < contactValue.length(); ++i) {
            char c = contactValue.charAt(i);
            if (c < ' ') {
                hasIllegalCharsReplaced = true;
                contactValueStringBuilder.append(' ');
                continue;
            }
            contactValueStringBuilder.append(c);
        }
        if (hasIllegalCharsReplaced) {
            log.warn((Object)("Contact value [" + contactValue + "] has some illegal characters"));
        }
        return contactValueStringBuilder.toString();
    }
}

