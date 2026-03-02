/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControl;
import com.projectgoth.fusion.accesscontrol.AuthenticatedAccessControlTypeEnum;
import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.SystemProperty;
import com.projectgoth.fusion.common.SystemPropertyEntities;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.FusionEJBException;
import com.projectgoth.fusion.interfaces.UserLocal;
import com.projectgoth.fusion.interfaces.UserLocalHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.data.UserAccessData;
import com.projectgoth.fusion.restapi.enums.GuardCapabilityEnum;
import com.projectgoth.fusion.restapi.enums.MigboAccessMemberTypeEnum;
import javax.ejb.CreateException;
import javax.ws.rs.GET;
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
@Path(value="/gating")
public class GatingResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(GatingResource.class));

    @GET
    @Path(value="/{userid}/{functionid}")
    @Produces(value={"application/json"})
    public DataHolder<UserAccessData> isUserAllowedMigboAccess(@PathParam(value="userid") String userIdStr, @PathParam(value="functionid") String functionIdStr) throws FusionRestException {
        int userId = StringUtil.toIntOrDefault(userIdStr, -1);
        if (userId == -1) {
            log.error((Object)String.format("Failed to check guard due to invalid userid '%s'", userIdStr));
            throw new FusionRestException(101, "Internal error while checking access credentials");
        }
        int functionId = StringUtil.toIntOrDefault(functionIdStr, -1);
        if (functionId == -1) {
            log.error((Object)String.format("Failed to check guard due to invalid functionid '%s'", functionIdStr));
            throw new FusionRestException(101, "Internal error while checking access credentials");
        }
        GuardCapabilityEnum guardCapability = GuardCapabilityEnum.fromValue(functionId);
        if (guardCapability == null) {
            log.error((Object)String.format("Failed to check guard due to invalid functionid '%s'", functionIdStr));
            throw new FusionRestException(101, "Internal error while checking access credentials");
        }
        if (SystemProperty.getBool(new SystemPropertyEntities.GuardsetEnabled(guardCapability))) {
            UserLocal userBean = null;
            try {
                userBean = (UserLocal)EJBHomeCache.getLocalObject("UserLocal", UserLocalHome.class);
            }
            catch (CreateException e) {
                log.error((Object)("Unable to create user bean: " + e.getMessage()));
                throw new FusionRestException(101, "Internal error while checking access credentials");
            }
            try {
                if (userBean.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.WHITELIST.value(), functionId)) {
                    log.debug((Object)("User [" + userId + "] was granted access because user was in whitelist"));
                    UserAccessData data = new UserAccessData(true);
                    return new DataHolder<UserAccessData>(data);
                }
            }
            catch (FusionEJBException e) {
                log.error((Object)("Unable to check guard: " + e.getMessage()));
                throw new FusionRestException(101, "Internal error while checking access credentials");
            }
            try {
                if (userBean.isUserInMigboAccessList(userId, MigboAccessMemberTypeEnum.BLACKLIST.value(), functionId)) {
                    log.debug((Object)("User [" + userId + "] was denied access because user was in blacklist"));
                    UserAccessData data = new UserAccessData(false);
                    return new DataHolder<UserAccessData>(data);
                }
            }
            catch (FusionEJBException e) {
                log.error((Object)("Unable to check guard: " + e.getMessage()));
                throw new FusionRestException(101, "Internal error while checking access credentials");
            }
            try {
                Boolean allowedAccess = userBean.isUserLevelAllowedMigboAccess(userId, functionId);
                UserAccessData data = new UserAccessData(allowedAccess);
                return new DataHolder<UserAccessData>(data);
            }
            catch (FusionEJBException e) {
                log.error((Object)("Unable to check guard: " + e.getMessage()));
            }
        }
        UserAccessData data = new UserAccessData(true);
        return new DataHolder<UserAccessData>(data);
    }

    @GET
    @Path(value="/authenticatedaccesscontrol/{userid}/{authentiatedaccesscontroltype}")
    @Produces(value={"application/json"})
    public DataHolder<UserAccessData> isUserAllowedAuthenticatedAccess(@PathParam(value="userid") String userIdOrUsernameStr, @PathParam(value="authentiatedaccesscontroltype") String authenticatedAccessControlTypeStr, @QueryParam(value="useUsername") String useUsernameStr) throws FusionRestException {
        boolean useUsername = StringUtil.toBooleanOrDefault(useUsernameStr, false);
        int userId = -1;
        String username = null;
        if (useUsername) {
            username = userIdOrUsernameStr;
        } else {
            userId = StringUtil.toIntOrDefault(userIdOrUsernameStr, -1);
            if (userId == -1) {
                log.error((Object)String.format("Failed to check authenticated access control due to invalid userid '%s'", userIdOrUsernameStr));
                throw new FusionRestException(101, "Internal error while checking access");
            }
        }
        AuthenticatedAccessControlTypeEnum type = null;
        try {
            type = AuthenticatedAccessControlTypeEnum.valueOf(authenticatedAccessControlTypeStr);
        }
        catch (IllegalArgumentException e) {
            log.error((Object)String.format("Failed to check authenticated access control of %s due to invalid type '%s'", userIdOrUsernameStr, authenticatedAccessControlTypeStr));
            throw new FusionRestException(101, "Internal error while checking access");
        }
        boolean allowed = useUsername ? AuthenticatedAccessControl.hasAccessByUsernameLocal(type, username) : AuthenticatedAccessControl.hasAccessByUseridLocal(type, userId);
        return new DataHolder<UserAccessData>(new UserAccessData(allowed));
    }
}

