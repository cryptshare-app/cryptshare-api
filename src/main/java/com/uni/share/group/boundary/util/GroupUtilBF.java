package com.uni.share.group.boundary.util;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import com.uni.share.group.control.util.GroupUtilBA;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.user.types.UserBE;

/**
 * Facade for wrapping util group logic.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class GroupUtilBF {

    @Inject
    private GroupUtilBA groupUtilBA;


    /**
     * Get all groups where the given user participates currently
     *
     * @param user the user
     * @return a list of groups
     */
    public List<GroupTO> getAllGroupsForUser(final UserBE user) {
        return groupUtilBA.getAllGroupsForUser(user);
    }


    /**
     * Get group memberships for a user filtered by status
     *
     * @param user   the user
     * @param status the status of the membership
     * @return a list of memberships
     */
    public List<GroupMembershipTO> getMembershipsByStatusForUser(final UserBE user,
                                                                 final GroupMembershipStatus status) {
        return groupUtilBA.getMembershipByStatusForUser(user, status);
    }


    /**
     * get all group members for a group and a user. The user has to be member of this group
     *
     * @param user       the current user
     * @param groupTitle the title of the group
     * @return
     */
    public List<GroupMembershipTO> getGroupMembershipsByGroupTitleForUser(final UserBE user, final String groupTitle) {
        return groupUtilBA.getGroupMembershipsForUserByGroupTitle(user, groupTitle);

    }


    /**
     * Get all informations from all groups  the current user is participating.
     *
     * @param user the current user
     * @return information about the group
     */
    public List<GroupTO> getGroupInformationForUser(final UserBE user) {
        return groupUtilBA.getGroupInformationForUser(user);
    }


    /**
     * Get information about the group for the current user and for a specified group.
     *
     * @param user       the current user
     * @param groupTitle the title of the group for which the information is about.
     * @return information about the group
     */
    public GroupTO getGroupInformationForUserByGroupTitle(final UserBE user, final String groupTitle) {
        return groupUtilBA.getGroupInformationForUserByTitle(user, groupTitle);
    }
}
