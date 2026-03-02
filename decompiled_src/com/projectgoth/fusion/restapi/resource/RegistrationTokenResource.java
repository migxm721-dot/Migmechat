/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.DataUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.data.AccountEntrySourceData;
import com.projectgoth.fusion.data.UserData;
import com.projectgoth.fusion.data.UserProfileData;
import com.projectgoth.fusion.data.UserRegistrationContextData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.invitation.InvitationData;
import com.projectgoth.fusion.invitation.InvitationUtils;
import com.projectgoth.fusion.invitation.restapi.data.InvitationDetailsData;
import com.projectgoth.fusion.restapi.data.BooleanData;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.UserCreationData;
import com.projectgoth.fusion.restapi.data.UserVerificationData;
import com.projectgoth.fusion.restapi.enums.RegistrationType;
import java.sql.Timestamp;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/registration_token")
public class RegistrationTokenResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(RegistrationTokenResource.class));

    @POST
    @Path(value="")
    @Produces(value={"application/json"})
    @Consumes(value={"application/json"})
    public DataHolder<BooleanData> generateVerificationToken(DataHolder<UserCreationData> dataholder) throws FusionRestException {
        String invitationToken;
        UserCreationData userCreationData = (UserCreationData)dataholder.data;
        if (log.isDebugEnabled()) {
            log.debug((Object)("generateVerificationToken:UserCreationData:username=[" + userCreationData.username + "],invitationToken=[" + userCreationData.invitationToken + "]" + ",campaign=[" + userCreationData.campaign + "]" + ",userAgent=[" + userCreationData.userAgent + "]" + ",emailAddress=[" + userCreationData.emailAddress + "]" + ",fbid=[" + userCreationData.fbid + "]" + ",accessToken=[" + userCreationData.accessToken + "]" + ",countryISOCode=[" + userCreationData.countryISOCode + "]" + ",registrationIPAddress=[" + userCreationData.registrationIPAddress + "]" + ",registrationDevice=[" + userCreationData.registrationDevice + "]" + ",registrationToken=[" + userCreationData.registrationToken + "]" + ",registrationType=[" + userCreationData.registrationType + "]"));
        }
        Integer invitationID = null;
        if (InvitationUtils.isInvitationEngineEnabled(null) && !StringUtil.isBlank(invitationToken = userCreationData.invitationToken) && (invitationID = Integer.valueOf(InvitationUtils.decryptReferralInvitation(invitationToken))) < 0) {
            throw new FusionRestException(FusionRestException.RestException.INVALID_REFERRAL_TOKEN);
        }
        userCreationData.initializeToDefaultValues();
        UserData userData = userCreationData.getUserData();
        if (SystemPropertyEntities.Temp.Cache.ER170_ENABLED.getValue().booleanValue()) {
            String mobileDevice;
            userData.registrationDevice = mobileDevice = DataUtils.truncateMobileDevice(userData.getRegistrationDevice(), true, String.format("new user registration '%s'", userData.username));
        }
        UserProfileData userProfileData = new UserProfileData();
        String registrationType = userCreationData.registrationType;
        String campaign = userCreationData.campaign;
        RegistrationType registrationTypeEnum = RegistrationType.fromValue(registrationType);
        if (registrationTypeEnum == null) {
            throw new FusionRestException(101, "Registration failed:Please provide a registrationType(email1 or email2)");
        }
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            AccountEntrySourceData accountEntrySourceData = new AccountEntrySourceData(userData.registrationIPAddress, null, userData.registrationDevice, userData.userAgent);
            userData = userBean.createUser(userData, userProfileData, true, new UserRegistrationContextData(campaign, false, registrationTypeEnum, invitationID), accountEntrySourceData);
        }
        catch (CreateException e) {
            throw new FusionRestException(101, "Registration failed:" + e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, "Registration failed, EJBException message:" + e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Registration failed:" + e.getMessage());
        }
        return new DataHolder<BooleanData>(new BooleanData(true));
    }

    @GET
    @Path(value="/{verificationToken}")
    @Produces(value={"application/json"})
    public DataHolder<UserVerificationData> verifyToken(@PathParam(value="verificationToken") String token, @QueryParam(value="includingExpiredToken") String includingExpiredTokenStr) throws FusionRestException {
        boolean includingExpiredToken = StringUtil.toBooleanOrDefault(includingExpiredTokenStr, false);
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserVerificationData userVerificationData = userBean.getVerificationDataFromToken(token, includingExpiredToken);
            return new DataHolder<UserVerificationData>(userVerificationData);
        }
        catch (CreateException e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, "Verification failed, EJBException message:" + e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
    }

    @PUT
    @Path(value="/{verificationToken}")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> validateRegistrationToken(@PathParam(value="verificationToken") String token) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            UserVerificationData userVerificationData = userBean.getVerificationDataFromToken(token);
            if (!userBean.validateRegistrationToken(userVerificationData)) {
                throw new Exception("Token is not valid");
            }
            return new DataHolder<BooleanData>(new BooleanData(true));
        }
        catch (CreateException e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, "Verification failed, EJBException message:" + e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
    }

    @GET
    @Path(value="/username_check/{username}")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> checkUsernameAvailability(@PathParam(value="username") String username) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            boolean isAvailable = userBean.isUsernameAvailable(username);
            return new DataHolder<BooleanData>(new BooleanData(isAvailable));
        }
        catch (CreateException e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
        catch (EJBException e) {
            throw new FusionRestException(101, "Verification failed, EJBException message:" + e.getMessage());
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Verification failed:" + e.getMessage());
        }
    }

    @GET
    @Path(value="/email_check/{email}")
    @Produces(value={"application/json"})
    public DataHolder<BooleanData> checkEmailAvailability(@PathParam(value="email") String email) throws FusionRestException {
        try {
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            boolean isAvailable = userBean.emailAddressExists(email);
            return new DataHolder<BooleanData>(new BooleanData(isAvailable));
        }
        catch (Exception e) {
            log.error((Object)("Unable to check the existence of email address [" + email + "]: " + e));
            throw new FusionRestException(FusionRestException.RestException.ERROR);
        }
    }

    @GET
    @Path(value="/invitation_token/{invitationToken}")
    @Produces(value={"application/json"})
    public DataHolder<InvitationDetailsData> getInvitationTokenData(@PathParam(value="invitationToken") String invitationTokenCode, @QueryParam(value="fetchExtraParameters") String fetchExtraParametersStr) throws FusionRestException {
        if (!InvitationUtils.isInvitationEngineEnabled(null)) {
            throw new FusionRestException(FusionRestException.RestException.INVITATION_DISABLED);
        }
        try {
            boolean fetchExtraParameters = StringUtil.toBooleanOrDefault(fetchExtraParametersStr, false);
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return new DataHolder<InvitationDetailsData>(userBean.getInvitationDetailsData(invitationTokenCode, fetchExtraParameters, new Timestamp(System.currentTimeMillis())));
        }
        catch (CreateException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path(value="/facebook_invite/{facebookRequestId}/{facebookUserId}")
    @Produces(value={"application/json"})
    public DataHolder<InvitationDetailsData> getInvitationTokenData(@PathParam(value="facebookRequestId") String facebookRequestId, @PathParam(value="facebookUserId") String facebookUserId, @QueryParam(value="fetchExtraParameters") String fetchExtraParametersStr) throws FusionRestException {
        if (!InvitationUtils.isInvitationEngineEnabled(InvitationData.ChannelType.FB)) {
            throw new FusionRestException(FusionRestException.RestException.INVITATION_DISABLED);
        }
        try {
            boolean fetchExtraParameters = StringUtil.toBooleanOrDefault(fetchExtraParametersStr, false);
            UserLocal userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            return new DataHolder<InvitationDetailsData>(userBean.getInvitationDetailsDataForFBInvite(facebookRequestId, facebookUserId, fetchExtraParameters, new Timestamp(System.currentTimeMillis())));
        }
        catch (CreateException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (EJBException e) {
            log.error((Object)"CreateException on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
        catch (Exception e) {
            log.error((Object)"Unhandled exception on getInvitationTokenData", (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.INTERNAL_SERVER_ERROR);
        }
    }
}

