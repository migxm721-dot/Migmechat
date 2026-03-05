/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.LocalException
 *  com.danga.MemCached.MemCachedClient
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  javax.transaction.UserTransaction
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.ejb;

import Ice.LocalException;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.cache.BroadcastList;
import com.projectgoth.fusion.cache.BroadcastListPersisted;
import com.projectgoth.fusion.cache.ContactList;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.MemCachedClientWrapper;
import com.projectgoth.fusion.common.MemCachedKeySpaces;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.data.DisplayPictureAndStatusMessage;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.ejb.ConnectionHolder;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.ejb.util.LookupUtil;
import com.projectgoth.fusion.eventqueue.EventQueue;
import com.projectgoth.fusion.eventqueue.events.FriendAddedEvent;
import com.projectgoth.fusion.fdl.enums.ImType;
import com.projectgoth.fusion.fdl.enums.PresenceType;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import com.projectgoth.fusion.slice.ContactDataIce;
import com.projectgoth.fusion.slice.EventSystemPrx;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.PresenceAndCapabilityIce;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import com.projectgoth.fusion.slice.UserPrx;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ContactBean
implements SessionBean {
    public static final String CONTACT_LIST_NAMESPACE = "CL";
    public static final String PENDING_CONTACT_MESSAGE = "<pending>";
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ContactBean.class));
    private static final Logger contactDeletedLog = Logger.getLogger((String)"ContactDeletedLog");
    private static MemCachedClient contactListMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.contactList);
    private static MemCachedClient broadcastListMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.broadcastList);
    private static MemCachedClient bclPersistedMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.bclPersisted);
    private static MemCachedClient displayPictureAndStatusMessageMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.userDisplayPictureAndStatus);
    private DataSource dataSourceMaster;
    private DataSource dataSourceSlave;
    private SessionContext context;
    private static boolean checkAndPopulateBCL = true;

    public void setSessionContext(SessionContext newContext) throws EJBException {
        this.context = newContext;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws CreateException {
        try {
            this.dataSourceMaster = LookupUtil.getFusionMasterDataSource();
            this.dataSourceSlave = LookupUtil.getFusionSlaveDataSource();
            SystemProperty.ejbInit(this.dataSourceSlave);
        }
        catch (Exception e) {
            log.error((Object)"Unable to create Contact EJB", (Throwable)e);
            throw new CreateException("Unable to create Contact EJB: " + e.getMessage());
        }
    }

    private void onContactAccepted(String username, String contactUsername) {
        if (SystemProperty.getBool("AddFriendEventEnabled", true)) {
            try {
                if (SystemProperty.getBool(SystemPropertyEntities.Default.EVENT_SYSTEM_ENABLED)) {
                    EventSystemPrx eventSystem = EJBIcePrxFinder.getEventSystemProxy();
                    eventSystem.addedFriend(username, contactUsername);
                    eventSystem.addedFriend(contactUsername, username);
                }
                EventQueue.enqueueSingleEvent(new FriendAddedEvent(username, contactUsername));
            }
            catch (Exception e) {
                log.error((Object)("failed to log add friend event for user [" + username + "]"), (Throwable)e);
            }
        }
    }

    /*
     * Loose catch block
     */
    public ContactData getContact(int contactID) throws EJBException {
        block31: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block25: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from contact where id = ?");
                ps.setInt(1, contactID);
                rs = ps.executeQuery();
                if (!rs.next()) break block25;
                ContactData contactData = new ContactData(rs);
                this.assignDisplayPictureAndStatusMessageToContacts(conn, Collections.nCopies(1, contactData));
                ContactData contactData2 = contactData;
                Object var8_8 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                return contactData2;
            }
            try {
                Object var8_9 = null;
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block31;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block31;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        return null;
    }

    /*
     * Loose catch block
     */
    public ContactData getContact(String username, String contactUsername) throws EJBException {
        block31: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block25: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from contact where username = ? and fusionUsername = ?");
                ps.setString(1, username);
                ps.setString(2, contactUsername);
                rs = ps.executeQuery();
                if (!rs.next()) break block25;
                ContactData contactData = new ContactData(rs);
                this.assignDisplayPictureAndStatusMessageToContacts(conn, Collections.nCopies(1, contactData));
                ContactData contactData2 = contactData;
                Object var9_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                return contactData2;
            }
            try {
                Object var9_10 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block31;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block31;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        return new ContactData();
    }

    /*
     * Loose catch block
     */
    public boolean isFriend(String username, String contactUsername) throws FusionEJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select 1 from broadcastlist where username = ? and broadcastUsername = ?");
        ps.setString(1, username);
        ps.setString(2, contactUsername);
        rs = ps.executeQuery();
        boolean bl = rs.next();
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.error((Object)String.format("unable to check where '%s' is a friend of '%s': %s", contactUsername, username, e.getMessage()));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean isBlocking(String username, String blockedUsername) throws FusionEJBException {
        boolean bl;
        Connection conn = null;
        try {
            try {
                UserPrx userPrx = null;
                try {
                    userPrx = EJBIcePrxFinder.findUserPrx(username);
                }
                catch (Exception e) {
                    log.warn((Object)String.format("Exception caught while trying to find user proxy of [%s] when checking whether [%s] is on blocklist, falling back to slave database", username, blockedUsername));
                }
                conn = this.dataSourceSlave.getConnection();
                bl = this.isOnBlockList(username, blockedUsername, conn, userPrx, true);
                Object var7_8 = null;
            }
            catch (Exception e) {
                log.error((Object)String.format("Unable to check whether '%s' is blocking  '%s': %s", username, blockedUsername, e.getMessage()), (Throwable)e);
                throw new FusionEJBException(e.getMessage());
            }
        }
        catch (Throwable throwable) {
            Object var7_9 = null;
            try {
                if (conn == null) throw throwable;
                conn.close();
                throw throwable;
            }
            catch (SQLException e2) {
                conn = null;
                throw throwable;
            }
        }
        try {}
        catch (SQLException e2) {
            return bl;
        }
        if (conn == null) return bl;
        conn.close();
        return bl;
    }

    /*
     * Loose catch block
     */
    public boolean isFriend(int userid, int contactUserid) throws FusionEJBException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        conn = this.dataSourceSlave.getConnection();
        ps = conn.prepareStatement("select 1 from broadcastlist bcl, userid uid1, userid uid2 where bcl.username = uid1.username and bcl.broadcastUsername = uid2.username and uid1.id = ? and uid2.id = ?");
        ps.setInt(1, userid);
        ps.setInt(2, contactUserid);
        rs = ps.executeQuery();
        boolean bl = rs.next();
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return bl;
        catch (SQLException e) {
            try {
                log.error((Object)String.format("unable to check where '%d' is a friend of '%d': %s", contactUserid, userid, e.getMessage()));
                throw new FusionEJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public ContactGroupData getGroup(int groupID) throws EJBException {
        ContactGroupData contactGroup;
        block22: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block19: {
                contactGroup = new ContactGroupData();
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from contactgroup where id = ?");
                ps.setInt(1, groupID);
                rs = ps.executeQuery();
                if (!rs.next()) break block19;
                contactGroup = new ContactGroupData(rs);
            }
            Object var8_6 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block22;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block22;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var8_7 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return contactGroup;
    }

    private String getDefaultDisplayName(Connection conn, ContactData contactData) throws EJBException {
        String displayName = null;
        if (contactData.firstName != null) {
            displayName = contactData.firstName;
            if (contactData.lastName != null) {
                displayName = displayName + " " + contactData.lastName;
            }
        } else if (contactData.lastName != null) {
            displayName = contactData.lastName;
        } else if (contactData.fusionUsername != null) {
            displayName = contactData.fusionUsername;
        } else if (contactData.msnUsername != null) {
            displayName = contactData.msnUsername;
        } else if (contactData.aimUsername != null) {
            displayName = contactData.aimUsername;
        } else if (contactData.yahooUsername != null) {
            displayName = contactData.yahooUsername;
        } else if (contactData.gtalkUsername != null) {
            displayName = contactData.gtalkUsername;
        } else if (contactData.facebookUsername != null) {
            displayName = contactData.facebookUsername;
        } else if (contactData.emailAddress != null) {
            displayName = contactData.emailAddress;
        } else if (contactData.mobilePhone != null) {
            displayName = contactData.mobilePhone;
        } else if (contactData.homePhone != null) {
            displayName = contactData.homePhone;
        } else if (contactData.officePhone != null) {
            displayName = contactData.officePhone;
        }
        return displayName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void assignDisplayPictureAndStatusMessageToContacts(Connection conn, Collection<ContactData> contactList) throws SQLException {
        HashMap<String, ContactData> contactMap = new HashMap<String, ContactData>();
        for (ContactData contact : contactList) {
            if (contact.fusionUsername == null) continue;
            DisplayPictureAndStatusMessage avatar = DisplayPictureAndStatusMessage.getDisplayPictureAndStatusMessage(displayPictureAndStatusMessageMemcache, contact.fusionUsername);
            if (avatar == null) {
                contactMap.put(contact.fusionUsername, contact);
                continue;
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("AVATAR for " + contact.fusionUsername + " from Memcached"));
            }
            contact.displayPicture = avatar.getDisplayPicture();
            contact.statusMessage = StringUtil.stripHTML(avatar.getStatusMessage());
            contact.statusTimeStamp = avatar.getStatusTimestamp();
        }
        if (contactMap.size() == 0) {
            return;
        }
        Statement ps = null;
        ResultSet rs = null;
        boolean needToCloseConn = false;
        try {
            String parameters = "?" + StringUtil.repeat(",?", contactMap.size() - 1);
            if (conn == null) {
                conn = this.dataSourceSlave.getConnection();
                needToCloseConn = true;
            }
            ps = conn.prepareStatement("select username, displayPicture, statusMessage, statusTimeStamp, dateRegistered from user where username in (" + parameters + ")");
            int i = 0;
            for (String key : contactMap.keySet()) {
                ps.setString(++i, key);
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                ContactData contact = (ContactData)contactMap.get(rs.getString("username"));
                if (contact == null) continue;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("AVATAR for " + contact.fusionUsername + " from DB"));
                }
                contact.displayPicture = rs.getString("displayPicture");
                contact.statusMessage = StringUtil.stripHTML(rs.getString("statusMessage"));
                try {
                    contact.statusTimeStamp = rs.getTimestamp("statusTimeStamp");
                }
                catch (Exception e) {
                    contact.statusTimeStamp = rs.getTimestamp("dateRegistered");
                }
                DisplayPictureAndStatusMessage avatar = new DisplayPictureAndStatusMessage();
                avatar.setDisplayPicture(contact.displayPicture);
                avatar.setStatusMessage(contact.statusMessage);
                avatar.setStatusTimestamp(contact.statusTimeStamp);
                DisplayPictureAndStatusMessage.setDisplayPictureAndStatusMessage(displayPictureAndStatusMessageMemcache, contact.fusionUsername, avatar);
            }
            Object var12_13 = null;
        }
        catch (Throwable throwable) {
            Object var12_14 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (needToCloseConn && conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (needToCloseConn && conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int updateContactListVersion(int userID, Connection conn) throws SQLException {
        int n;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            int version = this.getContactListVersion(userID, conn);
            if (version == 0) {
                ps = conn.prepareStatement("insert into contactlistversion (userid, version) values (?, 1)");
                ps.setInt(1, userID);
            } else {
                ps = conn.prepareStatement("update contactlistversion set version = ? where userid = ? and version = ?");
                ps.setInt(1, version + 1);
                ps.setInt(2, userID);
                ps.setInt(3, version);
            }
            MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CONTACT_LIST_VERSION, String.valueOf(userID));
            if (ps.executeUpdate() != 1) {
                throw new SQLException("Unable to update contact list version");
            }
            n = version + 1;
            Object var8_7 = null;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkContactGroupOwnership(String username, int contactGroupId, Connection conn) throws Exception {
        if (contactGroupId == -1 || contactGroupId == -2 || contactGroupId == -3 || contactGroupId == -4 || contactGroupId == -5 || contactGroupId == -6) {
            return;
        }
        PreparedStatement ps = conn.prepareStatement("select id from contactgroup where username=? and id=?");
        ResultSet rs = null;
        try {
            ps.setString(1, username);
            ps.setInt(2, contactGroupId);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception("An invalid group was specified. If this problem persists, please log out and log back in again");
            }
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isOnDBStringList(String username, String qualifyingUsername, String tableName, String fieldName, Connection conn) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        block17: {
            boolean bl;
            ps = conn.prepareStatement("select * from " + tableName + " where username = ? and " + fieldName + " = ?");
            rs = null;
            try {
                ps.setString(1, username);
                ps.setString(2, qualifyingUsername);
                rs = ps.executeQuery();
                if (!rs.next()) break block17;
                bl = true;
                Object var10_10 = null;
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var10_11 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        return bl;
    }

    private boolean isOnBlockList(String username, String contactUsername, Connection conn, UserPrx userPrx, boolean fallbackToDB) throws Exception {
        if (userPrx != null) {
            return userPrx.isOnBlockList(contactUsername);
        }
        if (fallbackToDB) {
            return this.isOnDBStringList(username, contactUsername, "blocklist", "blockusername", conn);
        }
        return false;
    }

    private boolean isOnContactList(String username, String contactUsername, Connection conn, UserPrx userPrx, boolean fallbackToDB) throws Exception {
        if (userPrx != null) {
            return userPrx.isOnContactList(contactUsername);
        }
        if (fallbackToDB) {
            return this.isOnDBStringList(username, contactUsername, "contact", "fusionusername", conn);
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isOnContactListForIdOtherThan(String username, String contactUsername, int id, Connection conn) throws SQLException {
        boolean bl;
        block5: {
            PreparedStatement ps;
            block3: {
                boolean bl2;
                block4: {
                    ps = conn.prepareStatement("select id from contact where id != ? and username = ? and fusionusername = ?");
                    try {
                        ps.setInt(1, id);
                        ps.setString(2, username);
                        ps.setString(3, contactUsername);
                        ResultSet rs = ps.executeQuery();
                        if (!rs.next()) break block3;
                        bl2 = true;
                        Object var9_9 = null;
                        if (ps == null) break block4;
                    }
                    catch (Throwable throwable) {
                        block6: {
                            Object var9_11 = null;
                            if (ps == null) break block6;
                            ps.close();
                        }
                        throw throwable;
                    }
                    ps.close();
                }
                return bl2;
            }
            bl = false;
            Object var9_10 = null;
            if (ps == null) break block5;
            ps.close();
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String hasMobilePhoneNumber(String username, Connection conn) throws Exception {
        String string;
        if (username == null) {
            return null;
        }
        PreparedStatement ps = conn.prepareStatement("select mobilephone from user where username = ?");
        ResultSet rs = null;
        try {
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new Exception(username + " is not a valid user");
            }
            string = rs.getString("mobilephone");
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        return string;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void persistContact(ContactData contactData, Connection conn) throws Exception {
        String statement = "insert into contact (username,displayname,firstname,lastname,fusionusername,msnusername,aimusername,yahoousername,icqusername,jabberusername,emailaddress,mobilephone,homephone,officephone,defaultim,defaultphonenumber,contactgroupid,sharemobilephone,displayonphone,status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(statement, 1);
        ResultSet rs = null;
        try {
            ps.setString(1, contactData.username);
            ps.setString(2, contactData.displayName);
            ps.setString(3, contactData.firstName);
            ps.setString(4, contactData.lastName);
            ps.setString(5, contactData.fusionUsername);
            ps.setString(6, contactData.msnUsername);
            ps.setString(7, contactData.aimUsername);
            ps.setString(8, contactData.yahooUsername);
            ps.setString(9, contactData.facebookUsername);
            ps.setString(10, contactData.gtalkUsername);
            ps.setString(11, contactData.emailAddress);
            ps.setString(12, contactData.mobilePhone);
            ps.setString(13, contactData.homePhone);
            ps.setString(14, contactData.officePhone);
            ps.setObject(15, contactData.defaultIM == null ? null : Byte.valueOf(contactData.defaultIM.value()));
            ps.setObject(16, contactData.defaultPhoneNumber == null ? null : Byte.valueOf(contactData.defaultPhoneNumber.value()));
            ps.setObject(17, contactData.contactGroupId == null || contactData.contactGroupId == -1 || contactData.contactGroupId == -2 || contactData.contactGroupId == -3 || contactData.contactGroupId == -4 || contactData.contactGroupId == -5 || contactData.contactGroupId == -6 ? null : contactData.contactGroupId);
            ps.setObject(18, contactData.shareMobilePhone == null ? null : Integer.valueOf(contactData.shareMobilePhone != false ? 1 : 0));
            ps.setObject(19, contactData.displayOnPhone == null ? null : Integer.valueOf(contactData.displayOnPhone != false ? 1 : 0));
            ps.setObject(20, contactData.status == null ? null : Integer.valueOf(contactData.status.value()));
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (!rs.next()) {
                throw new Exception("Failed to add a new contact to database");
            }
            contactData.id = rs.getInt(1);
            Object var7_6 = null;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean persistPendingContact(String contactUsername, String username, Connection conn) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        block17: {
            boolean bl;
            ps = null;
            rs = null;
            try {
                ps = conn.prepareStatement("select * from pendingcontact where username = ? and pendingContact = ?");
                ps.setString(1, contactUsername);
                ps.setString(2, username);
                rs = ps.executeQuery();
                if (rs.next()) break block17;
                rs.close();
                ps.close();
                ps = conn.prepareStatement("select * from blocklist where username = ? and blockusername = ?");
                ps.setString(1, contactUsername);
                ps.setString(2, username);
                rs = ps.executeQuery();
                if (rs.next()) break block17;
                ps.close();
                ps = conn.prepareStatement("insert into pendingcontact (username, pendingContact) values (?,?)");
                ps.setString(1, contactUsername);
                ps.setString(2, username);
                int result = ps.executeUpdate();
                bl = result == 1;
                Object var9_9 = null;
            }
            catch (Throwable throwable) {
                Object var9_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            return bl;
        }
        boolean bl = false;
        Object var9_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean contactSharesMobilePhone(String username, String contactUsername) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        Connection conn;
        block24: {
            boolean bl;
            conn = null;
            ps = null;
            rs = null;
            try {
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select sharemobilephone from contact where username = ? and fusionusername = ?");
                ps.setString(1, username);
                ps.setString(2, contactUsername);
                rs = ps.executeQuery();
                if (!rs.next() || rs.getInt("sharemobilephone") != 1) break block24;
                bl = true;
                Object var8_7 = null;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                conn = null;
            }
            return bl;
        }
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            conn = null;
        }
        return false;
    }

    private void unmaskFusionContact(String username, String mobilephone, String fusionusername, Connection conn) throws SQLException {
        log.debug((Object)("Executing: update contact set displayname = " + fusionusername + ", fusionusername = " + fusionusername + " where username = " + username + " and mobilephone = " + mobilephone));
        PreparedStatement psUpdateRow = conn.prepareStatement("update contact set displayname = ?, fusionusername = ? where username = ? and mobilephone = ?");
        psUpdateRow.setString(1, fusionusername);
        psUpdateRow.setString(2, fusionusername);
        psUpdateRow.setString(3, username);
        psUpdateRow.setString(4, mobilephone);
        psUpdateRow.executeUpdate();
        psUpdateRow.close();
    }

    /*
     * Loose catch block
     */
    private ContactData getMaskedFusionContact(String username, String maskedusername, String mobilephone) throws SQLException {
        block31: {
            ResultSet rs;
            PreparedStatement ps;
            Connection conn;
            block25: {
                conn = null;
                ps = null;
                rs = null;
                conn = this.dataSourceSlave.getConnection();
                ps = conn.prepareStatement("select * from contact where username = ? and mobilephone = ? and fusionusername is null");
                ps.setString(1, username);
                ps.setString(2, mobilephone);
                rs = ps.executeQuery();
                if (!rs.next()) break block25;
                ContactData contactData = new ContactData(rs);
                this.assignDisplayPictureAndStatusMessageToContacts(conn, Collections.nCopies(1, contactData));
                contactData.fusionUsername = maskedusername;
                contactData.displayName = maskedusername;
                contactData.mobilePhone = null;
                log.debug((Object)("contactdata.fusionusername:" + contactData.fusionUsername + " and ID: " + contactData.id));
                ContactData contactData2 = contactData;
                Object var10_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                return contactData2;
            }
            try {
                Object var10_11 = null;
            }
            catch (Throwable throwable) {
                Object var10_12 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block31;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block31;
            {
                catch (SQLException e) {
                    throw new EJBException(e.getMessage());
                }
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String updateContactsMobilePhone(String username, String contactUsername, Connection masterConn) throws SQLException {
        SQLException e22;
        Statement psUpdateRow;
        Statement psGetIDs;
        Connection slaveConn;
        String mobilePhone;
        ResultSet rsGetIDs;
        PreparedStatement psGetMobilePhone;
        block35: {
            if (log.isDebugEnabled()) {
                log.debug((Object)("sharing contact [" + username + "] mobilephone with user [" + contactUsername + "]"));
            }
            psGetMobilePhone = null;
            ResultSet rsGetMobilePhone = null;
            rsGetIDs = null;
            mobilePhone = null;
            slaveConn = null;
            psGetIDs = null;
            psUpdateRow = null;
            try {
                slaveConn = this.dataSourceSlave.getConnection();
                psGetMobilePhone = slaveConn.prepareStatement("select mobilephone from user where username = ?");
                psGetMobilePhone.setString(1, username);
                rsGetMobilePhone = psGetMobilePhone.executeQuery();
                if (rsGetMobilePhone.next()) {
                    mobilePhone = rsGetMobilePhone.getString(1);
                }
                if (mobilePhone != null) {
                    psGetIDs = slaveConn.prepareStatement("select id from contact where mobilephone is null and username = ? and fusionusername = ?");
                    psGetIDs.setString(1, contactUsername);
                    psGetIDs.setString(2, username);
                    rsGetIDs = psGetIDs.executeQuery();
                    psUpdateRow = masterConn.prepareStatement("update contact set mobilephone = ? where id = ?");
                    while (rsGetIDs.next()) {
                        psUpdateRow.setString(1, mobilePhone);
                        psUpdateRow.setInt(2, rsGetIDs.getInt(1));
                        if (psUpdateRow.executeUpdate() >= 1) continue;
                        log.warn((Object)("unable to share mobile phone number [" + mobilePhone + "] belonging to contact [" + username + "] with user [" + contactUsername + "]"));
                    }
                }
                Object var12_11 = null;
                if (rsGetMobilePhone == null) break block35;
            }
            catch (Throwable throwable) {
                SQLException e22;
                Object var12_12 = null;
                if (rsGetMobilePhone != null) {
                    try {
                        rsGetMobilePhone.close();
                    }
                    catch (SQLException e22) {
                        rsGetMobilePhone = null;
                    }
                }
                if (rsGetIDs != null) {
                    try {
                        rsGetIDs.close();
                    }
                    catch (SQLException e22) {
                        rsGetIDs = null;
                    }
                }
                if (psGetMobilePhone != null) {
                    try {
                        psGetMobilePhone.close();
                    }
                    catch (SQLException e22) {
                        psGetMobilePhone = null;
                    }
                }
                if (psUpdateRow != null) {
                    try {
                        psUpdateRow.close();
                    }
                    catch (SQLException e22) {
                        psUpdateRow = null;
                    }
                }
                if (psGetIDs != null) {
                    try {
                        psGetIDs.close();
                    }
                    catch (SQLException e22) {
                        psGetIDs = null;
                    }
                }
                if (slaveConn != null) {
                    try {
                        slaveConn.close();
                    }
                    catch (SQLException e22) {
                        slaveConn = null;
                    }
                }
                throw throwable;
            }
            try {
                rsGetMobilePhone.close();
            }
            catch (SQLException e22) {
                rsGetMobilePhone = null;
            }
        }
        if (rsGetIDs != null) {
            try {
                rsGetIDs.close();
            }
            catch (SQLException e22) {
                rsGetIDs = null;
            }
        }
        if (psGetMobilePhone != null) {
            try {
                psGetMobilePhone.close();
            }
            catch (SQLException e22) {
                psGetMobilePhone = null;
            }
        }
        if (psUpdateRow != null) {
            try {
                psUpdateRow.close();
            }
            catch (SQLException e22) {
                psUpdateRow = null;
            }
        }
        if (psGetIDs != null) {
            try {
                psGetIDs.close();
            }
            catch (SQLException e22) {
                psGetIDs = null;
            }
        }
        if (slaveConn != null) {
            try {
                slaveConn.close();
            }
            catch (SQLException e22) {
                slaveConn = null;
            }
        }
        return mobilePhone;
    }

    private void setContactOffline(ContactData contactData) {
        contactData.fusionPresence = PresenceType.OFFLINE;
        contactData.aimPresence = PresenceType.OFFLINE;
        contactData.gtalkPresence = PresenceType.OFFLINE;
        contactData.msnPresence = PresenceType.OFFLINE;
        contactData.yahooPresence = PresenceType.OFFLINE;
        contactData.facebookPresence = PresenceType.OFFLINE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean hasTooManyPendingContacts(String username, Connection conn) throws SQLException, NoSuchFieldException {
        ConnectionHolder ch;
        block3: {
            boolean bl;
            ch = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ch = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = ch.getConnection().prepareStatement("select count(*) from pendingcontact where username=?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) < SystemProperty.getInt("MaxPendingContacts")) break block3;
                bl = true;
                Object var8_7 = null;
                if (ch == null) return bl;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                if (ch == null) throw throwable;
                ch.close();
                throw throwable;
            }
            ch.close();
            return bl;
        }
        Object var8_8 = null;
        if (ch == null) return false;
        ch.close();
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean hasTooManyFusionContacts(String username, Connection conn) throws SQLException, NoSuchFieldException {
        ConnectionHolder ch;
        block3: {
            boolean bl;
            ch = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ch = new ConnectionHolder(this.dataSourceSlave, conn);
                ps = ch.getConnection().prepareStatement("select count(*) from contact where username=?");
                ps.setString(1, username);
                rs = ps.executeQuery();
                if (!rs.next() || rs.getInt(1) < SystemProperty.getInt("MaxFusionContacts")) break block3;
                bl = true;
                Object var8_7 = null;
                if (ch == null) return bl;
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                if (ch == null) throw throwable;
                ch.close();
                throw throwable;
            }
            ch.close();
            return bl;
        }
        Object var8_8 = null;
        if (ch == null) return false;
        ch.close();
        return false;
    }

    /*
     * Loose catch block
     */
    public ContactData addFusionUserAsContact(int userID, ContactData contactData, boolean followContactOnMiniblog) throws EJBException, FusionEJBException {
        block55: {
            UserTransaction userTransaction;
            Connection connSlave;
            Connection connMaster;
            block53: {
                connMaster = null;
                connSlave = null;
                userTransaction = null;
                if (userID < 1) {
                    throw new FusionEJBException("Invalid userID provided");
                }
                if (contactData == null) {
                    throw new FusionEJBException("Invalid contact data provided");
                }
                if (StringUtil.isBlank(contactData.username)) {
                    throw new FusionEJBException("Please provide a valid username.");
                }
                if (this.hasTooManyFusionContacts(contactData.username, null)) {
                    throw new FusionEJBException("You cannot add any more migme contacts. Please remove some first");
                }
                if (StringUtil.isBlank(contactData.fusionUsername)) {
                    throw new FusionEJBException("Please provide a valid username to add.");
                }
                if (contactData.username.equalsIgnoreCase(contactData.fusionUsername)) {
                    throw new FusionEJBException("You cannot add yourself to your contact list.");
                }
                log.info((Object)String.format("addFusionUserAsContact  %s add %s as contact. userid %d followContactOnMiniblog %s", contactData.username, contactData.fusionUsername, userID, followContactOnMiniblog));
                connSlave = this.dataSourceSlave.getConnection();
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                int toUserId = userEJB.getUserID(contactData.fusionUsername, connSlave, false);
                if (toUserId < 0) {
                    throw new FusionEJBException(String.format("Unable to find user '%s'", contactData.fusionUsername));
                }
                if (!AuthenticatedAccessControl.hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum.ADD_FRIEND, contactData.username)) {
                    throw new FusionEJBException("You must be authenticated before you can add new contacts.");
                }
                if (!AuthenticatedAccessControl.hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum.BE_ADDED_AS_FRIEND, contactData.fusionUsername)) {
                    throw new FusionEJBException("You can only add authenticated accounts to your contact list.");
                }
                if (contactData.displayName == null) {
                    contactData.displayName = this.getDefaultDisplayName(connMaster, contactData);
                }
                if (contactData.displayOnPhone == null) {
                    contactData.displayOnPhone = true;
                }
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(contactData.username);
                UserPrx contactPrx = EJBIcePrxFinder.findUserPrx(contactData.fusionUsername);
                if (this.isOnBlockList(contactData.username, contactData.fusionUsername, connSlave, userPrx, true)) {
                    throw new FusionEJBException(contactData.displayName + " is blocked, you need to unblock them before adding them to your contact list.");
                }
                if (this.isOnContactList(contactData.username, contactData.fusionUsername, connSlave, userPrx, true)) {
                    throw new FusionEJBException(contactData.displayName + " is already on the contact list");
                }
                if (this.isOnBlockList(contactData.fusionUsername, contactData.username, connSlave, contactPrx, true)) {
                    throw new FusionEJBException(contactData.displayName + " is not accepting invitations currently.");
                }
                if (contactData.contactGroupId != null) {
                    this.checkContactGroupOwnership(contactData.username, contactData.contactGroupId, connSlave);
                }
                boolean friendingEventOccurred = this.isOnContactList(contactData.fusionUsername, contactData.username, connSlave, contactPrx, true);
                int newInviterContactListVersion = -1;
                if (connSlave != null) {
                    connSlave.close();
                    connSlave = null;
                }
                contactData.status = ContactData.StatusEnum.ACTIVE;
                this.setContactOffline(contactData);
                connMaster = this.dataSourceMaster.getConnection();
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                this.persistContact(contactData, connMaster);
                contactData.statusMessage = "";
                if (friendingEventOccurred) {
                    log.debug((Object)(contactData.fusionUsername + " aready have " + contactData.username + " on contact list, updating broadcastlist in database"));
                    this.persistBroadcastListEntry(contactData.username, contactData.fusionUsername, connMaster);
                    this.persistBroadcastListEntry(contactData.fusionUsername, contactData.username, connMaster);
                    newInviterContactListVersion = this.onContactListModified(toUserId, contactData.fusionUsername, connMaster);
                }
                this.removeFromPendingContacts(contactData.username, contactData.fusionUsername, connMaster);
                int newContactListVersion = this.onContactListModified(userID, contactData.username, connMaster);
                userTransaction.commit();
                if (friendingEventOccurred) {
                    log.debug((Object)(contactData.fusionUsername + " aready have " + contactData.username + " on contact list, updating broadcastlist in memcache"));
                    this.updateBroadcastListEntryInMemCached(contactData.username, contactData.fusionUsername);
                    this.updateBroadcastListEntryInMemCached(contactData.fusionUsername, contactData.username);
                    try {
                        this.onContactAccepted(contactData.username, contactData.fusionUsername);
                        this.onContactAccepted(contactData.fusionUsername, contactData.username);
                    }
                    catch (Exception e) {
                        log.error((Object)String.format("Failed to submit friend-added event from %d to %d : %s", userID, toUserId, e.getMessage()), (Throwable)e);
                    }
                }
                try {
                    if (followContactOnMiniblog) {
                        MigboApiUtil apiUtil = MigboApiUtil.getInstance();
                        long curTime = System.currentTimeMillis();
                        boolean oneWayCall = SystemProperty.getBool(SystemPropertyEntities.Contacts.ONEWAY_MIGBO_API_CALLS_ENABLED);
                        String pathPrefix = String.format("/user/%d/following_request/%d?requestingUserid=%s&action=%s", userID, toUserId, userID, "follow");
                        if (oneWayCall) {
                            apiUtil.postOneWay(pathPrefix, "");
                            log.info((Object)String.format("Follow request from %d to %d completed in %d ms - result ONEWAY", userID, toUserId, System.currentTimeMillis() - curTime));
                        } else {
                            JSONObject result = apiUtil.post(pathPrefix, "");
                            log.info((Object)String.format("Follow request from %d to %d completed in %d ms - result %s", userID, toUserId, System.currentTimeMillis() - curTime, result == null ? "FAILED" : result.toString()));
                        }
                    }
                }
                catch (Exception e) {
                    log.error((Object)String.format("Failed to push following request from %d to %d : %s", userID, toUserId, e.getMessage()), (Throwable)e);
                }
                if (userPrx == null) break block53;
                if (friendingEventOccurred) {
                    ContactDataIce updatedContactDataWithPresence = null;
                    log.debug((Object)String.format("Updating userproxy of [%s] with new contact list information & version", contactData.username));
                    updatedContactDataWithPresence = userPrx.acceptContactRequest(contactData.toIceObject(), contactPrx, newInviterContactListVersion, newContactListVersion);
                    if (contactPrx != null) {
                        if (updatedContactDataWithPresence != null) {
                            log.debug((Object)"Getting presence for contact and sending back to acceptor");
                            contactData.copyPresenceAndCapability(updatedContactDataWithPresence);
                        }
                    } else {
                        contactData.fusionPresence = PresenceType.OFFLINE;
                    }
                    break block53;
                }
                if (contactData.contactGroupId != null && contactData.contactGroupId < 0) {
                    contactData.contactGroupId = -1;
                }
                userPrx.addContact(contactData.toIceObject(), newContactListVersion);
            }
            Object var21_25 = null;
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
                break block55;
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            break block55;
            {
                catch (LocalException e) {
                    log.warn((Object)"Failed to update User object in ObjectCache, ignoring.", (Throwable)e);
                    Object var21_26 = null;
                    try {
                        if (connMaster != null) {
                            connMaster.close();
                        }
                    }
                    catch (SQLException e2) {
                        connMaster = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                        break block55;
                    }
                    catch (SQLException e2) {
                        connSlave = null;
                    }
                    break block55;
                }
                catch (FusionEJBException fe) {
                    log.error((Object)("Failed to addFusionUserAsContact - FusionEJBException caught. Username [" + contactData.username + "]. Contact username [" + contactData.fusionUsername + "]"), (Throwable)fe);
                    throw fe;
                }
                catch (Exception e) {
                    log.error((Object)("Failed to addFusionUserAsContact - EJBException caught. Username [" + contactData.username + "]. Contact username [" + contactData.fusionUsername + "]"), (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage(), e);
                }
            }
            catch (Throwable throwable) {
                Object var21_27 = null;
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
        return contactData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<String> getRecentFollowers(int userID) throws EJBException {
        if (userID < 1) {
            throw new EJBException("Invalid userID provided");
        }
        HashSet<String> recentFollowerUsernames = new HashSet<String>();
        try {
            try {
                UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                Map<String, Map<String, String>> newFollowerAlerts = unsProxy.getPendingNotificationDataForUserByType(userID, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType());
                for (Map<String, String> p : newFollowerAlerts.values()) {
                    String username = p.get("otherUsername");
                    if (StringUtil.isBlank(username)) continue;
                    recentFollowerUsernames.add(username);
                }
                Object var9_10 = null;
            }
            catch (FusionException e) {
                log.warn((Object)("Unexpected FusionException while retrieving recent followers :" + e.message), (Throwable)((Object)e));
                Object var9_11 = null;
            }
            catch (Exception e) {
                log.warn((Object)("Unexpected exception while retrieving recent followers :" + e.getMessage()), (Throwable)e);
                Object var9_12 = null;
            }
        }
        catch (Throwable throwable) {
            Object var9_13 = null;
            throw throwable;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning [" + recentFollowerUsernames.size() + "] recent followers"));
        }
        return recentFollowerUsernames;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void removeFusionUserFromContact(int userID, String username, int contactID, boolean unfollowContactOnMiniblog) throws EJBException, FusionEJBException {
        if (userID < 1) {
            throw new FusionEJBException("Invalid userID provided");
        }
        if (contactID < 1) {
            ContactBean.log.info((Object)String.format("Invalid contactID provided [%d] skipping.", new Object[]{contactID}));
            return;
        }
        if (StringUtil.isBlank(username)) {
            throw new FusionEJBException("Invalid username provided");
        }
        ContactBean.log.info((Object)String.format("removeFusionUserFromContact userid %d username %s contactID %d followContactOnMiniblog %s", new Object[]{userID, username, contactID, unfollowContactOnMiniblog != false ? "true" : "false"}));
        connMaster = null;
        connSlave = null;
        ps = null;
        rs = null;
        userTransaction = this.context.getUserTransaction();
        try {
            block42: {
                connMaster = this.dataSourceMaster.getConnection();
                connSlave = this.dataSourceSlave.getConnection();
                ps = connSlave.prepareStatement("select username, fusionusername from contact where id = ?");
                ps.setInt(1, contactID);
                rs = ps.executeQuery();
                if (rs.next()) break block42;
                ContactBean.log.warn((Object)("contact id [" + contactID + "] was previously deleted from contact [" + username + "] or never was a contact?"));
                var23_10 = null;
                ** GOTO lbl127
            }
            if (!username.equalsIgnoreCase(rs.getString("username"))) {
                throw new FusionEJBException("You cannot remove contact that does not belong to you.");
            }
            fusionUsername = rs.getString("fusionUsername");
            ps.close();
            userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            contactUserId = userEJB.getUserID(fusionUsername, connSlave);
            userTransaction.begin();
            this.removeContact(contactID, connMaster);
            if (StringUtils.hasLength((String)fusionUsername)) {
                this.removeFromBroadcastList(username, fusionUsername, connMaster);
                this.removeFromBroadcastListInMemCached(username, fusionUsername);
                this.removeFromBroadcastList(fusionUsername, username, connMaster);
                this.removeFromBroadcastListInMemCached(fusionUsername, username);
            }
            newContactListVersion = this.onContactListModified(userID, username, connMaster);
            userTransaction.commit();
            if (StringUtils.hasLength((String)fusionUsername)) {
                ContactBean.contactDeletedLog.info((Object)(username + " removed " + fusionUsername + "(ID: " + contactID + ")"));
            }
            if ((userPrx = EJBIcePrxFinder.findUserPrx(username)) != null) {
                userPrx.removeContact(contactID, newContactListVersion);
            }
            if ((contactPrx = EJBIcePrxFinder.findUserPrx(fusionUsername)) != null) {
                contactPrx.stopBroadcastingTo(username);
            }
            try {
                block43: {
                    if (!unfollowContactOnMiniblog) ** GOTO lbl147
                    apiUtil = MigboApiUtil.getInstance();
                    curTime = System.currentTimeMillis();
                    oneWayCall = SystemProperty.getBool(SystemPropertyEntities.Contacts.ONEWAY_MIGBO_API_CALLS_ENABLED);
                    pathPrefix = String.format("/user/%d/following_request/%d?requestingUserid=%s&action=%s", new Object[]{userID, contactUserId, userID, "unfollow"});
                    if (!oneWayCall) break block43;
                    apiUtil.postOneWay(pathPrefix, "");
                    ContactBean.log.info((Object)String.format("Unfollow request from %d to %d completed in %d ms - result ONEWAY", new Object[]{userID, contactUserId, System.currentTimeMillis() - curTime}));
                    ** GOTO lbl147
                }
                result = apiUtil.post(pathPrefix, "");
                ContactBean.log.info((Object)String.format("Unfollow request from %d to %d completed in %d ms - result %s", new Object[]{userID, contactUserId, System.currentTimeMillis() - curTime, result == null ? "FAILED" : result.toString()}));
            }
            catch (Exception e) {
                ContactBean.log.error((Object)String.format("Failed to push following request from %d to %d : %s", new Object[]{userID, contactUserId, e.getMessage()}), (Throwable)e);
            }
            ** GOTO lbl147
        }
        catch (LocalException e) {
            var23_12 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e) {
                connMaster = null;
            }
            try {
                if (connSlave == null) return;
                connSlave.close();
                return;
            }
            catch (SQLException e) {
                return;
            }
        }
        catch (Exception e) {
            ContactBean.log.error((Object)"Failed to remove contact", (Throwable)e);
            try {
                if (userTransaction == null) throw new EJBException(e.getMessage());
                userTransaction.rollback();
                throw new EJBException(e.getMessage());
            }
            catch (Exception ie) {
                // empty catch block
            }
            throw new EJBException(e.getMessage());
        }
        {
            block49: {
                block48: {
                    block47: {
                        block46: {
                            catch (Throwable var22_33) {
                                block45: {
                                    block44: {
                                        var23_13 = null;
                                        ** try [egrp 4[TRYBLOCK] [10 : 796->811)] { 
lbl107:
                                        // 1 sources

                                        if (ps != null) {
                                            ps.close();
                                        }
                                        break block44;
lbl110:
                                        // 1 sources

                                        catch (SQLException e) {
                                            ps = null;
                                        }
                                    }
                                    ** try [egrp 5[TRYBLOCK] [11 : 816->831)] { 
lbl114:
                                    // 1 sources

                                    if (connMaster != null) {
                                        connMaster.close();
                                    }
                                    break block45;
lbl117:
                                    // 1 sources

                                    catch (SQLException e) {
                                        connMaster = null;
                                    }
                                }
                                ** try [egrp 6[TRYBLOCK] [12 : 836->851)] { 
lbl121:
                                // 1 sources

                                if (connSlave == null) throw var22_33;
                                connSlave.close();
                                throw var22_33;
lbl124:
                                // 1 sources

                                catch (SQLException e) {
                                    connSlave = null;
                                }
                                throw var22_33;
                            }
lbl127:
                            // 1 sources

                            ** try [egrp 4[TRYBLOCK] [10 : 796->811)] { 
lbl128:
                            // 1 sources

                            if (ps != null) {
                                ps.close();
                            }
                            break block46;
lbl131:
                            // 1 sources

                            catch (SQLException e) {
                                ps = null;
                            }
                        }
                        ** try [egrp 5[TRYBLOCK] [11 : 816->831)] { 
lbl135:
                        // 1 sources

                        if (connMaster != null) {
                            connMaster.close();
                        }
                        break block47;
lbl138:
                        // 1 sources

                        catch (SQLException e) {
                            connMaster = null;
                        }
                    }
                    try {}
                    catch (SQLException e) {
                        return;
                    }
                    if (connSlave == null) return;
                    connSlave.close();
                    return;
lbl147:
                    // 4 sources

                    var23_11 = null;
                    ** try [egrp 4[TRYBLOCK] [10 : 796->811)] { 
lbl149:
                    // 1 sources

                    if (ps != null) {
                        ps.close();
                    }
                    break block48;
lbl152:
                    // 1 sources

                    catch (SQLException e) {
                        ps = null;
                    }
                }
                ** try [egrp 5[TRYBLOCK] [11 : 816->831)] { 
lbl156:
                // 1 sources

                if (connMaster != null) {
                    connMaster.close();
                }
                break block49;
lbl159:
                // 1 sources

                catch (SQLException e) {
                    connMaster = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (connSlave == null) return;
            connSlave.close();
            return;
        }
    }

    /*
     * Loose catch block
     */
    public ContactData addPendingFusionContact(int userID, ContactData contactData) throws EJBException {
        block54: {
            UserTransaction userTransaction;
            Connection connSlave;
            Connection connMaster;
            block52: {
                UserPrx contactPrx;
                UserPrx userPrx;
                block49: {
                    connMaster = null;
                    connSlave = null;
                    userTransaction = null;
                    if (StringUtil.isBlank(contactData.username)) {
                        throw new EJBException("Please provide a contact to add.");
                    }
                    if (contactData.username.equalsIgnoreCase(contactData.fusionUsername)) {
                        throw new EJBException("You cannot add yourself to your contact list.");
                    }
                    if (contactData.displayName == null) {
                        contactData.displayName = this.getDefaultDisplayName(connMaster, contactData);
                    }
                    if (!MemCachedRateLimiter.hit(MemCachedRateLimiter.NameSpace.CONTACT.toString(), contactData.fusionUsername, 1L, 10000L)) {
                        log.error((Object)("[" + contactData.username + "] tried to add [" + contactData.fusionUsername + "] too soon"));
                        throw new EJBException(contactData.displayName + " is unable to receive invites at this time. Please try again a little bit later");
                    }
                    if (this.hasTooManyPendingContacts(contactData.fusionUsername, null)) {
                        log.error((Object)("[" + contactData.username + "] tried to add [" + contactData.fusionUsername + "] who has too many invites"));
                        throw new EJBException(contactData.displayName + " is unable to receive invites at this time. Please try again later");
                    }
                    if (this.hasTooManyFusionContacts(contactData.username, null)) {
                        throw new EJBException("You cannot add any more migme contacts. Please remove some first");
                    }
                    if (!AuthenticatedAccessControl.hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum.ADD_FRIEND, contactData.username) && SystemProperty.getBool("AddContactDisabledForUnauthenticatedUsers", false)) {
                        throw new EJBException("You must be authenticated before you can add new contacts.");
                    }
                    if (!AuthenticatedAccessControl.hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum.BE_ADDED_AS_FRIEND, contactData.fusionUsername) && SystemProperty.getBool("OnlyAddAuthenticatedContacts", false)) {
                        throw new EJBException("You can only add authenticated accounts to your contact list.");
                    }
                    userPrx = EJBIcePrxFinder.findUserPrx(contactData.username);
                    contactPrx = EJBIcePrxFinder.findUserPrx(contactData.fusionUsername);
                    connSlave = this.dataSourceSlave.getConnection();
                    if (this.isOnBlockList(contactData.username, contactData.fusionUsername, connSlave, userPrx, false)) {
                        throw new EJBException(contactData.displayName + " is blocked, you need to unblock them before adding them to your contact list.");
                    }
                    if (this.isOnContactList(contactData.username, contactData.fusionUsername, connSlave, userPrx, true)) {
                        throw new Exception(contactData.displayName + " is already on the contact list");
                    }
                    ContactData maskedUserData = this.getMaskedFusionContact(contactData.username, contactData.fusionUsername, contactData.mobilePhone);
                    if (maskedUserData != null) {
                        throw new Exception(contactData.mobilePhone + " is already on the contact list");
                    }
                    if (this.isOnBlockList(contactData.fusionUsername, contactData.username, connSlave, contactPrx, true)) {
                        throw new EJBException(contactData.displayName + " is not accepting invitations currently.");
                    }
                    if (!this.isOnContactList(contactData.fusionUsername, contactData.username, connSlave, contactPrx, true)) break block49;
                    log.debug((Object)(contactData.fusionUsername + " aready have " + contactData.username + " on their contact list, going straight to accept"));
                    contactData.displayName = contactData.fusionUsername;
                    ContactData contactData2 = this.acceptContactRequest(userID, contactData, true);
                    Object var16_14 = null;
                    try {
                        if (connMaster != null) {
                            connMaster.close();
                        }
                    }
                    catch (SQLException e2) {
                        connMaster = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                    }
                    catch (SQLException e2) {
                        connSlave = null;
                    }
                    return contactData2;
                }
                if (contactData.contactGroupId != null) {
                    this.checkContactGroupOwnership(contactData.username, contactData.contactGroupId, connSlave);
                }
                this.hasMobilePhoneNumber(contactData.fusionUsername, connSlave);
                if (connSlave != null) {
                    connSlave.close();
                    connSlave = null;
                }
                contactData.status = ContactData.StatusEnum.ACTIVE;
                this.setContactOffline(contactData);
                connMaster = this.dataSourceMaster.getConnection();
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                if (StringUtils.hasLength((String)contactData.fusionUsername)) {
                    this.persistPendingContact(contactData.fusionUsername, contactData.username, connMaster);
                }
                String originalfusionUsername = null;
                if (!contactData.fusionUsername.equalsIgnoreCase(contactData.displayName) && contactData.mobilePhone != null) {
                    originalfusionUsername = contactData.fusionUsername;
                    contactData.fusionUsername = null;
                }
                this.persistContact(contactData, connMaster);
                if (originalfusionUsername != null) {
                    contactData.fusionUsername = originalfusionUsername;
                }
                contactData.statusMessage = PENDING_CONTACT_MESSAGE;
                int newContactListVersion = this.onContactListModified(userID, contactData.username, connMaster);
                userTransaction.commit();
                try {
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    int toUserId = userEJB.getUserID(contactData.fusionUsername, connMaster);
                    UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("requestorId", Integer.toString(userID));
                    parameters.put("requestorUserName", contactData.username);
                    unsProxy.notifyFusionUser(new Message(contactData.username, toUserId, contactData.fusionUsername, Enums.NotificationTypeEnum.FRIEND_INVITE.getType(), System.currentTimeMillis(), parameters));
                }
                catch (Exception e) {
                    log.error((Object)("Failed to push friend invite notification for user [" + contactData.username + "]"), (Throwable)e);
                }
                if (userPrx != null) {
                    if (contactData.contactGroupId != null && contactData.contactGroupId < 0) {
                        contactData.contactGroupId = -1;
                    }
                    if (originalfusionUsername != null) {
                        contactData.fusionUsername = null;
                    }
                    userPrx.addContact(contactData.toIceObject(), newContactListVersion);
                }
                if (contactPrx == null) break block52;
                contactPrx.addPendingContact(contactData.username);
            }
            Object var16_15 = null;
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                connMaster = null;
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
                break block54;
            }
            catch (SQLException e2) {
                connSlave = null;
            }
            break block54;
            {
                catch (LocalException e) {
                    log.warn((Object)"Failed to update User object in ObjectCache, ignore", (Throwable)e);
                    Object var16_16 = null;
                    try {
                        if (connMaster != null) {
                            connMaster.close();
                        }
                    }
                    catch (SQLException e2) {
                        connMaster = null;
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                        break block54;
                    }
                    catch (SQLException e2) {
                        connSlave = null;
                    }
                    break block54;
                }
                catch (Exception e) {
                    log.error((Object)("Failed to add pending contact. Username [" + contactData.username + "]. Contact username [" + contactData.fusionUsername + "]"), (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var16_17 = null;
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    connMaster = null;
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    connSlave = null;
                }
                throw throwable;
            }
        }
        return contactData;
    }

    /*
     * Loose catch block
     */
    public ContactData acceptContactRequest(int userID, ContactData contactData, boolean ignoreMissingContactRequest) throws EJBException {
        block37: {
            UserTransaction userTransaction;
            Connection masterConnection;
            block36: {
                Set<String> bcl;
                if (log.isDebugEnabled()) {
                    log.debug((Object)("user [" + contactData.username + "] accepting contact [" + contactData.fusionUsername + "]"));
                }
                masterConnection = null;
                userTransaction = null;
                if (this.hasTooManyFusionContacts(contactData.username, null)) {
                    throw new EJBException("You cannot accept any more migme contact requests. Please remove some contacts first");
                }
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(contactData.username);
                UserPrx contactPrx = EJBIcePrxFinder.findUserPrx(contactData.fusionUsername);
                masterConnection = this.dataSourceMaster.getConnection();
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                UserData userData = userEJB.loadUserFromID(userID);
                if (contactData.contactGroupId != null) {
                    this.checkContactGroupOwnership(contactData.username, contactData.contactGroupId, masterConnection);
                }
                if (this.isOnContactList(contactData.username, contactData.fusionUsername, masterConnection, userPrx, false)) {
                    log.warn((Object)("contact [" + contactData.fusionUsername + "] is already on the user's [" + contactData.username + "] contact list"));
                    throw new EJBException(contactData.fusionUsername + " is already on the contact list");
                }
                Boolean usernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(contactData.username);
                Boolean fusionUsernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(contactData.fusionUsername);
                this.hasMobilePhoneNumber(contactData.fusionUsername, masterConnection);
                if (contactData.displayName == null) {
                    contactData.displayName = this.getDefaultDisplayName(masterConnection, contactData);
                }
                contactData.status = ContactData.StatusEnum.ACTIVE;
                ContactData maskedContactData = this.getMaskedFusionContact(contactData.fusionUsername, contactData.username, userData.mobilePhone);
                int inviterUserID = userEJB.getUserID(contactData.fusionUsername, masterConnection);
                boolean sharesMobilePhone = this.contactSharesMobilePhone(contactData.fusionUsername, contactData.username);
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                this.persistContact(contactData, masterConnection);
                int newInviterContactListVersion = -1;
                if (maskedContactData != null) {
                    this.unmaskFusionContact(contactData.fusionUsername, userData.mobilePhone, contactData.username, masterConnection);
                }
                if (contactData.shareMobilePhone != null && contactData.shareMobilePhone.booleanValue()) {
                    this.updateContactsMobilePhone(contactData.username, contactData.fusionUsername, masterConnection);
                }
                if (maskedContactData != null || contactData.shareMobilePhone != null && contactData.shareMobilePhone.booleanValue()) {
                    newInviterContactListVersion = this.onContactListModified(inviterUserID, contactData.fusionUsername, masterConnection);
                }
                if (sharesMobilePhone) {
                    contactData.mobilePhone = this.updateContactsMobilePhone(contactData.fusionUsername, contactData.username, masterConnection);
                }
                if (!this.removeFromPendingContacts(contactData.username, contactData.fusionUsername, masterConnection) && !ignoreMissingContactRequest) {
                    throw new Exception("There is no longer an invitation from " + contactData.fusionUsername + " to accept");
                }
                if (usernameBCLHasBeenPersisted.booleanValue()) {
                    this.persistBroadcastListEntry(contactData.username, contactData.fusionUsername, masterConnection);
                }
                this.updateBroadcastListEntryInMemCached(contactData.username, contactData.fusionUsername);
                if (checkAndPopulateBCL && fusionUsernameBCLHasBeenPersisted.booleanValue() && !(bcl = this.checkAndPopulateBCL(contactData.fusionUsername, masterConnection)).contains(contactData.username)) {
                    this.persistBroadcastListEntry(contactData.fusionUsername, contactData.username, masterConnection);
                }
                log.debug((Object)String.format("Updating broadcastlist entry in memcached [%s] [%s]", contactData.fusionUsername, contactData.username));
                this.updateBroadcastListEntryInMemCached(contactData.fusionUsername, contactData.username);
                log.debug((Object)"Assigning display picture and status message");
                this.assignDisplayPictureAndStatusMessageToContacts(masterConnection, Collections.nCopies(1, contactData));
                log.debug((Object)String.format("updating contactlist version for userid [%d] username [%s]", userID, contactData.username));
                int newContactListVersion = this.onContactListModified(userID, contactData.username, masterConnection);
                log.debug((Object)"Committing transaction");
                userTransaction.commit();
                try {
                    if (masterConnection != null) {
                        masterConnection.close();
                        masterConnection = null;
                    }
                }
                catch (SQLException e) {
                    // empty catch block
                }
                log.debug((Object)String.format("Create user events for both sides of the relationship [%s] [%s]", contactData.username, contactData.fusionUsername));
                this.onContactAccepted(contactData.username, contactData.fusionUsername);
                log.debug((Object)String.format("Remove Friend Invite Notification for userid [%d] username[%s]", userID, contactData.fusionUsername));
                this.removeFriendInviteAndNewFollowerNotifications(userID, contactData.fusionUsername, inviterUserID);
                ContactDataIce updatedContactDataWithPresence = null;
                if (userPrx != null) {
                    log.debug((Object)String.format("Updating userproxy of [%s] with new contact list version", contactData.username));
                    updatedContactDataWithPresence = userPrx.acceptContactRequest(contactData.toIceObject(), contactPrx, newInviterContactListVersion, newContactListVersion);
                }
                if (contactPrx != null) {
                    if (updatedContactDataWithPresence != null) {
                        log.debug((Object)"Getting presence for contact and sending back to acceptor");
                        contactData.copyPresenceAndCapability(updatedContactDataWithPresence);
                    }
                    if (maskedContactData != null) {
                        log.debug((Object)String.format("Sending alert to  [%s] ", contactData.fusionUsername));
                        contactPrx.putAlertMessage(contactData.username + " (" + userData.mobilePhone + ") has accepted your invitation. Please relogin to refresh your contact list.", "Friends Invite", (short)0);
                    }
                } else {
                    contactData.fusionPresence = PresenceType.OFFLINE;
                }
                if (userPrx == null) break block36;
                log.debug((Object)String.format("Notifying new contact to session belonging to [%s] ", contactData.username));
                userPrx.notifySessionsOfNewContact(contactData.toIceObject(), newContactListVersion, false);
            }
            Object var19_23 = null;
            try {
                if (masterConnection != null) {
                    masterConnection.close();
                }
                break block37;
            }
            catch (SQLException e2) {}
            break block37;
            {
                catch (LocalException e) {
                    log.warn((Object)"Failed to update User object in ObjectCache, ignore", (Throwable)e);
                    Object var19_24 = null;
                    try {
                        if (masterConnection != null) {
                            masterConnection.close();
                        }
                        break block37;
                    }
                    catch (SQLException e2) {}
                    break block37;
                }
                catch (Exception e) {
                    log.error((Object)("Failed to accept contact request. username: " + contactData.username + " fusionUsername: " + contactData.fusionUsername + " Exception: " + e.getMessage()), (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage(), e);
                }
            }
            catch (Throwable throwable) {
                Object var19_25 = null;
                try {
                    if (masterConnection != null) {
                        masterConnection.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("acceptContactRequest, returning contact [" + contactData.fusionUsername + "] with presence [" + contactData.fusionPresence + "]"));
        }
        return contactData;
    }

    private void removeFriendInviteAndNewFollowerNotifications(int userId, String inviterUsername, int inviterUserid) {
        try {
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            unsProxy.clearNotificationsForUser(userId, Enums.NotificationTypeEnum.FRIEND_INVITE.getType(), new String[]{inviterUsername});
            unsProxy.clearNotificationsForUser(userId, Enums.NotificationTypeEnum.NEW_FOLLOWER_ALERT.getType(), new String[]{Integer.toString(inviterUserid)});
        }
        catch (Exception e) {
            log.warn((Object)("Failed to remove friend invite / new follower notification for user [" + userId + "]"), (Throwable)e);
        }
    }

    /*
     * Loose catch block
     */
    public void makeReferrerAndReferreeFriends(int referreeUserID, String referreeUsername, String referreeMobilePhone, int referrerUserID, String referrerUsername, String referrerDisplayName, String referrerMobilePhone) throws EJBException {
        block34: {
            Connection connMaster = null;
            Statement ps = null;
            ResultSet rs = null;
            UserTransaction userTransaction = null;
            connMaster = this.dataSourceMaster.getConnection();
            userTransaction = this.context.getUserTransaction();
            userTransaction.begin();
            this.onContactListModified(referreeUserID, referreeUsername, connMaster);
            int newReferrerContactListVersion = this.onContactListModified(referrerUserID, referrerUsername, connMaster);
            ContactData referreesNewContact = new ContactData();
            referreesNewContact.username = referreeUsername;
            referreesNewContact.displayName = referrerDisplayName == null ? referrerUsername : referrerDisplayName;
            referreesNewContact.fusionUsername = referrerUsername;
            referreesNewContact.mobilePhone = referrerMobilePhone;
            referreesNewContact.status = ContactData.StatusEnum.ACTIVE;
            referreesNewContact.displayOnPhone = true;
            this.persistContact(referreesNewContact, connMaster);
            ContactData referrersNewContact = new ContactData();
            referrersNewContact.username = referrerUsername;
            referrersNewContact.displayName = referreeUsername;
            referrersNewContact.fusionUsername = referreeUsername;
            referrersNewContact.mobilePhone = referreeMobilePhone;
            referrersNewContact.status = ContactData.StatusEnum.ACTIVE;
            referrersNewContact.displayOnPhone = true;
            this.persistContact(referrersNewContact, connMaster);
            this.persistBroadcastListEntry(referreeUsername, referrerUsername, connMaster);
            if (SystemProperty.getBool(SystemPropertyEntities.Default.BCLPERSISTED_ENABLED)) {
                BroadcastListPersisted.setBroadcastListPersisted(bclPersistedMemcache, referreeUsername, 1);
            }
            Set<String> referreeBCL = BroadcastList.newBroadcastList();
            referreeBCL.add(referrerUsername);
            BroadcastList.setBroadcastList(broadcastListMemcache, referreeUsername, referreeBCL);
            if (checkAndPopulateBCL && ContactBean.isBroadcastListPersisted(referrerUsername).booleanValue()) {
                this.persistBroadcastListEntry(referrerUsername, referreeUsername, connMaster);
            }
            userTransaction.commit();
            this.updateBroadcastListEntryInMemCached(referrerUsername, referreeUsername);
            try {
                this.onContactAccepted(referreeUsername, referrerUsername);
                UserPrx referrerUserPrx = EJBIcePrxFinder.findUserPrx(referrerUsername);
                if (referrerUserPrx != null) {
                    referrersNewContact.fusionPresence = PresenceType.OFFLINE;
                    referrerUserPrx.addToContactAndBroadcastLists(referrersNewContact.toIceObject(), newReferrerContactListVersion);
                }
            }
            catch (Exception e) {
                log.warn((Object)"Failed post processing in makeReferrerAndReferreeFriends()", (Throwable)e);
            }
            Object var18_22 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
                break block34;
            }
            catch (SQLException e) {
                connMaster = null;
            }
            break block34;
            {
                catch (SQLException e) {
                    log.warn((Object)"SQLException in makeReferrerAndReferreeFriends", (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
                catch (Exception e) {
                    log.warn((Object)"Exception in makeReferrerAndReferreeFriends", (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var18_23 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e) {
                    ps = null;
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e) {
                    connMaster = null;
                }
                throw throwable;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"makeReferrerAndReferreeFriends finished");
        }
    }

    public Set<String> checkAndPopulateBCL(String username, Connection slaveConnection) throws EJBException {
        block7: {
            block8: {
                if (!MemCachedUtils.getLock(broadcastListMemcache, "BLDL", username, 15000)) break block7;
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                Set broadcastList = userEJB.loadBroadcastList(username, slaveConnection);
                if (broadcastList == null || broadcastList.isEmpty()) break block8;
                log.debug((Object)(username + " already has a BCL, not generating, using the existing one"));
                Set set = broadcastList;
                Object var8_8 = null;
                MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
                return set;
            }
            Set<String> bcl = this.generateBCL(username, slaveConnection);
            BroadcastList.setBroadcastList(broadcastListMemcache, username, bcl);
            Set<String> set = bcl;
            Object var8_9 = null;
            MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
            return set;
        }
        try {
            try {
                log.error((Object)("Failed to get a lock to update user [" + username + "]'s BCL in 10 seconds"));
            }
            catch (Exception ex) {
                log.error((Object)"failed to commit new BCL, rolling back", (Throwable)ex);
                throw new EJBException("failed to persist new BCL for user [" + username + "]");
            }
            Object var8_10 = null;
        }
        catch (Throwable throwable) {
            Object var8_11 = null;
            MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
            throw throwable;
        }
        MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
        if (log.isDebugEnabled()) {
            log.debug((Object)("broadcast list for user [" + username + "] not persisted as it's empty"));
        }
        return Collections.emptySet();
    }

    public void persistBroadcastList(String username, Set<String> bcl, Connection connection) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persisting BCL for user [" + username + "]"));
        }
        PreparedStatement ps = connection.prepareStatement("insert into broadcastlist values (?,?)");
        for (String receiver : bcl) {
            ps.setString(1, username);
            ps.setString(2, receiver);
            ps.addBatch();
        }
        if (ps.executeBatch().length < 1) {
            log.warn((Object)("failed to persist BCL for user [" + username + "]"));
        }
        ps.close();
    }

    private Set<String> generateBCL(String username, Connection conn) throws SQLException {
        ResultSet rs;
        PreparedStatement ps;
        Set<String> contacts;
        if (log.isDebugEnabled()) {
            log.debug((Object)("generating BCL for user [" + username + "]"));
        }
        if ((contacts = ContactList.getFusionContactUsernames(contactListMemcache, username)) == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("no contact list found in memcached for user [" + username + "], falling back to DB"));
            }
            contacts = new HashSet<String>();
            ps = conn.prepareStatement("select fusionUsername from contact where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                contacts.add(rs.getString(1));
            }
            rs.close();
            ps.close();
        }
        ps = conn.prepareStatement("select username from contact where fusionUsername = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        HashSet<String> contactsContacts = new HashSet<String>();
        while (rs.next()) {
            contactsContacts.add(rs.getString(1));
        }
        contacts.retainAll(contactsContacts);
        ps = conn.prepareStatement("select blockusername from blocklist where username = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        while (rs.next()) {
            contacts.remove(rs.getString(1));
        }
        ps = conn.prepareStatement("select username from blocklist where blockusername = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        while (rs.next()) {
            contacts.remove(rs.getString(1));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("found " + contacts.size() + " BCL entries for user [" + username + "]"));
        }
        return contacts;
    }

    /*
     * Loose catch block
     */
    public ContactData addIMContact(int userID, ContactData contactData, boolean notifyObjectCache) throws EJBException {
        block35: {
            UserTransaction userTransaction;
            ResultSet rs;
            Statement ps;
            Connection conn;
            block32: {
                UserPrx userPrx;
                conn = null;
                ps = null;
                rs = null;
                userTransaction = null;
                if (!AuthenticatedAccessControl.hasAccessByUseridLocal(AuthenticatedAccessControlTypeEnum.ADD_FRIEND, userID) && SystemProperty.getBool("AddContactDisabledForUnauthenticatedUsers", false)) {
                    throw new EJBException("You must be authenticated before you can add new contacts.");
                }
                conn = this.dataSourceMaster.getConnection();
                if (contactData.contactGroupId != null) {
                    this.checkContactGroupOwnership(contactData.username, contactData.contactGroupId, conn);
                }
                if (contactData.displayName == null) {
                    contactData.displayName = this.getDefaultDisplayName(conn, contactData);
                }
                contactData.status = ContactData.StatusEnum.ACTIVE;
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                this.persistContact(contactData, conn);
                int newContactListVersion = this.onContactListModified(userID, contactData.username, conn);
                userTransaction.commit();
                if (!notifyObjectCache || (userPrx = EJBIcePrxFinder.findUserPrx(contactData.username)) == null) break block32;
                userPrx.addContact(contactData.toIceObject(), newContactListVersion);
            }
            Object var11_13 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block35;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block35;
            {
                catch (LocalException e) {
                    log.warn((Object)"Failed to update User object in ObjectCache, ignore", (Throwable)e);
                    Object var11_14 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block35;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block35;
                }
                catch (Exception e) {
                    log.error((Object)("Failed to add IM contact. Username [" + contactData.username + "]. IM display name [" + contactData.displayName + "]"), (Throwable)e);
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_15 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return contactData;
    }

    public ContactData addPhoneContact(int userID, ContactData contactData) throws EJBException {
        return this.addIMContact(userID, contactData, true);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void removeContact(int userID, String username, int contactID) throws EJBException {
        if (contactID < 0) {
            ContactBean.log.info((Object)"Looks like an other IM contact... skipping");
            return;
        }
        conn = null;
        ps = null;
        rs = null;
        userTransaction = this.context.getUserTransaction();
        try {
            block30: {
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select username, fusionusername from contact where id = ?");
                ps.setInt(1, contactID);
                rs = ps.executeQuery();
                if (rs.next()) break block30;
                ContactBean.log.warn((Object)("contact id [" + contactID + "] was previously deleted from contact [" + username + "] or never was a contact?"));
                var15_8 = null;
                ** GOTO lbl92
            }
            if (!username.equalsIgnoreCase(rs.getString("username"))) {
                throw new Exception("You cannot remove contact that does not belong to you.");
            }
            fusionUsername = rs.getString("fusionUsername");
            ps.close();
            userTransaction.begin();
            usernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(username);
            fusionUsernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(fusionUsername);
            this.removeContact(contactID, conn);
            if (StringUtils.hasLength((String)fusionUsername)) {
                this.removeFromPendingContacts(fusionUsername, username, conn);
                if (usernameBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(username, fusionUsername, conn);
                }
                this.removeFromBroadcastListInMemCached(username, fusionUsername);
                if (fusionUsernameBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(fusionUsername, username, conn);
                }
                this.removeFromBroadcastListInMemCached(fusionUsername, username);
            }
            newContactListVersion = this.onContactListModified(userID, username, conn);
            userTransaction.commit();
            if (StringUtils.hasLength((String)fusionUsername)) {
                ContactBean.contactDeletedLog.info((Object)(username + " removed " + fusionUsername + "(ID: " + contactID + ")"));
            }
            if ((userPrx = EJBIcePrxFinder.findUserPrx(username)) != null) {
                userPrx.removeContact(contactID, newContactListVersion);
            }
            if ((contactPrx = EJBIcePrxFinder.findUserPrx(fusionUsername)) != null) {
                contactPrx.stopBroadcastingTo(username);
            }
            ** GOTO lbl105
        }
        catch (LocalException e) {
            var15_10 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e) {
                ps = null;
            }
            try {
                if (conn == null) return;
                conn.close();
                return;
            }
            catch (SQLException e) {
                return;
            }
        }
        catch (Exception e) {
            ContactBean.log.error((Object)"Failed to remove contact", (Throwable)e);
            try {
                userTransaction.rollback();
                throw new EJBException(e.getMessage());
            }
            catch (Exception ie) {
                // empty catch block
            }
            throw new EJBException(e.getMessage());
        }
        {
            block33: {
                block32: {
                    catch (Throwable var14_25) {
                        block31: {
                            var15_11 = null;
                            ** try [egrp 3[TRYBLOCK] [9 : 449->464)] { 
lbl79:
                            // 1 sources

                            if (ps != null) {
                                ps.close();
                            }
                            break block31;
lbl82:
                            // 1 sources

                            catch (SQLException e) {
                                ps = null;
                            }
                        }
                        ** try [egrp 4[TRYBLOCK] [10 : 469->484)] { 
lbl86:
                        // 1 sources

                        if (conn == null) throw var14_25;
                        conn.close();
                        throw var14_25;
lbl89:
                        // 1 sources

                        catch (SQLException e) {
                            conn = null;
                        }
                        throw var14_25;
                    }
lbl92:
                    // 1 sources

                    ** try [egrp 3[TRYBLOCK] [9 : 449->464)] { 
lbl93:
                    // 1 sources

                    if (ps != null) {
                        ps.close();
                    }
                    break block32;
lbl96:
                    // 1 sources

                    catch (SQLException e) {
                        ps = null;
                    }
                }
                try {}
                catch (SQLException e) {
                    return;
                }
                if (conn == null) return;
                conn.close();
                return;
lbl105:
                // 1 sources

                var15_9 = null;
                ** try [egrp 3[TRYBLOCK] [9 : 449->464)] { 
lbl107:
                // 1 sources

                if (ps != null) {
                    ps.close();
                }
                break block33;
lbl110:
                // 1 sources

                catch (SQLException e) {
                    ps = null;
                }
            }
            try {}
            catch (SQLException e) {}
            return;
            if (conn == null) return;
            conn.close();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void persistUpdatedContact(ContactData contactData, Connection conn) throws Exception {
        PreparedStatement ps = conn.prepareStatement("update contact set displayname=?, firstname=?, lastname=?, fusionusername=?, msnusername=?, aimusername=?, yahoousername=?, icqusername=?, jabberusername=?, emailaddress=?, mobilephone=?, homephone=?, officephone=?, defaultim=?, defaultphonenumber=? , contactgroupid=? where id = ?");
        try {
            ps.setString(1, contactData.displayName);
            ps.setString(2, contactData.firstName);
            ps.setString(3, contactData.lastName);
            ps.setString(4, contactData.fusionUsername);
            ps.setString(5, contactData.msnUsername);
            ps.setString(6, contactData.aimUsername);
            ps.setString(7, contactData.yahooUsername);
            ps.setString(8, contactData.facebookUsername);
            ps.setString(9, contactData.gtalkUsername);
            ps.setString(10, contactData.emailAddress);
            ps.setString(11, contactData.mobilePhone);
            ps.setString(12, contactData.homePhone);
            ps.setString(13, contactData.officePhone);
            ps.setObject(14, contactData.defaultIM == null ? null : Byte.valueOf(contactData.defaultIM.value()));
            ps.setObject(15, contactData.defaultPhoneNumber == null ? null : Byte.valueOf(contactData.defaultPhoneNumber.value()));
            ps.setObject(16, contactData.contactGroupId == null || contactData.contactGroupId == -1 || contactData.contactGroupId == -2 || contactData.contactGroupId == -3 || contactData.contactGroupId == -4 || contactData.contactGroupId == -5 || contactData.contactGroupId == -6 ? null : contactData.contactGroupId);
            ps.setObject(17, contactData.id);
            if (ps.executeUpdate() != 1) {
                throw new Exception("Failed to update contact detail, does the contact exist?");
            }
            Object var5_4 = null;
            if (ps == null) return;
        }
        catch (Throwable throwable) {
            Object var5_5 = null;
            if (ps == null) throw throwable;
            ps.close();
            throw throwable;
        }
        ps.close();
    }

    /*
     * Loose catch block
     */
    public ContactData updateContactDetail(int userID, ContactData contactData) throws EJBException {
        block60: {
            ContactData existingContactData;
            Connection connMaster = null;
            Connection connSlave = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            if (StringUtil.isBlank(contactData.username)) {
                throw new EJBException("Please provide a contact to add.");
            }
            UserTransaction userTransaction = this.context.getUserTransaction();
            connSlave = this.dataSourceSlave.getConnection();
            ps = connSlave.prepareStatement("SELECT contact.id, contact.contactgroupid FROM contact, userid WHERE contact.username = userid.username AND userid.id = ? AND contact.id = ?");
            ps.setInt(1, userID);
            ps.setInt(2, contactData.id);
            rs = ps.executeQuery();
            if (!rs.next()) {
                log.warn((Object)("User with ID " + userID + " attempted to update contact with ID " + contactData.id + " that does not belong to them"));
                throw new EJBException("Unable to update contact");
            }
            int originalContactGroupID = rs.getInt(2);
            rs.close();
            ps.close();
            if (contactData.contactGroupId != null && contactData.contactGroupId != -1 && contactData.contactGroupId != originalContactGroupID) {
                ps = connSlave.prepareStatement("SELECT userid.id FROM contactgroup, userid WHERE contactgroup.username = userid.username AND contactgroup.id = ?");
                ps.setInt(1, contactData.contactGroupId);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    log.warn((Object)("User with ID " + userID + " attempted to change contactgroupid to " + contactData.contactGroupId + " for the contact with ID " + contactData.id + ", but the group does not exist"));
                    throw new EJBException("Unable to update contact.");
                }
                int groupOwnerUserID = rs.getInt(1);
                rs.close();
                ps.close();
                if (groupOwnerUserID != userID) {
                    log.warn((Object)("User with ID " + userID + " attempted to change contactgroupid to " + contactData.contactGroupId + " for the contact with ID " + contactData.id + ", but they do not own that group"));
                    throw new FusionEJBException("Unable to update contact details");
                }
            }
            if ((existingContactData = this.getContact(contactData.id)) == null) {
                throw new Exception("Contact " + contactData.id + " does not exist in database");
            }
            if (null != existingContactData.fusionUsername && !existingContactData.fusionUsername.equalsIgnoreCase(contactData.fusionUsername)) {
                log.warn((Object)("User with ID " + userID + "attempted to change contact with username '" + existingContactData.fusionUsername + "' to '" + contactData.fusionUsername + "'"));
                throw new Exception("Unable to edit the username of an existing migme contact.");
            }
            String dbFusionUsername = existingContactData.fusionUsername;
            UserPrx userPrx = EJBIcePrxFinder.findUserPrx(contactData.username);
            UserPrx oldContactUserPrx = EJBIcePrxFinder.findUserPrx(dbFusionUsername);
            UserPrx newContactUserPrx = EJBIcePrxFinder.findUserPrx(contactData.fusionUsername);
            if (log.isDebugEnabled()) {
                log.debug((Object)("userPrx [" + userPrx + "] oldContactUserPrx [" + oldContactUserPrx + "] newContactUserPrx [" + newContactUserPrx + "]"));
            }
            boolean newContactAlreadyOnContactList = this.isOnContactList(contactData.fusionUsername, contactData.username, connSlave, newContactUserPrx, true);
            connSlave.close();
            connSlave = null;
            userTransaction.begin();
            connMaster = this.dataSourceMaster.getConnection();
            if (StringUtils.hasLength((String)contactData.fusionUsername)) {
                if (this.isOnContactListForIdOtherThan(contactData.username, contactData.fusionUsername, contactData.id, connMaster)) {
                    throw new Exception(contactData.fusionUsername + " is already on the contact list");
                }
                if (dbFusionUsername == null || !contactData.fusionUsername.equalsIgnoreCase(dbFusionUsername)) {
                    this.hasMobilePhoneNumber(contactData.fusionUsername, connMaster);
                }
            }
            Boolean usernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(contactData.username);
            Boolean dbFusionUsernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(dbFusionUsername);
            Boolean fusionUsernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(contactData.fusionUsername);
            this.persistUpdatedContact(contactData, connMaster);
            boolean changedFusionContact = false;
            boolean acceptedContactRequest = false;
            if (contactData.fusionUsername != null && !contactData.fusionUsername.equalsIgnoreCase(dbFusionUsername)) {
                changedFusionContact = true;
                this.removeFromPendingContacts(dbFusionUsername, contactData.username, connMaster);
                if (usernameBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(contactData.username, dbFusionUsername, connMaster);
                }
                this.removeFromBroadcastListInMemCached(contactData.username, dbFusionUsername);
                if (dbFusionUsernameBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(dbFusionUsername, contactData.username, connMaster);
                }
                this.removeFromBroadcastListInMemCached(dbFusionUsername, contactData.username);
                if (newContactAlreadyOnContactList) {
                    this.removeFromPendingContacts(contactData.fusionUsername, contactData.username, connMaster);
                    if (fusionUsernameBCLHasBeenPersisted.booleanValue()) {
                        this.persistBroadcastListEntry(contactData.fusionUsername, contactData.username, connMaster);
                    }
                    this.updateBroadcastListEntryInMemCached(contactData.fusionUsername, contactData.username);
                    if (usernameBCLHasBeenPersisted.booleanValue()) {
                        this.persistBroadcastListEntry(contactData.username, contactData.fusionUsername, connMaster);
                    }
                    this.updateBroadcastListEntryInMemCached(contactData.username, contactData.fusionUsername);
                    acceptedContactRequest = true;
                } else {
                    this.persistPendingContact(contactData.fusionUsername, contactData.username, connMaster);
                }
            }
            this.assignDisplayPictureAndStatusMessageToContacts(connMaster, Collections.nCopies(1, contactData));
            int newContactListVersion = this.onContactListModified(userID, contactData.username, connMaster);
            userTransaction.commit();
            PresenceAndCapabilityIce presence = null;
            if (userPrx != null) {
                presence = userPrx.contactUpdated(contactData.toIceObject(), dbFusionUsername, acceptedContactRequest, changedFusionContact, newContactUserPrx, newContactListVersion);
            }
            if (changedFusionContact) {
                if (oldContactUserPrx != null) {
                    oldContactUserPrx.oldUserContactUpdated(contactData.username);
                }
                if (newContactUserPrx != null && newContactAlreadyOnContactList) {
                    newContactUserPrx.newUserContactUpdated(contactData.username, acceptedContactRequest);
                }
            }
            contactData.assignPresence(presence);
            Object var23_26 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                // empty catch block
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                // empty catch block
            }
            try {
                if (connMaster != null) {
                    connMaster.close();
                }
            }
            catch (SQLException e2) {
                // empty catch block
            }
            try {
                if (connSlave != null) {
                    connSlave.close();
                }
                break block60;
            }
            catch (SQLException e2) {}
            break block60;
            {
                catch (LocalException e) {
                    log.error((Object)"failed to update objectcache with updated contact details", (Throwable)e);
                    Object var23_27 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    try {
                        if (connMaster != null) {
                            connMaster.close();
                        }
                    }
                    catch (SQLException e2) {
                        // empty catch block
                    }
                    try {
                        if (connSlave != null) {
                            connSlave.close();
                        }
                        break block60;
                    }
                    catch (SQLException e2) {}
                    break block60;
                }
                catch (Exception e) {
                    log.error((Object)"Failed to update contact", (Throwable)e);
                    try {
                        userTransaction.rollback();
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var23_28 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                try {
                    if (connMaster != null) {
                        connMaster.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                try {
                    if (connSlave != null) {
                        connSlave.close();
                    }
                }
                catch (SQLException e2) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        return contactData;
    }

    /*
     * Loose catch block
     */
    public void unblockContact(String username, String allowUsername, boolean shareMobilePhone) throws EJBException {
        block18: {
            UserTransaction userTransaction;
            UserPrx userPrx;
            Connection conn;
            block17: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("user [" + username + "] unblocking [" + allowUsername + "]"));
                }
                conn = null;
                userPrx = EJBIcePrxFinder.findUserPrx(username);
                UserPrx allowUserPrx = EJBIcePrxFinder.findUserPrx(allowUsername);
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                conn = this.dataSourceMaster.getConnection();
                this.removeFromBlockList(username, allowUsername, conn);
                if (shareMobilePhone) {
                    this.updateContactsMobilePhone(allowUsername, username, conn);
                }
                userTransaction.commit();
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.BLOCK_LIST, username);
                if (userPrx != null) {
                    userPrx.unblockUser(allowUsername);
                }
                if (allowUserPrx == null || userPrx == null) break block17;
                allowUserPrx.contactChangedPresenceOneWay(ImType.FUSION.value(), username, userPrx.getOverallFusionPresence(allowUsername));
            }
            Object var11_8 = null;
            try {
                if (conn != null) {
                    conn.close();
                }
                break block18;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block18;
            {
                catch (LocalException e) {
                    log.error((Object)("failed to update userproxy [" + userPrx + "] in objectcache"), (Throwable)e);
                    Object var11_9 = null;
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block18;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block18;
                }
                catch (Exception e) {
                    log.error((Object)"Failed to unblock contact", (Throwable)e);
                    try {
                        userTransaction.rollback();
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_10 = null;
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void blockContact(int userID, String username, String blockUsername) throws EJBException {
        block21: {
            UserTransaction userTransaction;
            Connection conn;
            block20: {
                try {
                    UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                    int blockedUserId = userEJB.getUserID(blockUsername, null);
                }
                catch (Exception excep) {
                    throw new EJBException("Invalid username specified");
                }
                conn = null;
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
                UserPrx blockUserPrx = EJBIcePrxFinder.findUserPrx(blockUsername);
                userTransaction = this.context.getUserTransaction();
                Boolean usernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(username);
                Boolean blockUsernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(blockUsername);
                userTransaction.begin();
                conn = this.dataSourceMaster.getConnection();
                boolean removed = this.removeFusionContact(username, blockUsername, conn);
                this.removeFromPendingContacts(username, blockUsername, conn);
                this.persistBlockListEntry(username, blockUsername, conn);
                if (removed) {
                    if (usernameBCLHasBeenPersisted.booleanValue()) {
                        this.removeFromBroadcastList(username, blockUsername, conn);
                    }
                    this.removeFromBroadcastListInMemCached(username, blockUsername);
                }
                if (blockUsernameBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(blockUsername, username, conn);
                }
                this.removeFromBroadcastListInMemCached(blockUsername, username);
                int newContactListVersion = this.onContactListModified(userID, username, conn);
                userTransaction.commit();
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.BLOCK_LIST, username);
                contactDeletedLog.info((Object)(username + " blocked " + blockUsername));
                if (userPrx != null) {
                    userPrx.blockUser(blockUsername, newContactListVersion);
                }
                if (blockUserPrx == null) break block20;
                blockUserPrx.stopBroadcastingTo(username);
            }
            Object var13_17 = null;
            try {
                if (conn != null) {
                    conn.close();
                }
                break block21;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block21;
            {
                catch (LocalException e) {
                    Object var13_18 = null;
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block21;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block21;
                }
                catch (Exception e) {
                    log.error((Object)"Failed to block contact", (Throwable)e);
                    try {
                        userTransaction.rollback();
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var13_19 = null;
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void rejectContactRequest(int inviteeUserID, String inviteeUsername, String inviterUsername) throws EJBException {
        block20: {
            UserTransaction userTransaction;
            Connection conn;
            block19: {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("[" + inviteeUsername + "] rejecting contact request from user [" + inviterUsername + "]"));
                }
                conn = null;
                UserPrx inviterPrx = EJBIcePrxFinder.findUserPrx(inviterUsername);
                UserPrx inviteePrx = EJBIcePrxFinder.findUserPrx(inviteeUsername);
                userTransaction = this.context.getUserTransaction();
                Boolean usernameBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(inviteeUsername);
                Boolean inviterBCLHasBeenPersisted = ContactBean.isBroadcastListPersisted(inviterUsername);
                userTransaction.begin();
                conn = this.dataSourceMaster.getConnection();
                boolean removed = this.removeFusionContact(inviterUsername, inviteeUsername, conn);
                this.removeFromPendingContacts(inviteeUsername, inviterUsername, conn);
                if (removed) {
                    if (usernameBCLHasBeenPersisted.booleanValue()) {
                        this.removeFromBroadcastList(inviteeUsername, inviterUsername, conn);
                    }
                    this.removeFromBroadcastListInMemCached(inviteeUsername, inviterUsername);
                }
                if (inviterBCLHasBeenPersisted.booleanValue()) {
                    this.removeFromBroadcastList(inviterUsername, inviteeUsername, conn);
                }
                this.removeFromBroadcastListInMemCached(inviterUsername, inviteeUsername);
                UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
                int inviterUserID = userEJB.getUserID(inviterUsername, conn);
                int newContactListVersion = this.onContactListModified(inviterUserID, inviterUsername, conn);
                userTransaction.commit();
                contactDeletedLog.info((Object)(inviteeUsername + " rejected " + inviterUsername));
                this.removeFriendInviteAndNewFollowerNotifications(inviteeUserID, inviterUsername, inviterUserID);
                if (inviterPrx != null) {
                    inviterPrx.contactRequestWasRejected(inviteeUsername, newContactListVersion);
                }
                if (inviteePrx == null) break block19;
                inviteePrx.rejectContactRequest(inviterUsername);
            }
            Object var15_17 = null;
            try {
                if (conn != null) {
                    conn.close();
                }
                break block20;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block20;
            {
                catch (LocalException e) {
                    Object var15_18 = null;
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block20;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block20;
                }
                catch (Exception e) {
                    log.error((Object)"Failed to block contact", (Throwable)e);
                    try {
                        userTransaction.rollback();
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var15_19 = null;
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    private void updateContactListInMemCached(String username, Set<ContactData> contacts) {
        if (contactListMemcache != null && contacts != null) {
            Calendar now = Calendar.getInstance();
            now.add(6, 5);
            contactListMemcache.set(MemCachedUtils.getCacheKeyInNamespace(CONTACT_LIST_NAMESPACE, username), contacts, now.getTime());
        }
    }

    /*
     * Loose catch block
     */
    public int getContactListVersion(int userID, Connection conn) throws EJBException {
        ConnectionHolder ch = null;
        Statement ps = null;
        ResultSet rs = null;
        Integer version = MemCachedClientWrapper.getInt(MemCachedKeySpaces.CommonKeySpace.CONTACT_LIST_VERSION, String.valueOf(userID));
        if (version == null) {
            ch = new ConnectionHolder(this.dataSourceMaster, conn);
            ps = ch.getConnection().prepareStatement("select version from contactlistversion where userid = ?");
            ps.setInt(1, userID);
            rs = ps.executeQuery();
            version = rs.next() ? rs.getInt("version") : 0;
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CONTACT_LIST_VERSION, String.valueOf(userID), version);
        }
        int n = version;
        Object var9_9 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (ch != null) {
                ch.close();
            }
        }
        catch (SQLException e2) {
            ch = null;
        }
        return n;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var9_10 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (ch != null) {
                        ch.close();
                    }
                }
                catch (SQLException e2) {
                    ch = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public Set<ContactData> getContactList(String username) throws EJBException {
        Set<ContactData> contacts;
        ResultSet rs;
        Statement ps;
        Connection conn;
        block28: {
            conn = null;
            ps = null;
            rs = null;
            conn = this.dataSourceSlave.getConnection();
            contacts = ContactList.getContactList(contactListMemcache, username);
            if (contacts == null) break block28;
            log.debug((Object)("contact list cache HIT for user [" + username + "] contacts [" + contacts.size() + "]"));
            this.assignDisplayPictureAndStatusMessageToContacts(conn, contacts);
            Set<ContactData> set = contacts;
            Object var8_9 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e2) {
                conn = null;
            }
            return set;
        }
        log.debug((Object)("contact list cache MISS for user [" + username + "] or memcache is disabled"));
        ps = conn.prepareStatement("select * from contact where username = ?");
        ps.setString(1, username);
        rs = ps.executeQuery();
        contacts = new HashSet<ContactData>();
        while (rs.next()) {
            contacts.add(new ContactData(rs));
        }
        if (contacts != null) {
            this.assignDisplayPictureAndStatusMessageToContacts(conn, contacts);
            this.updateContactListInMemCached(username, contacts);
        }
        Set<ContactData> set = contacts;
        Object var8_10 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return set;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_11 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public Set<String> getPendingContacts(String username) throws Exception {
        HashSet<String> pendingContacts;
        block12: {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            pendingContacts = new HashSet<String>();
            if (log.isDebugEnabled()) {
                log.debug((Object)("Retrieving pending contacts for " + username));
            }
            conn = this.dataSourceSlave.getConnection();
            ConnectionHolder ch = new ConnectionHolder(this.dataSourceMaster, conn);
            ps = ch.getConnection().prepareStatement("SELECT pendingContact FROM pendingcontact WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            while (rs.next()) {
                pendingContacts.add(rs.getString("pendingContact"));
            }
            Object var8_8 = null;
            try {
                if (conn != null) {
                    conn.close();
                }
                break block12;
            }
            catch (SQLException e) {
                conn = null;
            }
            break block12;
            {
                catch (Exception e) {
                    log.error((Object)("Unable to retrieve pending contact list - " + e.getMessage()));
                    throw new Exception("Unable to retrieve pending contact list.");
                }
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e) {
                    conn = null;
                }
                throw throwable;
            }
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Returning " + pendingContacts.size() + " pending contacts for " + username));
        }
        return pendingContacts;
    }

    /*
     * Loose catch block
     */
    public ContactGroupData addGroup(int userID, ContactGroupData groupData, boolean notifyUserObject) throws EJBException {
        block37: {
            UserTransaction userTransaction;
            ResultSet rs;
            Statement ps;
            Connection conn;
            block34: {
                conn = null;
                ps = null;
                rs = null;
                UserPrx userPrx = null;
                userTransaction = null;
                if (groupData.name == null || groupData.name.trim().length() == 0) {
                    throw new EJBException("A group name was not specified");
                }
                if (notifyUserObject) {
                    userPrx = EJBIcePrxFinder.findUserPrx(groupData.username);
                }
                if (!AuthenticatedAccessControl.hasAccessByUsernameLocal(AuthenticatedAccessControlTypeEnum.ADD_CONTACT_GROUP, groupData.username) && SystemProperty.getBool("AddContactDisabledForUnauthenticatedUsers", false)) {
                    throw new EJBException("You must be authenticated before you can create a contact group.");
                }
                conn = this.dataSourceMaster.getConnection();
                groupData.name = groupData.name.trim();
                ps = conn.prepareStatement("select id from contactgroup where username=? and name=?");
                ps.setString(1, groupData.username);
                ps.setString(2, groupData.name);
                rs = ps.executeQuery();
                if (rs.next()) {
                    throw new EJBException("Group " + groupData.name + " already exist");
                }
                rs.close();
                ps.close();
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                ps = conn.prepareStatement("insert into contactgroup (username, name) values (?,?)", 1);
                ps.setString(1, groupData.username);
                ps.setString(2, groupData.name);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new EJBException("Failed to add a new contact group to database");
                }
                groupData.id = rs.getInt(1);
                int newContactListVersion = this.updateContactListVersion(userID, conn);
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CONTACT_GROUP, groupData.username);
                userTransaction.commit();
                if (!notifyUserObject || userPrx == null) break block34;
                userPrx.contactGroupDetailChanged(groupData.toIceObject(), newContactListVersion);
            }
            Object var12_12 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block37;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block37;
            {
                catch (LocalException e) {
                    Object var12_13 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block37;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block37;
                }
                catch (Exception e) {
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_14 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
        return groupData;
    }

    /*
     * Loose catch block
     */
    public void removeGroup(int userID, String username, int groupID) throws EJBException {
        block26: {
            UserTransaction userTransaction;
            Statement ps;
            Connection conn;
            block24: {
                conn = null;
                ps = null;
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(username);
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("update contact set contactgroupid = null where contactgroupid = ?");
                ps.setInt(1, groupID);
                if (ps.executeUpdate() > 0) {
                    throw new Exception("The group is not empty (it may contain hidden contacts).");
                }
                ps.close();
                ps = conn.prepareStatement("delete from contactgroup where id = ? and username = ?");
                ps.setInt(1, groupID);
                ps.setString(2, username);
                if (ps.executeUpdate() != 1) {
                    throw new Exception("Failed to remove group ID " + groupID + " for " + username);
                }
                ps.close();
                int newContactListVersion = this.updateContactListVersion(userID, conn);
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CONTACT_GROUP, username);
                userTransaction.commit();
                if (userPrx == null) break block24;
                userPrx.contactGroupDeleted(groupID, newContactListVersion);
            }
            Object var11_11 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block26;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block26;
            {
                catch (LocalException e) {
                    Object var11_12 = null;
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block26;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block26;
                }
                catch (Exception e) {
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_13 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void updateGroupDetail(int userID, ContactGroupData groupData) throws EJBException {
        block35: {
            UserTransaction userTransaction;
            ResultSet rs;
            Statement ps;
            Connection conn;
            block32: {
                conn = null;
                ps = null;
                rs = null;
                if (groupData.name == null || groupData.name.trim().length() == 0) {
                    throw new EJBException("A group name was not specified");
                }
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(groupData.username);
                userTransaction = this.context.getUserTransaction();
                groupData.name = groupData.name.trim();
                userTransaction.begin();
                conn = this.dataSourceMaster.getConnection();
                ps = conn.prepareStatement("select id from contactgroup where username=? and name=? and id<>?");
                ps.setString(1, groupData.username);
                ps.setString(2, groupData.name);
                ps.setObject(3, groupData.id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    throw new EJBException("Group " + groupData.name + " already exist");
                }
                rs.close();
                ps.close();
                ps = conn.prepareStatement("update contactgroup set name = ? where id = ? and username = ?");
                ps.setString(1, groupData.name);
                ps.setObject(2, groupData.id);
                ps.setString(3, groupData.username);
                int rowsUpdated = ps.executeUpdate();
                if (rowsUpdated != 1) {
                    throw new EJBException("Failed to update group detail");
                }
                int newContactListVersion = this.updateContactListVersion(userID, conn);
                MemCachedClientWrapper.delete(MemCachedKeySpaces.CommonKeySpace.CONTACT_GROUP, groupData.username);
                userTransaction.commit();
                if (userPrx == null) break block32;
                userPrx.contactGroupDetailChanged(groupData.toIceObject(), newContactListVersion);
            }
            Object var11_13 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e2) {
                rs = null;
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block35;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block35;
            {
                catch (LocalException e) {
                    Object var11_14 = null;
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    }
                    catch (SQLException e2) {
                        rs = null;
                    }
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block35;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block35;
                }
                catch (Exception e) {
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var11_15 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public List<ContactGroupData> getGroupList(String username) throws EJBException {
        Connection conn = null;
        Statement ps = null;
        ResultSet rs = null;
        LinkedList<ContactGroupData> groupList = (LinkedList<ContactGroupData>)MemCachedClientWrapper.get(MemCachedKeySpaces.CommonKeySpace.CONTACT_GROUP, username);
        if (groupList == null) {
            conn = this.dataSourceSlave.getConnection();
            ps = conn.prepareStatement("select * from contactgroup where username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();
            groupList = new LinkedList<ContactGroupData>();
            while (rs.next()) {
                groupList.add(new ContactGroupData(rs));
            }
            MemCachedClientWrapper.set(MemCachedKeySpaces.CommonKeySpace.CONTACT_GROUP, username, groupList);
        }
        LinkedList<ContactGroupData> linkedList = groupList;
        Object var8_8 = null;
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e2) {
            rs = null;
        }
        try {
            if (ps != null) {
                ps.close();
            }
        }
        catch (SQLException e2) {
            ps = null;
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e2) {
            conn = null;
        }
        return linkedList;
        catch (SQLException e) {
            try {
                throw new EJBException(e.getMessage());
            }
            catch (Throwable throwable) {
                Object var8_9 = null;
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }
                catch (SQLException e2) {
                    rs = null;
                }
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    /*
     * Loose catch block
     */
    public void moveContactToGroup(int userID, String username, int contactID, Integer groupID) throws EJBException {
        block29: {
            UserTransaction userTransaction;
            Statement ps;
            Connection conn;
            block27: {
                conn = null;
                ps = null;
                userTransaction = null;
                conn = this.dataSourceMaster.getConnection();
                ContactData contact = this.getContact(contactID);
                if (contact == null) {
                    throw new EJBException("Invalid contact ID " + contactID);
                }
                if (!contact.username.equalsIgnoreCase(username)) {
                    throw new EJBException("Contact ID " + contactID + " does not belong to " + username);
                }
                UserPrx userPrx = EJBIcePrxFinder.findUserPrx(contact.username);
                userTransaction = this.context.getUserTransaction();
                userTransaction.begin();
                contact.contactGroupId = groupID;
                if (groupID == null || groupID == -1 || groupID == -2 || groupID == -3 || groupID == -4 || groupID == -5 || groupID == -6) {
                    ps = conn.prepareStatement("update contact set contactgroupid = null where id = ?");
                    ps.setInt(1, contactID);
                } else {
                    ps = conn.prepareStatement("update contact set contactgroupid = ? where exists (select * from contactgroup where id = ? and username = contact.username) and id = ?");
                    ps.setInt(1, groupID);
                    ps.setInt(2, groupID);
                    ps.setInt(3, contactID);
                }
                if (ps.executeUpdate() != 1) {
                    throw new EJBException("Failed to add contact " + contactID + " to group " + groupID);
                }
                int newContactListVersion = this.onContactListModified(userID, username, conn);
                userTransaction.commit();
                if (userPrx == null) break block27;
                userPrx.contactDetailChanged(contact.toIceObject(), newContactListVersion);
            }
            Object var12_14 = null;
            try {
                if (ps != null) {
                    ps.close();
                }
            }
            catch (SQLException e2) {
                ps = null;
            }
            try {
                if (conn != null) {
                    conn.close();
                }
                break block29;
            }
            catch (SQLException e2) {
                conn = null;
            }
            break block29;
            {
                catch (LocalException e) {
                    Object var12_15 = null;
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (SQLException e2) {
                        ps = null;
                    }
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                        break block29;
                    }
                    catch (SQLException e2) {
                        conn = null;
                    }
                    break block29;
                }
                catch (Exception e) {
                    try {
                        if (userTransaction != null) {
                            userTransaction.rollback();
                        }
                    }
                    catch (Exception ie) {
                        // empty catch block
                    }
                    throw new EJBException(e.getMessage());
                }
            }
            catch (Throwable throwable) {
                Object var12_16 = null;
                try {
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (SQLException e2) {
                    ps = null;
                }
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (SQLException e2) {
                    conn = null;
                }
                throw throwable;
            }
        }
    }

    private int onContactListModified(int userID, String username, Connection conn) throws SQLException {
        int newContactListVersion = this.updateContactListVersion(userID, conn);
        if (contactListMemcache != null) {
            contactListMemcache.delete(MemCachedUtils.getCacheKeyInNamespace(CONTACT_LIST_NAMESPACE, username));
        }
        return newContactListVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int removeFromStringList(String username, String qualifyingUsername, String tableName, String fieldName, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("delete from " + tableName + " where username = ? and " + fieldName + " = ?");
        int rowCount = 0;
        try {
            ps.setString(1, username);
            ps.setString(2, qualifyingUsername);
            rowCount = ps.executeUpdate();
            Object var9_8 = null;
        }
        catch (Throwable throwable) {
            Object var9_9 = null;
            ps.close();
            throw throwable;
        }
        ps.close();
        return rowCount;
    }

    private boolean removeFromBlockList(String username, String blockedUsername, Connection conn) throws SQLException {
        return this.removeFromStringList(username, blockedUsername, "blocklist", "blockUsername", conn) > 0;
    }

    private boolean removeFromPendingContacts(String username, String pendingContact, Connection conn) throws SQLException, CreateException {
        if (StringUtil.isBlank(pendingContact) || StringUtil.isBlank(username)) {
            log.warn((Object)String.format("Unable to remove pending contacts. Both user and pendingcontact must not be null. user[%s] pendingcontact[%s]", username, pendingContact));
            return false;
        }
        boolean ret = this.removeFromStringList(username, pendingContact, "pendingcontact", "pendingContact", conn) > 0;
        try {
            UserLocal userEJB = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            int userId = userEJB.getUserID(username, conn);
            int pendingContactUserID = userEJB.getUserID(pendingContact, conn);
            this.removeFriendInviteAndNewFollowerNotifications(userId, pendingContact, pendingContactUserID);
        }
        catch (Exception e) {
            log.warn((Object)String.format("Unable to remove notifications for user[%s] pendingcontact[%s] : %s", username, pendingContact, e.getMessage()));
        }
        return ret;
    }

    private boolean removeFromBroadcastList(String username, String broadcastUsername, Connection conn) throws SQLException {
        boolean persisted = this.removeFromStringList(username, broadcastUsername, "broadcastlist", "broadcastUsername", conn) > 0;
        return persisted;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean removeFromBroadcastListInMemCached(String username, String broadcastUsername) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("removing contact [" + broadcastUsername + "] from the broadcast list for user [" + username + "] in memcached"));
        }
        boolean removed = false;
        try {
            if (MemCachedUtils.getLock(broadcastListMemcache, "BLDL", username, 15000)) {
                Set<String> bcl = BroadcastList.getBroadcastList(broadcastListMemcache, username);
                if (bcl != null && bcl.remove(broadcastUsername)) {
                    removed = BroadcastList.setBroadcastList(broadcastListMemcache, username, bcl);
                }
            } else {
                log.error((Object)("Failed to get a lock to update user [" + username + "]'s BCL in 10 seconds"));
                BroadcastList.deleteBroadcastList(broadcastListMemcache, username);
            }
            Object var6_5 = null;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
            throw throwable;
        }
        MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
        return removed;
    }

    private int persistStringListEntry(String username, String qualifyingUsername, String tableName, String fieldName, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("select * from " + tableName + " where username = ? and " + fieldName + " = ?");
        ps.setString(1, username);
        ps.setString(2, qualifyingUsername);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) {
            ps.close();
            ps = conn.prepareStatement("insert into " + tableName + " (username, " + fieldName + ") values (?,?)");
            ps.setString(1, username);
            ps.setString(2, qualifyingUsername);
            int result = ps.executeUpdate();
            return result;
        }
        ps.close();
        return 0;
    }

    private boolean persistBroadcastListEntry(String username, String broadcastUsername, Connection conn) throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("persisting broadcast entry for user [" + username + "] and broadcastUsername [" + broadcastUsername + "]"));
        }
        boolean persisted = this.persistStringListEntry(username, broadcastUsername, "broadcastlist", "broadcastUsername", conn) > 0;
        return persisted;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean updateBroadcastListEntryInMemCached(String username, String broadcastUsername) {
        if (log.isDebugEnabled()) {
            log.debug((Object)("adding contact [" + broadcastUsername + "] to the broadcast list for user [" + username + "] in memcached"));
        }
        boolean added = false;
        try {
            if (MemCachedUtils.getLock(broadcastListMemcache, "BLDL", username, 15000)) {
                Set<String> bcl = BroadcastList.getBroadcastList(broadcastListMemcache, username);
                if (bcl != null) {
                    bcl.add(broadcastUsername);
                    added = BroadcastList.setBroadcastList(broadcastListMemcache, username, bcl);
                }
            } else {
                log.error((Object)("Failed to get a lock to update user [" + username + "]'s BCL in 10 seconds"));
                BroadcastList.deleteBroadcastList(broadcastListMemcache, username);
            }
            Object var6_5 = null;
        }
        catch (Throwable throwable) {
            Object var6_6 = null;
            MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
            throw throwable;
        }
        MemCachedUtils.releaseLock(broadcastListMemcache, "BLDL", username);
        return added;
    }

    private boolean persistBlockListEntry(String username, String blockedUsername, Connection conn) throws SQLException {
        return this.persistStringListEntry(username, blockedUsername, "blocklist", "blockusername", conn) > 0;
    }

    private boolean removeContact(int contactId, Connection conn) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("delete from contact where id = ?");
        ps.setInt(1, contactId);
        return ps.executeUpdate() >= 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean removeFusionContact(String username, String contactUsername, Connection conn) throws SQLException {
        boolean bl;
        PreparedStatement psGetIDs = null;
        Statement psDeleteRow = null;
        ResultSet rs = null;
        boolean rowsRemoved = false;
        Connection slaveConn = null;
        try {
            slaveConn = this.dataSourceSlave.getConnection();
            psGetIDs = slaveConn.prepareStatement("select id from contact where username = ? and fusionUsername = ?");
            psGetIDs.setString(1, username);
            psGetIDs.setString(2, contactUsername);
            rs = psGetIDs.executeQuery();
            psDeleteRow = conn.prepareStatement("delete from contact where id = ?");
            while (rs.next()) {
                psDeleteRow.setInt(1, rs.getInt(1));
                if (psDeleteRow.executeUpdate() < 1) continue;
                rowsRemoved = true;
            }
            bl = rowsRemoved;
            Object var11_10 = null;
        }
        catch (Throwable throwable) {
            Object var11_11 = null;
            try {
                if (rs != null) {
                    rs.close();
                }
            }
            catch (SQLException e) {
                // empty catch block
            }
            try {
                if (psGetIDs != null) {
                    psGetIDs.close();
                }
            }
            catch (SQLException e) {
                // empty catch block
            }
            try {
                if (psDeleteRow != null) {
                    psDeleteRow.close();
                }
            }
            catch (SQLException e) {
                // empty catch block
            }
            try {
                if (slaveConn != null) {
                    slaveConn.close();
                }
            }
            catch (SQLException e) {}
            throw throwable;
        }
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            if (psGetIDs != null) {
                psGetIDs.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            if (psDeleteRow != null) {
                psDeleteRow.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        try {
            if (slaveConn != null) {
                slaveConn.close();
            }
        }
        catch (SQLException e) {
            // empty catch block
        }
        return bl;
    }

    private static Boolean isBroadcastListPersisted(String username) {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.BCLPERSISTED_ENABLED)) {
            Boolean persisted = bclPersistedMemcache.keyExists(username);
            if (!persisted.booleanValue()) {
                log.info((Object)("found unpersisted bcl for user : " + username));
            }
            return persisted;
        }
        return true;
    }
}

