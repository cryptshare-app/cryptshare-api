package com.uni.share.group.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import com.uni.share.group.control.membership.GroupMembershipMapperBA;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.group.types.GroupTO;
import com.uni.share.group.types.enums.GroupMembershipStatus;
import com.uni.share.group.types.enums.GroupRoles;
import com.uni.share.payments.control.PaymentMapperBA;
import com.uni.share.products.control.ProductMapperBA;
import com.uni.share.user.types.UserBE;

/**
 * Mapper class to map transport objects to business objects and vice versa.
 *
 * @author Felix Rottler , 09.12.2018
 **/
@Stateless
public class GroupMapperBA {

    @Inject
    private GroupMembershipMapperBA groupMembershipMapperBA;
    @Inject
    private ProductMapperBA productMapperBA;
    @Inject
    private PaymentMapperBA paymentMapperBA;


    /**
     * Maps a given group business entity to an transport object
     *
     * @param source the group business entity to map.
     * @return the mapped group transport object.
     */
    public GroupTO toTO(final GroupBE source, final long userId) {
        final GroupTO target = new GroupTO();

        source.getGroupMembershipBES().stream().filter(it -> it.getUserBE().getId() == userId).findFirst()
                .ifPresent(it -> target.setUserMembership(groupMembershipMapperBA.toTO(it)));
        target.setId(source.getId());
        target.setDescription(source.getDescription());
        target.setTitle(source.getTitle());
        target.setImageUrl(source.getImageUrl());
        target.setProducts(productMapperBA.toTO(source.getProducts()));
        target.setGroupMemberships(groupMembershipMapperBA.toTO(source.getGroupMembershipBES()));
        target.setPayments(paymentMapperBA.toTO(source.getPayments()));
        return target;
    }


    /**
     * Maps a group transport object with its owner to an group business object.
     *
     * @param groupTO the source group transport object
     * @param owner   the owner of the group
     * @return the mapped group business entity.
     */
    public GroupBE toBE(final GroupTO groupTO, final UserBE owner) {
        final GroupBE target = new GroupBE();
        target.setTitle(groupTO.getTitle());
        target.setDescription(groupTO.getDescription());
        target.setImageUrl(groupTO.getImageUrl());

        final GroupMembershipBE membershipBE = new GroupMembershipBE();
        membershipBE.setUserBE(owner);
        membershipBE.setGroupBE(target);
        membershipBE.setStatus(GroupMembershipStatus.ACCEPTED);
        membershipBE.setRole(GroupRoles.OWNER);

        target.setGroupMembershipBES(Collections.singletonList(membershipBE));
        target.setUserBE(owner);

        return target;
    }


    /**
     * Maps a set of group business entities to an set of group transport objects
     *
     * @param source the source set to map
     * @param id
     * @return the mapped set of group transport objects.
     */
    public List<GroupTO> toTO(final List<GroupBE> source, final Long id) {
        final List<GroupTO> result = new ArrayList<>();
        source.forEach(groupBE -> result.add(toTO(groupBE, id)));
        return result;
    }
}
