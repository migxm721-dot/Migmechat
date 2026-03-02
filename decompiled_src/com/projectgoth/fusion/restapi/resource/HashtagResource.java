/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.ext.Provider
 *  org.json.JSONObject
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.json.JSONObject;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/hashtag")
public class HashtagResource {
    @GET
    @Path(value="/countries")
    @Produces(value={"application/json"})
    public DataHolder<List<CountryData>> getCountriesSupporetdHashtag() throws FusionRestException {
        List countryData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misBean.getCountriesSupportedHashtag();
            return new DataHolder<List<CountryData>>(countryData);
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Internal Server Error: Could not fetch supported hashtag country");
        }
    }

    @POST
    @Path(value="/description")
    @Produces(value={"application/json"})
    public void updateHashTagDescription(@QueryParam(value="hashtag") String hashtag, @QueryParam(value="countryId") int countryId, String jsonString) throws FusionRestException {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            misBean.updateHashTagData("#" + hashtag, countryId, jsonObject.getString("description"));
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Internal Server Error: Could not update description hashtag");
        }
    }
}

