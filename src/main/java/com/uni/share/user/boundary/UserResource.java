package com.uni.share.user.boundary;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.*;

import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.user.types.SearchUserNamesTO;
import com.uni.share.user.types.UserInformationTO;
import com.uni.share.user.types.UserTO;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * User resource for accepting request concerning action that users can execute.
 *
 * @author Felix Rottler
 */
@Path("users")
@JWTSecured
public class UserResource {


    @Inject
    private UserBF userBF;

    @Inject
    private UserTransaction tx;


    @Context
    private SecurityContext securityContext;


    @GET
    @Path("names")
    @Produces(MediaType.APPLICATION_JSON)
    public SearchUserNamesTO fetchUserNames() {
        return new SearchUserNamesTO(userBF.fetchUserNames());
    }


    @DELETE
    @Path("{name}")
    @ApiOperation(value = "Delete a User by its Name",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User successfully deleted"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid")

    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public void deleteUserByName(@ApiParam(value = "The name of the user to delete", required = true)
                                 @PathParam("name") @NotBlank final String name) {

        userBF.deleteByName(name, tx.getCurrentUser());

    }


    @PUT
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
    public void updateUser(@ApiParam(value = "The User to update", required = true) @Valid final UserTO userTO) {
        userBF.update(userTO, tx.getCurrentUser());
    }


    @GET
    @Path("current")
    public UserInformationTO getCurrentUserInformation() {
        return userBF.getCurrentUserInformation(securityContext);
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Fetches a User by the user's internal id",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User with given id successful fetched"),
            @ApiResponse(code = 404, message = "User with the given id does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})

    public UserInformationTO getUserInformationById(@ApiParam(value = "The user id", required = true)
                                                    @QueryParam("userId") final long id) {
        return userBF.findUserInformationById(id);
    }

}
