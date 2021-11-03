package com.uni.share.group.control;

import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.group.entity.GroupEM;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupTO;
import com.uni.share.user.types.UserBE;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business Activity for Group related logic like creating or finding groups.
 *
 * @author Felix Rottler , 07.12.2018
 **/
@Stateless
public class GroupBA {

    @Inject
    private GroupMapperBA groupMapperBA;

    @Inject
    private GroupEM groupEM;


    /**
     * Checks if a group with the given title exists and returns this particular group
     *
     * @param groupTitle the title of the group
     * @return the identified group
     */
    public GroupTO findGroupByTitle(final String groupTitle, final UserBE user) {
        final GroupBE groupBE = groupEM.getGroupByTitle(groupTitle)
                .orElseThrow(() -> groupDoesNotExist(groupTitle));
        return groupMapperBA.toTO(groupBE, user.getId());
    }


    /**
     * Creates a given group with owner id.
     *
     * @param groupTO the group to create
     * @param owner   the owner of the group
     * @return the group that was created.
     */
    public GroupTO create(final GroupTO groupTO, final UserBE owner) {
        final Optional<GroupBE> current = groupEM.getGroupByTitle(groupTO.getTitle());
        if (current.isPresent()) {
            throw groupAlreadyExists(groupTO.getTitle());
        }
        final GroupBE groupBE = groupMapperBA.toBE(groupTO, owner);
        final GroupBE persistedGroupBE = groupEM.persist(groupBE);
        return groupMapperBA.toTO(persistedGroupBE, owner.getId());
    }


    /**
     * Delete a group by its title where the given user id is also the owner of the group.
     *
     * @param title the title of the group.
     * @param owner the user id;
     */
    public void deleteByTitle(final String title, final UserBE owner) {

        final GroupBE result = groupEM.getGroupByTitle(title)
                .orElseThrow(() -> groupDoesNotExist(title));
        if (!result.getUserBE().getId().equals(owner.getId())) {
            throwUserIsNotAllowedToDestroyGroup();
        }
        groupEM.delete(result.getId());

    }


    /**
     * Update a given group where the user id is the owner of the group
     *
     * @param groupTO the group to update
     * @param owner   the id of the user.
     * @return the updated group.
     */
    public GroupTO update(final String groupToUpdate, final GroupTO groupTO, final UserBE owner) {

        final GroupBE toBeMerged = groupEM.getGroupByTitle(groupToUpdate)
                .orElseThrow(() -> groupDoesNotExist(groupTO.getTitle()));
        if (!toBeMerged.getUserBE().getId().equals(owner.getId())) {
            throwUserIsNotAllowedToUpdateGroup();
        }
        toBeMerged.setTitle(groupTO.getTitle());
        toBeMerged.setDescription(groupTO.getDescription());

        groupEM.merge(toBeMerged);
        return groupMapperBA.toTO(toBeMerged, owner.getId());
    }


    // TODO wird ausgelagert ExceptionHandling
    private BusinessValidationException groupDoesNotExist(final String title) {
        return new BusinessValidationException(CryptShareErrors.GROUP_NOT_FOUND,
                Response.Status.NOT_FOUND);
    }


    // TODO wird ausgelagert ExceptionHandling
    private BusinessValidationException groupAlreadyExists(final String title) {
        return new BusinessValidationException(CryptShareErrors.GROUP_ALREADY_EXISTS,
                Response.Status.BAD_REQUEST);
    }


    private BusinessValidationException throwUserIsNotAllowedToDestroyGroup() {
        throw new BusinessValidationException(CryptShareErrors.USER_NOT_ALLOWED_DELETE_GROUP,
                Response.Status.FORBIDDEN);
    }


    private void throwUserIsNotAllowedToUpdateGroup() {
        throw new BusinessValidationException(CryptShareErrors.USER_NOT_ALLOWED_UPDATE_GROUP,
                Response.Status.FORBIDDEN);
    }


    public Long[] getAllUserIdsByGroupTitle(String groupTitle) {
        Optional<GroupBE> groupByTitle = groupEM.getGroupByTitle(groupTitle);
        if (groupByTitle.isPresent()) {
            List<Long> userIds = groupByTitle.get().getGroupMembershipBES().stream().map(it -> it.getUserBE().getId()).collect(Collectors.toList());
            Long[] userIdArr = new Long[userIds.size()];
            return userIds.toArray(userIdArr);
        }
        return new Long[0];
    }
}
