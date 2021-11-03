package com.uni.share.group.boundary.util;

import java.util.List;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.user.types.UserInformationTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Group utility endpoint. Used for receiving additional informations regarding groups. For example get all groups the user is participating in
 * Or get group memberships that are on pending or so on.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Path("groups/util")
@JWTSecured
@Api(tags = "Group Util", value = "group util")
public class GroupUtilResource {


    @Inject
    private UserTransaction tx;

    @Inject
    private GroupUtilBF groupUtilBF;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all Groups the user is currently participating",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Groups for the given User", response = GroupTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public List<GroupTO> getAllGroupsForUser() {

        return groupUtilBF.getAllGroupsForUser(tx.getCurrentUser());
    }


    /**
     * Endpoint for requesting all group memberships of an group for an owner of these group given by its title.
     *
     * @param status the title of the group
     * @return a list of memberships.
     */
    @GET
    @Path("{status}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all group memberships the user currently has (accepted and pending)",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group Memberships for the given User", response = GroupMembershipTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 500, message = "Invalid status")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public List<GroupMembershipTO> getMembershipForUserByStatus(
            @NotNull @PathParam("status") final GroupMembershipStatus status) {
        return groupUtilBF.getMembershipsByStatusForUser(tx.getCurrentUser(), status);
    }


    @GET
    @Path("invites")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all Group Memberships of the user where the status is currently PENDING",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group Memberships for the given User where the status is PENDING", response = GroupMembershipTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public List<GroupMembershipTO> getGroupInvitesForUser() {
        return groupUtilBF.getMembershipsByStatusForUser(tx.getCurrentUser(), GroupMembershipStatus.PENDING);
    }


    @GET
    @Path("members")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get all Group Memberships for a given Group if you are participating in this Group",
            notes = "This can only be done with a valid JWT Token.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Members for a Group", response = UserInformationTO.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Given User is not part of the group or User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid")
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public List<GroupMembershipTO> getMembershipsForGroup(
            @NotNull @QueryParam("groupTitle") final String groupTitle
    ) {
        return groupUtilBF.getGroupMembershipsByGroupTitleForUser(tx.getCurrentUser(), groupTitle);
    }
}
