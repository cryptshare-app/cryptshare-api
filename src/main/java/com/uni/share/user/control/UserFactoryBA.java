package com.uni.share.user.control;

import com.uni.share.user.types.UserBE;
import com.uni.share.user.types.UserTO;

import javax.ejb.Stateless;

/**
 * Factory activity for users.
 *
 * @author Felix Rottler , 08.11.2018
 **/
@Stateless
public class UserFactoryBA {

    /**
     * Create a user transport object from given parameters
     *
     * @param userName the name of the user
     * @param email    the email of the user
     * @param password the password of the user
     * @return the newly created user.
     */
    public UserTO create(final String userName,
                         final String email,
                         final String password) {
        return new UserTO(userName, email, password);
    }

    /**
     * Create a user business entity
     *
     * @param userName     the name of the user.
     * @param email        the email of the user
     * @param passwordHash the password hash of the user
     * @param passwordSalt the password salt of the user
     * @return the newly created business entity.
     */
    public UserBE create(final String userName,
                         final String email,
                         final String passwordHash,
                         final String passwordSalt) {
        final UserBE userBE = new UserBE();
        userBE.setUserName(userName);
        userBE.setEmail(email);
        userBE.setPasswordHash(passwordHash);
        userBE.setPasswordSalt(passwordSalt);
        return userBE;
    }
}
