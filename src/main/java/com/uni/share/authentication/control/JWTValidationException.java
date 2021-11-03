package com.uni.share.authentication.control;

import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.errors.CryptShareErrors;

import javax.ws.rs.core.Response;

public class JWTValidationException extends BusinessValidationException {

    /**
     * for business validation exception.
     */
    public JWTValidationException() {
        super(CryptShareErrors.JWT_TOKEN_INVALID, Response.Status.UNAUTHORIZED);
    }
}
