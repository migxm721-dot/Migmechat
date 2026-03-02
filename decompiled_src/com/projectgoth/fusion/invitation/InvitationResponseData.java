/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.projectgoth.leto.common.event.invites.InviteeResponse
 *  com.projectgoth.leto.common.utils.enums.ValueToEnumMap
 */
package com.projectgoth.fusion.invitation;

import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.leto.common.event.invites.InviteeResponse;
import com.projectgoth.leto.common.utils.enums.ValueToEnumMap;
import java.io.Serializable;
import java.util.Date;

public class InvitationResponseData
implements Serializable {
    public long id;
    public int invitationId;
    public Date responseTime;
    public ResponseType responseType;
    public String username;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum ResponseType implements EnumUtils.IEnumValueGetter<Integer>
    {
        SIGN_UP_UNVERIFIED(InviteeResponse.SIGN_UP_UNVERIFIED),
        SIGN_UP_VERIFIED(InviteeResponse.SIGN_UP_VERIFIED),
        ACCEPT_INVITATION(InviteeResponse.ACCEPT_INVITATION),
        REJECT_INVITATION(InviteeResponse.REJECT_INVITATION),
        LOGIN_USING_EXISTING_ACCOUNT(InviteeResponse.LOGIN_USING_EXISTING_ACCOUNT),
        INVALIDATE(InviteeResponse.INVALIDATE);

        private InviteeResponse inviteeResponseType;

        private ResponseType(InviteeResponse inviteeResponseType) {
            this.inviteeResponseType = inviteeResponseType;
        }

        public Integer getEnumValue() {
            return this.inviteeResponseType.getEnumValue();
        }

        public int getTypeCode() {
            return this.getEnumValue();
        }

        public InviteeResponse toInviteeResponseType() {
            return this.inviteeResponseType;
        }

        public static ResponseType fromTypeCode(int typeCode) {
            return (ResponseType)ValueToEnumMapInstance.INSTANCE.toEnum((Object)typeCode);
        }

        private static final class ValueToEnumMapInstance {
            private static final ValueToEnumMap<Integer, ResponseType> INSTANCE = new ValueToEnumMap(ResponseType.class);

            private ValueToEnumMapInstance() {
            }
        }
    }
}

