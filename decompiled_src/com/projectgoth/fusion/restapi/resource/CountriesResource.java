/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.ext.Provider
 *  org.apache.log4j.Logger
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.common.ConfigUtils;
import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/countries")
public class CountriesResource {
    private static final Logger log = Logger.getLogger((String)ConfigUtils.getLoggerName(CountriesResource.class));

    @GET
    @Path(value="/all")
    @Produces(value={"application/json"})
    public DataHolder<List<CountryData>> getCountries() throws FusionRestException {
        List countrylist = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countrylist = misBean.getCountries();
            return new DataHolder<List<CountryData>>(countrylist);
        }
        catch (Exception e) {
            log.error((Object)("Exception caught while removing user from blacklist: " + e.getMessage()));
            throw new FusionRestException(101, "Internal Server Error: Could not fetch countrylist");
        }
    }

    @GET
    @Path(value="/{ipNumber}")
    @Produces(value={"application/json"})
    public DataHolder<CountryData> getCountryFromIPNumber(@PathParam(value="ipNumber") String ipNumber) throws FusionRestException {
        CountryData countryData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misBean.getCountryFromIPNumber(Double.parseDouble(ipNumber));
            return new DataHolder<CountryData>(countryData);
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Internal Server Error: Could not fetch countryData for the given ipNumber");
        }
    }

    @GET
    @Path(value="/location/{id}")
    @Produces(value={"application/json"})
    public DataHolder<CountryData> getCountryByLocation(@PathParam(value="id") int id) throws FusionRestException {
        CountryData countryData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misBean.getCountryByLocation(id);
            return new DataHolder<CountryData>(countryData);
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Internal Server Error: Could not fetch countryData for the given id");
        }
    }
}

