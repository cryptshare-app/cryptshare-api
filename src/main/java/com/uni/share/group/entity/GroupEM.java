package com.uni.share.group.entity;

import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.group.types.GroupBE;
import com.uni.share.user.types.UserBE;
import static com.uni.share.group.types.QGroupBE.groupBE;

/**
 * Entity Manager for Groups
 *
 * @author Felix Rottler , 09.12.2018
 **/
@Stateless
public class GroupEM extends AbstractBaseEM<GroupBE> {


    @Override
    protected Class<GroupBE> getEntityClass() {
        return GroupBE.class;
    }


    /**
     * Get all Groups.
     *
     * @return a list of groups.
     */
    public List<GroupBE> getAll() {
        return query().select(groupBE).fetch();
    }


    /**
     * Find a group by its title.
     *
     * @param title the title of the group.
     * @return an optional that could contain the group with the given title.
     */
    public Optional<GroupBE> getGroupByTitle(final String title) {
        return Optional.ofNullable(queryFactory().selectFrom(groupBE)
                .where(groupBE.title.eq(title))
                .fetchOne());

    }


    /**
     * Collect information about all groups the given user participates
     *
     * @param user the current user
     * @return a list of information about all groups
     */
    public List<GroupBE> getGroupForUser(final UserBE user) {
        return queryFactory()
                .select(groupBE)
                .from(groupBE)
                .where(groupBE.groupMembershipBES.any().userBE.id.eq(user.getId()))
                .fetch();
    }


    /**
     * Collect a single group information for a group where the current user participates in
     *
     * @param user       the current user
     * @param groupTitle the title of the group
     * @return information about the group
     */
    public Optional<GroupBE> getGroupForUserByTitle(final UserBE user, final String groupTitle) {
        return Optional.ofNullable(queryFactory()
                .select(groupBE)
                .from(groupBE)
                .where(groupBE.groupMembershipBES.any().userBE.id.eq(user.getId()))
                .where(groupBE.title.eq(groupTitle))
                .fetchFirst());
    }
}
