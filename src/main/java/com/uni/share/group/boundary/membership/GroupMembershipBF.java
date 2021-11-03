package com.uni.share.group.boundary.membership;

import com.uni.share.group.boundary.GroupBF;
import com.uni.share.group.control.membership.GroupMembershipBA;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.user.boundary.UserBF;
import com.uni.share.user.types.UserBE;
import com.uni.share.websocket.WebsocketEndpoint;

import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Business Facade to wrap group membership related business code.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class GroupMembershipBF {

    @Inject
    private GroupMembershipBA groupMembershipBA;

    @Inject
    private GroupBF groupBF;

    @Inject
    private UserBF userBF;

    /**
     * Invite a given user by name to an existing group
     *  @param userBE        the owner id of the group
     * @param groupTitle    the title of the group
     * @param otherUsername the name of the user that gets invited.
     * @return
     */
    public GroupMembershipTO inviteUserToGroup(final UserBE userBE,
                                               final String groupTitle,
                                               final String otherUsername,
                                               final GroupRoles role) {
        GroupMembershipTO groupMembershipTO = groupMembershipBA.inviteUserToGroup(userBE, groupTitle, otherUsername, role);
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED,groupTitle,false,groupBF.getAllUserIdsByGroupTitle(groupTitle));
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.NEW_GROUP_INVITATION,groupTitle,false, userBF.findUserByUserName(otherUsername).getId());
        return groupMembershipTO;
    }


    /**
     * Accept a group invitation
     *
     * @param user       the current user
     * @param groupTitle the title of the group for which the invitation is for.
     * @return information about the group
     */
    public GroupTO acceptGroupInvitation(final UserBE user, final String groupTitle) {
        GroupTO groupTO = groupMembershipBA.acceptGroupInvitation(user, groupTitle);
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED,groupTitle,false,groupBF.getAllUserIdsByGroupTitle(groupTitle));
        return groupTO;

    }


    /**
     * Reject a group invitation
     *
     * @param currentUser the current user
     * @param groupTitle  the title of the group for which the invitation will be rejected.
     */
    public void rejectGroupInvitation(final UserBE currentUser, final String groupTitle) {
        groupMembershipBA.rejectGroupInvitation(currentUser, groupTitle);
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED,groupTitle,false,groupBF.getAllUserIdsByGroupTitle(groupTitle));
    }


    /**
     * Leave a group identified by its title
     *
     * @param currentUser the user that wants to leave the group
     * @param groupTitle  the title of the group.
     */
    public void leaveGroup(final UserBE currentUser, final String groupTitle) {
        groupMembershipBA.leaveGroup(currentUser, groupTitle);
        WebsocketEndpoint.sendMessage(WebsocketEndpoint.GROUP_CHANGED,groupTitle,false,groupBF.getAllUserIdsByGroupTitle(groupTitle));
    }

    /**
     * Set auto pay in a specific group for a specified membership.
     *
     * @param groupMembershipTO the membership where auto pay will be set.
     * @return
     */
    public GroupMembershipTO setAutoPay(GroupMembershipTO groupMembershipTO) {
        return groupMembershipBA.updateGroupMembership(groupMembershipTO);

    }
}
