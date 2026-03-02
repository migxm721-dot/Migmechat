/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.Enums;
import com.projectgoth.fusion.common.ExceptionHelper;
import com.projectgoth.fusion.common.PasswordUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.common.metrics.MetricsEnums;
import com.projectgoth.fusion.common.metrics.MetricsLogger;
import com.projectgoth.fusion.data.ValidateCredentialResult;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.ejb.EJBIcePrxFinder;
import com.projectgoth.fusion.interfaces.MessageLocal;
import com.projectgoth.fusion.interfaces.MessageLocalHome;
import com.projectgoth.fusion.interfaces.User;
import com.projectgoth.fusion.interfaces.UserHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.slice.Message;
import com.projectgoth.fusion.slice.UserNotificationServicePrx;
import java.util.HashMap;
import java.util.Iterator;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/test")
public class TestResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(TestResource.class));

    @GET
    @Path(value="/ping")
    @Produces(value={"application/json"})
    public DataHolder<String> getPingTest() {
        MetricsLogger.log(MetricsEnums.FusionRestMetrics.TEST_PING, "", 1);
        return new DataHolder<String>("OK");
    }

    @GET
    @Path(value="/password")
    @Produces(value={"application/json"})
    public DataHolder<ValidateCredentialResult> validatePassword(@QueryParam(value="username") String username, @QueryParam(value="password") String password) {
        return new DataHolder<ValidateCredentialResult>(PasswordUtils.validatePassword(username, password));
    }

    @POST
    @Path(value="/email/{username}")
    public DataHolder<String> sendTestMig33Email(@PathParam(value="username") String username, String jsonData) throws FusionRestException {
        try {
            JSONObject json = new JSONObject(jsonData);
            String senderPassword = json.getString("password");
            String to = json.getString("to");
            String subject = json.getString("subject");
            String content = json.getString("content");
            MessageLocal messageBean = (MessageLocal)EJBHomeCache.getLocalObject("MessageLocal", MessageLocalHome.class);
            messageBean.sendEmail(username, senderPassword, to, subject, content);
            return new DataHolder<String>("OK");
        }
        catch (CreateException e) {
            return new DataHolder<String>(ExceptionHelper.getRootMessage((Exception)((Object)e)));
        }
        catch (EJBException e) {
            return new DataHolder<String>(ExceptionHelper.setErrorMessage(e.getMessage()));
        }
        catch (JSONException e) {
            return new DataHolder<String>(ExceptionHelper.setErrorMessage(e.getMessage()));
        }
    }

    @POST
    @Path(value="/alerts/{userid}/{type}")
    @Produces(value={"application/json"})
    public DataHolder<String> createTestAlert(@PathParam(value="userid") String useridStr, @PathParam(value="type") String typeStr, String jsondata) throws FusionRestException {
        int userid = StringUtil.toIntOrDefault(useridStr, -1);
        if (userid == -1) {
            log.error((Object)String.format("Invalid userid '%s' specified", useridStr));
            throw new FusionRestException(-1, String.format("Invalid userid '%s' specified", useridStr));
        }
        int type = StringUtil.toIntOrDefault(typeStr, -1);
        Enums.NotificationTypeEnum notfnType = Enums.NotificationTypeEnum.fromType(type);
        if (notfnType == null) {
            log.error((Object)String.format("Invalid type '%s' specified", typeStr));
            throw new FusionRestException(-1, String.format("Invalid type '%s' specified", typeStr));
        }
        try {
            User userBean = (User)EJBHomeCache.getObject("ejb/User", UserHome.class);
            String username = userBean.getUsernameByUserid(userid, null);
            if (StringUtil.isBlank(username)) {
                throw new FusionRestException(101, String.format("User with id '%d' not found", userid));
            }
            UserNotificationServicePrx unsProxy = EJBIcePrxFinder.getUserNotificationServiceProxy();
            String key = "test-" + System.currentTimeMillis();
            HashMap<String, String> parameters = new HashMap<String, String>();
            JSONObject jobj = new JSONObject(jsondata);
            Iterator iter = jobj.keys();
            while (iter.hasNext()) {
                String paramkey = (String)iter.next();
                String value = jobj.getString(paramkey);
                parameters.put(paramkey, value);
            }
            Message m = new Message(key, userid, username, notfnType.getType(), System.currentTimeMillis(), parameters);
            unsProxy.notifyFusionUser(m);
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught %s", e.getMessage()), (Throwable)e);
            return new DataHolder<String>("ERROR");
        }
        return new DataHolder<String>("OK");
    }
}

