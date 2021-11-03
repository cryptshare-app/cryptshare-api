package com.uni.share.iota.entity;

import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.iota.types.IotaOrderBE;

import java.util.List;
import java.util.Optional;

import static com.uni.share.iota.types.QIotaOrderBE.iotaOrderBE;


public class IotaOrderEM extends AbstractBaseEM<IotaOrderBE> {

    @Override
    protected Class<IotaOrderBE> getEntityClass() {
        return IotaOrderBE.class;
    }

    public Optional<List<IotaOrderBE>> getAllOrders() {
        return Optional.ofNullable(queryFactory().selectFrom(iotaOrderBE).fetch());
    }

    public Optional<IotaOrderBE> getOrderById(Long _id){
        return Optional.ofNullable(queryFactory().selectFrom(iotaOrderBE)
                .where(iotaOrderBE.id.eq(_id)).fetchOne());
    }

}
