package com.uni.share.group.boundary;

import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.group.types.GroupTO;
import io.swagger.annotations.*;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * I'am the endpoint for group related logic. Each Request must provide an valid JWT Token.
 *
 * @author Felix Rottler , 07.12.2018
 **/
@Path("groups")
@JWTSecured
@Api(tags = "Groups", value = "groups")
public class GroupResource {

    @Inject
    private GroupBF groupBF;

    @Inject
    private UserTransaction userTx;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get a Group by its title",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Group successfully fetched"),
            @ApiResponse(code = 400, message = "Group with the given Title does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public GroupTO getGroup(
            @QueryParam("groupTitle")
            @ApiParam(value = "The Group to fetch", required = true) final String groupTitle) {
        return groupBF.findGroupByTitle(groupTitle, userTx.getCurrentUser());
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a Group by the given body",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Group successfully created"),
            @ApiResponse(code = 400, message = "Group with the given Title already exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public GroupTO create(@Valid @ApiParam(value = "The Group to create", required = true) final GroupTO groupTO) {
        return groupBF.create(groupTO, userTx.getCurrentUser());

    }


    @DELETE
    @Path("delete")
    @ApiOperation(value = "Delete a Group by its Name",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Group successfully deleted"),
            @ApiResponse(code = 400, message = "Group with the given Title does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 403, message = "User with the provided ID can not delete the Group.")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public void delete(@ApiParam(value = "The title of the group to delete", required = true)
                       @QueryParam("name") final String name) {
        groupBF.deleteByTitle(name, userTx.getCurrentUser());

    }


    @PUT
    @Path("{groupToUpdate}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Updates a Group by the given body",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Group successfully updated"),
            @ApiResponse(code = 400, message = "Group with the given Title already exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public void update(@ApiParam(value = "The title of the group to delete", required = true)
                       @PathParam("groupToUpdate") final String groupToUpdate,
                       @Valid @ApiParam(value = "The Group to update", required = true) final GroupTO groupTO) {
        groupBF.update(groupToUpdate, groupTO, userTx.getCurrentUser());
    }
}
