package com.uni.share.iota.entity;

import com.uni.share.common.entity.AbstractBaseEM;
import com.uni.share.iota.types.IotaArchiveSeedBE;

import java.util.List;
import java.util.Optional;

import static com.uni.share.iota.types.QIotaArchiveSeedBE.iotaArchiveSeedBE;

public class IotaArchiveSeedEM extends AbstractBaseEM<IotaArchiveSeedBE> {

    @Override
    protected Class<IotaArchiveSeedBE> getEntityClass() {
        return IotaArchiveSeedBE.class;
    }

    public Optional<List<IotaArchiveSeedBE>> findArchiveSeedsByUserID(final Long _userID) {
        return Optional.ofNullable(queryFactory().selectFrom(iotaArchiveSeedBE)
                .where(iotaArchiveSeedBE.userBE.id.eq(_userID)).fetch());
    }


}
