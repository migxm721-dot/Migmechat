/*
 * Decompiled with CFR 0.152.
 */
package com.projectgoth.fusion.invitation;

import com.projectgoth.fusion.invitation.InvitationUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CreateInvitationsResult
extends HashMap<String, CreateInvitationDetails> {
    public Map<String, Integer> getSendInvitationStatusSummary() {
        HashMap<String, Integer> summary = new HashMap<String, Integer>();
        for (Map.Entry entry : this.entrySet()) {
            summary.put((String)entry.getKey(), ((CreateInvitationDetails)entry.getValue()).sendInvitationResult.value());
        }
        return summary;
    }

    public static class CreateInvitationDetails
    implements Serializable {
        public InvitationUtils.SendInvitationResultEnum sendInvitationResult;
        public Integer invitationID;
        public Integer existingUserID;

        public CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum resultEnum) {
            this.sendInvitationResult = resultEnum;
        }

        public CreateInvitationDetails(InvitationUtils.SendInvitationResultEnum sendInvitationResult, Integer existingUserID) {
            this.sendInvitationResult = sendInvitationResult;
            this.existingUserID = existingUserID;
        }
    }
}

