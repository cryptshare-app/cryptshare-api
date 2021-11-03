package com.uni.share.actions.boundary;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.uni.share.actions.control.ActionsMapperBA;
import com.uni.share.actions.types.ActionsBE;
import com.uni.share.actions.types.ActionsTO;
import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;

/**
 * Endpoint for acquiring actions related to events that occur in the cryptshare api
 */
@Path("actions")
@JWTSecured
public class ActionsResource {


    @Inject
    private UserTransaction tx;

    @Inject
    private ActionsBF actionsBF;

    @Inject
    private ActionsMapperBA actionsMapperBA;


    /**
     * Get all actions related to the user that performs the request
     *
     * @return a list of all action
     */
    @Path("getAllActions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ActionsTO> getAllActions() {
        List<ActionsBE> allActions = actionsBF.getAllActionsForUser(tx.getCurrentUser().getId());
        return actionsMapperBA.toTO(allActions);
    }


}
