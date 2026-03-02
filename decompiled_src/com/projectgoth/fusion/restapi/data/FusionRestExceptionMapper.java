/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.ext.ExceptionMapper
 *  javax.ws.rs.ext.Provider
 */
package com.projectgoth.fusion.restapi.data;

import com.projectgoth.fusion.restapi.data.FusionRestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Provider
public class FusionRestExceptionMapper
implements ExceptionMapper<FusionRestException> {
    public Response toResponse(FusionRestException e) {
        return Response.ok().entity((Object)e.getEntity()).build();
    }
}

