package com.uni.share.user.types;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.group.types.GroupBE;
import com.uni.share.group.types.GroupMembershipBE;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.products.types.ProductBE;


/**
 * Business entity for users.
 *
 * @author Felix Rottler
 */
@Entity
@Table(name = "t_users")
public class UserBE extends AbstractBaseBE {


    @Id
    @GeneratedValue(generator = "g_t_users", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_users", sequenceName = "sq_users", allocationSize = 1)
    private Long id;

    @Version
    private Long version;

    @Column(name = "user_name")
    private String userName;

    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "password_salt")
    private String passwordSalt;

    @OneToMany(mappedBy = "userBE")
    private List<GroupMembershipBE> memberShipBES;

    @OneToMany(mappedBy = "userBE")
    private List<GroupBE> groupBES;

    @OneToMany(mappedBy = "userBE", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductBE> products;

    @OneToMany(mappedBy = "sender", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentBE> sendingPayments;

    @OneToMany(mappedBy = "receiver", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentBE> receivingPayments;


    /**
     * Empty constructor
     */
    public UserBE() {
        // empty constructor.
    }


    public Long getVersion() {
        return version;
    }


    public void setVersion(final Long version) {
        this.version = version;
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(final String userName) {
        this.userName = userName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(final String email) {
        this.email = email;
    }


    public String getPasswordHash() {
        return passwordHash;
    }


    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }


    public String getPasswordSalt() {
        return passwordSalt;
    }


    public void setPasswordSalt(final String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }


    public List<GroupMembershipBE> getMemberShipBES() {
        return memberShipBES;
    }


    public void setMemberShipBES(final List<GroupMembershipBE> memberShipBES) {
        this.memberShipBES = memberShipBES;
    }


    public List<GroupBE> getGroupBES() {
        return groupBES;
    }


    public void setGroupBES(final List<GroupBE> groupBES) {
        this.groupBES = groupBES;
    }


    public List<ProductBE> getProducts() {
        return products;
    }


    public void setProducts(final List<ProductBE> products) {
        this.products = products;
    }


    public List<PaymentBE> getSendingPayments() {
        return sendingPayments;
    }


    public void setSendingPayments(final List<PaymentBE> sendingPayments) {
        this.sendingPayments = sendingPayments;
    }


    public List<PaymentBE> getReceivingPayments() {
        return receivingPayments;
    }


    public void setReceivingPayments(final List<PaymentBE> receivingPayments) {
        this.receivingPayments = receivingPayments;
    }


    public void addSendingPayment(final PaymentBE paymentBE) {
        this.sendingPayments.add(paymentBE);
    }


    public void addReceivingPayment(final PaymentBE paymentBE) {
        this.receivingPayments.add(paymentBE);
    }
}
