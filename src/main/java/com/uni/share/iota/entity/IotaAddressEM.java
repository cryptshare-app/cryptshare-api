package com.uni.share.iota.entity;

import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.iota.types.IotaAddressBE;

import java.util.List;
import java.util.Optional;

import static com.uni.share.iota.types.QIotaAddressBE.iotaAddressBE;


public class IotaAddressEM extends AbstractBaseEM<IotaAddressBE> {

    @Override
    protected Class<IotaAddressBE> getEntityClass() {
        return IotaAddressBE.class;
    }

    public Optional<List<IotaAddressBE>> findAddressesByUserID(final Long _userID) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaAddressBE)
                .where(iotaAddressBE.userBE.id.eq(_userID)).fetch());
    }

    public Optional<IotaAddressBE> findAddressesByUserIDAndIndex(final Long _userID, final int _index) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaAddressBE)
                .where(iotaAddressBE.userBE.id.eq(_userID), iotaAddressBE.index.eq(_index)).fetchOne());
    }

    public Optional<List<IotaAddressBE>> findAddressesForOrder(final Long _orderId) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaAddressBE)
                .where(iotaAddressBE.usedInOrder.eq(_orderId)).fetch());
    }

}
