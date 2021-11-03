package com.uni.share.products.types;

import java.util.ArrayList;
import java.util.List;

import com.uni.share.payments.types.PaymentTO;

/**
 * Transport object for products.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class ProductTO {

    private long id;
    //@NotBlank(message = "Product name may not be empty")
    private String productName;
    private String productDescription;
    private String imageUrl;
    //@Min(value = 0L, message = "The value must be positive")
    private double price;
    private long creatorId;
    private long groupId;
    private String groupName;
    private List<PaymentTO> payments = new ArrayList<>();


    /**
     * Empty constructor
     */
    public ProductTO() {
        //empty constructor
    }


    public long getId() {
        return id;
    }


    public void setId(final long id) {
        this.id = id;
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


    public long getCreatorId() {
        return creatorId;
    }


    public void setCreatorId(final long creatorId) {
        this.creatorId = creatorId;
    }


    public long getGroupId() {
        return groupId;
    }


    public void setGroupId(final long groupId) {
        this.groupId = groupId;
    }


    public String getGroupName() {
        return groupName;
    }


    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }


    public List<PaymentTO> getPayments() {
        return payments;
    }


    public void setPayments(final List<PaymentTO> payments) {
        this.payments = payments;
    }
}
