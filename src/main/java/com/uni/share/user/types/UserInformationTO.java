package com.uni.share.user.types;

import java.util.List;
import com.uni.share.group.types.GroupMembershipTO;
import com.uni.share.payments.types.PaymentTO;

/**
 * Transport object which contains non sensitive information about a user.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
public class UserInformationTO {

    private String userName;
    private String email;
    private Long id;
    private List<PaymentTO> senderPayments;
    private List<PaymentTO> receiverPayments;
    private List<GroupMembershipTO> groupMemberships;


    /**
     * Empty constructor
     */
    public UserInformationTO() {
        // empty constructor
    }


    /**
     * Parameterized constructor
     *
     * @param userName the name of the user
     * @param email    the email of the user
     */
    public UserInformationTO(final String userName, final String email, final Long id) {
        this.userName = userName;
        this.email = email;
        this.id = id;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
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


    public List<PaymentTO> getSenderPayments() {
        return senderPayments;
    }


    public void setSenderPayments(final List<PaymentTO> senderPayments) {
        this.senderPayments = senderPayments;
    }


    public List<PaymentTO> getReceiverPayments() {
        return receiverPayments;
    }


    public void setReceiverPayments(final List<PaymentTO> receiverPayments) {
        this.receiverPayments = receiverPayments;
    }


    public List<GroupMembershipTO> getGroupMemberships() {
        return groupMemberships;
    }


    public void setGroupMemberships(final List<GroupMembershipTO> groupMemberships) {
        this.groupMemberships = groupMemberships;
    }
}
