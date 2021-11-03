package com.uni.share.authentication.filter;


import com.uni.share.authentication.control.JWTAuthenticationBA;
import com.uni.share.authentication.control.JWTValidationException;
import com.uni.share.authentication.session.UserTransaction;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;
import com.uni.share.user.control.UserBA;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;

/**
 * A filter for REST endpoints that need authentication.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Provider
@JWTSecured
@Priority(Priorities.AUTHENTICATION)
public class JWTFilter implements ContainerRequestFilter {

    @Inject
    private JWTAuthenticationBA jwtAuthenticationBA;

    @Inject
    private UserBA userBA;

    @Inject
    private UserTransaction userTransaction;


    /**
     * Filter incoming requests and extract Authentication Header to verify the request comes from
     * a user which is validated.
     * <p>
     * Throws an exception if the json-web-token inside the header can not be verified.
     *
     * @param requestContext the request context
     * @throws IOException
     */
    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String authenticationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authenticationHeader == null) {
            throw new BusinessValidationException(CryptShareErrors.JWT_AUTHORIZATION_INVALID, Response.Status.BAD_REQUEST);
        }


        final String jwToken = jwtAuthenticationBA.extractToken(authenticationHeader);
        jwtAuthenticationBA.validate(jwToken).orElseThrow(JWTValidationException::new);
        final Long userId = jwtAuthenticationBA.getUserId(jwToken);
        userTransaction.setCurrentUser(userBA.getUserById(userId));
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {

                return () -> {

                    return jwtAuthenticationBA.getUserId(jwToken).toString();
                };
            }


            @Override
            public boolean isUserInRole(final String s) {
                return false;
            }


            @Override
            public boolean isSecure() {
                return false;
            }


            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });
    }
}
