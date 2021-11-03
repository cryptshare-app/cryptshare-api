package com.uni.share.group.boundary.membership;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import com.uni.share.authentication.filter.JWTSecured;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Resource for handling endpoints regarding group memberships logic.
 * For example inviting users to a group or accept a invitation
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Path("groups/membership")
@JWTSecured
@Api(tags = "Group Memberships", value = "group memberships")
public class GroupMembershipResource {

    @Inject
    private GroupMembershipBF groupMembershipBF;


    @Inject
    private UserTransaction tx;


    /**
     *
     */
    @POST
    @Path("update")
    public GroupMembershipTO update(GroupMembershipTO groupMembershipTO) {
        return groupMembershipBF.setAutoPay(groupMembershipTO);
    }


    /**
     * Invites a given user by name to an existing group by title.
     *
     * @param groupTitle    the title of the group
     * @param otherUsername the user to invite.
     * @return
     */
    @GET
    @Path("invite")
    @ApiOperation(value = "Invite other User into your Group",
            notes = "This can only be done with a valid JWT Token and if you are the owner of the Group.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "User invited successfully"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 403, message = "User with the provided ID can not invite to this Group.")

    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public GroupMembershipTO invite(@QueryParam("group") final String groupTitle,
                                    @QueryParam("user") final String otherUsername,
                                    @QueryParam("role") final GroupRoles role) {

        return groupMembershipBF.inviteUserToGroup(tx.getCurrentUser(), groupTitle, otherUsername, role);
    }


    /**
     * Accept a group invitation for the current user.
     *
     * @param groupTitle the title of the group
     * @return the group information for the group with information about the user.
     */
    @GET
    @Path("accept")
    @ApiOperation(value = "Accept a Group invitation",
            notes = "This can only be done with a valid JWT Token and if you were previously invited to the Group.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Accepted the invitation successfully"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 403, message = "User with the provided ID can not accept invitation to this Group.")

    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public GroupTO accept(@NotNull @QueryParam("group") final String groupTitle) {
        return groupMembershipBF.acceptGroupInvitation(tx.getCurrentUser(), groupTitle);
    }


    /**
     * Reject a group invitation
     *
     * @param groupTitle the title of the group for which the invitation will be rejected.
     */
    @POST
    @Path("reject")
    @ApiOperation(value = "Rejects a Group invitation",
            notes = "This can only be done with a valid JWT Token and if you were previously invited to the Group.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Reject group invitation successfully the invitation successfully"),
            @ApiResponse(code = 400, message = "User with the given name does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 403, message = "User has already accepted the invitation."),
            @ApiResponse(code = 404, message = "Group with the given name not found."),

    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public void reject(@NotNull @QueryParam("groupTitle") final String groupTitle) {
        groupMembershipBF.rejectGroupInvitation(tx.getCurrentUser(), groupTitle);
    }


    /**
     * Leave a group
     *
     * @param groupTitle the title of the group the user wants to leave.
     */
    @DELETE
    @Path("leave")
    @ApiOperation(value = "Leave a group",
            notes = "This can only be done with a valid JWT Token and if you were previously invited to the Group.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Leave group successfully"),
            @ApiResponse(code = 400, message = "User with the given name does not exist or Group with given title does not exist"),
            @ApiResponse(code = 401, message = "Authorization Information is missing or invalid"),
            @ApiResponse(code = 403, message = "User has open payments."),
            @ApiResponse(code = 405, message = "Leaving group is not allowed if user is not accepted."),

    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    public void leave(@NotNull @QueryParam("groupTitle") final String groupTitle) {
        groupMembershipBF.leaveGroup(tx.getCurrentUser(), groupTitle);
    }
}
