package com.uni.share.common.exceptions.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.uni.share.common.exceptions.BusinessValidationException;
import com.uni.share.common.exceptions.types.BusinessErrorTO;

/**
 * Helper class that maps BusinessValidationExceptions to an response.
 *
 * @author Felix Rottler , 08.11.2018
 **/
@Provider
public class BusinessValidationExceptionMapper implements ExceptionMapper<BusinessValidationException> {

    /**
     * Maps a thrown BusinessValidationException to an response
     *
     * @param validationException the validation exception that was thrown-
     * @return the mapped response.
     */
    @Override
    public Response toResponse(final BusinessValidationException validationException) {
        return BusinessErrorTO.buildResponse(validationException);
    }
}
