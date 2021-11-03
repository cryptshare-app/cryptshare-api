package com.uni.share.payments.entity;

import java.util.List;
import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.payments.types.PaymentStatus;
import static com.uni.share.payments.types.QPaymentBE.paymentBE;

/**
 * Entity manager for Payments
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class PaymentEM extends AbstractBaseEM<PaymentBE> {
    @Override
    protected Class<PaymentBE> getEntityClass() {
        return PaymentBE.class;
    }


    /**
     * Get all payments for a given user independent of the groups the user is participating in.
     *
     * @param userId the id of the user.
     * @return a list of payments {@link PaymentBE} that correspond the given userId. This includes payments where the user is sender and also the receiver.
     */
    public List<PaymentBE> getAllPaymentsForUser(final Long userId) {
        return queryFactory()
                .selectFrom(paymentBE)
                .where(paymentBE.sender.id.eq(userId).or(paymentBE.receiver.id.eq(userId)))
                .fetch();
    }


    /**
     * Get all payments for a user in which the user is the receiving part of the payment.
     *
     * @param userId the id of the user
     * @return a list of  receiving payments {@link PaymentBE} that correspond the given userId.
     */
    public List<PaymentBE> getReceivingPaymentsForUser(final Long userId) {
        return queryFactory()
                .selectFrom(paymentBE)
                .where(paymentBE.receiver.id.eq(userId))
                .fetch();

    }


    /**
     * Get all payments for a user in which the user is the sending part of the payment
     *
     * @param userId the id of the user.
     * @return a list of sending payments {@link PaymentBE} that correspond the given userId.
     */
    public List<PaymentBE> getSendingPaymentsForUser(final Long userId) {
        return queryFactory()
                .selectFrom(paymentBE)
                .where(paymentBE.sender.id.eq(userId))
                .fetch();

    }


    /**
     * Get all payments for a user that are not paid already.
     *
     * @param userId the id of the user.
     * @return a list of open payments {@link PaymentBE} that correspond the given userId.
     */
    public List<PaymentBE> getOpenPaymentsForUser(final Long userId) {
        return queryFactory()
                .selectFrom(paymentBE)
                .where(paymentBE.sender.id.eq(userId)
                        .or(paymentBE.receiver.id.eq(userId)
                                .and(paymentBE.status.eq(PaymentStatus.OPEN))))
                .fetch();

    }
}
