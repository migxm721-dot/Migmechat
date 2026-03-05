/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.thirdpartysites;

import com.projectgoth.fusion.authentication.PasswordType;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.restapi.data.FacebookCredentialData;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.TwitterCredentialData;
import com.projectgoth.fusion.slice.AuthenticationServiceCredentialResponse;
import com.projectgoth.fusion.slice.AuthenticationServicePrx;
import com.projectgoth.fusion.slice.AuthenticationServiceResponseCodeEnum;
import com.projectgoth.fusion.slice.Credential;
import com.projectgoth.fusion.slice.FusionServerException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ThirdPartySiteCredentialManager {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(ThirdPartySiteCredentialManager.class));

    public static boolean updateCredential(int userId, byte siteId, String credentialsJson) throws FusionRestException {
        Object credentialData;
        if (PasswordType.fromValue(siteId) == null) {
            log.info((Object)("Invalid third party site ID: " + siteId));
            return false;
        }
        Credential credential = null;
        if (siteId == PasswordType.TWITTER.value()) {
            try {
                credentialData = new TwitterCredentialData();
                ((TwitterCredentialData)credentialData).fromJSONString(credentialsJson);
                String accessToken = ((TwitterCredentialData)credentialData).toJSONStringAccessToken();
                credential = new Credential(userId, ((TwitterCredentialData)credentialData).id, accessToken, siteId);
            }
            catch (JSONException e) {
                log.error((Object)("Unable to convert TwitterCredentialData from JSON string :" + (Object)((Object)e)));
                return false;
            }
        } else if (siteId == PasswordType.FACEBOOK_IM.value()) {
            try {
                credentialData = new FacebookCredentialData();
                ((FacebookCredentialData)credentialData).fromJSONString(credentialsJson);
                credential = new Credential(userId, ((FacebookCredentialData)credentialData).id, ((FacebookCredentialData)credentialData).key, siteId);
            }
            catch (JSONException e) {
                log.error((Object)("Unable to convert FacebookCredentialData from JSON string :" + (Object)((Object)e)));
                return false;
            }
        } else {
            log.info((Object)("Invalid third party site ID: " + siteId));
            return false;
        }
        try {
            AuthenticationServiceResponseCodeEnum removalResp;
            AuthenticationServicePrx authenticationService = EJBIcePrxFinder.getAuthenticationServiceProxy();
            AuthenticationServiceCredentialResponse getResp = authenticationService.getCredential(credential.userID, credential.passwordType);
            if (getResp != null && getResp.code == AuthenticationServiceResponseCodeEnum.Success && ((removalResp = authenticationService.removeCredential(getResp.userCredential)) == null || removalResp != AuthenticationServiceResponseCodeEnum.Success)) {
                log.error((Object)("Unable to remove credential for credential update. User ID [" + credential.userID + "], Response code: " + removalResp));
                return false;
            }
            AuthenticationServiceResponseCodeEnum createResp = authenticationService.createCredential(credential);
            if (createResp != null && createResp == AuthenticationServiceResponseCodeEnum.Success) {
                return true;
            }
            log.error((Object)("Unable to update credentials using the authentication service. Response code: " + createResp));
            return false;
        }
        catch (FusionServerException e) {
            log.error((Object)("Unable to update third party site credentials: " + (Object)((Object)e)));
            if (e.errorCode == AuthenticationServiceResponseCodeEnum.CredentialAlreadyExists.value()) {
                throw new FusionRestException(107, "Account is already linked to another user");
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to update third party site credentials: " + e));
        }
        return false;
    }

    public static boolean deleteCredential(int userId, byte siteId) {
        if (PasswordType.fromValue(siteId) == null) {
            log.info((Object)("Invalid third party site ID: " + siteId));
            return false;
        }
        try {
            AuthenticationServicePrx authenticationService = EJBIcePrxFinder.getAuthenticationServiceProxy();
            AuthenticationServiceCredentialResponse credential = authenticationService.getCredential(userId, siteId);
            if (credential != null && credential.userCredential != null) {
                AuthenticationServiceResponseCodeEnum resp = authenticationService.removeCredential(credential.userCredential);
                if (resp != null && resp == AuthenticationServiceResponseCodeEnum.Success) {
                    return true;
                }
                log.error((Object)("Unable to delete third party site credentials using the authentication service. Response code: " + resp));
                return false;
            }
            return true;
        }
        catch (Exception e) {
            log.error((Object)("Unable to delete third party site credential for User ID [" + userId + "], Site ID: [" + siteId + "]. Exception: " + e));
            return false;
        }
    }

    public static String getCredentialsJsonStr(int userId) {
        JSONArray credenitalsListJson = new JSONArray();
        TwitterCredentialData twitterCredential = ThirdPartySiteCredentialManager.getTwitterCredential(userId);
        if (twitterCredential != null) {
            try {
                log.debug((Object)("User [" + userId + "] has a twitter credential: " + twitterCredential.toJSONString()));
                JSONObject credentialJson = new JSONObject();
                credentialJson.put("type", (Object)PasswordType.TWITTER.value());
                credentialJson.put("credential", (Object)twitterCredential.toJSONObject());
                credenitalsListJson.put((Object)credentialJson);
            }
            catch (JSONException e) {
                log.error((Object)("Unable to parse twitter credential for User ID: " + userId));
            }
        } else {
            log.debug((Object)("User [" + userId + "] does not have a twitter credenital"));
        }
        FacebookCredentialData facebookCredential = ThirdPartySiteCredentialManager.getFacebookCredential(userId);
        if (facebookCredential != null) {
            try {
                log.debug((Object)("User [" + userId + "] has a facebook credential: " + facebookCredential.toJSONString()));
                JSONObject credentialJson = new JSONObject();
                credentialJson.put("type", (Object)PasswordType.FACEBOOK_IM.value());
                credentialJson.put("credential", (Object)facebookCredential.toJSONObject());
                credenitalsListJson.put((Object)credentialJson);
            }
            catch (JSONException e) {
                log.error((Object)("Unable to parse facebook credential for User ID: " + userId));
            }
        } else {
            log.debug((Object)("User [" + userId + "] does not have a facebook credenital"));
        }
        return ThirdPartySiteCredentialManager.packageCredentialData(credenitalsListJson);
    }

    private static String packageCredentialData(JSONArray credenitalsList) {
        try {
            JSONObject credentials = new JSONObject();
            credentials.put("credentials", (Object)credenitalsList);
            JSONObject data = new JSONObject();
            return data.put("data", (Object)credentials).toString();
        }
        catch (JSONException e) {
            log.error((Object)("Unable to package credential data into a JSON string: " + credenitalsList));
            return null;
        }
    }

    private static TwitterCredentialData getTwitterCredential(int userId) {
        try {
            AuthenticationServicePrx authenticationService = EJBIcePrxFinder.getAuthenticationServiceProxy();
            AuthenticationServiceCredentialResponse resp = authenticationService.getCredential(userId, PasswordType.TWITTER.value());
            if (resp != null && resp.userCredential != null) {
                TwitterCredentialData credential = new TwitterCredentialData();
                credential.fromJSONString(resp.userCredential.username, resp.userCredential.password);
                return credential;
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to get twitter credentials: " + e));
        }
        return null;
    }

    private static FacebookCredentialData getFacebookCredential(int userId) {
        try {
            AuthenticationServicePrx authenticationService = EJBIcePrxFinder.getAuthenticationServiceProxy();
            AuthenticationServiceCredentialResponse resp = authenticationService.getCredential(userId, PasswordType.FACEBOOK_IM.value());
            if (resp != null && resp.userCredential != null) {
                return new FacebookCredentialData(resp.userCredential.username, resp.userCredential.password);
            }
        }
        catch (Exception e) {
            log.error((Object)("Unable to get facebook credentials: " + e));
        }
        return null;
    }
}

