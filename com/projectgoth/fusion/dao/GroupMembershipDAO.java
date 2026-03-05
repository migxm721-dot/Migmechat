/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.dao;

import com.projectgoth.fusion.domain.UsernameAndMobileNumber;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface GroupMembershipDAO {
    public List<String> getGroupMemberUsernamesForEmailNotification(int var1);

    public List<UsernameAndMobileNumber> getGroupMemberUsernamesAndMobileNumbersForSMSNotification(int var1);

    public List<UsernameAndMobileNumber> getGroupMemberUsernamesAndMobileNumbersForGroupEventSMSNotification(int var1);

    public List<String> getGroupMemberUsernamesForEventNotification(int var1);

    public List<String> getGroupMemberUsernamesForGroupEventNotification(int var1);

    public List<String> getGroupMemberUsernamesForNewGroupUserPostNotificationViaEmail(int var1);

    public List<String> getGroupMemberUsernamesForNewGroupUserPostNotificationViaEventSystem(int var1);
}

