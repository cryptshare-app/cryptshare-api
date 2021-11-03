package com.uni.share.common.exceptions.types;

import javax.ws.rs.core.Response;
import com.uni.share.common.exceptions.BusinessValidationException;

/**
 * Error transport object
 *
 * @author Felix Rottler , 08.11.2018
 **/
public class BusinessErrorTO extends AbstractBusinessError<BusinessValidationException> {
    private String errorMessage;
    private int status;


    /**
     * Empty constructor
     */
    public BusinessErrorTO() {
        //empty constructor
    }


    public String getErrorMessage() {
        return errorMessage;
    }


    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
    }


    public int getStatus() {
        return status;
    }


    public void setStatus(final int status) {
        this.status = status;
    }


    public void setErrorMessageFormatted(final String errorMessage, Object... params) {
        this.errorMessage = String.format(errorMessage, params);

    }


    @Override
    public BusinessErrorTO fromException(final BusinessValidationException exception) {
        final BusinessErrorTO errorTO = new BusinessErrorTO();
        errorTO.setErrorMessage(exception.getErrorMessage());
        return errorTO;
    }


    public static Response buildResponse(final BusinessValidationException exception) {
        BusinessErrorTO result = new BusinessErrorTO();
        result = result.fromException(exception);
        result.setStatus(exception.getStatus().getStatusCode());
        return Response.status(exception.getStatus()).entity(result).build();
    }
}
