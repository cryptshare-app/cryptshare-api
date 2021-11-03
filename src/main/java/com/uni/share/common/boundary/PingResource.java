package com.uni.share.common.boundary;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Simple PING resource.
 *
 * @author Felix Rottler
 */
@Path("common")
public class PingResource {

    @Inject
    private PingBF pingBF;



    /**
     * Endpoint for PING
     *
     * @return
     */
    @Path("ping")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {

        return pingBF.getPing();
    }


}
