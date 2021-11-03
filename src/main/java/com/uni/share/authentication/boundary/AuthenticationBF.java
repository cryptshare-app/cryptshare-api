package com.uni.share.authentication.boundary;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import com.uni.share.authentication.control.AuthenticationBA;
import com.uni.share.authentication.control.JWTAuthenticationBA;
import com.uni.share.authentication.types.JWTokenTO;
import com.uni.share.authentication.types.LoginTO;
import com.uni.share.authentication.types.ValidationResult;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;

/**
 * Business facade for handling authentication.
 * <p>Authentication Business Facade handles business logic for user
 * authentication and jwt requests.</p>
 *
 * @author Felix Rottler , 07.11.2018
 **/
public class AuthenticationBF {

    @Inject
    private AuthenticationBA authenticationBA;

    @Inject
    private JWTAuthenticationBA jwtAuthenticationBA;


    /**
     * Authenticates given credentials and returns a valid jwt token.
     *
     * @param loginTO the credentials to authenticate
     * @return a valid jwt token.
     */
    public JWTokenTO login(final LoginTO loginTO) {
        return authenticationBA.login(loginTO);
    }


    /**
     * Entrypoint for refreshing tokens.
     * <p>If the access token has expired, the user may request a new access token
     * with an refresh token.</p>
     *
     * @param token The token to refresh
     * @return the newly generated tokens
     */
    public JWTokenTO refreshToken(final JWTokenTO token) {
        return jwtAuthenticationBA.refresh(token);
    }


    public JWTokenTO issueToken(final LoginTO loginTO, final int time) {
        return authenticationBA.issueToken(loginTO, time);
    }


    /**
     * Validate if a given jwt is valid or not.
     *
     * @param token the token to validate
     */
    public void validateToken(final String token) {
        final ValidationResult validationResult = jwtAuthenticationBA.validate(token);
        if (!validationResult.isValid()) {
            throw new BusinessValidationException(CryptShareErrors.JWT_TOKEN_INVALID, Response.Status.BAD_REQUEST);
        }
    }
}
