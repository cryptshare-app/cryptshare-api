package com.uni.share.common.exceptions.mapper;

import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import com.uni.share.common.exceptions.types.ValidationErrorTO;

/**
 * Exception mapper for input validation exceptions.
 *
 * @author Felix Rottler, MaibornWolff GmbH
 */
@Provider
public class ConstraintValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(final ConstraintViolationException exception) {
        return ValidationErrorTO.buildResponse(exception);
    }

}
