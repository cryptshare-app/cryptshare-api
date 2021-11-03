package com.uni.share.group.types;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import com.uni.share.common.types.AbstractBaseBE;
import com.uni.share.payments.types.PaymentBE;
import com.uni.share.products.types.ProductBE;
import com.uni.share.user.types.UserBE;

/**
 * Group Business Entity that represents a group object in the database.
 *
 * @author Felix Rottler , 07.12.2018
 **/
@Entity
@Table(name = "t_groups")
@NamedQueries(value = {
        @NamedQuery(name = GroupBE.GET_ALL,
                query = "SELECT groupBE " +
                        "FROM GroupBE groupBe"),
        @NamedQuery(name = GroupBE.FIND_BY_TITLE,
                query = "SELECT groupBE " +
                        "FROM GroupBE groupBE " +
                        "WHERE groupBE.title= :title")
})
public class GroupBE extends AbstractBaseBE {

    public static final String GET_ALL = "GroupBE.getAll";
    public static final String FIND_BY_TITLE = "GroupBE.getGroupByTitle";


    @Id
    @GeneratedValue(generator = "g_t_groups", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "g_t_groups", sequenceName = "sq_groups", allocationSize = 1)
    private Long id;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserBE userBE;


    @OneToMany(mappedBy = "groupBE", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<GroupMembershipBE> groupMembershipBES;

    @OneToMany(mappedBy = "groupBE", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ProductBE> products;

    @OneToMany(mappedBy = "groupBE", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PaymentBE> payments;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;


    /**
     * Empty constructor.
     */
    public GroupBE() {
        // empty constructor.
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public UserBE getUserBE() {
        return userBE;
    }


    public void setUserBE(final UserBE userBE) {
        this.userBE = userBE;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(final String title) {
        this.title = title;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(final String description) {
        this.description = description;
    }


    public List<GroupMembershipBE> getGroupMembershipBES() {
        return groupMembershipBES;
    }


    public void setGroupMembershipBES(final List<GroupMembershipBE> groupMembershipBES) {
        this.groupMembershipBES = groupMembershipBES;
    }


    public List<ProductBE> getProducts() {
        return products;
    }


    public void setProducts(final List<ProductBE> products) {
        this.products = products;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
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
