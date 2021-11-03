package com.uni.share.iota.types;

import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.user.types.UserBE;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "t_iota")

public class IotaBE extends AbstractBaseBE {


    @Id
    @GeneratedValue(generator = "g_t_iota", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_iota", sequenceName = "sq_iota", allocationSize = 1)
    private Long id;

    @Column(name = "current_seed")
    private String currentSeed;

    @Column(name = "current_balance")
    private long currentBalance;

    @JoinColumn(name = "user_id")
    @OneToOne
    private UserBE userBE;

    @Column(name = "lowest_unused_index_wob")
    private int lowestUnusedIndexWithoutBalance;

    @Version
    private Long version;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public UserBE getUserBE() {
        return userBE;
    }

    public void setUserBE(UserBE userBE) {
        this.userBE = userBE;
    }

    public int getLowestUnusedIndexWithoutBalance() {
        return lowestUnusedIndexWithoutBalance;
    }

    public void setLowestUnusedIndexWithoutBalance(int lowestUnusedIndexWithoutBalance) {
        this.lowestUnusedIndexWithoutBalance = lowestUnusedIndexWithoutBalance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrentSeed() {
        return currentSeed;
    }

    public void setCurrentSeed(String currentSeed) {
        this.currentSeed = currentSeed;
    }

    public long getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(long currentBalance) {
        this.currentBalance = currentBalance;
    }
}
