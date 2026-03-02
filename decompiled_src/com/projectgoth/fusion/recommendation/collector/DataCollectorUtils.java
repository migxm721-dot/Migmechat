/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Communicator
 *  Ice.ObjectPrx
 *  javax.ejb.CreateException
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.recommendation.collector;

import Ice.Communicator;
import Ice.ObjectPrx;
import com.projectgoth.fusion.cache.MemCacheOrEJB;
import com.projectgoth.fusion.common.AppStartupInfo;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.ErrorCause;
import com.projectgoth.fusion.common.HashUtils;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedRateLimiter;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.recommendation.collector.CollectedDataTypeEnum;
import com.projectgoth.fusion.recommendation.collector.DataCollectorContext;
import com.projectgoth.fusion.recommendation.collector.DataCollectorException;
import com.projectgoth.fusion.recommendation.collector.DataCollectorWriterStrategy;
import com.projectgoth.fusion.recommendation.collector.DataUploadRequest;
import com.projectgoth.fusion.recommendation.collector.DataUploadTicket;
import com.projectgoth.fusion.recommendation.collector.addressbook.AddressBookDataLog4JSink;
import com.projectgoth.fusion.recommendation.collector.addressbook.AddressBookRecordData;
import com.projectgoth.fusion.recommendation.collector.addressbook.CollectedAddressBookDataTransformer;
import com.projectgoth.fusion.recommendation.collector.rewardsystem.CollectedRewardProgramTriggerSummaryDataTransformer;
import com.projectgoth.fusion.recommendation.collector.rewardsystem.CollectedRewardProgramTriggerSummaryLog4jSink;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import com.projectgoth.fusion.slice.CollectedDataIce;
import com.projectgoth.fusion.slice.CollectedRewardProgramTriggerSummaryDataIce;
import com.projectgoth.fusion.slice.FusionExceptionWithRefCode;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrx;
import com.projectgoth.fusion.slice.RecommendationDataCollectionServicePrxHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.CreateException;
import org.apache.log4j.Logger;

