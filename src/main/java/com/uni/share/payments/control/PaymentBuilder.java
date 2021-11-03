package com.uni.share.payments.control;

import com.uni.share.payments.types.PaymentTO;

/**
 * Builder Pattern example
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public final class PaymentBuilder {

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


    public PaymentBuilder withId(final Long id) {
        this.id = id;
        return this;
    }


    public PaymentBuilder withAmount(final double amount) {
        this.amount = amount;
        return this;
    }


    public PaymentBuilder withSenderName(final String senderUserName) {
        this.senderUserName = senderUserName;
        return this;
    }


    public PaymentBuilder withSenderId(final Long id) {
        this.senderId = id;
        return this;
    }


    public PaymentBuilder withReceiverName(final String receiverName) {
        this.receiverUserName = receiverName;
        return this;
    }


    public PaymentBuilder withReceiverId(final Long id) {
        this.receiverId = id;
        return this;
    }


    public PaymentBuilder withGroupTitle(final String groupTitle) {
        this.groupTitle = groupTitle;
        return this;
    }


    public PaymentBuilder withGroupId(final Long groupId) {
        this.groupId = groupId;
        return this;
    }


    public PaymentBuilder withUpdatedAt(final String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }


    public PaymentBuilder withCreatedAt(final String createdAt) {
        this.createdAt = createdAt;
        return this;
    }


    public PaymentBuilder withProductId(final Long id) {
        this.productId = id;
        return this;
    }


    public PaymentBuilder withStatus(final String status) {
        this.status = status;
        return this;
    }


    public PaymentTO build() {
        final PaymentTO result = new PaymentTO();
        result.setReceiverUserName(receiverUserName);
        result.setSenderId(senderId);
        result.setId(id);
        result.setSenderUserName(senderUserName);
        result.setAmount(amount);
        result.setGroupId(groupId);
        result.setGroupTitle(groupTitle);
        result.setReceiverId(receiverId);
        result.setCreatedAt(createdAt);
        result.setUpdatedAt(updatedAt);
        result.setStatus(status);
        result.setProductId(productId);
        return result;
    }


}
