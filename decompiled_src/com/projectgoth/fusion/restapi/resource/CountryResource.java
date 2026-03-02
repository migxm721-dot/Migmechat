/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.ext.Provider
 */
package com.projectgoth.fusion.restapi.resource;

import com.projectgoth.fusion.data.CountryData;
import com.projectgoth.fusion.ejb.EJBHomeCache;
import com.projectgoth.fusion.interfaces.MISLocal;
import com.projectgoth.fusion.interfaces.MISLocalHome;
import com.projectgoth.fusion.restapi.data.DataHolder;
import com.projectgoth.fusion.restapi.data.FusionRestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
@Path(value="/country")
public class CountryResource {
    @GET
    @Path(value="/{id}")
    @Produces(value={"application/json"})
    public DataHolder<CountryData> getCountry(@PathParam(value="id") int id) throws FusionRestException {
        CountryData countryData = null;
        try {
            MISLocal misBean = (MISLocal)EJBHomeCache.getLocalObject("MISLocal", MISLocalHome.class);
            countryData = misBean.getCountry(id);
            return new DataHolder<CountryData>(countryData);
        }
        catch (Exception e) {
            throw new FusionRestException(101, "Internal Server Error: Could not fetch countryData for the given id");
        }
    }
}

