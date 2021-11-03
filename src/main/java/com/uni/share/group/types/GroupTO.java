package com.uni.share.group.types;

import java.util.List;
import javax.validation.constraints.Size;
import com.uni.share.payments.types.PaymentTO;
import com.uni.share.products.types.ProductTO;

/**
 * Group Transport Object containing title and description of the group.
 *
 * @author Felix Rottler , 07.12.2018
 **/
public class GroupTO {

    private Long id;
    @Size(min = 3, max = 32, message = "Title must be between 3 and 32 characters")
    private String title;
    private String description;
    private String imageUrl;
    private List<GroupMembershipTO> groupMemberships;
    private List<ProductTO> products;
    private List<PaymentTO> payments;
    private GroupMembershipTO userMembership;


    /**
     * Empty constructor.
     */
    public GroupTO() {
        // empty constructor
    }


    /**
     * Parameterized constructor.
     *
     * @param title            the title of the group
     * @param description      the description of the group.
     * @param groupMemberships
     * @param products
     */
    public GroupTO(
            @Size(min = 3, max = 32, message = "Title must be between 3 and 32 characters") final String title,
            final String description, final List<GroupMembershipTO> groupMemberships,
            final List<ProductTO> products, final GroupMembershipTO userMembership, final String imageUrl) {
        this.title = title;
        this.description = description;
        this.groupMemberships = groupMemberships;
        this.products = products;
        this.userMembership = userMembership;
        this.imageUrl = imageUrl;
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
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


    public List<GroupMembershipTO> getGroupMemberships() {
        return groupMemberships;
    }


    public void setGroupMemberships(final List<GroupMembershipTO> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }


    public List<ProductTO> getProducts() {
        return products;
    }


    public void setProducts(final List<ProductTO> products) {
        this.products = products;
    }


    public GroupMembershipTO getUserMembership() {
        return userMembership;
    }


    public void setUserMembership(final GroupMembershipTO userMembership) {
        this.userMembership = userMembership;
    }


    public List<PaymentTO> getPayments() {
        return payments;
    }


    public void setPayments(final List<PaymentTO> payments) {
        this.payments = payments;
    }
}
