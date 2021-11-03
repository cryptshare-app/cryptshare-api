package com.uni.share.group.control.membership;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import com.uni.share.actions.boundary.ActionsBF;
import com.uni.share.actions.entity.ActionsEM;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.group.control.GroupMapperBA;
import com.uni.share.group.entity.GroupEM;
import com.uni.share.group.entity.GroupMembershipEM;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.payments.control.PaymentBA;
import com.uni.share.payments.types.PaymentStatus;
import com.uni.share.user.entity.UserEM;
import com.uni.share.user.types.UserBE;

/**
 * Business activity for group membership {@link GroupMembershipBE} related logic.
 * Handles group invites/accepts and provides method for getting all memberships of an group.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class GroupMembershipBA {

    @Inject
    private GroupEM groupEM;

    @Inject
    private GroupMembershipEM groupMembershipEM;

    @Inject
    private GroupMapperBA groupMapperBA;

    @Inject
    private PaymentBA paymentBA;


    @Inject
    private UserEM userEM;

    @Inject
    private ActionsBF actionsBF;

    @Inject
    private GroupMembershipMapperBA groupMembershipMapperBA;


    /**
     * Invites a user to a group
     * 1. Check if the user who starts the invitation exists -> Throws exception if not.
     * 2. Check if group for the invitation exists -> Throws exception if not.
     * 3. Check if user is owner of the group -> Throws exception if user is not the owner.
     * 4. Check if user who should be invited exists -> Throws exception if user does not exist.
     * 5. Check if invited User has either membership status PENDING or ACCEPTED for the given group -> If so then an exception is thrown.
     * <p>
     * Group membership is default set to Member.
     *
     * @param userBE        the id of the user that starts the invitation
     * @param groupTitle    the title of the group
     * @param otherUsername the the user who is invited to the group.
     * @return
     */
    public GroupMembershipTO inviteUserToGroup(final UserBE userBE,
                                               final String groupTitle,
                                               final String otherUsername,
                                               final GroupRoles role) {

        // Check if user is owner of group
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle).orElseThrow(this::throwGroupNotFound);

        if (!groupBE.getUserBE().getId().equals(userBE.getId())) {
            throw new BusinessValidationException(CryptShareErrors.USER_NOT_OWNER, Response.Status.BAD_REQUEST);
        }

        // Check if other user exists
        final UserBE otherUser = userEM.findByUserName(otherUsername).orElseThrow(this::throwUserNotFound);


        final Optional<GroupMembershipBE> groupMembershipBE = groupMembershipEM.findByUserAndGroupId(otherUser.getId(),
                groupBE.getId());
        if (groupMembershipBE.isPresent() && groupMembershipBE.get().getStatus().equals(
                GroupMembershipStatus.ACCEPTED)) {
            throw new BusinessValidationException(CryptShareErrors.USER_ALREADY_MEMBER, Response.Status.BAD_REQUEST);
        }
        if (groupMembershipBE.isPresent() && groupMembershipBE.get().getStatus().equals(
                GroupMembershipStatus.PENDING)) {
            throw new BusinessValidationException(CryptShareErrors.USER_ALREADY_INVITED, Response.Status.BAD_REQUEST);
        }

        final GroupMembershipBE newMembership = new GroupMembershipBE();
        newMembership.setGroupBE(groupBE);
        newMembership.setUserBE(otherUser);
        newMembership.setRole(role);
        newMembership.setAutoPay(false);
        newMembership.setStatus(GroupMembershipStatus.PENDING);

        //Create Action
        actionsBF.create(ActionsEM.CATEGORY_GROUP,
                "newInvitation",
                "You have been invited to group " + groupTitle,
                "/dashboard/groups",
                otherUser.getId());
        
        return groupMembershipMapperBA.toTO(groupMembershipEM.persist(newMembership));
    }


    /**
     * Get all memberships for a given group if the provided user id is also the owner of the group.
     * Throws exception if the user id does not exist.
     * Throws an exception if a group with the given title does not exist.
     * Throws exception if provided user id is not the owner of the group.
     *
     * @param user       the id of the user.
     * @param groupTitle the title of the group.
     * @return a list of {@link GroupMembershipBE} for the given group.
     */
    public List<GroupMembershipBE> getGroupMemberships(final UserBE user, final String groupTitle) {
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle).orElseThrow(
                this::throwGroupNotFound);

        if (!groupBE.getUserBE().getId().equals(user.getId())) {
            throw new BusinessValidationException(CryptShareErrors.USER_NOT_OWNER, Response.Status.BAD_REQUEST);
        }
        return groupMembershipEM.getAllByGroupId(groupBE.getId());
    }


    /**
     * Accept a group invitation for a given user.
     * Checks if the user and the group exists and if the membership status is pending.
     *
     * @param user       the id of the user {@link UserBE}.
     * @param groupTitle the title of the group.
     */
    public GroupTO acceptGroupInvitation(final UserBE user, final String groupTitle) {
        //1. check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle).orElseThrow(
                this::throwGroupNotFound);

        //2. check if groupmembership exists initially, which is true when an invitation is sent
        final GroupMembershipBE groupMembershipBE = groupMembershipEM.findByUserAndGroupId(user.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        //3. If this status is not on pending then the user cannot accept the invitation
        if (!groupMembershipBE.getStatus().equals(GroupMembershipStatus.PENDING)) {
            throw new BusinessValidationException(CryptShareErrors.ACCEPT_INVITATION_NOT_POSSIBLE_NOT_PENDING,
                    Response.Status.BAD_REQUEST);
        }

        //4. Update the group membership
        groupMembershipBE.setStatus(GroupMembershipStatus.ACCEPTED);
        groupMembershipEM.merge(groupMembershipBE);


        return groupMapperBA.toTO(groupBE, user.getId());
    }


    /**
     * Reject a group invitation for a given user.
     * Checks if the user and the group exists and if the membership status is pending.
     *
     * @param currentUser the id of the user {@link UserBE}.
     * @param groupTitle  the title of the group.
     */
    public void rejectGroupInvitation(final UserBE currentUser, final String groupTitle) {
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle).orElseThrow(
                this::throwGroupNotFound);

        final GroupMembershipBE groupMembershipBE = groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);
        if (!groupMembershipBE.getStatus().equals(GroupMembershipStatus.PENDING)) {
            throw new BusinessValidationException(
                    CryptShareErrors.REJECT_INVITATION_NOT_POSSIBLE_ALREADY_ACCEPTED,
                    Response.Status.METHOD_NOT_ALLOWED);
        }
        groupMembershipEM.delete(groupMembershipBE.getId());
    }


    /**
     * Leave a group given by its title for the current user.
     *
     * @param currentUser the current user
     * @param groupTitle  the title of the group.
     */
    public void leaveGroup(final UserBE currentUser, final String groupTitle) {

        //1. Check if group exists
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle).orElseThrow(
                this::throwGroupNotFound);
        //2. check if user participates in the group
        final GroupMembershipBE groupMembershipBE = groupMembershipEM.findByUserAndGroupId(currentUser.getId(),
                groupBE.getId())
                .orElseThrow(this::throwGroupMembershipNotFound);

        //3. check if user is owner then the user can't leave the group bust must delete it
        if (groupBE.getUserBE().getId().equals(currentUser.getId())) {
            throw new BusinessValidationException(CryptShareErrors.LEAVE_GROUP_NOT_POSSIBLE_FOR_OWNER,
                    Response.Status.METHOD_NOT_ALLOWED);
        }
        //4. check if user is accepted in group membership
        if (!groupMembershipBE.getStatus().equals(GroupMembershipStatus.ACCEPTED)) {
            throw new BusinessValidationException(
                    CryptShareErrors.LEAVE_GROUP_NOT_POSSIBLE_FOR_PENDING,
                    Response.Status.METHOD_NOT_ALLOWED);

        }
        //5 check if current user has open payments in this group
        currentUser.getSendingPayments().forEach(paymentBE -> {
            if (paymentBE.getGroupBE().getId().equals(groupBE.getId()) &&
                    paymentBE.getStatus().equals(PaymentStatus.OPEN)) {
                throw new BusinessValidationException(CryptShareErrors.LEAVE_GROUP_NOT_POSSIBLE_OPEN_PAYMENTS,
                        Response.Status.FORBIDDEN);
            }
        });

        groupMembershipEM.delete(groupMembershipBE.getId());

    }


    /**
     * Update a given group membership
     *
     * @param groupMembershipTO
     * @return
     */
    public GroupMembershipTO updateGroupMembership(GroupMembershipTO groupMembershipTO) {
        GroupMembershipBE groupMembershipBE = groupMembershipEM.findById(groupMembershipTO.getId()).orElseThrow(
                this::throwGroupMembershipNotFound);
        groupMembershipBE.setAutoPay(groupMembershipTO.isAutoPay());
        return groupMembershipMapperBA.toTO(groupMembershipEM.merge(groupMembershipBE));
    }


    // ErrorProvider
    // throwUserNotFound -> ErrorEnum .> USER_NOT_FOUND = " User not found"
    private BusinessValidationException throwUserNotFound() {
        return new BusinessValidationException(CryptShareErrors.USER_NOT_FOUND, Response.Status.BAD_REQUEST);
    }


    private BusinessValidationException throwGroupNotFound() {
        return new BusinessValidationException(CryptShareErrors.GROUP_NOT_FOUND, Response.Status.BAD_REQUEST);
    }


    private BusinessValidationException throwGroupMembershipNotFound() {
        return new BusinessValidationException(CryptShareErrors.GROUP_MEMBERSHIP_NOT_FOUND,
                Response.Status.BAD_REQUEST);
    }


}
