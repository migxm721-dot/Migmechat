/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.danga.MemCached.MemCachedClient
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.cache;

import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.data.ContactData;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContactList {
    public static Set<ContactData> newContactList() {
        return new HashSet<ContactData>();
    }

    public static Set<ContactData> newContactList(Collection<ContactData> contacts) {
        if (contacts == null) {
            return null;
        }
        return new HashSet<ContactData>(contacts);
    }

    public static String getKey(String username) {
        return MemCachedUtils.getCacheKeyInNamespace("CL", username);
    }

    public static Set<ContactData> getContactList(MemCachedClient instance, String username) {
        if (instance == null) {
            instance = MemCachedClientWrapper.getMemCachedClient(MemCachedUtils.Instance.contactList);
        }
        return (Set)instance.get(ContactList.getKey(username));
    }

    public static boolean setContactList(MemCachedClient instance, String username, Set<ContactData> contactList) {
        if (instance == null) {
            instance = MemCachedClientWrapper.getMemCachedClient(MemCachedUtils.Instance.contactList);
        }
        Calendar now = Calendar.getInstance();
        now.add(6, 5);
        return instance.set(ContactList.getKey(username), contactList, now.getTime());
    }

    public static boolean deleteContactList(MemCachedClient instance, String username) {
        return instance.delete(ContactList.getKey(username));
    }

    public static Set<String> getFusionContactUsernames(MemCachedClient instance, String username) {
        Set<ContactData> contacts = ContactList.getContactList(instance, username);
        if (contacts != null) {
            return ContactList.fusionContactUsernames(contacts);
        }
        return null;
    }

    public static Set<String> fusionContactUsernames(Set<ContactData> contacts) {
        HashSet<String> contactUsernames = new HashSet<String>();
        for (ContactData contactData : contacts) {
            if (!StringUtils.hasLength((String)contactData.fusionUsername)) continue;
            contactUsernames.add(contactData.fusionUsername);
        }
        return contactUsernames;
    }

    public static SortedSet<ContactData> sortByDisplayName(Set<ContactData> contacts) {
        TreeSet<ContactData> sortedSet = new TreeSet<ContactData>(new SortByDisplayName());
        sortedSet.addAll(contacts);
        return sortedSet;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SortByDisplayName
    implements Comparator<ContactData> {
        private SortByDisplayName() {
        }

        @Override
        public int compare(ContactData o1, ContactData o2) {
            return o1.displayName.toUpperCase().compareTo(o2.displayName.toUpperCase());
        }
    }
}

