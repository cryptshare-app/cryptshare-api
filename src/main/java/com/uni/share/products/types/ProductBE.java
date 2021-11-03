package com.uni.share.products.types;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.group.types.GroupBE;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.user.types.UserBE;

/**
 * Business entity to represent products
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Entity
@Table(name = "t_products")
public class ProductBE extends AbstractBaseBE {

    @Id
    @GeneratedValue(generator = "g_t_products", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_products", sequenceName = "sq_products", allocationSize = 1)
    private Long id;

    @Version
    private Long version;

    @Column(name = "image_url")
    private String imageUrl;

    private double price;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_description")
    private String productDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private UserBE userBE;

    @OneToMany(mappedBy = "product", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentBE> payments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupBE groupBE;


    public ProductBE() {
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public Long getVersion() {
        return version;
    }


    public void setVersion(final Long version) {
        this.version = version;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public double getPrice() {
        return price;
    }


    public void setPrice(final double price) {
        this.price = price;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(final String productName) {
        this.productName = productName;
    }


    public String getProductDescription() {
        return productDescription;
    }


    public void setProductDescription(final String productDescription) {
        this.productDescription = productDescription;
    }


    public UserBE getUserBE() {
        return userBE;
    }


    public void setUserBE(final UserBE userBE) {
        this.userBE = userBE;
    }


    public GroupBE getGroupBE() {
        return groupBE;
    }


    public void setGroupBE(final GroupBE groupBE) {
        this.groupBE = groupBE;
    }


    public List<PaymentBE> getPayments() {
        return payments;
    }


    public void setPayments(final List<PaymentBE> payments) {
        this.payments = payments;
    }


    public void addPayment(final PaymentBE paymentBE) {
        this.payments.add(paymentBE);
    }
}
