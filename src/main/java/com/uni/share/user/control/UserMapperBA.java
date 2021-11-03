package com.uni.share.user.control;

import javax.ejb.Stateless;
import javax.inject.Inject;
import com.uni.share.group.control.membership.GroupMembershipMapperBA;
import com.uni.share.payments.control.PaymentMapperBA;
import com.uni.share.user.types.UserBE;
import com.uni.share.user.types.UserInformationTO;

/**
 * Mapper class for Users. For example transforming a {@link UserBE} to an {@link UserInformationTO}
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Stateless
public class UserMapperBA {

    @Inject
    private PaymentMapperBA paymentMapperBA;

    @Inject
    private GroupMembershipMapperBA groupMembershipMapperBA;


    /**
     * Maps a given user model to a representation which contains NON sensitive informations
     * about this user.
     *
     * @param userBE the user to map
     * @return the information of the user.
     */
    public UserInformationTO toInformationTO(final UserBE userBE) {
        final UserInformationTO result = new UserInformationTO();
        result.setEmail(userBE.getEmail());
        result.setUserName(userBE.getUserName());
        result.setId(userBE.getId());
        result.setReceiverPayments(paymentMapperBA.toTO(userBE.getReceivingPayments()));
        result.setSenderPayments(paymentMapperBA.toTO(userBE.getSendingPayments()));
        result.setGroupMemberships(groupMembershipMapperBA.toTO(userBE.getMemberShipBES()));

        return result;

    }
}
