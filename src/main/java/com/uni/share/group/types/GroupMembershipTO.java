package com.uni.share.group.types;

import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;

/**
 * Group membership transport object to expose relation between group and user.
 *
 * @author Felix Rottler , 07.12.2018
 **/
public class GroupMembershipTO {

    private Long id;
    private String groupTitle;
    private String userName;

    private GroupRoles userRole;
    private GroupMembershipStatus invitationStatus;
    private boolean autoPay;

    /**
     * Empty constructor
     */
    public GroupMembershipTO() {
        // needs to be empty
    }


    /**
     * Parameterized constructor for group memberships
     *
     * @param groupTitle       the title of the group
     * @param userName         the name of the user
     * @param userRole         the role the user has in this group
     * @param invitationStatus the status of the invitation
     */
    public GroupMembershipTO(final String groupTitle, final String userName, final GroupRoles userRole,
                             final GroupMembershipStatus invitationStatus) {
        this.groupTitle = groupTitle;
        this.userName = userName;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isAutoPay() {
        return autoPay;
    }

    public void setAutoPay(boolean autoPay) {
        this.autoPay = autoPay;
    }

    public String getGroupTitle() {
        return groupTitle;
    }


    public void setGroupTitle(final String groupTitle) {
        this.groupTitle = groupTitle;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(final String userName) {
        this.userName = userName;
    }


    public GroupRoles getUserRole() {
        return userRole;
    }


    public void setUserRole(final GroupRoles userRole) {
        this.userRole = userRole;
    }


    public GroupMembershipStatus getInvitationStatus() {
        return invitationStatus;
    }


    public void setInvitationStatus(final GroupMembershipStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
