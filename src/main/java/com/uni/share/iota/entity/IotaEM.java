package com.uni.share.iota.entity;

import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.iota.types.AddressTO;
import com.uni.share.iota.types.IotaBE;
import com.uni.share.iota.types.QIotaBE;
import com.uni.share.user.types.QUserBE;
import com.uni.share.user.types.UserBE;

import java.util.List;
import java.util.Optional;

import static com.uni.share.iota.types.QIotaBE.*;
import static com.uni.share.user.types.QUserBE.*;

public class IotaEM extends AbstractBaseEM<IotaBE> {

    @Override
    protected Class<IotaBE> getEntityClass() {
        return IotaBE.class;
    }

    public Optional<IotaBE> findIotaByUserID(final Long _userID) {
       return Optional.ofNullable(queryFactory().selectFrom(iotaBE)
               .where(iotaBE.userBE.id.eq(_userID)).fetchOne());
    }

}
