package com.uni.share.user.types;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.uni.share.payments.types.PaymentTO;

/**
 * Transport object for users.
 *
 * @author Felix Rottler
 */

public class UserTO {

    // https://docs.jboss.org/hibernate/beanvalidation/spec/1.1/api/
    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username may not be blank, empty or null.")
    private String userName;

    @Size(min = 2, max = 12, message = "Password must be between 2 and 12 characters")
    private String password;

    @Pattern(regexp = "^\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "Must be a valid email")
    private String email;

    private List<PaymentTO> payments;


    /**
     * Empty constructor
     */
    public UserTO() {
        // empty constructor
    }


    /**
     * Parameterized constructor
     *
     * @param userName name of the user
     * @param password password of the user
     * @param email    email of the user
     */
    public UserTO(final String userName, final String password, final String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(final String userName) {
        this.userName = userName;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(final String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(final String email) {
        this.email = email;
    }


    public List<PaymentTO> getPayments() {
        return payments;
    }


    public void setPayments(final List<PaymentTO> payments) {
        this.payments = payments;
    }
}


