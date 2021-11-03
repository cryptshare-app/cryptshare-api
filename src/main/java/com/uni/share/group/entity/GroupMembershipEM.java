package com.uni.share.group.entity;

import java.util.List;
import java.util.Optional;
import com.querydsl.core.BooleanBuilder;
import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.group.types.GroupMembershipBE;
import static com.uni.share.group.types.QGroupMembershipBE.groupMembershipBE;

/**
 * Entity Manager for Group Memberships
 *
 * @author Felix Rottler , 10.12.2018
 **/
public class GroupMembershipEM extends AbstractBaseEM<GroupMembershipBE> {
    @Override
    protected Class<GroupMembershipBE> getEntityClass() {
        return GroupMembershipBE.class;
    }


    /**
     * Finds a {@link GroupMembershipBE} (link between Group and Member) for a user, specified by id
     * and a group, also specified by id.
     *
     * @param userId  the id of the user.
     * @param groupId th id of the group.
     * @return an optional that may contain the membership between group and user.
     */
    public Optional<GroupMembershipBE> findByUserAndGroupId(final Long userId, final Long groupId) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(groupMembershipBE.userBE.id.eq(userId));
        booleanBuilder.and(groupMembershipBE.groupBE.id.eq(groupId));
        return Optional.ofNullable(queryFactory().selectFrom(groupMembershipBE)
                .where(booleanBuilder)
                .fetchFirst());
    }


    /**
     * Finds all memberships for a given group
     *
     * @param groupId the id of the group
     * @return a list of memberships for the given group.
     */
    public List<GroupMembershipBE> getAllByGroupId(final Long groupId) {
        return queryFactory().selectFrom(groupMembershipBE)
                .where(groupMembershipBE.groupBE.id.eq(groupId))
                .fetch();
    }

}
