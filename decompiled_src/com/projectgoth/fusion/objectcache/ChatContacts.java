/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.objectcache;

import com.projectgoth.fusion.app.dao.data.UserObject;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.MessageData;
import com.projectgoth.fusion.data.UserSettingData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.MessageType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.interfaces.Contact;
import com.projectgoth.fusion.interfaces.ContactHome;
import com.projectgoth.fusion.objectcache.ChatObjectManagerUser;
import com.projectgoth.fusion.objectcache.ChatUserData;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.ContactGroupDataIce;
import com.projectgoth.fusion.slice.ContactList;
import com.projectgoth.fusion.slice.MessageDataIce;
import com.projectgoth.fusion.slice.ObjectNotFoundException;
import com.projectgoth.fusion.slice.PresenceAndCapabilityIce;
import com.projectgoth.fusion.slice.UserErrorResponse;
import com.projectgoth.fusion.slice.UserPrx;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ChatContacts {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ChatContacts.class));
    private final List<ContactGroupData> groupList;
    private final Set<ContactData> contactList;
    private int contactListVersion;
    private final Set<String> contactRequestSentList = new HashSet<String>();
    private ChatUserData userData;
    private String username;
    private Integer userID;
    private boolean showOfflineMSNContacts = false;
    private boolean showOfflineYahooContacts = false;
    private boolean showOfflineAIMContacts = false;
    private boolean showOfflineGTalkContacts = false;
    private boolean showOfflineFacebookContacts = false;
    private Integer idIncrement = 0;
    private static UserErrorResponse USER_ERROR_RESPONSE_SILENT_FAIL;
    private static UserErrorResponse USER_ERROR_RESPONSE_OK;
    private static UserErrorResponse USER_ERROR_RESPONSE_FAIL_NOT_ONLINE;
    private static UserErrorResponse USER_ERROR_RESPONSE_FAIL_NOT_ACCEPTING_NOTIFICATIONS;
    private List<String> currentChatrooms = Collections.synchronizedList(new ArrayList());

    public ChatContacts(Contact contactEJB, ChatUserData userData) throws Exception {
        this.userData = userData;
        this.userID = userData.getUserID();
        this.username = userData.getUsername();
        List groupList = null;
        Set<Object> contactList = null;
        if (SystemProperty.getBool(SystemPropertyEntities.DAOSettings.ENABLED_USERDATA_DAO)) {
            UserObject user = new UserObject(this.username);
            groupList = user.getGroupList();
            contactList = user.getContactList();
            if (log.isDebugEnabled()) {
                log.debug((Object)String.format("DAO: get group list and contact list for user:%s, group list:%s, contact list:%s", user, groupList, contactList));
            }
        } else {
            if (contactEJB == null) {
                contactEJB = (Contact)EJBHomeCache.getObject("ejb/Contact", ContactHome.class);
            }
            if (contactEJB != null) {
                contactList = contactEJB.getContactList(this.username);
                groupList = contactEJB.getGroupList(this.username);
                this.contactListVersion = contactEJB.getContactListVersion(this.userID, null);
            }
            if (contactList == null) {
                contactList = new HashSet();
            }
            if (groupList == null) {
                groupList = new LinkedList<ContactGroupData>();
            }
        }
        this.groupList = groupList;
        this.contactList = contactList;
        USER_ERROR_RESPONSE_OK = new UserErrorResponse(null, true, false);
        USER_ERROR_RESPONSE_SILENT_FAIL = new UserErrorResponse(null, true, true);
        USER_ERROR_RESPONSE_FAIL_NOT_ONLINE = new UserErrorResponse(this.username + " is not online", false, true);
        USER_ERROR_RESPONSE_FAIL_NOT_ACCEPTING_NOTIFICATIONS = new UserErrorResponse(this.username + " is not accepting messages", false, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData createContact(String username, String displayname, ImType imType) {
        ContactData contact = new ContactData();
        Object object = this.idIncrement;
        synchronized (object) {
            contact.id = this.idIncrement = Integer.valueOf(this.idIncrement - 1);
        }
        contact.username = username;
        contact.displayName = displayname;
        if (imType == ImType.MSN) {
            contact.msnUsername = username;
            contact.defaultIM = ImType.MSN;
            contact.msnPresence = PresenceType.OFFLINE;
            contact.contactGroupId = -5;
        } else if (imType == ImType.YAHOO) {
            contact.yahooUsername = username;
            contact.defaultIM = ImType.YAHOO;
            contact.yahooPresence = PresenceType.OFFLINE;
            contact.contactGroupId = -6;
        } else if (imType == ImType.AIM) {
            contact.aimUsername = username;
            contact.defaultIM = ImType.AIM;
            contact.aimPresence = PresenceType.OFFLINE;
            contact.contactGroupId = -2;
        } else if (imType == ImType.GTALK) {
            contact.gtalkUsername = username;
            contact.defaultIM = ImType.GTALK;
            contact.gtalkPresence = PresenceType.OFFLINE;
            contact.contactGroupId = -4;
        } else if (imType == ImType.FACEBOOK) {
            contact.facebookUsername = username;
            contact.defaultIM = ImType.FACEBOOK;
            contact.facebookPresence = PresenceType.OFFLINE;
            contact.contactGroupId = -3;
        }
        contact.displayOnPhone = true;
        contact.status = ContactData.StatusEnum.ACTIVE;
        object = this.contactList;
        synchronized (object) {
            this.contactList.add(contact);
        }
        return contact;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addContact(ContactData contact, int contactListVersion) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            this.contactList.add(contact);
            this.contactListVersion = contactListVersion;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeContact(ContactData contact) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            this.contactList.remove(contact);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeContact(ContactData contact, int contactListVersion) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            this.contactList.remove(contact);
            this.contactListVersion = contactListVersion;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData removeContact(int contactId) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            Iterator<ContactData> iterator = this.contactList.iterator();
            while (iterator.hasNext()) {
                ContactData contactData = iterator.next();
                if (contactData.id != contactId) continue;
                iterator.remove();
                return contactData;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData removeContact(String username) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            Iterator<ContactData> iterator = this.contactList.iterator();
            while (iterator.hasNext()) {
                ContactData contactData = iterator.next();
                if (!StringUtils.hasLength((String)contactData.fusionUsername) || !contactData.fusionUsername.equals(username)) continue;
                iterator.remove();
                return contactData;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<Integer> removeContacts(ImType type) {
        ArrayList<Integer> contactsRemoved = new ArrayList<Integer>();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            Iterator<ContactData> i = this.contactList.iterator();
            while (i.hasNext()) {
                ContactData contact = i.next();
                if (!(type == ImType.MSN && contact.isMSNOnly() || type == ImType.YAHOO && contact.isYahooOnly() || type == ImType.AIM && contact.isAIMOnly() || type == ImType.GTALK && contact.isGTalkOnly()) && (type != ImType.FACEBOOK || !contact.isFacebookOnly())) continue;
                i.remove();
                contactsRemoved.add(contact.id);
            }
        }
        return contactsRemoved;
    }

    public int getContactListVersion() {
        return this.contactListVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setContactListVersion(int version) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            this.contactListVersion = version;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData getContact(String contactName) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!contactName.equalsIgnoreCase(contact.fusionUsername)) continue;
                return contact;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData getContact(int id) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (contact.id != id) continue;
                return contact;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactData getContact(String username, ImType imType) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData existingContact : this.contactList) {
                if (!(imType == ImType.MSN && username.equals(existingContact.msnUsername) || imType == ImType.YAHOO && username.equals(existingContact.yahooUsername) || imType == ImType.AIM && username.equals(existingContact.aimUsername) || imType == ImType.GTALK && username.equals(existingContact.gtalkUsername)) && (imType != ImType.FACEBOOK || !username.equals(existingContact.facebookUsername))) continue;
                return existingContact;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer findSourceContactID(MessageType messageType, String source) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                switch (messageType) {
                    case FUSION: {
                        if (contact.fusionUsername == null || !contact.fusionUsername.equals(source)) break;
                        return contact.id;
                    }
                    case SMS: {
                        if (contact.mobilePhone == null || !contact.mobilePhone.equals(source)) break;
                        return contact.id;
                    }
                    case EMAIL: {
                        if (contact.emailAddress == null || !contact.emailAddress.equals(source)) break;
                        return contact.id;
                    }
                    case MSN: {
                        if (contact.msnUsername == null || !contact.msnUsername.equals(source)) break;
                        return contact.id;
                    }
                    case AIM: {
                        if (contact.aimUsername == null || !contact.aimUsername.equals(source)) break;
                        return contact.id;
                    }
                    case YAHOO: {
                        if (contact.yahooUsername == null || !contact.yahooUsername.equals(source)) break;
                        return contact.id;
                    }
                    case GTALK: {
                        if (contact.gtalkUsername == null || !contact.gtalkUsername.equals(source)) break;
                        return contact.id;
                    }
                    case FACEBOOK: {
                        if (contact.facebookUsername == null || !contact.facebookUsername.equals(source)) break;
                        return contact.id;
                    }
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactDataIce[] getOnlineContacts() {
        ArrayList<ContactDataIce> contactDataIceArray = new ArrayList<ContactDataIce>();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (contact.isMSNOnly() && !this.showOfflineMSNContacts && (contact.msnPresence == null || contact.msnPresence == PresenceType.OFFLINE) || contact.isYahooOnly() && !this.showOfflineYahooContacts && (contact.yahooPresence == null || contact.yahooPresence == PresenceType.OFFLINE) || contact.isAIMOnly() && !this.showOfflineAIMContacts && (contact.aimPresence == null || contact.aimPresence == PresenceType.OFFLINE) || contact.isGTalkOnly() && !this.showOfflineGTalkContacts && (contact.gtalkPresence == null || contact.gtalkPresence == PresenceType.OFFLINE) || contact.isFacebookOnly() && !this.showOfflineFacebookContacts && (contact.facebookPresence == null || contact.facebookPresence == PresenceType.OFFLINE)) continue;
                contactDataIceArray.add(contact.toIceObject());
            }
        }
        return contactDataIceArray.toArray(new ContactDataIce[contactDataIceArray.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactDataIce[] getContacts() {
        ArrayList<ContactDataIce> contactDataIceArray = new ArrayList<ContactDataIce>();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (contact.isMSNOnly() && !this.showOfflineMSNContacts && (contact.msnPresence == null || contact.msnPresence == PresenceType.OFFLINE) || contact.isYahooOnly() && !this.showOfflineYahooContacts && (contact.yahooPresence == null || contact.yahooPresence == PresenceType.OFFLINE) || contact.isAIMOnly() && !this.showOfflineAIMContacts && (contact.aimPresence == null || contact.aimPresence == PresenceType.OFFLINE) || contact.isGTalkOnly() && !this.showOfflineGTalkContacts && (contact.gtalkPresence == null || contact.gtalkPresence == PresenceType.OFFLINE) || contact.isFacebookOnly() && !this.showOfflineFacebookContacts && (contact.facebookPresence == null || contact.facebookPresence == PresenceType.OFFLINE)) continue;
                contactDataIceArray.add(contact.toIceObject());
            }
        }
        return contactDataIceArray.toArray(new ContactDataIce[contactDataIceArray.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<ContactData> getContacts(String source, ImType imTypeEnum) {
        ArrayList<ContactData> contacts = new ArrayList<ContactData>();
        switch (imTypeEnum) {
            case FUSION: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.fusionUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            case MSN: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.msnUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            case AIM: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.aimUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            case YAHOO: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.yahooUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            case GTALK: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.gtalkUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            case FACEBOOK: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.facebookUsername)) continue;
                        contacts.add(contact);
                    }
                    break;
                }
            }
            default: {
                log.warn((Object)("getContacts() received invalid IM type '" + imTypeEnum.name() + "'"));
                return null;
            }
        }
        return contacts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactDataIce[] getOtherIMContacts() {
        ArrayList<ContactDataIce> contactDataIceArray = new ArrayList<ContactDataIce>();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!contact.isOtherIMOnly()) continue;
                contactDataIceArray.add(contact.toIceObject());
            }
        }
        return contactDataIceArray.toArray(new ContactDataIce[contactDataIceArray.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getOnlineContactsCount() {
        int onlineContacts = 0;
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!contact.isOnline()) continue;
                ++onlineContacts;
            }
        }
        return onlineContacts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void contactChangedPresence(ImType imTypeEnum, String source, PresenceType presence, ArrayList<Integer> contactIDs, HashSet<ContactData> IMContactsNoLongerOffline) {
        switch (imTypeEnum) {
            case FUSION: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.fusionUsername)) continue;
                        contactIDs.add(contact.id);
                        contact.fusionPresence = presence;
                        break;
                    }
                    break;
                }
            }
            case MSN: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.msnUsername)) continue;
                        if (!this.showOfflineMSNContacts && contact.isMSNOnly() && (contact.msnPresence == null || contact.msnPresence == PresenceType.OFFLINE) && presence != PresenceType.OFFLINE) {
                            IMContactsNoLongerOffline.add(contact);
                        }
                        contactIDs.add(contact.id);
                        contact.msnPresence = presence;
                    }
                    break;
                }
            }
            case AIM: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.aimUsername)) continue;
                        if (!this.showOfflineAIMContacts && contact.isAIMOnly() && (contact.aimPresence == null || contact.aimPresence == PresenceType.OFFLINE) && presence != PresenceType.OFFLINE) {
                            IMContactsNoLongerOffline.add(contact);
                        }
                        contactIDs.add(contact.id);
                        contact.aimPresence = presence;
                    }
                    break;
                }
            }
            case YAHOO: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.yahooUsername)) continue;
                        if (!this.showOfflineYahooContacts && contact.isYahooOnly() && (contact.yahooPresence == null || contact.yahooPresence == PresenceType.OFFLINE) && presence != PresenceType.OFFLINE) {
                            IMContactsNoLongerOffline.add(contact);
                        }
                        contactIDs.add(contact.id);
                        contact.yahooPresence = presence;
                    }
                    break;
                }
            }
            case GTALK: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.gtalkUsername)) continue;
                        if (!this.showOfflineGTalkContacts && contact.isGTalkOnly() && (contact.gtalkPresence == null || contact.gtalkPresence == PresenceType.OFFLINE) && presence != PresenceType.OFFLINE) {
                            IMContactsNoLongerOffline.add(contact);
                        }
                        contactIDs.add(contact.id);
                        contact.gtalkPresence = presence;
                    }
                    break;
                }
            }
            case FACEBOOK: {
                Set<ContactData> set = this.contactList;
                synchronized (set) {
                    for (ContactData contact : this.contactList) {
                        if (!source.equals(contact.facebookUsername)) continue;
                        if (!this.showOfflineFacebookContacts && contact.isFacebookOnly() && (contact.facebookPresence == null || contact.facebookPresence == PresenceType.OFFLINE) && presence != PresenceType.OFFLINE) {
                            IMContactsNoLongerOffline.add(contact);
                        }
                        contactIDs.add(contact.id);
                        contact.facebookPresence = presence;
                    }
                    break;
                }
            }
            default: {
                log.warn((Object)("ChatUser.contactChangedPresence() received invalid IM type '" + imTypeEnum + "'"));
                return;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setContactsPresence(ImType imType, PresenceType presence) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                this.setContactDataPresence(contact, imType, presence);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<String> setContactsPresenceOffline() {
        LinkedList<String> contactUsernames = new LinkedList<String>();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (contact.fusionUsername != null) {
                    contact.fusionPresence = PresenceType.OFFLINE;
                    contactUsernames.add(contact.fusionUsername);
                }
                if (contact.msnUsername != null && contact.msnPresence == null) {
                    contact.msnPresence = PresenceType.OFFLINE;
                }
                if (contact.yahooUsername != null && contact.yahooPresence == null) {
                    contact.yahooPresence = PresenceType.OFFLINE;
                }
                if (contact.aimUsername != null && contact.aimPresence == null) {
                    contact.aimPresence = PresenceType.OFFLINE;
                }
                if (contact.gtalkUsername != null && contact.gtalkPresence == null) {
                    contact.gtalkPresence = PresenceType.OFFLINE;
                }
                if (contact.facebookUsername == null || contact.facebookPresence != null) continue;
                contact.facebookPresence = PresenceType.OFFLINE;
            }
        }
        return contactUsernames;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setContactsDataPresence(ImType imType, PresenceType presence) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                this.setContactDataPresence(contact, imType, presence);
            }
        }
    }

    public void setContactDataPresence(ContactData contact, ImType imType, PresenceType presence) {
        switch (imType) {
            case AIM: {
                contact.aimPresence = presence;
                break;
            }
            case FACEBOOK: {
                contact.facebookPresence = presence;
                break;
            }
            case FUSION: {
                contact.fusionPresence = presence;
                break;
            }
            case GTALK: {
                contact.gtalkPresence = presence;
                break;
            }
            case MSN: {
                contact.msnPresence = presence;
                break;
            }
            case YAHOO: {
                contact.yahooPresence = presence;
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactGroupDataIce[] getGroups() {
        ContactGroupDataIce[] contactGroupDataIceArray;
        List<ContactGroupData> list = this.groupList;
        synchronized (list) {
            contactGroupDataIceArray = new ContactGroupDataIce[this.groupList.size()];
            for (int i = 0; i < this.groupList.size(); ++i) {
                contactGroupDataIceArray[i] = this.groupList.get(i).toIceObject();
            }
        }
        return contactGroupDataIceArray;
    }

    public void assignPresence(Map<String, UserPrx> contactUserProxies) {
        if (contactUserProxies == null) {
            return;
        }
        for (ContactData contact : this.contactList) {
            try {
                UserPrx contactUserPrx = contactUserProxies.get(contact.fusionUsername);
                if (contactUserPrx == null) continue;
                contact.fusionPresence = PresenceType.fromValue(contactUserPrx.getOverallFusionPresence(this.username));
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("User " + this.username + ": Contact=" + contact.fusionUsername + " has presence=" + contact.fusionPresence));
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) continue;
                log.debug((Object)("ChatContacts.assignPresence: unable to determine presence for " + contact.fusionUsername + " due to " + e));
            }
        }
    }

    public boolean getIsContactWithPresence(ImType imTypeEnum, ContactData contact, PresenceType presence) {
        switch (imTypeEnum) {
            case FUSION: {
                return true;
            }
            case MSN: {
                if (!this.showOfflineMSNContacts || !contact.isMSNOnly() || contact.msnPresence != null && contact.msnPresence != PresenceType.OFFLINE || presence == PresenceType.OFFLINE) break;
                return true;
            }
            case AIM: {
                if (this.showOfflineAIMContacts || !contact.isAIMOnly() || contact.aimPresence != null && contact.aimPresence != PresenceType.OFFLINE || presence == PresenceType.OFFLINE) break;
                return true;
            }
            case YAHOO: {
                if (this.showOfflineYahooContacts || !contact.isYahooOnly() || contact.yahooPresence != null && contact.yahooPresence != PresenceType.OFFLINE || presence == PresenceType.OFFLINE) break;
                return true;
            }
            case GTALK: {
                if (this.showOfflineGTalkContacts || !contact.isGTalkOnly() || contact.gtalkPresence != null && contact.gtalkPresence != PresenceType.OFFLINE || presence == PresenceType.OFFLINE) break;
                return true;
            }
            case FACEBOOK: {
                if (this.showOfflineFacebookContacts || !contact.isFacebookOnly() || contact.facebookPresence != null && contact.facebookPresence != PresenceType.OFFLINE || presence == PresenceType.OFFLINE) break;
                return true;
            }
            default: {
                log.warn((Object)("invalid IM type '" + imTypeEnum + "'"));
                return false;
            }
        }
        return false;
    }

    public boolean getIsContactShown(ContactData contact, ImType imType) {
        return imType == ImType.MSN && this.showOfflineMSNContacts && contact.isMSNOnly() || imType == ImType.YAHOO && this.showOfflineYahooContacts && contact.isYahooOnly() || imType == ImType.AIM && this.showOfflineAIMContacts && contact.isAIMOnly() || imType == ImType.GTALK && this.showOfflineGTalkContacts && contact.isGTalkOnly() || imType == ImType.FACEBOOK && this.showOfflineFacebookContacts && contact.isFacebookOnly();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isOnContactList(String contactUsername) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!contactUsername.equalsIgnoreCase(contact.fusionUsername)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isOnBlockList(String contactUsername) {
        return this.userData.isOnBlockList(contactUsername);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer blockUser(String blockUsername, int contactListVersion) {
        Integer contactID = this.findSourceContactID(MessageType.FUSION, blockUsername);
        this.removeContact(blockUsername);
        this.userData.blockUser(blockUsername);
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            if (contactID != null) {
                this.contactListVersion = contactListVersion;
            }
        }
        return contactID;
    }

    public boolean unblockUser(String username) {
        return this.userData.unblockUser(username);
    }

    public UserErrorResponse userCanContactMe(String username, MessageDataIce message, boolean silencedNotifications) {
        if (this.userData.isOnBlockList(username)) {
            return USER_ERROR_RESPONSE_FAIL_NOT_ONLINE;
        }
        if (silencedNotifications && !this.isOnContactList(username)) {
            if (message == null) {
                return USER_ERROR_RESPONSE_FAIL_NOT_ACCEPTING_NOTIFICATIONS;
            }
            if (!MessageData.isMessageToAChatRoom(message)) {
                return USER_ERROR_RESPONSE_FAIL_NOT_ACCEPTING_NOTIFICATIONS;
            }
        }
        if (this.userData.getMessageSetting() == UserSettingData.MessageEnum.FRIENDS_ONLY && message != null && !this.userData.isOnBroadcastList(message.source) && MessageData.isMessageToAnIndividual(message)) {
            return USER_ERROR_RESPONSE_FAIL_NOT_ACCEPTING_NOTIFICATIONS;
        }
        return USER_ERROR_RESPONSE_OK;
    }

    public ContactDataIce assignPresence(ContactDataIce contactIce) {
        ContactData contact = this.getContact(contactIce.id);
        if (contact == null) {
            log.warn((Object)("contact id " + contactIce.id + " not found in contact list for user [" + this.username + "]"));
            return contactIce;
        }
        if (contact.fusionPresence != null) {
            contactIce.fusionPresence = contact.fusionPresence.value();
        }
        if (contact.msnPresence != null) {
            contactIce.msnPresence = contact.msnPresence.value();
        }
        if (contact.aimPresence != null) {
            contactIce.aimPresence = contact.aimPresence.value();
        }
        if (contact.yahooPresence != null) {
            contactIce.yahooPresence = contact.yahooPresence.value();
        }
        if (contact.gtalkPresence != null) {
            contactIce.gtalkPresence = contact.gtalkPresence.value();
        }
        if (contact.facebookPresence != null) {
            contactIce.facebookPresence = contact.facebookPresence.value();
        }
        return contactIce;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean assignPresence(PresenceAndCapabilityIce presence, int contactId) {
        boolean found = false;
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (contact.id != contactId) continue;
                if (contact.fusionPresence != null) {
                    presence.fusionPresence = contact.fusionPresence.value();
                }
                if (contact.msnPresence != null) {
                    presence.msnPresence = contact.msnPresence.value();
                }
                if (contact.aimPresence != null) {
                    presence.aimPresence = contact.aimPresence.value();
                }
                if (contact.yahooPresence != null) {
                    presence.yahooPresence = contact.yahooPresence.value();
                }
                if (contact.gtalkPresence != null) {
                    presence.gtalkPresence = contact.gtalkPresence.value();
                }
                if (contact.facebookPresence != null) {
                    presence.facebookPresence = contact.facebookPresence.value();
                }
                found = true;
                break;
            }
        }
        return found;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer changedDisplayPicture(String source, String displayPicture, long timeStamp) {
        Integer contactId = null;
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!source.equals(contact.fusionUsername)) continue;
                contactId = contact.id;
                contact.displayPicture = displayPicture;
                contact.statusTimeStamp = new Date(timeStamp);
                break;
            }
        }
        return contactId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Integer changedStatusMessage(String source, String statusMessage, long timeStamp) {
        Integer contactId = null;
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData contact : this.contactList) {
                if (!source.equals(contact.fusionUsername)) continue;
                contactId = contact.id;
                contact.statusMessage = statusMessage;
                contact.statusTimeStamp = new Date(timeStamp);
                break;
            }
        }
        return contactId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ContactList getContactList() {
        ContactList contactList = new ContactList();
        contactList.contacts = this.getContacts();
        contactList.contactGroups = this.getGroups();
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            contactList.version = this.contactListVersion;
        }
        return contactList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getOutstandingContactRequests(Set<String> pendingContacts) {
        Set<String> set = this.contactRequestSentList;
        synchronized (set) {
            pendingContacts.removeAll(this.contactRequestSentList);
        }
        return pendingContacts;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateOutstandingContactRequests(Set<String> users) {
        Set<String> set = this.contactRequestSentList;
        synchronized (set) {
            for (String username : users) {
                this.contactRequestSentList.add(username);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void changedDetail(ContactData newContact, int contactListVersion) {
        Set<ContactData> set = this.contactList;
        synchronized (set) {
            for (ContactData existingContact : this.contactList) {
                if (!existingContact.id.equals(newContact.id)) continue;
                newContact.msnPresence = existingContact.msnPresence;
                newContact.yahooPresence = existingContact.yahooPresence;
                newContact.aimPresence = existingContact.aimPresence;
                newContact.gtalkPresence = existingContact.gtalkPresence;
                newContact.facebookPresence = existingContact.facebookPresence;
                this.contactList.remove(existingContact);
                break;
            }
            this.contactList.add(newContact);
            this.contactListVersion = contactListVersion;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeGroup(int contactGroupID) {
        List<ContactGroupData> list = this.groupList;
        synchronized (list) {
            for (ContactGroupData contactGroup : this.groupList) {
                if (contactGroup.id != contactGroupID) continue;
                this.groupList.remove(contactGroup);
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void updateGroup(ContactGroupData newContactGroup) {
        List<ContactGroupData> list = this.groupList;
        synchronized (list) {
            for (ContactGroupData existingGroup : this.groupList) {
                if (!existingGroup.id.equals(newContactGroup.id)) continue;
                this.groupList.remove(existingGroup);
                break;
            }
            this.groupList.add(newContactGroup);
        }
    }

    public PresenceType findOverallFusionPresenceForContact(ChatObjectManagerUser objectManager, ContactData contact, String username) {
        try {
            UserPrx contactUserPrx = objectManager.findUserPrxFromRegistry(contact.fusionUsername);
            return PresenceType.fromValue(contactUserPrx.getOverallFusionPresence(username));
        }
        catch (ObjectNotFoundException e) {
            return PresenceType.OFFLINE;
        }
        catch (Exception e) {
            if (contact.fusionPresence == null) {
                return PresenceType.OFFLINE;
            }
            return contact.fusionPresence;
        }
    }

    public void addToCurrentChatroomList(String chatroom) {
        this.currentChatrooms.add(chatroom);
    }

    public void removeFromCurrentChatroomList(String chatroom) {
        this.currentChatrooms.remove(chatroom);
    }

    public String[] getCurrentChatrooms() {
        return this.currentChatrooms.toArray(new String[this.currentChatrooms.size()]);
    }

    public void copyPresenceForExistingIMContacts(ContactData oldContactData, ContactData newContactData) {
        newContactData.aimPresence = newContactData.aimUsername != null && newContactData.aimUsername.equals(oldContactData.aimUsername) ? oldContactData.aimPresence : PresenceType.OFFLINE;
        newContactData.gtalkPresence = newContactData.gtalkUsername != null && newContactData.gtalkUsername.equals(oldContactData.gtalkUsername) ? oldContactData.gtalkPresence : PresenceType.OFFLINE;
        newContactData.yahooPresence = newContactData.yahooUsername != null && newContactData.yahooUsername.equals(oldContactData.yahooUsername) ? oldContactData.yahooPresence : PresenceType.OFFLINE;
        newContactData.msnPresence = newContactData.msnUsername != null && newContactData.msnUsername.equals(oldContactData.msnUsername) ? oldContactData.msnPresence : PresenceType.OFFLINE;
        newContactData.facebookPresence = newContactData.facebookUsername != null && newContactData.facebookUsername.equals(oldContactData.facebookUsername) ? oldContactData.facebookPresence : PresenceType.OFFLINE;
    }
}

