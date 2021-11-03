package com.uni.share.authentication.control;


import javax.inject.Inject;
import javax.ws.rs.core.Response;
import org.mindrot.jbcrypt.BCrypt;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.user.control.UserBA;
import com.uni.share.user.types.UserBE;

/**
 * Business activity for handling user related logic.
 *
 * @author Felix Rottler , 07.11.2018
 **/

public class AuthenticationBA {

    @Inject
    private UserBA userBA;

    @Inject
    private JWTAuthenticationBA jwtAuthenticationBA;


    /**
     * Checks if a user with the given credentials exists and issues a token if the user exists.
     *
     * @param loginTO the credentials to check
     * @return a valid jwt token.
     */
    public JWTokenTO login(final LoginTO loginTO) {
        return this.issueToken(loginTO, 0);
    }


    /**
     * Issue a token for valid credentials
     *
     * @param loginTO the credentials to provide the jwt for.
     * @param time    the amount of time the jwt will be valid
     * @return a valid jwt,
     */
    public JWTokenTO issueToken(final LoginTO loginTO, final int time) {
        final UserBE userBE = userBA.findUserByUserName(loginTO.getUserName());
        if (BCrypt.checkpw(loginTO.getPassword(), userBE.getPasswordHash())) {
            return jwtAuthenticationBA.issue(userBE, time);
        } else {
            throw new BusinessValidationException(CryptShareErrors.USER_PASSWORD_INVALID,
                    Response.Status.BAD_REQUEST);
        }

    }
}
