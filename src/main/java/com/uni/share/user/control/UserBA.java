package com.uni.share.user.control;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.user.entity.UserEM;
import com.uni.share.user.types.UserBE;
import com.uni.share.user.types.UserInformationTO;
import com.uni.share.user.types.UserTO;

import java.util.List;

/**
 * Business activity for handling user related logic.
 *
 * @author Felix Rottler
 */
@Stateless
public class UserBA {

    @Inject
    private UserEM userEM;

    @Inject
    private UserMapperBA userMapperBA;


    /**
     * Find a user by username.
     *
     * @param userName the username of the user.
     * @return the user.
     */
    public UserBE findUserByUserName(final String userName) {
        return userEM.findByUserName(userName).orElseThrow(
                () -> new BusinessValidationException(CryptShareErrors.USER_NOT_FOUND,
                        Response.Status.BAD_REQUEST)
        );
    }


    /**
     * Persists a given user and creates a iota for it.
     *
     * @param userBE the user to persist
     * @return the persisted user.
     */
    public UserBE create(final UserBE userBE) {
        return userEM.persist(userBE);
    }


    /**
     * Finds a user by id and throws exception if user does not exist.
     *
     * @param userId the id of the user
     * @return the identified user .
     */
    public UserBE getUserById(final Long userId) {
        return userEM.findById(userId).orElseThrow(
                () -> new BusinessValidationException(CryptShareErrors.USER_NOT_FOUND,
                        Response.Status.BAD_REQUEST)
        );
    }


    /**
     * Get the user that performs a request and is identified by its user id
     *
     * @param securityContext the security context containg the user id
     * @return the identified user.
     */
    public UserBE getCurrentUser(final SecurityContext securityContext) {
        final long userId;
        try {
            userId = Long.parseLong(securityContext.getUserPrincipal().getName());
        } catch (NumberFormatException e) {
            throw new BusinessValidationException(CryptShareErrors.USER_ID_INVALID, Response.Status.BAD_REQUEST);
        }
        return getUserById(userId);
    }


    /**
     * Get the user information of the current user {@see getCurrentUser}
     *
     * @param securityContext the security context of the request.
     * @return the information of the identified user.
     */
    public UserInformationTO getCurrentUserInformation(final SecurityContext securityContext) {
        final UserBE userBE = getCurrentUser(securityContext);
        return userMapperBA.toInformationTO(userBE);
    }


    /**
     * @param userTO
     * @param userBE
     */
    public void update(UserTO userTO, UserBE userBE) {
        userBE.setUserName(userTO.getUserName());

    }


    /**
     * @param userId
     * @return
     */
    public UserInformationTO getUserInformationById(final long userId) {
        final UserBE userBE = userEM.findById(userId).orElseThrow(
                () -> throwUserNotFound(userId)
        );
        return userMapperBA.toInformationTO(userBE);
    }


    private BusinessValidationException throwUserNotFound(final long userId) {
        return new BusinessValidationException(CryptShareErrors.USER_NOT_FOUND, Response.Status.NOT_FOUND);
    }

    public List<String> fetchUserNames() {

        return userEM.getAllUserNames();
    }
}
