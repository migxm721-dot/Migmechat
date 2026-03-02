/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.common.StringUtil;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import com.projectgoth.fusion.restapi.util.MigboApiUtil;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import org.json.JSONObject;

@Provider
@Path(value="/migbo-datasvc-proxy")
public class MigboDataserviceProxyResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(MigboDataserviceProxyResource.class));

    @GET
    @DELETE
    @Path(value="{urlToProxy:.*}")
    @Produces(value={"application/json"})
    public Response httpMethodWithNoBody(@PathParam(value="urlToProxy") String urlToProxy, @Context HttpServletRequest req) throws FusionRestException {
        if (StringUtil.isBlank(urlToProxy)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Path cannot be empty");
        }
        JSONObject response = null;
        String path = this.getFullPathWithQuery(urlToProxy, req);
        String method = req.getMethod();
        try {
            log.info((Object)String.format("Proxying [%s %s]", method, path));
            MigboApiUtil apiUtil = MigboApiUtil.getInstance();
            if ("get".equalsIgnoreCase(method)) {
                response = apiUtil.get(path);
            } else if ("delete".equalsIgnoreCase(method)) {
                response = apiUtil.delete(path);
            }
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while proxying [%s %s] : %s", method, path, e.getMessage()), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        if (response != null) {
            return Response.ok((Object)response.toString()).build();
        }
        throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Unexpected error while proxying [%s]. Path: %s", urlToProxy, path));
    }

    @POST
    @PUT
    @Path(value="{urlToProxy:.*}")
    @Produces(value={"application/json"})
    public Response post(@PathParam(value="urlToProxy") String urlToProxy, @Context HttpServletRequest req, String jsonData) throws FusionRestException {
        if (StringUtil.isBlank(urlToProxy)) {
            throw new FusionRestException(FusionRestException.RestException.ERROR, "Path cannot be empty");
        }
        JSONObject response = null;
        String path = this.getFullPathWithQuery(urlToProxy, req);
        String method = req.getMethod();
        try {
            log.info((Object)String.format("Proxying [%s %s]", method, path));
            MigboApiUtil apiUtil = MigboApiUtil.getInstance();
            if ("post".equalsIgnoreCase(method)) {
                response = apiUtil.post(path, jsonData);
            } else if ("put".equalsIgnoreCase(method)) {
                response = apiUtil.put(path, jsonData);
            }
        }
        catch (Exception e) {
            log.error((Object)String.format("Exception caught while proxying [%s %s] : %s", method, path, e.getMessage()), (Throwable)e);
            throw new FusionRestException(FusionRestException.RestException.ERROR, e.getMessage());
        }
        if (response != null) {
            return Response.ok((Object)response.toString()).build();
        }
        throw new FusionRestException(FusionRestException.RestException.ERROR, String.format("Unexpected error while proxying [%s]", urlToProxy));
    }

    private String getFullPathWithQuery(String urlToProxy, HttpServletRequest req) {
        StringBuffer sb = new StringBuffer();
        sb.append("/");
        sb.append(urlToProxy);
        String queryString = req.getQueryString();
        if (!StringUtil.isBlank(queryString)) {
            sb.append("?");
            sb.append(queryString);
        }
        return sb.toString();
    }
}

