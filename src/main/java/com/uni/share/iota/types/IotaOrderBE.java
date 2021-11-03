package com.uni.share.iota.types;

import com.uni.share.common.types.AbstractBaseBE;

import javax.persistence.*;

@Entity
@Table(name = "t_iota_orders")
public class IotaOrderBE extends AbstractBaseBE {

    @Id
    @GeneratedValue(generator = "t_iota_orders", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "t_iota_orders", sequenceName = "sq_iota_orders", allocationSize = 1)
    private Long id;

    @Version
    private Long version;

    private String seed;

    @Column(name = "receiver_address")
    private String receiverAddress;

    @Column(name = "remainder_address")
    private String remainderAddress;

    private Long amount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name="currently_processed")
    private boolean currentlyProcessed;

    public boolean isCurrentlyProcessed() {
        return currentlyProcessed;
    }

    public void setCurrentlyProcessed(boolean currentlyProcessed) {
        this.currentlyProcessed = currentlyProcessed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getRemainderAddress() {
        return remainderAddress;
    }

    public void setRemainderAddress(String remainderAddress) {
        this.remainderAddress = remainderAddress;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
