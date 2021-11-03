package com.uni.share.iota.types;

import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.user.types.UserBE;

import javax.persistence.*;

@Entity
@Table(name = "t_iota_addresses")
public class IotaAddressBE extends AbstractBaseBE {

    @Id
    @GeneratedValue(generator = "t_iota_addresses", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "t_iota_addresses", sequenceName = "sq_iota_addresses", allocationSize = 1)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserBE userBE;

    @Column(name = "address")
    private String address;

    @Column(name = "balance")
    private long balance;

    @Column(name = "index")
    private int index;

    @Version
    private Long version;

    @Column(name = "balance_before_order")
    private Long balanceBeforeOrder;

    @Column(name = "used_in_order")
    private Long usedInOrder;


    public Long getBalanceBeforeOrder() {
        return balanceBeforeOrder;
    }

    public void setBalanceBeforeOrder(Long balanceBeforeOrder) {
        this.balanceBeforeOrder = balanceBeforeOrder;
    }

    public Long getUsedInOrder() {
        return usedInOrder;
    }

    public void setUsedInOrder(Long usedInOrder) {
        this.usedInOrder = usedInOrder;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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


}
