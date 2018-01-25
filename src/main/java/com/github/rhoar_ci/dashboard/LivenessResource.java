package com.github.rhoar_ci.dashboard;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/live")
public class LivenessResource {
    @GET
    public Response get() {
        return Response.ok().build();
    }
}
