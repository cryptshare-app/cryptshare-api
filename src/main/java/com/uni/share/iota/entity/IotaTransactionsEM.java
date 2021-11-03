package com.uni.share.iota.entity;

import java.util.List;
import java.util.Optional;
import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.iota.types.IotaTransactionsBE;
import static com.uni.share.iota.types.QIotaTransactionsBE.iotaTransactionsBE;

public class IotaTransactionsEM extends AbstractBaseEM<IotaTransactionsBE> {


    public static final int STATUS_PENDING = 0;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_WAITING = 2;


    @Override
    protected Class<IotaTransactionsBE> getEntityClass() {
        return IotaTransactionsBE.class;
    }


    public Optional<List<IotaTransactionsBE>> findTransactionsByUserID(final Long _userID) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaTransactionsBE)
                .where(iotaTransactionsBE.receiverId.eq(_userID).or(iotaTransactionsBE.senderId.eq(_userID))).fetch());
    }


    public List<IotaTransactionsBE> findConfirmedTransactionsByUserID(final Long _userID) {
        return queryFactory().selectFrom(iotaTransactionsBE)
                .where((iotaTransactionsBE.receiverId.eq(_userID).or(iotaTransactionsBE.senderId.eq(_userID))).and(
                        iotaTransactionsBE.status.eq(STATUS_CONFIRMED))).fetch();
    }


    public Optional<List<IotaTransactionsBE>> findTransactionsByStatus(final int _status) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaTransactionsBE)
                .where(iotaTransactionsBE.status.eq(_status)).fetch());
    }


    public Optional<IotaTransactionsBE> findTransactionByReceiverAddress(final String _receiverAddress) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaTransactionsBE)
                .where(iotaTransactionsBE.receiverAddress.eq(_receiverAddress)).fetchOne());
    }


    public Optional<List<IotaTransactionsBE>> findTransactionsByStatusAndAmount0(final int _status) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaTransactionsBE)
                .where(iotaTransactionsBE.status.eq(_status), iotaTransactionsBE.amount.eq(0L)).fetch());
    }


}
