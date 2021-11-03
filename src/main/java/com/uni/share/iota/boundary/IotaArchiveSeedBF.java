package com.uni.share.iota.boundary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.iota.entity.IotaArchiveSeedEM;
import com.uni.share.iota.types.ArchiveSeedTO;
import com.uni.share.iota.types.IotaArchiveSeedBE;
import com.uni.share.user.types.UserBE;

@Stateless
public class IotaArchiveSeedBF {

    @Inject
    IotaArchiveSeedEM iotaArchiveSeedEM;


    public IotaArchiveSeedBE create(String _seed, UserBE _user) {
        IotaArchiveSeedBE newArchiveSeed = new IotaArchiveSeedBE();
        newArchiveSeed.setSeed(_seed);
        newArchiveSeed.setUserBE(_user);
        return iotaArchiveSeedEM.persist(newArchiveSeed);
    }


    public List<ArchiveSeedTO> getArchiveSeedsForUser(Long _userID) {
        Optional<List<IotaArchiveSeedBE>> o = iotaArchiveSeedEM.findArchiveSeedsByUserID(_userID);
        if (o.isPresent()) {
            List<IotaArchiveSeedBE> iotaArchiveSeedBEs = o.get();
            List<ArchiveSeedTO> seeds = new ArrayList<ArchiveSeedTO>();
            for (IotaArchiveSeedBE be : iotaArchiveSeedBEs) {
                ArchiveSeedTO newArchiveSeedTo = new ArchiveSeedTO();
                newArchiveSeedTo.setSeed(be.getSeed());
                newArchiveSeedTo.setCreatedAt(be.getCreatedAt());
                seeds.add(newArchiveSeedTo);
            }
            return seeds;
        } else {
            throw new BusinessValidationException(CryptShareErrors.IOTA_NO_ARCHIVE_SEED, Response.Status.NO_CONTENT);
        }
    }
}
