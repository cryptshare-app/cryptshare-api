package com.uni.share.payments.types;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.group.types.GroupBE;
import com.uni.share.products.types.ProductBE;
import com.uni.share.user.types.UserBE;

/**
 * Business entity for representing payments in the db
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Entity
@Table(name = "t_payments")
public class PaymentBE extends AbstractBaseBE {


    @Id
    @GeneratedValue(generator = "g_t_payments", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_payments", sequenceName = "sq_payments", allocationSize = 1)
    private Long id;

    @Version
    private Long version;

    @JoinColumn(name = "sender_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserBE sender;

    @JoinColumn(name = "receiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserBE receiver;

    private double amount;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @JoinColumn(name = "group_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GroupBE groupBE;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProductBE product;


    /**
     * Empty constructor.
     */
    public PaymentBE() {
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public UserBE getSender() {
        return sender;
    }


    public void setSender(final UserBE sender) {
        this.sender = sender;
    }


    public UserBE getReceiver() {
        return receiver;
    }


    public void setReceiver(final UserBE receiver) {
        this.receiver = receiver;
    }


    public double getAmount() {
        return amount;
    }


    public void setAmount(final double amount) {
        this.amount = amount;
    }


    public PaymentStatus getStatus() {
        return status;
    }


    public void setStatus(final PaymentStatus status) {
        this.status = status;
    }


    public GroupBE getGroupBE() {
        return groupBE;
    }


    public void setGroupBE(final GroupBE groupBE) {
        this.groupBE = groupBE;
    }


    public Long getVersion() {
        return version;
    }


    public void setVersion(final Long version) {
        this.version = version;
    }


    public ProductBE getProduct() {
        return product;
    }


    public void setProduct(final ProductBE productBE) {
        this.product = productBE;
    }
}
