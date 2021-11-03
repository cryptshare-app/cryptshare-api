package com.uni.share.payments.control;

import java.util.List;
import java.util.stream.Collectors;

import com.uni.share.group.types.GroupBE;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.payments.types.PaymentStatus;
import com.uni.share.payments.types.PaymentTO;
import com.uni.share.products.types.ProductBE;
import com.uni.share.user.types.UserBE;

/**
 * Mapper class to build frontend transport objects for Payments {@link PaymentBE}
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class PaymentMapperBA {

    /**
     * Maps a given payment {@link PaymentBE} to an payment transport object {@link PaymentTO}
     *
     * @param source the spurce payment object
     * @return the mapped payment transport object.
     */
    public PaymentTO toTO(final PaymentBE source) {
        return PaymentTO.newInstance()
                .withAmount(source.getAmount())
                .withGroupId(source.getGroupBE().getId())
                .withGroupTitle(source.getGroupBE().getTitle())
                .withId(source.getId())
                .withReceiverId(source.getReceiver().getId())
                .withReceiverName(source.getReceiver().getUserName())
                .withSenderId(source.getSender().getId())
                .withSenderName(source.getSender().getUserName())
                .withCreatedAt(source.getCreatedAt().toString())
                .withUpdatedAt(source.getUpdatedAt().toString())
                .withProductId(source.getProduct().getId())
                .withStatus(source.getStatus().toString())
                .build();
    }


    /**
     * Maps a list of given payments {@link PaymentBE} to a list of payment transport objects {@link PaymentTO}
     *
     * @param payments the source pamyent objects
     * @return the list of mapped paxment transport objects
     */
    public List<PaymentTO> toTO(final List<PaymentBE> payments) {
        return payments.stream().map(this::toTO).collect(Collectors.toList());
    }
}
