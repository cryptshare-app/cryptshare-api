package com.uni.share.authentication.session;

import javax.enterprise.context.RequestScoped;
import com.uni.share.user.types.UserBE;

/**
 * Objects that represents the current user in each request
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@RequestScoped
public class UserTransaction {
    private UserBE currentUser;


    /**
     * Empty constructor
     */
    public UserTransaction() {
        //empty constructor
    }


    public UserBE getCurrentUser() {
        return currentUser;
    }


    public void setCurrentUser(final UserBE currentUser) {
        this.currentUser = currentUser;
    }
}
