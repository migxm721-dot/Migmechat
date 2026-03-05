/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.EJBObject
 */
package com.projectgoth.fusion.interfaces;

import com.projectgoth.fusion.data.ContactData;
import com.projectgoth.fusion.data.ContactGroupData;
import com.projectgoth.fusion.ejb.FusionEJBException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.ejb.EJBObject;

public interface Contact
extends EJBObject {
    public ContactData getContact(int var1) throws RemoteException;

    public ContactData getContact(String var1, String var2) throws RemoteException;

    public boolean isFriend(String var1, String var2) throws FusionEJBException, RemoteException;

    public boolean isBlocking(String var1, String var2) throws FusionEJBException, RemoteException;

    public boolean isFriend(int var1, int var2) throws FusionEJBException, RemoteException;

    public ContactGroupData getGroup(int var1) throws RemoteException;

    public void assignDisplayPictureAndStatusMessageToContacts(Connection var1, Collection var2) throws SQLException, RemoteException;

    public ContactData addFusionUserAsContact(int var1, ContactData var2, boolean var3) throws FusionEJBException, RemoteException;

    public Set getRecentFollowers(int var1) throws RemoteException;

    public void removeFusionUserFromContact(int var1, String var2, int var3, boolean var4) throws FusionEJBException, RemoteException;

    public ContactData addPendingFusionContact(int var1, ContactData var2) throws RemoteException;

    public ContactData acceptContactRequest(int var1, ContactData var2, boolean var3) throws RemoteException;

    public Set checkAndPopulateBCL(String var1, Connection var2) throws RemoteException;

    public ContactData addIMContact(int var1, ContactData var2, boolean var3) throws RemoteException;

    public ContactData addPhoneContact(int var1, ContactData var2) throws RemoteException;

    public void removeContact(int var1, String var2, int var3) throws RemoteException;

    public ContactData updateContactDetail(int var1, ContactData var2) throws RemoteException;

    public void unblockContact(String var1, String var2, boolean var3) throws RemoteException;

    public void blockContact(int var1, String var2, String var3) throws RemoteException;

    public void rejectContactRequest(int var1, String var2, String var3) throws RemoteException;

    public int getContactListVersion(int var1, Connection var2) throws RemoteException;

    public Set getContactList(String var1) throws RemoteException;

    public Set getPendingContacts(String var1) throws Exception, RemoteException;

    public ContactGroupData addGroup(int var1, ContactGroupData var2, boolean var3) throws RemoteException;

    public void removeGroup(int var1, String var2, int var3) throws RemoteException;

    public void updateGroupDetail(int var1, ContactGroupData var2) throws RemoteException;

    public List getGroupList(String var1) throws RemoteException;

    public void moveContactToGroup(int var1, String var2, int var3, Integer var4) throws RemoteException;
}

