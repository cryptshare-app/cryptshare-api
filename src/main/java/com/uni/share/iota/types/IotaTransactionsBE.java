package com.uni.share.iota.types;

import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.user.types.UserBE;

import javax.persistence.*;

@Entity
@Table(name = "t_iota_transactions")
public class IotaTransactionsBE extends AbstractBaseBE {

    @Id
    @GeneratedValue(generator = "t_iota_transactions", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "t_iota_transactions", sequenceName = "sq_iota_transactions", allocationSize = 1)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private UserBE userBE;

    @Column(name = "receiver_address")
    private String receiverAddress;


    @Column(name = "amount")
    private long amount;

    @Column(name = "index")
    private int index;

    @Column(name = "status")
    private int status;

    @Column(name = "times_checked")
    private int timesChecked;

    @Column(name = "group_title")
    private String groupTitle;

    @Version
    private Long version;

    @Column(name = "is_remainder")
    private boolean isRemainder;

    @Column(name = "sender_id")
    private long senderId;

    @Column(name = "receiver_id")
    private long receiverId;

    public String getGroupTitle() {
        return groupTitle;
    }

    public boolean isRemainder() {
        return isRemainder;
    }

    public void setRemainder(boolean remainder) {
        isRemainder = remainder;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public int getTimesChecked() {
        return timesChecked;
    }

    public void setTimesChecked(int timesChecked) {
        this.timesChecked = timesChecked;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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
