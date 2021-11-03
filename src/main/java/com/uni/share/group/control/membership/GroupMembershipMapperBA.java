package com.uni.share.group.control.membership;

import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupMembershipTO;

import javax.ejb.Stateless;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for switching between {@link GroupMembershipBE} and {@link GroupMembershipTO}
 *
 * @author Felix Rottler , 10.12.2018
 **/
@Stateless
public class GroupMembershipMapperBA {


    /**
     * Map a given BE to TO.
     *
     * @param source the source BE object.
     * @return the mapped transport object.
     */
    public GroupMembershipTO toTO(final GroupMembershipBE source) {
        final GroupMembershipTO target = new GroupMembershipTO();
        target.setGroupTitle(source.getGroupBE().getTitle());
        target.setInvitationStatus(source.getStatus());
        target.setUserName(source.getUserBE().getUserName());
        target.setUserRole(source.getRole());
        target.setAutoPay(source.getAutoPay());
        target.setId(source.getId());
        return target;
    }


    /**
     * Maps a Collection of BE's to transport objects
     *
     * @param groupMembershipBES the BE to map
     * @return the mapped transport objects.
     */
    public List<GroupMembershipTO> toTO(final List<GroupMembershipBE> groupMembershipBES) {
        return groupMembershipBES.stream().map(this::toTO).collect(Collectors.toList());
    }
}
