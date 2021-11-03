package com.uni.share.group.control.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.group.control.GroupMapperBA;
import com.uni.share.group.control.membership.GroupMembershipMapperBA;
import com.uni.share.group.entity.GroupEM;
import com.uni.share.group.entity.GroupMembershipEM;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.user.types.UserBE;

/**
 * Helpers/Util class for group functionality. For example get all participating members for a group
 * and so on.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class GroupUtilBA {


    @Inject
    private GroupEM groupEM;

    @Inject
    private GroupMembershipEM groupMembershipEM;

    @Inject
    private GroupMapperBA groupMapperBA;

    @Inject
    private GroupMembershipMapperBA groupMembershipMapperBA;


    /**
     * Get all groups where the given user is currently participating
     *
     * @param user the user for which all groups need to be found
     * @return a list of groups where the user participates
     */
    public List<GroupTO> getAllGroupsForUser(final UserBE user) {
        return groupMapperBA.toTO(groupEM.getGroupForUser(user),user.getId());
    }


    /**
     * Get all memberships for a user filtered by a given status
     *
     * @param user   the user for which the request is for
     * @param status the status for filtering
     * @return a list of memberships
     */
    public List<GroupMembershipTO> getMembershipByStatusForUser(final UserBE user, final GroupMembershipStatus status) {
        return groupMembershipMapperBA.toTO(
                user.getMemberShipBES().stream().filter(it -> it.getStatus().equals(status)).collect(
                        Collectors.toList()));

    }


    /**
     * Get all group members in a group given by its title for a given user.
     * The given user has to be part of this group and the group must exist.
     *
     * @param user       the user for which the request is for.
     * @param groupTitle the title of the group.
     * @return a list of user information that participate in the given group
     */
    public List<GroupMembershipTO> getGroupMembershipsForUserByGroupTitle(final UserBE user, final String groupTitle) {
        // check if group exists
        final GroupBE groupBE = validateGroupExists(groupTitle);

        // check if user that requests information is part of the group
        final Optional<GroupMembershipBE> membershipBE = groupMembershipEM.findByUserAndGroupId(
                user.getId(),
                groupBE.getId());
        if (!membershipBE.isPresent()) {
            throw throwUserNotPartOfGroup();

        } else {
            final GroupMembershipBE membership = membershipBE.get();
            if (membership.getStatus().equals(GroupMembershipStatus.PENDING)) {
                throw throwUserNotPartOfGroup();
            }
        }


        // get all memberships from this group
        final List<GroupMembershipBE> allMembers = groupBE.getGroupMembershipBES();
        return groupMembershipMapperBA.toTO(allMembers);
    }


    /**
     * Get a single membership that correspondes to the user and the given group title.
     *
     * @param user       the user for which the memberships is looked up.
     * @param groupTitle the title of the group for which the membership should be looked up.
     * @return a membership object if the given user participates in a existing group  with the given title.
     */
    public GroupMembershipTO getGroupMembershipForUserByGroupTitle(final UserBE user, final String groupTitle) {
        final GroupBE groupBE = validateGroupExists(groupTitle);


        // check if user that requests information is part of the group
        final Optional<GroupMembershipBE> membershipBE = groupMembershipEM.findByUserAndGroupId(
                user.getId(),
                groupBE.getId());
        if (!membershipBE.isPresent()) {
            throw throwUserNotPartOfGroup();

        } else {
            final GroupMembershipBE membership = membershipBE.get();
            if (membership.getStatus().equals(GroupMembershipStatus.PENDING)) {
                throw throwUserNotPartOfGroup();
            }
            return groupMembershipMapperBA.toTO(membership);
        }
    }


    /**
     * Get all group informations for a given user
     *
     * @param user the user for which the group information should be looked up
     * @return a list of group informations.
     */
    public List<GroupTO> getGroupInformationForUser(final UserBE user) {
        final List<GroupBE> groupInformationForUser = groupEM.getGroupForUser(user);
        return groupMapperBA.toTO(groupInformationForUser, user.getId());
    }


    /**
     * Get a single group information for a given user that participates in a existing group with the given title.
     *
     * @param user       the user for which the request is executed for.
     * @param groupTitle the title of the group
     * @return the information about the group.
     */
    public GroupTO getGroupInformationForUserByTitle(final UserBE user, final String groupTitle) {
        // check if group exists
        final GroupBE groupBE = validateGroupExists(groupTitle);

        // check if user that requests information is part of the group
        final Optional<GroupMembershipBE> membershipBE = groupMembershipEM.findByUserAndGroupId(
                user.getId(),
                groupBE.getId());
        if (!membershipBE.isPresent()) {
            throw throwUserNotPartOfGroup();

        }
        return groupMapperBA.toTO(groupBE, user.getId());
    }


    private GroupBE validateGroupExists(final String groupTitle) {
        // check if group exists
        return groupEM.getGroupByTitle(groupTitle).orElseThrow(
                () -> new BusinessValidationException(CryptShareErrors.GROUP_NOT_FOUND, Response.Status.BAD_REQUEST)
        );
    }


    private BusinessValidationException throwUserNotPartOfGroup() {
        return new BusinessValidationException(CryptShareErrors.USER_IS_NOT_MEMBER_OF_GROUP,
                Response.Status.METHOD_NOT_ALLOWED);
    }

}
