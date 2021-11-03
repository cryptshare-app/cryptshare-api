package com.uni.share.common.exceptions;

import javax.ejb.ApplicationException;
import javax.ws.rs.core.Response;
import com.uni.share.common.exceptions.errors.CryptShareErrors;

/**
 * Business validation exception for wrapping exceptions .
 *
 * @author Felix Rottler , 07.11.2018
 **/
@ApplicationException
public class BusinessValidationException extends RuntimeException {

    private final Response.Status status;
    private final String errorMessage;


    /**
     * for business validation exception.
     *
     * @param errorMessage the custom error message of the exception.
     */
    public BusinessValidationException(final CryptShareErrors errorMessage, final Response.Status status) {
        super(errorMessage.toString());
        this.status = status;
        this.errorMessage = errorMessage.toString();
    }


    public Response.Status getStatus() {
        return status;
    }


    public String getErrorMessage() {
        return errorMessage;
    }

}
