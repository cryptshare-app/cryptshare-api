package com.uni.share.iota.types;

import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.user.types.UserBE;

import javax.persistence.*;

@Entity
@Table(name="t_iota_archive_seeds")
public class IotaArchiveSeedBE extends AbstractBaseBE {

    @Id
    @GeneratedValue(generator = "t_iota_archive_seeds", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "t_iota_archive_seeds", sequenceName = "sq_iota_archive_seeds", allocationSize = 1)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserBE userBE;

    private String seed;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserBE getUserBE() {
        return userBE;
    }

    public void setUserBE(UserBE userBE) {
        this.userBE = userBE;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }



}
