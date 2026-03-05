/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Ice.Current
 *  com.danga.MemCached.MemCachedClient
 *  org.apache.log4j.Logger
 *  org.keyczar.Crypter
 *  org.keyczar.exceptions.KeyczarException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.util.StringUtils
 */
package com.projectgoth.fusion.authentication;

import Ice.Current;
import com.danga.MemCached.MemCachedClient;
import com.projectgoth.fusion.authentication.CredentialVersion;
import com.projectgoth.fusion.authentication.DecayingFailedAuthsByIPScore;
import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.authentication.cache.CredentialList;
import com.projectgoth.fusion.authentication.domain.PersistedCredential;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.MemCachedKeyUtils;
import com.projectgoth.fusion.common.MemCachedUtils;
import com.projectgoth.fusion.common.PasswordUtils;
import com.projectgoth.fusion.common.RequestAndRateLongCounter;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.common.UsernameUtils;
import com.projectgoth.fusion.common.UsernameValidationException;
import com.projectgoth.fusion.dao.CredentialDAO;
import com.projectgoth.fusion.data.ValidateCredentialResult;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionBusinessException;
import com.projectgoth.fusion.slice.FusionCredential;
import com.projectgoth.fusion.slice.FusionException;
import com.projectgoth.fusion.slice.FusionServerException;
import com.projectgoth.fusion.slice._AuthenticationServiceDisp;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.keyczar.Crypter;
import org.keyczar.exceptions.KeyczarException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.StringUtils;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AuthenticationServiceI
extends _AuthenticationServiceDisp
implements InitializingBean {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(AuthenticationServiceI.class));
    private static final Logger authenticationLog = Logger.getLogger((String)"AuthenticationLog");
    private static MemCachedClient commonMemcache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.common);
    private static MemCachedClient authenticationServiceMemCache = MemCachedUtils.getMemCachedClient(MemCachedUtils.Instance.authenticationService);
    public static final String MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE = "MU_NS";
    private static final String CRYPTER_KEY_LOCATION = ConfigUtils.getConfigDirectory() + "/aeskeys";
    private CredentialDAO credentialDAO;
    private Crypter crypter;
    private String surgeMailPassword;
    private int suspectIPRatio = 20000;
    private int bruteForceIPRatio = 50000;
    private RequestAndRateLongCounter successfulAuthentications = new RequestAndRateLongCounter(3);
    private RequestAndRateLongCounter failedAuthentications = new RequestAndRateLongCounter(3);
    private ConcurrentMap<String, AtomicInteger> failedBreakdown = new ConcurrentHashMap<String, AtomicInteger>();
    private ConcurrentMap<String, AtomicInteger> succeededBreakdown = new ConcurrentHashMap<String, AtomicInteger>();

    public void afterPropertiesSet() throws Exception {
        this.crypter = new Crypter(CRYPTER_KEY_LOCATION);
    }

    void shutdown() {
    }

    public void setCredentialDAO(CredentialDAO credentialDAO) {
        this.credentialDAO = credentialDAO;
    }

    public void setSurgeMailPassword(String surgeMailPassword) {
        this.surgeMailPassword = surgeMailPassword;
    }

    public RequestAndRateLongCounter getSuccessfulAuthentications() {
        return this.successfulAuthentications;
    }

    public RequestAndRateLongCounter getFailedAuthentications() {
        return this.failedAuthentications;
    }

    public void setSuspectIPRatio(int suspectIPRatio) {
        this.suspectIPRatio = suspectIPRatio;
    }

    public void setBruteForceIPRatio(int bruteForceIPRatio) {
        this.bruteForceIPRatio = bruteForceIPRatio;
    }

    public void setMinimumAuthenticationsPerIP(int minimumAuthenticationsPerIP) {
    }

    private void logAuthenticationResult(AuthenticationServiceResponseCodeEnum response, Credential credential, String clientIP) {
        if (SystemProperty.getBool(SystemPropertyEntities.Default.LOG_SUCCESSFUL_LOGINS) || response == AuthenticationServiceResponseCodeEnum.Failed) {
            authenticationLog.info((Object)(response + " for user id [" + (credential == null ? null : Integer.valueOf(credential.userID)) + "] and name [" + (credential == null ? null : credential.username) + "] and IP [" + clientIP + "]"));
        } else if (authenticationLog.isDebugEnabled()) {
            authenticationLog.debug((Object)(response + " for user id [" + (credential == null ? null : Integer.valueOf(credential.userID)) + "] and name [" + (credential == null ? null : credential.username) + "] and IP [" + clientIP + "]"));
        }
        if (response == AuthenticationServiceResponseCodeEnum.Success) {
            this.successfulAuthentications.add();
        } else {
            this.failedAuthentications.add();
        }
    }

    private int failedIPRatio(String ip) {
        AtomicInteger fails = (AtomicInteger)this.failedBreakdown.get(ip);
        if (fails == null) {
            return 0;
        }
        AtomicInteger succeeds = (AtomicInteger)this.succeededBreakdown.get(ip);
        if (succeeds == null || succeeds.intValue() == 0) {
            if (fails.intValue() > 1000) {
                return fails.intValue();
            }
            return 0;
        }
        return fails.intValue() / succeeds.intValue();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AuthenticationServiceResponseCodeEnum authenticate(Credential userCredential, String clientIP, Current __current) throws FusionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("authenticating user [" + userCredential.username + "] with type [" + PasswordType.fromValue(userCredential.passwordType) + "]"));
        }
        long start = System.currentTimeMillis();
        AuthenticationServiceResponseCodeEnum authenticationResponse = AuthenticationServiceResponseCodeEnum.Failed;
        try {
            if (userCredential.passwordType == PasswordType.FUSION.value()) {
                authenticationResponse = this.handleFusionCredentialAuthentication((FusionCredential)userCredential, clientIP);
            } else if (userCredential.passwordType == PasswordType.MAIL.value()) {
                authenticationResponse = this.handleMailCredentialAuthentication(userCredential);
            }
            Object var8_6 = null;
        }
        catch (Throwable throwable) {
            Object var8_7 = null;
            long end = System.currentTimeMillis();
            if (log.isDebugEnabled()) {
                log.debug((Object)("authenticating user [" + userCredential.username + "] took [" + (end - start) + "] ms"));
            }
            throw throwable;
        }
        long end = System.currentTimeMillis();
        if (log.isDebugEnabled()) {
            log.debug((Object)("authenticating user [" + userCredential.username + "] took [" + (end - start) + "] ms"));
        }
        this.logAuthenticationResult(authenticationResponse, userCredential, clientIP);
        if (authenticationResponse == AuthenticationServiceResponseCodeEnum.Failed || authenticationResponse == AuthenticationServiceResponseCodeEnum.UnknownUsername) {
            this.increaseAuthFailureScoreForIP(userCredential, clientIP);
        }
        return authenticationResponse;
    }

    private void increaseAuthFailureScoreForIP(Credential userCredential, String clientIP) {
        try {
            DecayingFailedAuthsByIPScore.incrementScore(clientIP, 1.0);
        }
        catch (Exception e) {
            log.error((Object)("Failed to update failed auths score for [IP:" + clientIP + "] [USER:" + userCredential.username + "], e=" + e), (Throwable)e);
        }
    }

    private boolean isCredentialExpired(PersistedCredential persistedCredential) {
        if (persistedCredential == null || persistedCredential.getExpires() == null) {
            return false;
        }
        return persistedCredential.getExpires().getTime() < System.currentTimeMillis();
    }

    private static int getSha1HashCode(String saltedPassword) throws FusionServerException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(saltedPassword.getBytes("UTF-8"));
            int value = 0;
            for (int i = 0; i < result.length; i += 4) {
                int x = (result[i + 0] & 0xFF) << 24 | (result[i + 1] & 0xFF) << 16 | (result[i + 2] & 0xFF) << 8 | result[i + 3] & 0xFF;
                value ^= x;
            }
            return value;
        }
        catch (NoSuchAlgorithmException e) {
            throw new FusionServerException("failed to get digest algorithm", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (UnsupportedEncodingException e) {
            throw new FusionServerException("failed to get character set", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    private AuthenticationServiceResponseCodeEnum handleFusionCredentialAuthentication(Credential userCredential, String clientIP) throws FusionServerException {
        if (!(userCredential instanceof FusionCredential)) {
            log.error((Object)"passwordType == FUSION but we did not receive a FusionCredential");
            return AuthenticationServiceResponseCodeEnum.Failed;
        }
        FusionCredential fusionCredential = (FusionCredential)userCredential;
        try {
            AtomicInteger succeeded;
            PersistedCredential loadedCredential;
            int ratio = this.failedIPRatio(clientIP);
            if (ratio >= this.bruteForceIPRatio && ratio % 5000 == 0) {
                log.warn((Object)("brute force IP [" + clientIP + "], ratio = " + ratio));
            } else if (ratio > this.suspectIPRatio && ratio < this.bruteForceIPRatio && ratio % 5000 == 0) {
                log.warn((Object)("suspect IP [" + clientIP + "], ratio = " + ratio));
            }
            if (fusionCredential.userID < 1) {
                if (SystemProperty.getBool(SystemPropertyEntities.SSO.ENABLE_PRELOGIN_USERNAME_CHAR_VALIDATION)) {
                    try {
                        UsernameUtils.validateUsernameAsSearchKeyword(fusionCredential.username);
                    }
                    catch (UsernameValidationException ex) {
                        if (SystemProperty.getBool(SystemPropertyEntities.SSO.LOG_INVALID_USERNAME_ON_PRELOGIN) && SystemProperty.getBool(SystemPropertyEntities.SSO.LOG_INVALID_USERNAME_ON_PRELOGIN)) {
                            String errorCode = ex.getErrorCause() != null ? ex.getErrorCause().getCode() : "";
                            log.info((Object)("Received an invalid username.RemoteAddress:[" + clientIP + "].ErrorCode:[" + errorCode + "].Username[" + MemCachedKeyUtils.getFullKeyFromStrings(fusionCredential.username, new String[0]) + "]"));
                        }
                        return AuthenticationServiceResponseCodeEnum.InvalidCredential;
                    }
                    catch (Exception e) {
                        log.error((Object)"Unexpected Exception on handleFusionCredentialAuthentication", (Throwable)e);
                        return AuthenticationServiceResponseCodeEnum.InternalError;
                    }
                }
                try {
                    fusionCredential.userID = this.userIDForFusionUsername(fusionCredential.username);
                }
                catch (FusionException e) {
                    return AuthenticationServiceResponseCodeEnum.UnknownUsername;
                }
            }
            if ((loadedCredential = this.credentialDAO.getCredentialFromNewOrOldTable(fusionCredential.userID, PasswordType.FUSION, this.crypter)) == null) {
                return AuthenticationServiceResponseCodeEnum.UnknownCredential;
            }
            if (this.isCredentialExpired(loadedCredential)) {
                return AuthenticationServiceResponseCodeEnum.CredentialsExpired;
            }
            String clearText = PasswordUtils.decryptPassword(loadedCredential, this.crypter);
            if (StringUtils.hasLength((String)fusionCredential.password)) {
                if (!clearText.toLowerCase().equals(fusionCredential.password.toLowerCase())) {
                    AtomicInteger failed = this.failedBreakdown.putIfAbsent(clientIP, new AtomicInteger(1));
                    if (failed != null) {
                        failed.incrementAndGet();
                    }
                    return AuthenticationServiceResponseCodeEnum.Failed;
                }
            } else {
                String challenge = fusionCredential.loginChallenge + clearText;
                if (challenge.hashCode() != fusionCredential.clientHash && (challenge = fusionCredential.loginChallenge + clearText.toLowerCase()).hashCode() != fusionCredential.clientHash && AuthenticationServiceI.getSha1HashCode(fusionCredential.loginChallenge + clearText) != fusionCredential.clientHash) {
                    AtomicInteger failed = this.failedBreakdown.putIfAbsent(clientIP, new AtomicInteger(1));
                    if (failed != null) {
                        failed.incrementAndGet();
                    }
                    return AuthenticationServiceResponseCodeEnum.Failed;
                }
            }
            if ((succeeded = this.succeededBreakdown.putIfAbsent(clientIP, new AtomicInteger(1))) != null) {
                succeeded.incrementAndGet();
            }
            return AuthenticationServiceResponseCodeEnum.Success;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to decrypt credentials for username [" + fusionCredential.username + "] while handling fusion authentication"), (Throwable)e);
            throw new FusionServerException("failed to decrypt credentials for username [" + fusionCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (EmptyResultDataAccessException e) {
            log.error((Object)("there are no credentials for user [" + fusionCredential.username + "]"), (Throwable)e);
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + fusionCredential.username + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credential for user [" + fusionCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    private AuthenticationServiceResponseCodeEnum handleMailCredentialAuthentication(Credential userCredential) throws FusionServerException {
        if (StringUtils.hasLength((String)userCredential.username) && StringUtils.hasLength((String)userCredential.password) && userCredential.password.equals(this.surgeMailPassword)) {
            return AuthenticationServiceResponseCodeEnum.Success;
        }
        return AuthenticationServiceResponseCodeEnum.Failed;
    }

    @Override
    public AuthenticationServiceResponseCodeEnum createCredential(Credential userCredential, Current __current) throws FusionException {
        ValidateCredentialResult validationResult = PasswordUtils.validateCredential(userCredential);
        if (!validationResult.valid) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        try {
            PersistedCredential credentialToBePersisted = PersistedCredential.fromCredentialSecured(this.crypter, userCredential, CredentialVersion.KEYCZAR);
            int id = this.credentialDAO.createCredential(credentialToBePersisted);
            if (id > 0) {
                return AuthenticationServiceResponseCodeEnum.Success;
            }
            log.error((Object)("failed to create credentials for user [" + userCredential.username + "] and pass type [" + userCredential.passwordType + "]"));
            return AuthenticationServiceResponseCodeEnum.Failed;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to encrypt password for user [" + userCredential.username + "] creating credentials"), (Throwable)e);
            throw new FusionServerException("failed to encrypt credentials for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataIntegrityViolationException e) {
            log.error((Object)("failed to persist credential for user [" + userCredential.username + "] and type [" + PasswordType.fromValue(userCredential.passwordType) + "], already exists?"), (Throwable)e);
            throw new FusionServerException("credentials already exists for username [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.CredentialAlreadyExists.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to persist credential for user [" + userCredential.username + "]"), (Throwable)e);
            throw new FusionServerException("failed to persist credential for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public AuthenticationServiceResponseCodeEnum updateCredential(Credential userCredential, Current __current) throws FusionException {
        ValidateCredentialResult validationResult = PasswordUtils.validateCredential(userCredential);
        if (!validationResult.valid) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        if (userCredential.passwordType == PasswordType.FUSION.value()) {
            log.warn((Object)("username [" + userCredential.username + "] attempting to change password with updateCredential() must use updateFusionCredential()"));
            throw new FusionBusinessException("Internal error", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        try {
            PersistedCredential credentialToBePersisted;
            int rowsUpdated;
            PersistedCredential fusionCredential = this.credentialDAO.getCredential(userCredential.userID, PasswordType.FUSION);
            if (fusionCredential == null) {
                this.migrateUserCredentials(userCredential.userID);
            }
            if ((rowsUpdated = this.credentialDAO.updateCredentialPassword(credentialToBePersisted = PersistedCredential.fromCredentialSecured(this.crypter, userCredential, CredentialVersion.KEYCZAR))) > 0) {
                return AuthenticationServiceResponseCodeEnum.Success;
            }
            log.error((Object)("failed to update credentials for user [" + userCredential.userID + "] and pass type [" + userCredential.passwordType + "], no rows found?"));
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to encrypt password for user [" + userCredential.username + "] when updating credentials"), (Throwable)e);
            throw new FusionServerException("failed to encrypt credentials for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to update credential for user [" + userCredential.username + "]"), (Throwable)e);
            throw new FusionServerException("failed to update credentials for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public AuthenticationServiceResponseCodeEnum updateFusionCredential(Credential userCredential, String oldPassword, Current __current) throws FusionException {
        ValidateCredentialResult validationResult = PasswordUtils.validateCredential(userCredential);
        if (!validationResult.valid) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        try {
            PersistedCredential credentialToBePersisted;
            int rowsUpdated;
            PersistedCredential fusionCredential;
            if (userCredential.userID < 1) {
                userCredential.userID = this.userIDForFusionUsername(userCredential.username);
            }
            if ((fusionCredential = this.credentialDAO.getCredential(userCredential.userID, PasswordType.FUSION)) == null) {
                CredentialList.deleteCredentialList(authenticationServiceMemCache, userCredential.userID);
                return AuthenticationServiceResponseCodeEnum.Success;
            }
            if (oldPassword != null && !PasswordUtils.decryptPassword(fusionCredential, this.crypter).equals(oldPassword)) {
                log.warn((Object)("fusion username [" + userCredential.username + "] supplied incorrect old password when attempting to change password"));
                throw new FusionBusinessException("Username and password do not match", AuthenticationServiceResponseCodeEnum.Failed.value());
            }
            if (log.isDebugEnabled()) {
                log.debug((Object)("new password = " + userCredential.password));
            }
            if ((rowsUpdated = this.credentialDAO.updateCredentialPassword(credentialToBePersisted = PersistedCredential.fromCredentialSecured(this.crypter, userCredential, CredentialVersion.KEYCZAR))) > 0) {
                return AuthenticationServiceResponseCodeEnum.Success;
            }
            log.error((Object)("failed to update credentials for user [" + userCredential.userID + "] and pass type [" + userCredential.passwordType + "], no rows found?"));
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to encrypt password for fusion user [" + userCredential.username + "] when updating credentials"), (Throwable)e);
            throw new FusionServerException("failed to encrypt credentials for fusion user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to update credential for fusion user [" + userCredential.username + "]"), (Throwable)e);
            throw new FusionServerException("failed to update credentials for fusion user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    private PersistedCredential containsCredential(List<PersistedCredential> credentials, PasswordType passwordType) {
        if (credentials == null || credentials.isEmpty() || passwordType == null) {
            return null;
        }
        for (PersistedCredential credential : credentials) {
            if (credential.getPasswordType() != passwordType.value().byteValue()) continue;
            return credential;
        }
        return null;
    }

    private boolean removeCredential(List<PersistedCredential> credentials, PasswordType passwordType) {
        boolean removed = false;
        Iterator<PersistedCredential> ite = credentials.iterator();
        while (ite.hasNext()) {
            PersistedCredential credential = ite.next();
            if (credential.getPasswordType() != passwordType.value().byteValue()) continue;
            ite.remove();
            removed = true;
        }
        return removed;
    }

    private List<PersistedCredential> getExistingCredentialsOrMigrateOldCredentials(int userID) throws FusionException {
        List<PersistedCredential> existingCredentials = this.credentialDAO.getAllCredentials(userID);
        if ((existingCredentials == null || existingCredentials.isEmpty()) && ((existingCredentials = this.migrateAndLoadUserCredentials(userID)) == null || existingCredentials.isEmpty())) {
            log.error((Object)("failed to get existing or migrate credential for user [" + userID + "]"));
            throw new FusionBusinessException("failed to get existing or migrate credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        return existingCredentials;
    }

    @Override
    public AuthenticationServiceResponseCodeEnum removeCredential(Credential userCredential, Current __current) throws FusionException {
        if (userCredential == null || userCredential.userID < 1 || userCredential.passwordType < 1) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        try {
            PersistedCredential credentialToBePersisted = PersistedCredential.fromCredentialSecured(this.crypter, userCredential, CredentialVersion.KEYCZAR);
            int rowsDeleted = this.credentialDAO.removeCredential(credentialToBePersisted);
            if (rowsDeleted > 0) {
                return AuthenticationServiceResponseCodeEnum.Success;
            }
            log.error((Object)("failed to remove credentials for user [" + userCredential.username + "] and pass type [" + userCredential.passwordType + "], no rows found?"));
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to encrypt password for user [" + userCredential.username + "] when removing credentials"), (Throwable)e);
            throw new FusionServerException("failed to encrypt credentials for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to remove credential for user [" + userCredential.username + "]"), (Throwable)e);
            throw new FusionServerException("failed to remove credentials for user [" + userCredential.username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public AuthenticationServiceResponseCodeEnum exists(int userID, byte passwordType, Current __current) throws FusionServerException {
        if (userID < 1 || passwordType < 1) {
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        try {
            PersistedCredential persistedCredential = this.credentialDAO.getCredentialFromNewOrOldTable(userID, PasswordType.fromValue(passwordType), this.crypter);
            if (persistedCredential == null) {
                return AuthenticationServiceResponseCodeEnum.UnknownCredential;
            }
            return AuthenticationServiceResponseCodeEnum.Success;
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public AuthenticationServiceCredentialResponse getCredential(int userID, byte passwordType, Current __current) throws FusionException {
        if (userID < 1 || passwordType < 1) {
            return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.UnknownCredential, null);
        }
        try {
            PersistedCredential persistedCredential = this.credentialDAO.getCredentialFromNewOrOldTable(userID, PasswordType.fromValue(passwordType), this.crypter);
            if (log.isDebugEnabled()) {
                log.debug((Object)("found persisted cred for user id [" + userID + "]: " + persistedCredential));
            }
            if (persistedCredential == null) {
                return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.UnknownCredential, null);
            }
            return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.Success, persistedCredential.toUnSecuredCredential(this.crypter));
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to decrypt credentials for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to decrypt credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public byte[] availableCredentialTypes(int userID, Current __current) throws FusionException {
        if (userID < 0) {
            return new byte[0];
        }
        try {
            Byte[] passwordTypes = this.credentialDAO.availableCredentialTypes(userID);
            if (log.isDebugEnabled()) {
                log.debug((Object)("found " + passwordTypes.length + " available types from DAO"));
            }
            byte[] returnValue = new byte[passwordTypes.length];
            int index = 0;
            Byte[] arr$ = passwordTypes;
            int len$ = arr$.length;
            for (int i$ = 0; i$ < len$; ++i$) {
                byte value = arr$[i$];
                returnValue[index++] = value;
            }
            Arrays.sort(returnValue);
            if (log.isDebugEnabled()) {
                log.debug((Object)("sending back " + returnValue.length + " available types"));
            }
            return returnValue;
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load password types for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load password types for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public Credential[] getCredentialsForTypes(int userID, byte[] passwordTypes, Current __current) throws FusionException {
        if (userID < 1) {
            return new Credential[0];
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("finding credentials for userID " + userID));
        }
        try {
            List<PersistedCredential> persistedCredentials = this.credentialDAO.getCredentialsByTypes(userID, PasswordType.fromValues(passwordTypes));
            if (persistedCredentials == null || persistedCredentials.isEmpty()) {
                return new Credential[0];
            }
            int index = 0;
            Credential[] credentials = new Credential[persistedCredentials.size()];
            for (PersistedCredential persistedCredential : persistedCredentials) {
                credentials[index++] = persistedCredential.toUnSecuredCredential(this.crypter);
            }
            return credentials;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to decrypt credentials for user [" + userID + "] while getting fusion credentials for types"), (Throwable)e);
            throw new FusionServerException("failed to decrypt credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public Credential[] getAllCredentials(int userID, Current __current) throws FusionException {
        if (userID < 1) {
            return new Credential[0];
        }
        try {
            List<PersistedCredential> persistedCredentials = this.credentialDAO.getAllCredentials(userID);
            if (persistedCredentials == null || persistedCredentials.isEmpty()) {
                return new Credential[0];
            }
            int index = 0;
            Credential[] credentials = new Credential[persistedCredentials.size()];
            for (PersistedCredential persistedCredential : persistedCredentials) {
                credentials[index++] = persistedCredential.toUnSecuredCredential(this.crypter);
            }
            return credentials;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to decrypt credentials for user [" + userID + "] while getting all credentials"), (Throwable)e);
            throw new FusionServerException("failed to decrypt credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public Credential[] getAllCredentialsFromOldSource(int userID, Current __current) throws FusionException {
        if (userID < 1) {
            return new Credential[0];
        }
        try {
            List<PersistedCredential> persistedCredentials = this.credentialDAO.getAllCredentialsFromOldTable(userID);
            if (persistedCredentials == null || persistedCredentials.isEmpty()) {
                return new Credential[0];
            }
            int index = 0;
            Credential[] credentials = new Credential[persistedCredentials.size()];
            for (PersistedCredential persistedCredential : persistedCredentials) {
                credentials[index++] = persistedCredential.toUnSecuredCredential(this.crypter);
            }
            return credentials;
        }
        catch (KeyczarException e) {
            log.error((Object)("failed to decrypt credentials for user [" + userID + "] while getting all credentials from old source"), (Throwable)e);
            throw new FusionServerException("failed to decrypt credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
        catch (DataAccessException e) {
            log.error((Object)("failed to load credential for user [" + userID + "]"), (Throwable)e);
            throw new FusionServerException("failed to load credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<PersistedCredential> migrateAndLoadUserCredentials(int userID) throws FusionException {
        List<PersistedCredential> i$;
        block15: {
            PersistedCredential credential2;
            block14: {
                List<PersistedCredential> list;
                block13: {
                    try {
                        if (!MemCachedUtils.getLock(commonMemcache, MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE, Integer.toString(userID), 15000)) {
                            log.error((Object)("Failed to get a distributed lock in 15 seconds to migrate userID [" + userID + "]"));
                            throw new FusionServerException("failed to migrate user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
                        }
                        List<PersistedCredential> migratedCredentials = this.credentialDAO.getAllCredentialsFromMaster(userID);
                        if (migratedCredentials != null && !migratedCredentials.isEmpty()) {
                            for (PersistedCredential credential2 : migratedCredentials) {
                                if (credential2.getPasswordType() != PasswordType.FUSION.value().byteValue()) continue;
                                log.warn((Object)("credentials for user [" + userID + "] were already migrated"));
                                list = migratedCredentials;
                                Object var7_10 = null;
                                break block13;
                            }
                        }
                        log.info((Object)("migrating user [" + userID + "]"));
                        List<PersistedCredential> oldCredentials = this.credentialDAO.getAllCredentialsFromOldTable(userID);
                        if (oldCredentials == null || oldCredentials.isEmpty()) {
                            log.warn((Object)("no old credentials found for user [" + userID + "] to migrate"));
                            credential2 = null;
                            break block14;
                        }
                        try {
                            for (PersistedCredential credential3 : oldCredentials) {
                                credential3.secure(this.crypter, CredentialVersion.KEYCZAR);
                            }
                            for (PersistedCredential credential3 : oldCredentials) {
                                log.debug((Object)("secured cred " + credential3.getUsername() + " " + credential3.getPassword() + " " + PasswordType.fromValue(credential3.getPasswordType())));
                            }
                            this.credentialDAO.createCredentials(oldCredentials);
                            migratedCredentials = this.credentialDAO.getAllCredentialsFromMaster(userID);
                            i$ = migratedCredentials;
                            break block15;
                        }
                        catch (DataIntegrityViolationException e) {
                            log.error((Object)("failed to persist credentials for user [" + userID + "] during migration"), (Throwable)e);
                            throw new FusionServerException("failed to persist credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
                        }
                        catch (DataAccessException e) {
                            log.error((Object)("failed to read credentials for user [" + userID + "] during migration"), (Throwable)e);
                            throw new FusionServerException("failed to read credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
                        }
                        catch (KeyczarException e) {
                            log.error((Object)("failed to encrypt credentials for user [" + userID + "] during migration"), (Throwable)e);
                            throw new FusionServerException("failed to encrypt credentials for user [" + userID + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
                        }
                    }
                    catch (Throwable throwable) {
                        Object var7_13 = null;
                        MemCachedUtils.releaseLock(commonMemcache, MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE, Integer.toString(userID));
                        throw throwable;
                    }
                }
                MemCachedUtils.releaseLock(commonMemcache, MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE, Integer.toString(userID));
                return list;
            }
            Object var7_11 = null;
            MemCachedUtils.releaseLock(commonMemcache, MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE, Integer.toString(userID));
            return credential2;
        }
        Object var7_12 = null;
        MemCachedUtils.releaseLock(commonMemcache, MIGRATE_USER_DISTRIBUTED_LOCK_NAMESPACE, Integer.toString(userID));
        return i$;
    }

    @Override
    public void migrateUserCredentials(int userID, Current __current) throws FusionException {
        this.migrateAndLoadUserCredentials(userID);
    }

    @Override
    public int userIDForFusionUsername(String username, Current __current) throws FusionException {
        try {
            return this.credentialDAO.userIDForUsername(username);
        }
        catch (DataAccessException e) {
            throw new FusionServerException("failed to find user [" + username + "]", AuthenticationServiceResponseCodeEnum.InternalError.value());
        }
    }

    @Override
    public AuthenticationServiceCredentialResponse getLatestCredentialByUsernameAndPasswordType(String username, byte passwordType, Current __current) {
        List<PersistedCredential> credentials = this.credentialDAO.getCredentialsByUsernameAndPasswordType(username, PasswordType.fromValue(passwordType));
        if (credentials == null || credentials.size() == 0) {
            return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.UnknownCredential, null);
        }
        PersistedCredential latestCredential = null;
        if (credentials.size() == 1) {
            latestCredential = credentials.get(0);
        } else {
            latestCredential = credentials.remove(0);
            for (PersistedCredential credential : credentials) {
                if (!credential.getLastUpdated().after(latestCredential.getLastUpdated())) continue;
                latestCredential = credential;
            }
        }
        try {
            return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.Success, latestCredential.toUnSecuredCredential(this.crypter));
        }
        catch (KeyczarException e) {
            log.error((Object)("Unable to decrypt credential: " + (Object)((Object)e)));
            return new AuthenticationServiceCredentialResponse(AuthenticationServiceResponseCodeEnum.InternalError, null);
        }
    }

    @Override
    public AuthenticationServiceResponseCodeEnum checkCredential(Credential userCredential, Current __current) {
        if (userCredential == null) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        if (userCredential.userID == 0) {
            try {
                userCredential.userID = this.userIDForFusionUsername(userCredential.username);
            }
            catch (FusionException e) {
                return AuthenticationServiceResponseCodeEnum.UnknownUsername;
            }
        }
        return this.checkCredential(userCredential.userID, userCredential.password, userCredential.passwordType);
    }

    @Override
    public AuthenticationServiceResponseCodeEnum checkCredentialByUserId(int userid, String password, byte passwordType, Current __current) {
        return this.checkCredential(userid, password, passwordType);
    }

    @Override
    public AuthenticationServiceResponseCodeEnum checkCredentialByUsername(String username, String password, byte passwordType, Current __current) {
        int userID;
        try {
            userID = this.userIDForFusionUsername(username);
        }
        catch (FusionException e) {
            return AuthenticationServiceResponseCodeEnum.UnknownUsername;
        }
        return this.checkCredential(userID, password, passwordType);
    }

    private AuthenticationServiceResponseCodeEnum checkCredential(int userid, String password, byte passwordType) {
        Credential cred;
        PersistedCredential persistedCredential = this.credentialDAO.getCredential(userid, PasswordType.fromValue(passwordType));
        if (persistedCredential == null) {
            return AuthenticationServiceResponseCodeEnum.UnknownCredential;
        }
        try {
            cred = persistedCredential.toUnSecuredCredential(this.crypter);
        }
        catch (KeyczarException e) {
            return AuthenticationServiceResponseCodeEnum.InternalError;
        }
        if (cred == null) {
            return AuthenticationServiceResponseCodeEnum.InvalidCredential;
        }
        if (password.equals(cred.password)) {
            return AuthenticationServiceResponseCodeEnum.Success;
        }
        return AuthenticationServiceResponseCodeEnum.Failed;
    }
}

