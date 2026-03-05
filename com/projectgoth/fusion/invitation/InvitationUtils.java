/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.invitation;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.EnumUtils;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationResponseData;
import com.projectgoth.fusion.invitation.restapi.data.SendingInvitationData;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class InvitationUtils {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(InvitationUtils.class));
    private static final IvParameterSpec IVSPEC = new IvParameterSpec(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8});
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
    private static final String KEY_SPEC_ALGO = "AES";
    private static int MAX_REFERRAL_CODE_LENGTH = 32;
    public static final List<String> MIGBO_DESTINATION = Arrays.asList("migbo");

    public static void sendInvitationEmails(int userId, Map<String, Integer> emailIdToInvitationIdMap, SendingInvitationData data, InvitationData.ActivityType activityType) {
        try {
            String pathPrefix = String.format("/user/%d/referral?method=@email&confirmToInvitationEngine=%s", userId, SystemProperty.getBool("ReferralInvitationEnabled", true));
            JSONObject postData = new JSONObject();
            JSONObject emailReferralIds = new JSONObject();
            for (String email : emailIdToInvitationIdMap.keySet()) {
                emailReferralIds.put(email, (Object)InvitationUtils.encryptReferralInvitation(emailIdToInvitationIdMap.get(email)));
            }
            postData.put("destinations", (Object)emailReferralIds);
            if (activityType == InvitationData.ActivityType.JOIN_MIG33) {
                postData.put("invitationEmailType", 6);
            } else if (activityType == InvitationData.ActivityType.PLAY_A_GAME) {
                postData.put("invitationEmailType", 8);
                postData.put("thirdPartyAppID", (Object)data.invitationMetadata.gameId);
            } else if (activityType == InvitationData.ActivityType.GAME_HELP) {
                postData.put("invitationEmailType", 9);
                postData.put("thirdPartyAppID", (Object)data.invitationMetadata.gameId);
            } else if (activityType == InvitationData.ActivityType.SHARE_PROFILE) {
                postData.put("invitationEmailType", 12);
                postData.put("sharedUserID", (Object)data.invitationMetadata.sharedUserID);
            }
            MigboApiUtil apiUtil = MigboApiUtil.getInstance();
            apiUtil.postOneWay(pathPrefix, postData.toString());
        }
        catch (Exception e) {
            log.error((Object)e);
        }
    }

    private static byte[] getReferralCipherKey() {
        return HashUtils.md5(SystemProperty.get(SystemPropertyEntities.Invitation.REFERRAL_INVITATION_CIPHER_PASSPHRASE));
    }

    public static byte[] hexStringToByteArray(String s) {
        return StringUtil.hexStringToByteArray(s);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String encryptReferralInvitation(int invitationId) throws GeneralSecurityException, IOException {
        SecureRandom random = new SecureRandom();
        int randomNum = random.nextInt();
        SecretKeySpec keySpec = new SecretKeySpec(InvitationUtils.getReferralCipherKey(), KEY_SPEC_ALGO);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
        cipher.init(1, (Key)keySpec, IVSPEC);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(32);
        DataOutputStream encryptStream = new DataOutputStream(new CipherOutputStream(baos, cipher));
        try {
            encryptStream.writeInt(invitationId);
            encryptStream.writeInt(randomNum);
            Object var8_7 = null;
        }
        catch (Throwable throwable) {
            Object var8_8 = null;
            encryptStream.close();
            throw throwable;
        }
        encryptStream.close();
        byte[] cipherText = baos.toByteArray();
        if (log.isDebugEnabled()) {
            log.debug((Object)("invitationId:" + invitationId + ",encryptReferralInvitation:" + HashUtils.asHex(cipherText)));
        }
        return HashUtils.asHex(cipherText);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int decryptReferralInvitation(String encryptedStr) {
        if (StringUtil.isBlank(encryptedStr)) {
            log.error((Object)"decryptReferralInvitation:blank encrypted string");
            return -1;
        }
        if (encryptedStr.length() > MAX_REFERRAL_CODE_LENGTH) {
            log.error((Object)"decryptReferralInvitation:encrypted string too long");
            return -1;
        }
        int invitationId = -1;
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(2, (Key)new SecretKeySpec(InvitationUtils.getReferralCipherKey(), KEY_SPEC_ALGO), IVSPEC);
            ByteArrayInputStream bais = new ByteArrayInputStream(InvitationUtils.hexStringToByteArray(encryptedStr));
            DataInputStream encryptStream = new DataInputStream(new CipherInputStream(bais, cipher));
            try {
                invitationId = encryptStream.readInt();
                Object var6_8 = null;
            }
            catch (Throwable throwable) {
                Object var6_9 = null;
                encryptStream.close();
                throw throwable;
            }
            encryptStream.close();
            {
            }
        }
        catch (GeneralSecurityException e) {
            log.error((Object)"decryptReferralInvitation:Failed to decrypt ", (Throwable)e);
        }
        catch (IOException e) {
            log.error((Object)"decryptReferralInvitation:Failed to decrypt ", (Throwable)e);
        }
        catch (NumberFormatException e) {
            log.error((Object)"decryptReferralInvitation:Failed to decrypt ", (Throwable)e);
        }
        return invitationId;
    }

    public static InvitationData.StatusFieldValue getInvitationStatus(InvitationData invitationData, Date timeOfAction) {
        if (invitationData == null) {
            return InvitationData.StatusFieldValue.INVALID;
        }
        if (invitationData.status == InvitationData.StatusFieldValue.NO_RESPONSE && invitationData.expireTime != null && timeOfAction.after(invitationData.expireTime)) {
            invitationData.status = InvitationData.StatusFieldValue.EXPIRED;
        }
        return invitationData.status;
    }

    public static InvitationData.StatusFieldValue getNewInvitationStatusFieldValue(InvitationResponseData.ResponseType newResponseType, InvitationData invitationData) {
        if (invitationData.channel == InvitationData.ChannelType.EMAIL) {
            if (invitationData.type == InvitationData.ActivityType.JOIN_MIG33) {
                if (newResponseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED) {
                    return InvitationData.StatusFieldValue.CLOSED;
                }
                return null;
            }
        } else if (invitationData.channel == InvitationData.ChannelType.FB && invitationData.type == InvitationData.ActivityType.JOIN_MIG33) {
            if (newResponseType == InvitationResponseData.ResponseType.SIGN_UP_VERIFIED) {
                return InvitationData.StatusFieldValue.CLOSED;
            }
            return null;
        }
        throw new IllegalArgumentException("Unable to determine new invitation status field value when new response [" + newResponseType + "] for invitation " + invitationData);
    }

    public static void deduplicateEmails(Collection<String> emailCollectionSource, Collection<String> emailCollectionTarget) {
        HashSet<String> tmpTarget = new HashSet<String>();
        for (String emailAddr : emailCollectionSource) {
            String normalizedEmailAddr = StringUtil.isValidEmail(emailAddr) ? StringUtil.normalizeEmailAddress(emailAddr) : StringUtil.trimmedLowerCase(emailAddr);
            tmpTarget.add(normalizedEmailAddr);
        }
        emailCollectionTarget.addAll(tmpTarget);
    }

    public static void deduplicateDestinations(SendingInvitationData sendingInvitationData) {
        if (InvitationData.ChannelType.fromTypeCode(sendingInvitationData.channel) == InvitationData.ChannelType.EMAIL) {
            ArrayList<String> uniqueDestinations = new ArrayList<String>();
            InvitationUtils.deduplicateEmails(sendingInvitationData.destinations, uniqueDestinations);
            sendingInvitationData.destinations = uniqueDestinations;
        } else {
            HashSet<String> uniqueDestinations = new HashSet<String>(sendingInvitationData.destinations);
            sendingInvitationData.destinations.clear();
            sendingInvitationData.destinations.addAll(uniqueDestinations);
        }
    }

    public static boolean isInvitationEngineEnabled(InvitationData.ChannelType channel) {
        if (channel == null && (SystemProperty.getBool(SystemPropertyEntities.Invitation.INTERNAL_INVITATION_ENABLED) || SystemProperty.getBool(SystemPropertyEntities.Invitation.REFERRAL_INVITATION_ENABLED) || SystemProperty.getBool(SystemPropertyEntities.Invitation.FACEBOOK_INVITATION_ENABLED))) {
            return true;
        }
        return InvitationData.ChannelType.EMAIL == channel && SystemProperty.getBool(SystemPropertyEntities.Invitation.REFERRAL_INVITATION_ENABLED) || InvitationData.ChannelType.FB == channel && SystemProperty.getBool(SystemPropertyEntities.Invitation.FACEBOOK_INVITATION_ENABLED) || InvitationData.ChannelType.INTERNAL == channel && SystemProperty.getBool(SystemPropertyEntities.Invitation.INTERNAL_INVITATION_ENABLED);
    }

    public static boolean isUserMobileOrEmailVerified(UserData userData) {
        return userData != null && (userData.mobileVerified != null && userData.mobileVerified != false || userData.emailVerified != null && userData.emailVerified != false);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum SendInvitationResultEnum implements EnumUtils.IEnumValueGetter<Integer>
    {
        SUCCESS_SEND_INVITATION(1),
        SEND_FOLLOWING_ME_REQUEST_TO_EXISTING_USER(2),
        NOTHING_HAPPENS_TO_HALFWAY_REGISTERED_USER(3),
        FAILED_TO_SEND_INVITATION_TO_DESTINATION(-1),
        INVALID_DESTINATION(-2);

        private static final Map<Integer, SendInvitationResultEnum> lookup;
        int value;

        private SendInvitationResultEnum(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }

        public static SendInvitationResultEnum fromValue(int v) {
            return lookup.get(v);
        }

        public Integer getEnumValue() {
            return this.value();
        }

        static {
            lookup = new HashMap<Integer, SendInvitationResultEnum>();
            EnumUtils.populateLookUpMap(lookup, SendInvitationResultEnum.class);
        }
    }
}

