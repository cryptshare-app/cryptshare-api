package com.uni.share.payments.types;

import com.uni.share.payments.control.PaymentBuilder;

/**
 * Payment transport object
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class PaymentTO {

    private Long id;
    private Double amount;
    private Long senderId;
    private String senderUserName;
    private Long receiverId;
    private String receiverUserName;
    private Long groupId;
    private String groupTitle;
    private String updatedAt;
    private String createdAt;
    private Long productId;
    private String status;


    public static PaymentBuilder newInstance() {
        return new PaymentBuilder();
    }


    /**
     * empty constructor.
     */
    public PaymentTO() {
        //empty constructor.
    }


    public Long getId() {
        return id;
    }


    public void setId(final Long id) {
        this.id = id;
    }


    public Double getAmount() {
        return amount;
    }


    public void setAmount(final Double amount) {
        this.amount = amount;
    }


    public Long getSenderId() {
        return senderId;
    }


    public void setSenderId(final Long senderId) {
        this.senderId = senderId;
    }


    public String getSenderUserName() {
        return senderUserName;
    }


    public void setSenderUserName(final String senderUserName) {
        this.senderUserName = senderUserName;
    }


    public Long getReceiverId() {
        return receiverId;
    }


    public void setReceiverId(final Long receiverId) {
        this.receiverId = receiverId;
    }


    public String getReceiverUserName() {
        return receiverUserName;
    }


    public void setReceiverUserName(final String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }


    public Long getGroupId() {
        return groupId;
    }


    public void setGroupId(final Long groupId) {
        this.groupId = groupId;
    }


    public String getGroupTitle() {
        return groupTitle;
    }


    public void setGroupTitle(final String groupTitle) {
        this.groupTitle = groupTitle;
    }


    public String getUpdatedAt() {
        return updatedAt;
    }


    public void setUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
    }


    public Long getProductId() {
        return productId;
    }


    public void setProductId(final Long productId) {
        this.productId = productId;
    }


    public String getStatus() {
        return status;
    }


    public void setStatus(final String status) {
        this.status = status;
    }
}
