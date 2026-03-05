/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBException
 *  javax.ejb.EJBLocalObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;

public interface ContactLocal
extends EJBLocalObject {
    public ContactData getContact(int var1) throws EJBException;

    public ContactData getContact(String var1, String var2) throws EJBException;

    public boolean isFriend(String var1, String var2) throws FusionEJBException;

    public boolean isBlocking(String var1, String var2) throws FusionEJBException;

    public boolean isFriend(int var1, int var2) throws FusionEJBException;

    public ContactGroupData getGroup(int var1) throws EJBException;

    public void assignDisplayPictureAndStatusMessageToContacts(Connection var1, Collection var2) throws SQLException;

    public ContactData addFusionUserAsContact(int var1, ContactData var2, boolean var3) throws EJBException, FusionEJBException;

    public Set getRecentFollowers(int var1) throws EJBException;

    public void removeFusionUserFromContact(int var1, String var2, int var3, boolean var4) throws EJBException, FusionEJBException;

    public ContactData addPendingFusionContact(int var1, ContactData var2) throws EJBException;

    public ContactData acceptContactRequest(int var1, ContactData var2, boolean var3) throws EJBException;

    public void makeReferrerAndReferreeFriends(int var1, String var2, String var3, int var4, String var5, String var6, String var7) throws EJBException;

    public Set checkAndPopulateBCL(String var1, Connection var2) throws EJBException;

    public void persistBroadcastList(String var1, Set var2, Connection var3) throws SQLException;

    public ContactData addPhoneContact(int var1, ContactData var2) throws EJBException;

    public void removeContact(int var1, String var2, int var3) throws EJBException;

    public ContactData updateContactDetail(int var1, ContactData var2) throws EJBException;

    public void unblockContact(String var1, String var2, boolean var3) throws EJBException;

    public void blockContact(int var1, String var2, String var3) throws EJBException;

    public void rejectContactRequest(int var1, String var2, String var3) throws EJBException;

    public Set getContactList(String var1) throws EJBException;

    public Set getPendingContacts(String var1) throws Exception;

    public ContactGroupData addGroup(int var1, ContactGroupData var2, boolean var3) throws EJBException;

    public void updateGroupDetail(int var1, ContactGroupData var2) throws EJBException;

    public List getGroupList(String var1) throws EJBException;
}