public class DataCollectorUtils {
    public static final Integer UNSPECIFIED_DATA_TYPE = -1;
    private static final IvParameterSpec IVSPEC = new IvParameterSpec(new byte[]{91, 82, 73, 64, 55, 46, 37, 28, 11, 92, 83, 74, 65, 56, 47, 38});
    private static final String CIPHER_ALGO = "AES/CBC/PKCS5Padding";
    private static final String KEY_SPEC_ALGO = "AES";
    public static final int UPLOAD_SIGNATURE_LENGTH = 32;
    public static final int MAX_UPLOADTICKET_REF_LENGTH = 128;
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(DataCollectorUtils.class));

    public static String newErrorID() {
        UUID errorID = UUID.randomUUID();
        String errorIDStr = errorID.toString().replaceAll("-", "");
        BigInteger bigIntErrorID = new BigInteger(errorIDStr, 16);
        return "E-" + bigIntErrorID.toString(36).toUpperCase();
    }

    public static Integer getDataType(CollectedDataIce data) {
        if (data == null || data.dataType <= 0) {
            return UNSPECIFIED_DATA_TYPE;
        }
        return data.dataType;
    }

    public static RecommendationDataCollectionServicePrx getRDCSProxy(Communicator communicator) throws IllegalStateException {
        String rdcsEndPoints = SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.DATA_COLLECTION_SERVICE_ENDPOINTS);
        if (StringUtil.isBlank(rdcsEndPoints)) {
            String detailedMessage = SystemPropertyEntities.RecommendationServiceSettings.DATA_COLLECTION_SERVICE_ENDPOINTS.getName() + " not defined in the System Property";
            throw new IllegalStateException(detailedMessage);
        }
        ObjectPrx objectPrx = communicator.stringToProxy(rdcsEndPoints);
        boolean cachedConnection = SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.ENABLE_RDCS_PROXY_WITH_CACHED_CONNECTION);
        return RecommendationDataCollectionServicePrxHelper.checkedCast(objectPrx.ice_connectionCached(cachedConnection));
    }

    public static FusionExceptionWithRefCode prepareThrowingFusionExceptionWithRefCode(Logger log, String errorMessage, String additionalMessage, ErrorCause cause) {
        return DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, errorMessage, additionalMessage, cause, null);
    }

    public static FusionExceptionWithRefCode prepareThrowingFusionExceptionWithRefCode(Logger log, String errorMessage, ErrorCause cause) {
        return DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, errorMessage, null, cause, null);
    }

    public static FusionExceptionWithRefCode prepareThrowingFusionExceptionWithRefCode(Logger log, String errorMessage, ErrorCause cause, Throwable causeByException) {
        return DataCollectorUtils.prepareThrowingFusionExceptionWithRefCode(log, errorMessage, null, cause, causeByException);
    }

    public static FusionExceptionWithRefCode prepareThrowingFusionExceptionWithRefCode(Logger log, String errorMessage, String additionalLogMessage, ErrorCause errorCauseCode, Throwable cause) {
        StringBuilder logMessage = new StringBuilder();
        String errorRef = DataCollectorUtils.newErrorID();
        logMessage.append("ErrorRef:[").append(errorRef).append("].ErrorMessage:[").append(errorMessage).append("]");
        if (!StringUtil.isBlank(additionalLogMessage)) {
            logMessage.append("ExtraMessage:[").append(additionalLogMessage).append("]");
        }
        if (cause != null) {
            logMessage.append("CausedBy:[").append(cause).append("]");
            log.error((Object)logMessage.toString(), cause);
        } else {
            log.error((Object)logMessage.toString());
        }
        String errorCauseCodeStr = errorCauseCode == null ? "" : errorCauseCode.getCode();
        return new FusionExceptionWithRefCode(errorMessage, errorCauseCodeStr, errorRef);
    }

    private static byte[] getUploadTicketCipherKey(CollectedDataTypeEnum dataType) {
        return HashUtils.md5(SystemProperty.get(SystemPropertyEntities.RecommendationServiceSettings.UPLOAD_TICKET_CIPHER_CIPHER_PASSPHRASE.forCollectedDataType(dataType)));
    }

    public static byte[] getUploadHash(DataUploadRequest uploadRequest, int randomNum) throws DataCollectorException {
        StringBuilder stb = new StringBuilder(100);
        byte[] result = HashUtils.sha256(stb.append(uploadRequest.userid).append(uploadRequest.requestTimestamp).append(uploadRequest.dataType).append(randomNum).append(uploadRequest.sessionid).toString());
        if (result.length != 32) {
            log.error((Object)("Failed sanity check! Generated signature length is " + result.length + ". Expected " + 32));
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.INTERNAL_ERROR, new Object[0]);
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static DataUploadTicket createUploadTicket(DataUploadRequest uploadRequest) throws DataCollectorException {
        SecureRandom random = new SecureRandom();
        int randomNum = random.nextInt();
        CollectedDataTypeEnum collectedDataType = CollectedDataTypeEnum.fromCode(uploadRequest.dataType);
        SecretKeySpec keySpec = new SecretKeySpec(DataCollectorUtils.getUploadTicketCipherKey(collectedDataType), KEY_SPEC_ALGO);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(1, (Key)keySpec, IVSPEC);
            byte[] uploadHash = DataCollectorUtils.getUploadHash(uploadRequest, randomNum);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(64);
            DataOutputStream encryptStream = new DataOutputStream(new CipherOutputStream(baos, cipher));
            try {
                encryptStream.writeInt(uploadRequest.userid);
                encryptStream.writeLong(uploadRequest.requestTimestamp);
                encryptStream.writeInt(uploadRequest.dataType);
                encryptStream.writeInt(randomNum);
                encryptStream.write(uploadHash);
                Object var10_12 = null;
            }
            catch (Throwable throwable) {
                Object var10_13 = null;
                encryptStream.close();
                throw throwable;
            }
            encryptStream.close();
            byte[] cipherText = baos.toByteArray();
            DataUploadTicket ticket = new DataUploadTicket();
            ticket.dataType = uploadRequest.dataType;
            ticket.random = randomNum;
            ticket.requestTimestamp = uploadRequest.requestTimestamp;
            ticket.signature = uploadHash;
            ticket.userid = uploadRequest.userid;
            ticket.ticketRef = HashUtils.asHex(cipherText);
            return ticket;
        }
        catch (GeneralSecurityException e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.TICKET_REF_CREATION_FAILURE, new Object[0]);
        }
        catch (IOException e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.TICKET_REF_CREATION_FAILURE, new Object[0]);
        }
        catch (Exception e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.TICKET_REF_CREATION_FAILURE, new Object[0]);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static DataUploadTicket decryptDataUploadTicket(String uploadTicketRef, String expectedSessionID, CollectedDataTypeEnum expectedCollectedDataType) throws DataCollectorException {
        if (StringUtil.isBlank(uploadTicketRef)) {
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.BLANK_UPLOAD_TICKET_REF, new Object[0]);
        }
        if (uploadTicketRef.length() > 128) {
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.INVALID_UPLOAD_TICKET_REF, new Object[0]);
        }
        try {
            DataUploadTicket dataUploadTicket;
            Cipher cipher = Cipher.getInstance(CIPHER_ALGO);
            cipher.init(2, (Key)new SecretKeySpec(DataCollectorUtils.getUploadTicketCipherKey(expectedCollectedDataType), KEY_SPEC_ALGO), IVSPEC);
            ByteArrayInputStream bais = new ByteArrayInputStream(StringUtil.hexStringToByteArray(uploadTicketRef));
            DataInputStream decryptStream = new DataInputStream(new CipherInputStream(bais, cipher));
            try {
                int userid = decryptStream.readInt();
                long requestTimestamp = decryptStream.readLong();
                int dataType = decryptStream.readInt();
                int random = decryptStream.readInt();
                byte[] receivedHash = new byte[32];
                decryptStream.readFully(receivedHash);
                DataUploadRequest dataUploadRequest = new DataUploadRequest();
                dataUploadRequest.userid = userid;
                dataUploadRequest.requestTimestamp = requestTimestamp;
                dataUploadRequest.dataType = dataType;
                dataUploadRequest.sessionid = expectedSessionID;
                byte[] expectedHash = DataCollectorUtils.getUploadHash(dataUploadRequest, random);
                if (!Arrays.equals(receivedHash, expectedHash)) {
                    throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.WRONG_UPLOAD_TICKET_SIGNATURE, new Object[0]);
                }
                DataUploadTicket uploadTicket = new DataUploadTicket();
                uploadTicket.dataType = dataType;
                uploadTicket.random = random;
                uploadTicket.requestTimestamp = requestTimestamp;
                uploadTicket.signature = receivedHash;
                uploadTicket.ticketRef = uploadTicketRef;
                uploadTicket.userid = userid;
                dataUploadTicket = uploadTicket;
                Object var17_19 = null;
            }
            catch (Throwable throwable) {
                Object var17_20 = null;
                decryptStream.close();
                throw throwable;
            }
            decryptStream.close();
            return dataUploadTicket;
        }
        catch (DataCollectorException e) {
            throw e;
        }
        catch (GeneralSecurityException e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.INVALID_UPLOAD_TICKET_REF, new Object[0]);
        }
        catch (IOException e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.CORRUPTED_TICKET_REF, new Object[0]);
        }
        catch (NumberFormatException e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.INVALID_UPLOAD_TICKET_REF, new Object[0]);
        }
    }

    private static void checkUploadDataFeatureEnabled(CollectedDataTypeEnum collectedDataType) throws DataCollectorException {
        if (collectedDataType == null) {
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.UPLOAD_DATA_GLOBALLY_DISABLED, new Object[0]);
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.DATA_COLLECTION_SERVICE_ENABLED)) {
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.UPLOAD_DATA_GLOBALLY_DISABLED, new Object[0]);
        }
        if (!SystemProperty.getBool(SystemPropertyEntities.RecommendationServiceSettings.DATA_COLLECTION_SERVICE_ENABLED.forCollectedDataType(collectedDataType))) {
            throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.UPLOAD_DATA_FOR_SPECIFIC_TYPE_DISABLED, collectedDataType);
        }
    }

    private static void checkUserAllowedToUploadAddressBook(int userId) throws DataCollectorException {
        int minMigLevel = SystemProperty.getInt(SystemPropertyEntities.RecommendationServiceSettings.UPLOAD_DATA_MIN_MIG_LEVEL.forCollectedDataType(CollectedDataTypeEnum.ADDRESSBOOKCONTACT));
        try {
            User userEJB;
            boolean hasGuardCapability;
            int userReputationLevel;
            if (minMigLevel >= 1 && (userReputationLevel = MemCacheOrEJB.getUserReputationLevel(null, userId)) < minMigLevel) {
                throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.INSUFFICIENT_MIGLEVEL, minMigLevel);
            }
            if (SystemProperty.getBool(SystemPropertyEntities.GuardsetEnabled.UPLOAD_ADDRESSBOOK_DATA) && !(hasGuardCapability = (userEJB = (User)EJBHomeCache.getObject("ejb/User", UserHome.class)).isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), GuardCapabilityEnum.UPLOAD_ADDRESSBOOK_DATA.value()))) {
                throw new DataCollectorException(ErrorCause.DataCollectorErrorReasonType.INSUFFICIENT_RIGHTS, new Object[0]);
            }
            return;
        }
        catch (RemoteException ex) {
            throw new DataCollectorException(ex, ErrorCause.DataCollectorErrorReasonType.INTERNAL_ERROR, new Object[0]);
        }
        catch (CreateException ex) {
            throw new DataCollectorException(ex, ErrorCause.DataCollectorErrorReasonType.INTERNAL_ERROR, new Object[0]);
        }
        catch (FusionEJBException ex) {
            throw new DataCollectorException(ex, ErrorCause.DataCollectorErrorReasonType.INTERNAL_ERROR, new Object[0]);
        }
    }

    public static void checkUserAccess(int userId, CollectedDataTypeEnum collectedDataType) throws DataCollectorException {
        DataCollectorUtils.checkUploadDataFeatureEnabled(collectedDataType);
        DataCollectorUtils.checkUserAllowedToUploadAddressBook(userId);
    }

    public static String getPerUserIdRLLocalKey(int userId) {
        return String.valueOf(userId);
    }

    public static String getPerUserIdPerCollectedDataTypeRLLocalKey(int userId, CollectedDataTypeEnum dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("dataType is null");
        }
        return MemCachedKeyUtils.getFullKeyFromStrings(String.valueOf(userId), String.valueOf(dataType.getCode()));
    }

    public static void hitUploadDataRateLimit(int userId, CollectedDataTypeEnum collectedDataType) throws DataCollectorException {
        MemCachedRateLimiter.NameSpace nameSpace = MemCachedRateLimiter.NameSpace.UPLD_DATA;
        SystemPropertyEntities.RecommendationServiceSettings sysProp = SystemPropertyEntities.RecommendationServiceSettings.RATE_LIMIT_UPLOAD_DATA_PER_USER_ID;
        DataCollectorUtils.hitUploadRateLimit(nameSpace, DataCollectorUtils.getPerUserIdPerCollectedDataTypeRLLocalKey(userId, collectedDataType), sysProp.forCollectedDataType(collectedDataType));
        DataCollectorUtils.hitUploadRateLimit(nameSpace, DataCollectorUtils.getPerUserIdRLLocalKey(userId), sysProp);
        DataCollectorUtils.hitUploadRateLimit(nameSpace, "", SystemPropertyEntities.RecommendationServiceSettings.RATE_LIMIT_UPLOAD_DATA);
    }

    public static void hitUploadTicketRequestRateLimit(int userId, CollectedDataTypeEnum collectedDataType) throws DataCollectorException {
        MemCachedRateLimiter.NameSpace nameSpace = MemCachedRateLimiter.NameSpace.UPLD_TICKET_REQ;
        SystemPropertyEntities.RecommendationServiceSettings sysProp = SystemPropertyEntities.RecommendationServiceSettings.RATE_LIMIT_UPLOAD_TICKET_REQUEST_PER_USER_ID;
        DataCollectorUtils.hitUploadRateLimit(nameSpace, DataCollectorUtils.getPerUserIdPerCollectedDataTypeRLLocalKey(userId, collectedDataType), sysProp.forCollectedDataType(collectedDataType));
        DataCollectorUtils.hitUploadRateLimit(nameSpace, DataCollectorUtils.getPerUserIdRLLocalKey(userId), sysProp);
        DataCollectorUtils.hitUploadRateLimit(nameSpace, "", SystemPropertyEntities.RecommendationServiceSettings.RATE_LIMIT_UPLOAD_TICKET_REQUEST);
    }

    private static void hitUploadRateLimit(MemCachedRateLimiter.NameSpace rateLimitNameSpace, String key, SystemPropertyEntities.SystemPropertyEntryInterface sysPropEntity) throws DataCollectorException {
        String hitPerDurationKey = sysPropEntity.getName();
        String hitPerDurationValue = SystemProperty.get(sysPropEntity);
        try {
            if (log.isDebugEnabled()) {
                log.debug((Object)("ns:" + (Object)((Object)rateLimitNameSpace) + ";key:" + key + ";hitPerDurationValue:" + hitPerDurationValue));
            }
            MemCachedRateLimiter.hit(rateLimitNameSpace, key, hitPerDurationValue);
        }
        catch (MemCachedRateLimiter.LimitExceeded e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.RATE_LIMIT_BREACHED, hitPerDurationKey, hitPerDurationValue);
        }
        catch (MemCachedRateLimiter.FormatError e) {
            throw new DataCollectorException(e, ErrorCause.DataCollectorErrorReasonType.INVALID_RATE_LIMIT_CONFIG, hitPerDurationKey, hitPerDurationValue);
        }
    }

    public static DataCollectorContext createDefaultDataCollectorContext(AppStartupInfo appStartupInfo) {
        DataCollectorContext ctx = new DataCollectorContext(appStartupInfo);
        ctx.addStrategy(CollectedDataTypeEnum.ADDRESSBOOKCONTACT.getCode(), new DataCollectorWriterStrategy<AddressBookRecordData>("addressbookcontacts-stgy", new CollectedAddressBookDataTransformer("addressbookcontacts-xformer")).addSink(new AddressBookDataLog4JSink("addressbookcontacts-sink-log4j", "addressbookcontacts-logger")));
        ctx.addStrategy(CollectedDataTypeEnum.REWARD_PROGRAM_TRIGGER_SUMMARY.getCode(), new DataCollectorWriterStrategy<CollectedRewardProgramTriggerSummaryDataIce>("rewardprogramtriggersummary-stgy", new CollectedRewardProgramTriggerSummaryDataTransformer("rewardprogramtriggersummary-xformer")).addSink(new CollectedRewardProgramTriggerSummaryLog4jSink("rewardprogramtriggersummary-sink-log4j", "rewardprogramtriggersummary-logger")));
        return ctx;
    }

    public static long sumStringLength(String[] strArray) {
        long total = 0L;
        if (strArray != null) {
            for (String str : strArray) {
                total += str == null ? 0L : (long)str.length();
            }
        }
        return total;
    }
}

